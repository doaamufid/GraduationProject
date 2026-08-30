package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.FavoriteStory;

import java.util.List;

public class FavoriteStoryAdapter extends RecyclerView.Adapter<FavoriteStoryAdapter.ViewHolder> {

    public interface OnItemClickListener { void onClick(FavoriteStory item); }
    public interface OnDeleteClickListener { void onDelete(FavoriteStory item); }

    private List<FavoriteStory> items;
    private final OnItemClickListener clickListener;
    private final OnDeleteClickListener deleteListener;

    public FavoriteStoryAdapter(List<FavoriteStory> items,
                                OnItemClickListener clickListener,
                                OnDeleteClickListener deleteListener) {
        this.items = items;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    public void updateList(List<FavoriteStory> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteStory item = items.get(position);
        holder.titleText.setText(item.getTitle());
        holder.categoryText.setText(item.getCategory());

        holder.itemView.setOnClickListener(v -> clickListener.onClick(item));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, categoryText;
        ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.favTitleText);
            categoryText = itemView.findViewById(R.id.favCategoryText);
            deleteButton = itemView.findViewById(R.id.favDeleteButton);
        }
    }
}