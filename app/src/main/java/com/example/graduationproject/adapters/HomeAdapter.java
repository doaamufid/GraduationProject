package com.example.graduationproject.adapters;

import com.example.graduationproject.R;
import com.example.graduationproject.models.HomeItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private final List<HomeItem> itemList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HomeItem item);
    }

    public HomeAdapter(List<HomeItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeItem item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.iconView.setImageResource(item.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView iconView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            iconView = itemView.findViewById(R.id.iconView);
        }
    }
}