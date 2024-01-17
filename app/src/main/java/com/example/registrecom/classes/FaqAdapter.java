package com.example.registrecom.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Models.Canaux;
import com.example.registrecom.R;

import java.util.List;

public  class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.VersionVH> {

 List<Faq>  questionlist;

    public FaqAdapter(List<Faq> faqList) {
        this.questionlist = faqList ;
    }


    @NonNull
    @Override
    public FaqAdapter.VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faqrows,parent,false);
       return new VersionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqAdapter.VersionVH holder, int position) {
        Faq faq =  questionlist.get(position);
        holder.question.setText(faq.getQuestion());
        holder.reponse.setText(faq.getReponse());
        boolean isExpendable = questionlist.get(position).isExpendable();
        holder.expandable_layout.setVisibility(isExpendable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return questionlist.size();
    }

    public class VersionVH  extends  RecyclerView.ViewHolder{

        TextView question , reponse ;
        LinearLayout  linear_layout ;
        RelativeLayout expandable_layout;

        public VersionVH(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            reponse = itemView.findViewById(R.id.reponse);

            linear_layout  = itemView.findViewById(R.id.linear_layout);
            expandable_layout  = itemView.findViewById(R.id.expandable_layout);

            linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Faq faq = questionlist.get(getAdapterPosition());
                    faq.setExpendable(!faq.isExpendable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}