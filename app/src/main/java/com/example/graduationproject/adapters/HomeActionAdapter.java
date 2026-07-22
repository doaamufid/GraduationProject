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
import com.example.graduationproject.models.HomeAction;

import java.util.List;

public class HomeActionAdapter extends RecyclerView.Adapter<HomeActionAdapter.ActionViewHolder> {

    private final Context context;
    private final List<HomeAction> actionList;
    private final OnHomeItemClickListener listener;

    public HomeActionAdapter(Context context, List<HomeAction> actionList, OnHomeItemClickListener listener) {
        this.context = context;
        this.actionList = actionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_action, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
        HomeAction action = actionList.get(position);

        holder.ivIcon.setImageResource(action.getIcon());
        holder.ivIcon.setBackgroundResource(action.getIconBackground());
        holder.tvTitle.setText(action.getTitle());
        holder.tvSubtitle.setText(action.getSubtitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }

    static class ActionViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTitle;
        TextView tvSubtitle;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivActionIcon);
            tvTitle = itemView.findViewById(R.id.tvActionTitle);
            tvSubtitle = itemView.findViewById(R.id.tvActionSubtitle);
        }
    }
}
