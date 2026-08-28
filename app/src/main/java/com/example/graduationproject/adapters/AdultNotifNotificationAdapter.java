package com.example.graduationproject.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.google.android.material.card.MaterialCardView;
import com.example.graduationproject.models.AdultNotifNotificationItem;
import com.example.graduationproject.models.AdultNotifNotificationType;

import java.util.ArrayList;
import java.util.List;

/**
 * أدابتر واحد يعرض قائمة مقسّمة لمجموعات (عناوين + بطاقات إشعار)،
 * يقابل الجزء الخاص بعرض "groups" في الكود الأصلي (JSX map/map).
 *
 * نستخدم قائمة مسطّحة (flat list) من Object تحتوي إما String (عنوان مجموعة)
 * أو AdultNotifNotificationItem (بطاقة إشعار) للحفاظ على ترتيب العرض الأصلي بالضبط.
 */
public class AdultNotifNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /** مدة دورة أنيميشن "التنفّس" الخاصة بنقطة عدم القراءة، تطابق 2.6s في CSS الأصلي. */
    private static final long BREATHE_DURATION_MS = 2600L;

    public interface OnItemClickListener {
        void onItemClick(AdultNotifNotificationItem item, int adapterPosition);
    }

    private final List<Object> flatItems = new ArrayList<>();
    private final OnItemClickListener listener;

    public AdultNotifNotificationAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** يستبدل محتوى القائمة بالكامل (مثال: عند التحميل الأولي). */
    public void submitFlatList(List<Object> items) {
        flatItems.clear();
        flatItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (flatItems.get(position) instanceof AdultNotifNotificationItem) ? TYPE_ITEM : TYPE_HEADER;
    }

    @Override
    public int getItemCount() {
        return flatItems.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.adult_notif_item_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.adult_notif_item_notification, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object data = flatItems.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) data);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind((AdultNotifNotificationItem) data, listener);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).cancelBreathing();
        }
    }

    // ===================== ViewHolders =====================

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLabel;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = (TextView) itemView;
        }

        void bind(String label) {
            tvLabel.setText(label);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardRoot;
        private final FrameLayout iconCircleContainer;
        private final ImageView imgIcon;
        private final TextView tvTitle;
        private final TextView tvTime;
        private final TextView tvDesc;
        private final View dotUnread;

        private ObjectAnimator breatheAnimator;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.adult_notif_card_root);
            iconCircleContainer = itemView.findViewById(R.id.adult_notif_icon_circle);
            imgIcon = itemView.findViewById(R.id.adult_notif_img_type_icon);
            tvTitle = itemView.findViewById(R.id.adult_notif_tv_title);
            tvTime = itemView.findViewById(R.id.adult_notif_tv_time);
            tvDesc = itemView.findViewById(R.id.adult_notif_tv_desc);
            dotUnread = itemView.findViewById(R.id.adult_notif_dot_unread);

            // تأثير الضغط "active:scale-95" من التصميم الأصلي
            cardRoot.setOnTouchListener((v, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                    default:
                        break;
                }
                return false;
            });
        }

        void bind(AdultNotifNotificationItem item, OnItemClickListener listener) {
            Context ctx = itemView.getContext();

            tvTitle.setText(item.title);
            tvTime.setText(item.time);
            tvDesc.setText(item.desc);

            TypeStyle style = TypeStyle.of(item.type);

            // تلوين دائرة الأيقونة + الأيقونة نفسها حسب نوع الإشعار
            GradientDrawable circleBg = (GradientDrawable) iconCircleContainer.getBackground().mutate();
            circleBg.setColor(ContextCompat.getColor(ctx, style.bgColorRes));

            imgIcon.setImageResource(style.iconRes);
            imgIcon.setColorFilter(ContextCompat.getColor(ctx, style.fgColorRes), PorterDuff.Mode.SRC_IN);

            // نقطة "غير مقروء" + أنيميشن التنفس
            cancelBreathing();
            if (item.unread) {
                dotUnread.setVisibility(View.VISIBLE);
                dotUnread.setScaleX(1f);
                dotUnread.setScaleY(1f);
                dotUnread.setAlpha(0.55f);
                breatheAnimator = startBreathing(dotUnread);
            } else {
                dotUnread.setVisibility(View.GONE);
            }

            cardRoot.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item, getBindingAdapterPosition());
                }
            });
        }

        void cancelBreathing() {
            if (breatheAnimator != null) {
                breatheAnimator.cancel();
                breatheAnimator = null;
            }
        }

        /**
         * يطابق الكيفريمز التالية من CSS الأصلي:
         * 0%,100% { transform: scale(1); opacity: 0.55; }
         * 50%     { transform: scale(1.4); opacity: 1; }
         * مدة الدورة الكاملة 2.6s، تكرار لا نهائي، تسارع/تباطؤ متماثل.
         */
        private static ObjectAnimator startBreathing(View dot) {
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.4f, 1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.4f, 1f);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.55f, 1f, 0.55f);

            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(dot, scaleX, scaleY, alpha);
            animator.setDuration(BREATHE_DURATION_MS);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
            return animator;
        }
    }

    /** يربط كل نوع إشعار بلون الخلفية/الأيقونة والرسم المستخدم، يقابل TYPE_STYLES في الأصل. */
    private static class TypeStyle {
        final int bgColorRes;
        final int fgColorRes;
        final int iconRes;

        TypeStyle(int bgColorRes, int fgColorRes, int iconRes) {
            this.bgColorRes = bgColorRes;
            this.fgColorRes = fgColorRes;
            this.iconRes = iconRes;
        }

        static TypeStyle of(AdultNotifNotificationType type) {
            switch (type) {
                case QUOTE:
                    return new TypeStyle(R.color.adult_notif_type_quote_bg, R.color.adult_notif_type_quote_fg, R.drawable.ic_quote);
                case BREATHING:
                    return new TypeStyle(R.color.adult_notif_type_breathing_bg, R.color.adult_notif_type_breathing_fg, R.drawable.ic_wind);
                case CHILD:
                    return new TypeStyle(R.color.adult_notif_type_child_bg, R.color.adult_notif_type_child_fg, R.drawable.ic_baby);
                case REPORT:
                    return new TypeStyle(R.color.adult_notif_type_report_bg, R.color.adult_notif_type_report_fg, R.drawable.ic_bar_chart);
                case PARENT_REPORT:
                    return new TypeStyle(R.color.adult_notif_type_parent_report_bg, R.color.adult_notif_type_parent_report_fg, R.drawable.ic_trending_up);
                case DHIKR:
                    return new TypeStyle(R.color.adult_notif_type_dhikr_bg, R.color.adult_notif_type_dhikr_fg, R.drawable.ic_bell);
                default:
                    return new TypeStyle(R.color.adult_notif_type_quote_bg, R.color.adult_notif_type_quote_fg, R.drawable.ic_quote);
            }
        }
    }
}
