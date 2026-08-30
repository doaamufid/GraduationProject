package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmDurationOption;
import com.example.graduationproject.models.kidsCalmWordModel;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.util.kidsCalmDurChipsHelper;

import java.util.List;

public class kidsCalmWordAdapter extends RecyclerView.Adapter<kidsCalmWordAdapter.VH> {

    public interface Listener {
        void onToggleFavorite(kidsCalmWordModel w);
        void onChangeDuration(kidsCalmWordModel w, String durKey);
    }

    private final List<kidsCalmWordModel> data;
    private final Listener listener;

    public kidsCalmWordAdapter(List<kidsCalmWordModel> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kids_calm_item_word, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        kidsCalmWordModel w = data.get(position);
        Context context = h.itemView.getContext();

        h.emoji.setText(w.emoji);
        h.text.setText(w.text);

        h.emoji.setBackgroundResource(w.favorite ? R.drawable.kids_calm_bg_emoji_option_selected : R.drawable.kids_calm_bg_emoji_option);
        h.itemView.setBackgroundResource(w.favorite ? R.drawable.kids_calm_bg_list_item_active : R.drawable.kids_calm_bg_list_item_idle);

        // إصلاح مشكلة setText(ambiguous) وتمرير resource ID المباشر
        int stringResId = w.favorite ? R.string.kids_calm_heart_filled : R.string.kids_calm_heart_empty;
        h.heart.setText(stringResId);

        // إصلاح دالة الألوان لتجنب Deprecated warning
        int colorResId = w.favorite ? R.color.kids_calm_pink : R.color.kids_calm_cardBorder;
        h.heart.setTextColor(ContextCompat.getColor(context, colorResId));

        if (w.favorite) {
            kidsCalmDurationOption dur = kidsCalmAppState.get().durByKey(w.durKey);
            h.durLabel.setVisibility(View.VISIBLE);
            int resId = context.getResources().getIdentifier(dur.labelResName, "string", context.getPackageName());
            String label = resId != 0 ? context.getString(resId) : dur.key;
            h.durLabel.setText(dur.emoji + " " + label);

            h.durWrapper.setVisibility(View.VISIBLE);
            kidsCalmDurChipsHelper.render(h.durChips, context, w.durKey,
                    key -> listener.onChangeDuration(w, key));
        } else {
            h.durLabel.setVisibility(View.GONE);
            h.durWrapper.setVisibility(View.GONE);
        }

        h.heart.setOnClickListener(v -> listener.onToggleFavorite(w));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView emoji, text, durLabel, heart;
        LinearLayout durWrapper, durChips;

        VH(View v) {
            super(v);
            emoji = v.findViewById(R.id.wordEmoji);
            text = v.findViewById(R.id.wordText);
            durLabel = v.findViewById(R.id.wordDurLabel);
            heart = v.findViewById(R.id.favoriteHeart);
            durWrapper = v.findViewById(R.id.durChipsWrapper);
            durChips = v.findViewById(R.id.durChipsContainer);
        }
    }
}