package com.example.graduationproject.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.DrawingResult;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ResultViewHolder> {

    private final List<DrawingResult> results;

    public GalleryAdapter(List<DrawingResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawing_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        DrawingResult result = results.get(position);
        holder.imgThumb.setImageURI(Uri.fromFile(new java.io.File(result.getImagePath())));
        holder.tvFeedback.setText(result.getFeedbackText());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvFeedback;

        ResultViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
        }
    }
}