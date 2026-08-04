package com.example.graduationproject.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Environment;

import java.util.List;

public class EnvironmentAdapter extends RecyclerView.Adapter<EnvironmentAdapter.EnvViewHolder> {

    private List<Environment> environmentList;
    private OnItemClickListener listener;

    // واجهة (Interface) للتعامل مع نقرات العناصر
    public interface OnItemClickListener {
        void onItemClick(Environment environment);
    }

    public EnvironmentAdapter(List<Environment> environmentList, OnItemClickListener listener) {
        this.environmentList = environmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EnvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_environment_card, parent, false);
        return new EnvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnvViewHolder holder, int position) {
        Environment env = environmentList.get(position);
        holder.tvEnvTitle.setText(env.getTitle());
        holder.imgEnv.setImageResource(env.getImageResId());

        // عند الضغط على الكرت
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(env);
            }
        });
    }

    @Override
    public int getItemCount() {
        return environmentList.size();
    }

    static class EnvViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEnv;
        TextView tvEnvTitle;

        public EnvViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEnv = itemView.findViewById(R.id.imgEnv);
            tvEnvTitle = itemView.findViewById(R.id.tvEnvTitle);
        }
    }
}
