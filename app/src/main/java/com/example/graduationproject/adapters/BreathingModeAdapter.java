package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.BreathingMode;

import java.util.List;
import java.util.Locale;

public class BreathingModeAdapter extends RecyclerView.Adapter<BreathingModeAdapter.VH> {

    public interface OnModeClickListener {
        void onModeClick(BreathingMode mode);
    }

    private final List<BreathingMode> modes;
    private final OnModeClickListener listener;

    public BreathingModeAdapter(List<BreathingMode> modes, OnModeClickListener listener) {
        this.modes = modes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_breathing_mode, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BreathingMode mode = modes.get(position);
        
        // الترجمة للعربية
        String arName = translateName(mode.name);
        String arDesc = translateDesc(mode.name, mode.description);
        
        holder.tvName.setText(arName);
        holder.tvDesc.setText(arDesc);
        holder.ivIllustration.setImageResource(mode.illustrationResId);
        holder.cardRoot.setCardBackgroundColor(mode.backgroundColor);
        
        String patternStr = String.format(Locale.US, "%d-%d-%d-%d", mode.pattern[0], mode.pattern[1], mode.pattern[2], mode.pattern[3]);
        holder.tvPattern.setText(patternStr);
        holder.tvDuration.setText(String.format(Locale.getDefault(), "%d دقائق", mode.durationMinutes));

        View.OnClickListener click = v -> {
            if (listener != null) listener.onModeClick(mode);
        };
        holder.itemView.setOnClickListener(click);
        holder.btnStart.setOnClickListener(click);
    }

    private String translateName(String name) {
        switch (modeName(name)) {
            case "equal": return "التنفس المتساوي";
            case "box": return "تنفس المربع";
            case "478": return "تنفس ٤-٧-٨";
            case "711": return "تنفس ٧-١١";
            case "custom": return "نمط مخصص";
            default: return name;
        }
    }

    private String translateDesc(String name, String fallback) {
        switch (modeName(name)) {
            case "equal": return "نفس متوازن يساعدك على الاسترخاء والتركيز الذهني.";
            case "box": return "تقنية قوية لتقليل التوتر واستعادة الهدوء فوراً.";
            case "478": return "يساعد هذا النمط على تحسين جودة النوم والراحة.";
            case "711": return "يساعد في تقليل القلق الشديد وتعزيز الاسترخاء.";
            case "custom": return "اضغطي هنا لإنشاء نمط التنفس الخاص بكِ.";
            default: return fallback;
        }
    }

    private String modeName(String name) {
        String n = name.toLowerCase();
        if (n.contains("equal")) return "equal";
        if (n.contains("box")) return "box";
        if (n.contains("478")) return "478";
        if (n.contains("7-11")) return "711";
        if (n.contains("custom")) return "custom";
        return n;
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        CardView cardRoot;
        ImageView ivIllustration;
        TextView tvName, tvDesc, tvPattern, tvDuration;
        View btnStart;

        VH(@NonNull View itemView) {
            super(itemView);
            cardRoot = (CardView) itemView.findViewById(R.id.cardModeRoot);
            ivIllustration = itemView.findViewById(R.id.ivIllustration);
            tvName = itemView.findViewById(R.id.tvModeName);
            tvDesc = itemView.findViewById(R.id.tvModeDesc);
            tvPattern = itemView.findViewById(R.id.tvPattern);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnStart = itemView.findViewById(R.id.btnStartMode);
        }
    }
}
