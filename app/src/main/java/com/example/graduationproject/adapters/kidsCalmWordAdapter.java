package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        h.emoji.setText(w.emoji);
        h.text.setText(w.text);

        h.emoji.setBackgroundResource(w.favorite ? R.drawable.kids_calm_bg_emoji_option_selected : R.drawable.kids_calm_bg_emoji_option);
        h.itemView.setBackgroundResource(w.favorite ? R.drawable.kids_calm_bg_list_item_active : R.drawable.kids_calm_bg_list_item_idle);
        h.heart.setText(w.favorite
                ? h.itemView.getContext().getString(R.string.kids_calm_heart_filled)
                : h.itemView.getContext().getString(R.string.kids_calm_heart_empty));
        h.heart.setTextColor(h.itemView.getResources().getColor(w.favorite ? R.color.kids_calm_pink : R.color.kids_calm_cardBorder));

        if (w.favorite) {
            kidsCalmDurationOption dur = kidsCalmAppState.get().durByKey(w.durKey);
            h.durLabel.setVisibility(View.VISIBLE);
            int resId = h.itemView.getContext().getResources()
                    .getIdentifier(dur.labelResName, "string", h.itemView.getContext().getPackageName());
            String label = resId != 0 ? h.itemView.getContext().getString(resId) : dur.key;
            h.durLabel.setText(dur.emoji + " " + label);

            h.durWrapper.setVisibility(View.VISIBLE);
            kidsCalmDurChipsHelper.render(h.durChips, h.itemView.getContext(), w.durKey,
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
