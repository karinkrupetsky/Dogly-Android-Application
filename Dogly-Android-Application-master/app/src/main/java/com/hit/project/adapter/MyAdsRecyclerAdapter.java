package com.hit.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hit.project.R;
import com.hit.project.model.AdoptionItemData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdsRecyclerAdapter extends RecyclerView.Adapter<MyAdsRecyclerAdapter.MyRecyclerViewHolder>
{
    @Nullable
    private List<AdoptionItemData> listData;
    private MyAdsRecyclerListener listener;

    public void setData(List<AdoptionItemData> data) {
        listData = data;
    }

    public List<AdoptionItemData> getData() {
        return listData;
    }

    public void setRecyclerListener(MyAdsRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_adoption_card_content, parent, false);
        return new MyRecyclerViewHolder(view);
    }

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position) {
        assert listData != null;
        holder.title.setText(listData.get(position).getTitle());
        holder.city.setText(listData.get(position).getCity());
        holder.description.setText(listData.get(position).getText());

        if(listData.get(position).getImagesURL() != null) {
            Glide.with(holder.imageView.getContext())
                    .load(listData.get(position).getImagesURL().get(0))
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.mipmap.dog_profile);
        }
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }


    class MyRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView title;
        private TextView city;
        private TextView description;
        private ImageButton editB;
        private ImageButton deleteB;

        public MyRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            city = itemView.findViewById(R.id.city);
            description = itemView.findViewById(R.id.description);
            editB = itemView.findViewById(R.id.edit_button);
            deleteB = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });

            editB.setOnClickListener(view -> {
                if(listener !=null){
                    assert listData != null;
                    listener.onEditClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });

            deleteB.setOnClickListener(view -> {
                if(listener !=null){
                    assert listData != null;
                    listener.onDeleteClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });
        }
    }

    public static interface MyAdsRecyclerListener {
        void onItemClick(int position, View clickedView, AdoptionItemData clickedAd);
        void onEditClick(int position, View clickedView, AdoptionItemData clickedAd);
        void onDeleteClick(int position, View clickedView, AdoptionItemData clickedAd);
    }

}
