package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.VisualContent;
import java.util.List;

public class VisualContentAdapter extends RecyclerView.Adapter<VisualContentAdapter.ContentViewHolder> {

    private List<VisualContent> contentList;
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(VisualContent content);
    }

    public VisualContentAdapter(List<VisualContent> contentList, OnVideoClickListener listener) {
        this.contentList = contentList;
        this.listener = listener;
    }

    // لتحديث القائمة البرمجية بسهولة عند الفلترة
    public void updateData(List<VisualContent> newList) {
        this.contentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visual_card, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        VisualContent content = contentList.get(position);
        holder.tvVideoTitle.setText(content.getTitle());
        holder.tvVideoDuration.setText(content.getDuration());
        holder.imgThumbnail.setImageResource(content.getThumbnailResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(content);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvVideoTitle, tvVideoDuration;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
            tvVideoDuration = itemView.findViewById(R.id.tvVideoDuration);
        }
    }
}