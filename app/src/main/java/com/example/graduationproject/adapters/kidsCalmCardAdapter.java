package com.example.graduationproject.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmKidCardModel;

import java.util.List;

public class kidsCalmCardAdapter extends RecyclerView.Adapter<kidsCalmCardAdapter.VH> {

    public interface Listener {
        void onActivate(kidsCalmKidCardModel card);
        void onEdit(kidsCalmKidCardModel card);
        void onRequestDelete(long id);
        void onConfirmDelete(long id);
        void onCancelDelete();
    }

    private final List<kidsCalmKidCardModel> data;
    private final Listener listener;
    private long confirmDeleteId = -1;

    public kidsCalmCardAdapter(List<kidsCalmKidCardModel> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setConfirmDeleteId(long id) {
        this.confirmDeleteId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kids_calm_item_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        kidsCalmKidCardModel c = data.get(position);
        h.phrase.setText(c.phrase);

        boolean hasPhoto = c.photoUri != null;
        h.thumbPhoto.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
        h.thumbEmoji.setVisibility(hasPhoto ? View.GONE : View.VISIBLE);
        if (hasPhoto) {
            h.thumbPhoto.setImageURI(c.photoUri);
        } else if (c.sticker != null) {
            h.thumbEmoji.setText(c.sticker.emoji);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{c.sticker.colorStart, c.sticker.colorEnd});
            gd.setCornerRadius(16 * h.itemView.getResources().getDisplayMetrics().density);
            h.thumbEmoji.setBackground(gd);
        }

        boolean confirming = confirmDeleteId == c.id;
        h.actionsNormal.setVisibility(confirming ? View.GONE : View.VISIBLE);
        h.actionsConfirm.setVisibility(confirming ? View.VISIBLE : View.GONE);

        if (c.active) {
            h.itemRoot.setBackgroundResource(R.drawable.kids_calm_bg_list_item_active);
            h.tag.setText(R.string.kids_calm_gallery_active_tag);
            h.tag.setTextColor(h.itemView.getResources().getColor(R.color.kids_calm_sunDeep));
            h.itemRoot.setClickable(false);
        } else {
            h.itemRoot.setBackgroundResource(R.drawable.kids_calm_bg_list_item_idle);
            h.tag.setText(R.string.kids_calm_gallery_pick_tag);
            h.tag.setTextColor(h.itemView.getResources().getColor(R.color.kids_calm_navySoft));
            h.itemRoot.setClickable(true);
        }

        h.itemRoot.setOnClickListener(v -> { if (!c.active) listener.onActivate(c); });
        h.editButton.setOnClickListener(v -> listener.onEdit(c));
        h.deleteButton.setOnClickListener(v -> listener.onRequestDelete(c.id));
        h.confirmDeleteButton.setOnClickListener(v -> listener.onConfirmDelete(c.id));
        h.cancelDeleteButton.setOnClickListener(v -> listener.onCancelDelete());
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout itemRoot;
        ImageView thumbPhoto;
        TextView thumbEmoji;
        TextView phrase;
        TextView tag;
        LinearLayout actionsNormal, actionsConfirm;
        TextView editButton, deleteButton, confirmDeleteButton, cancelDeleteButton;

        VH(View v) {
            super(v);
            itemRoot = v.findViewById(R.id.itemRoot);
            thumbPhoto = v.findViewById(R.id.thumbPhoto);
            thumbEmoji = v.findViewById(R.id.thumbEmoji);
            phrase = v.findViewById(R.id.itemPhrase);
            tag = v.findViewById(R.id.itemTag);
            actionsNormal = v.findViewById(R.id.actionsNormal);
            actionsConfirm = v.findViewById(R.id.actionsConfirm);
            editButton = v.findViewById(R.id.editButton);
            deleteButton = v.findViewById(R.id.deleteButton);
            confirmDeleteButton = v.findViewById(R.id.confirmDeleteButton);
            cancelDeleteButton = v.findViewById(R.id.cancelDeleteButton);
        }
    }
}
