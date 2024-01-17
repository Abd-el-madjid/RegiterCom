package com.example.registrecom.classes;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.database.Cursor;

public class DownloadProgressUpdater {

    public static final long DOWNLOAD_SUCCESS = 100L;
    public static final long DOWNLOAD_FAILED = -100L;

    private DownloadManager manager;
    private long downloadId;
    private DownloadProgressListener listener;
    private DownloadManager.Query query;
    private int totalBytes;

    public DownloadProgressUpdater(DownloadManager manager, long downloadId, DownloadProgressListener listener) {
        this.manager = manager;
        this.downloadId = downloadId;
        this.listener = listener;
        this.query = new DownloadManager.Query();
        this.totalBytes = 0;
        query.setFilterById(downloadId);
    }

    @SuppressLint("Range")
    public void run() {
        while (downloadId > 0) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try (Cursor cursor = manager.query(query)) {
                if (cursor.moveToFirst()) {
                    if (totalBytes <= 0)
                        totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    int bytesDownloadSoFar = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                    switch (downloadStatus) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            listener.updateProgress(DOWNLOAD_SUCCESS);
                            return;
                        case DownloadManager.STATUS_FAILED:
                            listener.updateProgress(DOWNLOAD_FAILED);
                            return;
                        default:
                            long downloadProgress = bytesDownloadSoFar * 100L / totalBytes;
                            listener.updateProgress(downloadProgress);
                    }
                }
            }
        }
    }

    public interface DownloadProgressListener {
        void updateProgress(long progress);
    }
}
