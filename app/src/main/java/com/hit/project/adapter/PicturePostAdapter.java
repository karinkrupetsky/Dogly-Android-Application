package com.hit.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hit.project.R;

import java.io.File;
import java.util.List;

public class PicturePostAdapter extends RecyclerView.Adapter<PicturePostAdapter.PictureViewHolder> {
    private List<Uri> uriList;
    private Context context;
    private MyPictureListener listener;

    public interface MyPictureListener {
        void onRemoveClicked(int position, View view);
        void onPictureLongClicked(int position, View view);
    }

    public void setListener(MyPictureListener listener) {
        this.listener=listener;
    }

    public PicturePostAdapter(List<Uri> uriList, Context context) {

        this.uriList = uriList;
        this.context = context;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIv;
        ImageView removeIV;
        TextView countTV;
        LinearLayout linearLayout;

        public PictureViewHolder(View itemView) {
            super(itemView);
            imageIv = itemView.findViewById(R.id.dog_picture_post_layout);
            removeIV = itemView.findViewById(R.id.remove_picture);
            countTV = itemView.findViewById(R.id.picture_counter);
            linearLayout = itemView.findViewById(R.id.picture_linear_layout);

            removeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                        listener.onRemoveClicked(getAdapterPosition(), v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    if(listener!=null)
                        listener.onPictureLongClicked(getAdapterPosition(), v);
                    return false;
                }
            });
        }
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_post_layout, parent, false);
        PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
        return pictureViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        if(position>0) {
            holder.linearLayout.setBackground(null);
        }
        else {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#10A5F5"));
        }
        holder.countTV.setText(position+1+"");

        Uri uriImage = uriList.get(position);
        Glide.with(context)
                .load(uriImage)
                .apply(new RequestOptions())
                .into(holder.imageIv);

        //holder.imageIv.setImageURI(uriList.get(position));
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