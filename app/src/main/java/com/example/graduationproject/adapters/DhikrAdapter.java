package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.Constants;
import com.example.graduationproject.models.CalmDhikrItem;
import com.example.graduationproject.models.CategoryMeta;
import com.example.graduationproject.view.ChipRowHelper;

import java.util.List;

public class DhikrAdapter extends RecyclerView.Adapter<DhikrAdapter.VH> {

    public interface Callback {
        void onToggleFavorite(CalmDhikrItem item);
        void onChangeMinutes(CalmDhikrItem item, int minutes);
    }

    private final Context ctx;
    private final List<CalmDhikrItem> data;
    private final Callback callback;

    public DhikrAdapter(Context ctx, List<CalmDhikrItem> data, Callback callback) {
        this.ctx = ctx;
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_calm_dhikr, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CalmDhikrItem d = data.get(position);
        CategoryMeta meta = Constants.CATEGORY_META.get(d.category);

        h.dhikrText.setText(d.text);
        h.emojiText.setText(meta != null ? meta.emoji : "");

        String metaStr = (meta != null ? meta.label : "");
        if (d.favorite) {
            String unit = d.minutes == 1 ? ctx.getString(R.string.minute_1) : ctx.getString(R.string.minutes_n);
            metaStr += " · " + d.minutes + " " + unit;
        }
        h.dhikrMetaText.setText(metaStr);

        h.btnFavorite.setImageResource(d.favorite
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        h.btnFavorite.setColorFilter(ctx.getResources().getColor(d.favorite ? R.color.amber : R.color.mutedDim));

        h.rowCard.setBackgroundResource(d.favorite ? R.drawable.bg_card_active : R.drawable.bg_card);

        h.btnFavorite.setOnClickListener(v -> callback.onToggleFavorite(d));

        if (d.favorite) {
            h.durationSection.setVisibility(View.VISIBLE);
            ChipRowHelper.buildDurationChips(ctx, h.durationChipsContainer, Constants.DURATION_OPTIONS,
                    d.minutes, index -> callback.onChangeMinutes(d, Constants.DURATION_OPTIONS[index]));
        } else {
            h.durationSection.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout rowCard;
        TextView emojiText, dhikrText, dhikrMetaText;
        ImageButton btnFavorite;
        LinearLayout durationSection;
        LinearLayout durationChipsContainer;

        VH(@NonNull View itemView) {
            super(itemView);
            rowCard = (LinearLayout) itemView;
            emojiText = itemView.findViewById(R.id.emojiText);
            dhikrText = itemView.findViewById(R.id.dhikrText);
            dhikrMetaText = itemView.findViewById(R.id.dhikrMetaText);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            durationSection = itemView.findViewById(R.id.durationSection);
            durationChipsContainer = itemView.findViewById(R.id.durationChipsContainer);
        }
    }
}
