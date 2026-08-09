package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.OnHomeItemClickListener;
import com.example.graduationproject.R;
import com.example.graduationproject.models.HomeFeature;

import java.util.List;

public class HomeFeatureAdapter extends RecyclerView.Adapter<HomeFeatureAdapter.FeatureViewHolder> {

    private final Context context;
    private final List<HomeFeature> featureList;
    private final OnHomeItemClickListener listener;

    public HomeFeatureAdapter(Context context, List<HomeFeature> featureList, OnHomeItemClickListener listener) {
        this.context = context;
        this.featureList = featureList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        HomeFeature feature = featureList.get(position);

        holder.ivIcon.setImageResource(feature.getIcon());
        holder.ivIcon.setBackgroundResource(feature.getIconBackground());
        holder.tvTitle.setText(feature.getTitle());
        holder.tvSubtitle.setText(feature.getSubtitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTitle;
        TextView tvSubtitle;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivFeatureIcon);
            tvTitle = itemView.findViewById(R.id.tvFeatureTitle);
            tvSubtitle = itemView.findViewById(R.id.tvFeatureSubtitle);
        }
    }
}
