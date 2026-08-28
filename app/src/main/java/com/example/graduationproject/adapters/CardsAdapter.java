package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.CardItem;
import com.example.graduationproject.models.CardPhoto;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.VH> {

    public interface Callback {
        void onActivate(CardItem card);
        void onEdit(CardItem card);
        void onRequestDelete(long id);   // shows inline confirm
        void onConfirmDelete(long id);   // actually deletes
        void onCancelDelete();
    }

    private final Context ctx;
    private final List<CardItem> data;
    private final Callback callback;
    private Long confirmingDeleteId = null;

    public CardsAdapter(Context ctx, List<CardItem> data, Callback callback) {
        this.ctx = ctx;
        this.data = data;
        this.callback = callback;
    }

    public void setConfirmingDeleteId(Long id) {
        this.confirmingDeleteId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CardItem c = data.get(position);
        boolean isActive = c.active;
        boolean confirming = confirmingDeleteId != null && confirmingDeleteId == c.id;

        h.phraseText.setText(c.phrase);
        if (isActive) {
            h.statusText.setText(R.string.active_now_badge);
            h.statusText.setTextColor(ctx.getResources().getColor(R.color.primary));
            h.rowRoot.setBackgroundResource(R.drawable.bg_card_active);
        } else {
            h.statusText.setText(R.string.tap_to_activate);
            h.statusText.setTextColor(ctx.getResources().getColor(R.color.text_soft));
            h.rowRoot.setBackgroundResource(R.drawable.bg_card);
        }

        // thumbnail
        if (c.photo != null) {
            h.thumbQuoteMark.setVisibility(View.GONE);
            h.thumbImage.setVisibility(View.VISIBLE);
            if (c.photo.type == CardPhoto.Type.UPLOAD) {
                h.thumbImage.setBackground(null);
                h.thumbImage.setImageURI(c.photo.uploadUri);
            } else {
                h.thumbImage.setImageDrawable(null);
                h.thumbImage.setBackgroundResource(c.photo.preset.gradientDrawableRes);
            }
        } else {
            h.thumbImage.setVisibility(View.VISIBLE);
            h.thumbImage.setImageDrawable(null);
            h.thumbImage.setBackgroundResource(R.drawable.gradient_thumb_default);
            h.thumbQuoteMark.setVisibility(View.VISIBLE);
        }

        h.rowRoot.setOnClickListener(v -> {
            if (!isActive && !confirming) callback.onActivate(c);
        });

        if (confirming) {
            h.actionsNormal.setVisibility(View.GONE);
            h.actionsConfirm.setVisibility(View.VISIBLE);
            h.btnConfirmDelete.setOnClickListener(v -> callback.onConfirmDelete(c.id));
            h.btnCancelDelete.setOnClickListener(v -> callback.onCancelDelete());
        } else {
            h.actionsNormal.setVisibility(View.VISIBLE);
            h.actionsConfirm.setVisibility(View.GONE);
            h.btnEdit.setOnClickListener(v -> callback.onEdit(c));
            h.btnDelete.setOnClickListener(v -> callback.onRequestDelete(c.id));
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout rowRoot;
        ImageView thumbImage;
        TextView thumbQuoteMark;
        TextView phraseText, statusText;
        LinearLayout actionsNormal, actionsConfirm;
        ImageButton btnEdit, btnDelete, btnConfirmDelete, btnCancelDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            rowRoot = itemView.findViewById(R.id.rowRoot);
            thumbImage = itemView.findViewById(R.id.thumbImage);
            thumbQuoteMark = itemView.findViewById(R.id.thumbQuoteMark);
            phraseText = itemView.findViewById(R.id.phraseText);
            statusText = itemView.findViewById(R.id.statusText);
            actionsNormal = itemView.findViewById(R.id.actionsNormal);
            actionsConfirm = itemView.findViewById(R.id.actionsConfirm);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnConfirmDelete = itemView.findViewById(R.id.btnConfirmDelete);
            btnCancelDelete = itemView.findViewById(R.id.btnCancelDelete);
        }
    }
}
