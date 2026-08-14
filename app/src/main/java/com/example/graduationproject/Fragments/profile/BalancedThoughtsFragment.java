package com.example.graduationproject.Fragments.profile;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.BalancedThought;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mirrors <BalancedThoughtsScreen/> + <ThoughtCard/>.
 */
public class BalancedThoughtsFragment extends Fragment {

    private List<BalancedThought> thoughts;
    private Long expandedId = null;
    private final Set<Long> armedForDelete = new HashSet<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private LinearLayout container;
    private TextView txtCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thoughts = new ArrayList<>(SeedData.getSeedThoughts(requireContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balanced_thoughts, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> activity.showHome());

        container = view.findViewById(R.id.thoughts_container);
        txtCount = view.findViewById(R.id.txt_thoughts_count);

        render(activity);
    }

    private void render(ProfileNavigator activity) {
        txtCount.setText(getString(R.string.thoughts_count_fmt, thoughts.size()));
        container.removeAllViews();

        for (BalancedThought item : thoughts) {
            View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_thought_card, container, false);
            bindCard(card, item, activity);
            container.addView(card);
        }
    }

    private void bindCard(View card, BalancedThought item, ProfileNavigator activity) {
        int color = SeedData.colorForPattern(requireContext(), item.pattern);
        boolean expanded = expandedId != null && item.id == expandedId;
        boolean armed = armedForDelete.contains(item.id);

        LinearLayout header = card.findViewById(R.id.card_header);
        TextView tag = card.findViewById(R.id.txt_pattern_tag);
        TextView savedDate = card.findViewById(R.id.txt_saved_date);
        ImageView chevron = card.findViewById(R.id.img_chevron);
        TextView reframed = card.findViewById(R.id.txt_reframed);
        View expandedBox = card.findViewById(R.id.card_expanded);
        TextView original = card.findViewById(R.id.txt_original);
        View exerciseBox = card.findViewById(R.id.exercise_box);
        TextView exerciseLabel = card.findViewById(R.id.txt_exercise_label);
        TextView exerciseText = card.findViewById(R.id.txt_exercise_text);
        TextView btnStart = card.findViewById(R.id.btn_start_exercise);
        LinearLayout btnDelete = card.findViewById(R.id.btn_delete);
        TextView deleteLabel = card.findViewById(R.id.txt_delete_label);

        tag.setText(item.pattern);
        tag.setTextColor(color);
        tag.setBackground(rounded(withAlpha(color, 0x18), 100));
        savedDate.setText(item.savedDate);
        reframed.setText(item.reframed);
        chevron.setRotation(expanded ? 180f : 0f);

        expandedBox.setVisibility(expanded ? View.VISIBLE : View.GONE);
        if (expanded) {
            original.setText("\"" + item.original + "\"");
            exerciseBox.setBackground(rounded(withAlpha(color, 0x10), 16));
            exerciseLabel.setTextColor(color);
            exerciseText.setText(item.exercise);
            btnStart.setBackground(rounded(color, 100));

            btnStart.setOnClickListener(v -> {
                String preview = item.exercise.length() > 24 ? item.exercise.substring(0, 24) : item.exercise;
                activity.showToast(getString(R.string.toast_started_exercise_fmt, preview + "..."));
            });

            deleteLabel.setText(armed ? R.string.confirm_delete : R.string.delete_from_archive);
            int deleteColor = ContextCompat.getColor(requireContext(), armed ? R.color.pink : R.color.text_soft);
            deleteLabel.setTextColor(deleteColor);

            btnDelete.setOnClickListener(v -> {
                if (!armed) {
                    armedForDelete.add(item.id);
                    handler.postDelayed(() -> {
                        armedForDelete.remove(item.id);
                        render(activity);
                    }, 2000);
                    render(activity);
                    return;
                }
                armedForDelete.remove(item.id);
                thoughts.removeIf(t -> t.id == item.id);
                render(activity);
            });
        }

        header.setOnClickListener(v -> {
            expandedId = expanded ? null : item.id;
            render(activity);
        });
    }

    private GradientDrawable rounded(int color, float radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        return d;
    }

    private int withAlpha(int color, int alpha0to255) {
        return android.graphics.Color.argb(alpha0to255,
                android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color));
    }
}
