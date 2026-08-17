package com.example.graduationproject.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.data.Repository;
import com.example.graduationproject.data.SeedData;
import com.example.graduationproject.models.Message;

/**
 * Inflates and binds item_message_card.xml for a given Message.
 * Reused by: the wall grid, "my messages", "pinned", and the top slider.
 */
public class CardBinder {

    public static View bind(Context ctx, LayoutInflater inflater, ViewGroup parent,
                             Message msg, int index, CardHost host) {

        View root = inflater.inflate(R.layout.item_message_card, parent, false);

        int colorIdx = msg.colorIndex != -1 ? msg.colorIndex : index;
        CardColors.Pair colors = CardColors.forIndex(colorIdx);
        int bg = ContextCompat.getColor(ctx, colors.bgRes);
        int fg = ContextCompat.getColor(ctx, colors.fgRes);

        root.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bg));

        String avatarText = SeedData.AVATARS[Math.floorMod(index, SeedData.AVATARS.length)];

        TextView categoryIcon = root.findViewById(R.id.cardCategoryIcon);
        TextView categoryName = root.findViewById(R.id.cardCategory);
        View imageArea = root.findViewById(R.id.imageArea);
        TextView time = root.findViewById(R.id.cardTime);

        ImageView image = root.findViewById(R.id.cardImage);
        
        if (categoryIcon != null) {
            categoryIcon.setText(SeedData.getCategoryEmoji(msg.cat));
        }
        if (categoryName != null) {
            categoryName.setText(msg.cat);
        }

        if (msg.id < 1000) {
            time.setText(ctx.getString(R.string.posted_time_ago));
        } else {
            time.setText(ctx.getString(R.string.posted_just_now));
        }

        if (msg.img != null) {
            imageArea.setVisibility(View.VISIBLE);
            Glide.with(ctx).load(msg.img).centerCrop().into(image);
        } else {
            imageArea.setVisibility(View.GONE);
        }

        TextView text = root.findViewById(R.id.cardText);
        text.setText(msg.text);

        TextView emojiBadge = root.findViewById(R.id.emojiBadge);
        if (msg.emoji != null) {
            emojiBadge.setVisibility(View.VISIBLE);
            emojiBadge.setText(msg.emoji);
        } else {
            emojiBadge.setVisibility(View.GONE);
        }

        ImageView heartIcon = root.findViewById(R.id.heartIcon);
        TextView heartCount = root.findViewById(R.id.heartCount);
        View heartBtn = root.findViewById(R.id.heartBtn);
        heartCount.setTextColor(fg);

        Runnable[] refreshHeart = new Runnable[1];
        refreshHeart[0] = () -> {
            boolean hearted = Repository.get().isHearted(msg.id);
            heartIcon.setImageResource(hearted ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            heartIcon.setColorFilter(fg);
            int shown = msg.hearts + (hearted ? 1 : 0);
            heartCount.setText(String.valueOf(shown));
        };
        refreshHeart[0].run();

        heartBtn.setOnClickListener(v -> {
            Repository.get().toggleHeart(msg.id);
            refreshHeart[0].run();
            if (Repository.get().isHearted(msg.id)) popAnim(heartIcon);
        });

        ImageView shareBtn = root.findViewById(R.id.shareBtn);
        shareBtn.setColorFilter(fg);
        ImageView pinBtn = root.findViewById(R.id.pinBtn);
        pinBtn.setColorFilter(fg);

        Runnable[] refreshPin = new Runnable[1];
        refreshPin[0] = () -> {
            boolean pinned = Repository.get().isPinned(msg.id);
            pinBtn.setImageResource(pinned ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
            pinBtn.setColorFilter(fg);
        };
        refreshPin[0].run();

        pinBtn.setOnClickListener(v -> {
            boolean nowPinned = Repository.get().togglePin(msg.id);
            refreshPin[0].run();
            popAnim(pinBtn);
            if (host != null) {
                host.onPinnedCountChanged();
                ToastUtil.show(host.getToastOverlay(),
                        ctx.getString(nowPinned ? R.string.toast_pinned : R.string.toast_unpinned));
            }
        });

        shareBtn.setOnClickListener(v -> {
            String shareText = "\"" + msg.text + "\"\n— رسالة من مجتمع سلام 🤍";
            if (host != null) {
                host.copyToClipboard(shareText);
                ToastUtil.show(host.getToastOverlay(), ctx.getString(R.string.toast_copied));
            }
            // brief check-mark confirmation, mirrors the React "copied" state swap
            shareBtn.setImageResource(R.drawable.ic_check);
            shareBtn.setColorFilter(fg);
            shareBtn.postDelayed(() -> {
                shareBtn.setImageResource(R.drawable.ic_share);
                shareBtn.setColorFilter(fg);
            }, 1400);
        });

        return root;
    }

    private static void popAnim(View v) {
        v.setScaleX(0.6f);
        v.setScaleY(0.6f);
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, View.SCALE_X, 0.6f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, View.SCALE_Y, 0.6f, 1f);
        sx.setDuration(350);
        sy.setDuration(350);
        sx.setInterpolator(new OvershootInterpolator(2.2f));
        sy.setInterpolator(new OvershootInterpolator(2.2f));
        sx.start();
        sy.start();
    }
}
