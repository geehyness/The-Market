package com.yukisoft.themarket.JavaRepositories;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yukisoft.themarket.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<ItemModel> itemList;
    private OnItemClickListener itemListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDescription;
        public TextView itemCondition;
        public TextView itemPrice;

        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemCondition = itemView.findViewById(R.id.itemCondition);
            itemPrice = itemView.findViewById(R.id.itemPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ItemAdapter(ArrayList<ItemModel> exampleList) {
        itemList = exampleList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        ItemViewHolder evh = new ItemViewHolder(v, itemListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        ItemModel currentItem = itemList.get(i);

        //itemViewHolder.itemImage.setImageResource(currentItem.getImageUrl());
        itemViewHolder.itemTitle.setText(currentItem.getName());
        itemViewHolder.itemDescription.setText(currentItem.getDetails());
        itemViewHolder.itemCondition.setText("Condition: " + currentItem.getCondition().toString()
                .replace("_", " "));
        itemViewHolder.itemPrice.setText("R " + currentItem.getPrice().toString());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}