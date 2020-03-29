package com.proton.carepatchtemp.test;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.carepatchtemp.R;
import com.wms.logger.Logger;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.VH> {

    private Context context;
    private List<TestBean> datum;

    public TestAdapter(Context context, List<TestBean> datum) {
        this.context = context;
        this.datum = datum;
    }

    public void setProgress(int progress, int positon) {
        datum.get(positon).setProgress(progress);
        datum.get(positon).setProgressSize("已下载: "+progress+" M ");
        notifyItemChanged(positon,"局部刷新");
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_test_layout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            Logger.w("payloads is null ");
            onBindViewHolder(holder,position);
        }else {
            Logger.w("payloads is not null");
            holder.progressBar.setProgress(datum.get(position).getProgress());
            holder.txtProgressSize.setText(datum.get(position).getProgressSize());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.progressBar.setProgress(datum.get(position).getProgress());
        holder.txtProgressSize.setText(datum.get(position).getProgressSize());
        holder.img.setImageURI("https://pics3.baidu.com/feed/9f510fb30f2442a74cac27452fb3cb4dd31302a3.jpeg?token=cecfb89864eabcfba88ba60577a42142");
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView txtProgressSize;
        private SimpleDraweeView img;
        public VH(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.id_progress);
            txtProgressSize=itemView.findViewById(R.id.id_progress_size);
            img=itemView.findViewById(R.id.image);
        }
    }
}
