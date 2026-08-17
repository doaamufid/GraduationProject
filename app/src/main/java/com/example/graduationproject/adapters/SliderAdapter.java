package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.Message;
import com.example.graduationproject.util.CardBinder;
import com.example.graduationproject.util.CardHost;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.VH> {

    private final List<Message> slides;
    private final CardHost host;

    public SliderAdapter(List<Message> slides, CardHost host) {
        this.slides = slides;
        this.host = host;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Message msg = slides.get(position);
        holder.holder.removeAllViews();
        View card = CardBinder.bind(holder.itemView.getContext(),
                LayoutInflater.from(holder.itemView.getContext()), holder.holder, msg, position, host);
        holder.holder.addView(card);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ViewGroup holder;

        VH(@NonNull View itemView) {
            super(itemView);
            holder = itemView.findViewById(R.id.slideCardHolder);
        }
    }
}
