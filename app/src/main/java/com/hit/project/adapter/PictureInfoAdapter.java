package com.hit.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hit.project.R;

import java.util.List;

public class PictureInfoAdapter extends RecyclerView.Adapter<PictureInfoAdapter.PictureViewHolder> {
    private List<Uri> uriList;
    private Context context;
    private MyPictureListener listener;

    public interface MyPictureListener {
        void onPictureClicked(int position, View view);
    }

    public void setListener(MyPictureListener listener) {
        this.listener=listener;
    }

    public PictureInfoAdapter(List<Uri> uriList, Context context) {

        this.uriList = uriList;
        this.context = context;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIv;

        public PictureViewHolder(View itemView) {
            super(itemView);
            imageIv = itemView.findViewById(R.id.dog_picture_info_layout);

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    if(listener!=null)
                        listener.onPictureClicked(getAdapterPosition(), v);
                    return false;
                }
            });
        }
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_info_layout, parent, false);
        PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
        return pictureViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {

        Uri uriImage = uriList.get(position);
        Glide
                .with(context)
                .load(uriImage)
                .apply(new RequestOptions())
                .into(holder.imageIv);
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}