package com.example.registrecom.classes;
public class PdfList {

    String filename, fileurl;

    public PdfList() {
    }

    public PdfList(String filename, String fileurl) {
        this.filename = extractMiddlePart(filename);
        this.fileurl = fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = extractMiddlePart(filename);
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    private String extractMiddlePart(String fileName) {
        // Split the file name into three parts based on underscores
        String[] parts = fileName.split("_");

        // Extract the desired part (assuming it's the second part)
        if (parts.length >= 2) {
            return parts[1].trim();
        } else {
            // Return the original file name if there are not enough parts
            return fileName.trim();
        }
    }
}
