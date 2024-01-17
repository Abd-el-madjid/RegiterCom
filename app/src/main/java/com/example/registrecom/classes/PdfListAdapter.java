package com.example.registrecom.classes;

// CustomListAdapter.java

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;

import java.util.List;

// CustomListAdapter.java

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {
    private Context context;
    private List<PdfList> itemList;
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(String link);
    }


    public PdfListAdapter(Context context, List<PdfList> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PdfList currentItem = itemList.get(position);

        if (currentItem != null) {
            holder.nameTextView.setText(currentItem.getFilename());

            // Set the item ID_demande as a tag to the itemView
            holder.itemView.setTag(currentItem.getFileurl());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pdf_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PdfList currentItem = itemList.get(getAdapterPosition());
                    if (currentItem != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType("application/pdf");
                        intent.setData(Uri.parse(currentItem.getFileurl()));
                        context.startActivity(intent);
                    }
                }
            });
        }
    }


    public void updateData(List<PdfList> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }
}
