package com.example.graduationproject.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.FutureMessage;
import com.example.graduationproject.models.FutureSelfRepository;
import com.example.graduationproject.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Equivalent of <AllMessagesScreen/>: full list of every message, sorted
 * by target date ascending, with a tap-to-arm / tap-to-confirm delete
 * (matches the original's 2-second `armed` window) and an edit action
 * for messages that haven't arrived yet.
 */
public class AllMessagesActivity extends AppCompatActivity {

    private final FutureSelfRepository repo = FutureSelfRepository.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Set<Long> armedDeleteIds = new HashSet<>();

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAdd).setOnClickListener(v ->
                ComposeDialogFragment.newInstanceForCreate().show(getSupportFragmentManager(), "compose"));

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        getSupportFragmentManager().setFragmentResultListener(
                ComposeDialogFragment.REQUEST_KEY, this, (key, bundle) -> render());

        render();
    }

    private void render() {
        List<FutureMessage> sorted = new ArrayList<>(repo.getMessages());
        Collections.sort(sorted, Comparator.comparingLong(m -> m.targetDate.getTime()));

        boolean empty = sorted.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);

        adapter.submit(sorted);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
        private List<FutureMessage> items = new ArrayList<>();

        void submit(List<FutureMessage> newItems) {
            items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_row, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            FutureMessage m = items.get(position);
            boolean armed = armedDeleteIds.contains(m.id);

            holder.avatarBg.setBackgroundResource(m.arrived ? R.drawable.bg_avatar_arrived : R.drawable.bg_avatar_locked);
            holder.ivIcon.setImageResource(m.arrived ? R.drawable.ic_heart_filled_white : R.drawable.ic_lock);

            if (m.arrived) {
                holder.tvText.setText(m.text);
                holder.tvSub.setText(getString(R.string.arrived_on_format, DateUtils.formatDate(m.targetDate)));
            } else {
                holder.tvText.setText(R.string.locked_row_label);
                holder.tvSub.setText(getString(R.string.due_on_format,
                        DateUtils.formatDate(m.targetDate), DateUtils.toAr(DateUtils.daysLeft(m.targetDate))));
            }

            holder.btnEdit.setVisibility(m.arrived ? View.GONE : View.VISIBLE);

            holder.btnView.setOnClickListener(v ->
                    MessageDetailDialogFragment.newInstance(m.id).show(getSupportFragmentManager(), "detail"));

            holder.btnEdit.setOnClickListener(v ->
                    ComposeDialogFragment.newInstanceForEdit(m.id).show(getSupportFragmentManager(), "compose"));

            holder.btnDelete.setText(armed ? R.string.delete_confirm_action : R.string.delete_action);
            holder.btnDelete.setBackgroundResource(armed ? R.drawable.bg_delete_armed : android.R.color.transparent);
            holder.btnDelete.setTextColor(getResources().getColor(armed ? R.color.accent : R.color.text_soft));
            holder.btnDelete.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    armed ? R.drawable.ic_trash_accent : R.drawable.ic_trash, 0, 0, 0);

            holder.btnDelete.setOnClickListener(v -> {
                if (!armed) {
                    armedDeleteIds.add(m.id);
                    notifyItemChanged(position);
                    handler.postDelayed(() -> {
                        armedDeleteIds.remove(m.id);
                        int idx = items.indexOf(m);
                        if (idx >= 0) notifyItemChanged(idx);
                    }, 2000);
                } else {
                    armedDeleteIds.remove(m.id);
                    repo.deleteMessage(m.id);
                    render();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class VH extends RecyclerView.ViewHolder {
            View avatarBg;
            android.widget.ImageView ivIcon;
            TextView tvText, tvSub, btnDelete, btnEdit;
            View btnView;

            VH(@NonNull View itemView) {
                super(itemView);
                avatarBg = itemView.findViewById(R.id.avatarBg);
                ivIcon = itemView.findViewById(R.id.ivAvatarIcon);
                tvText = itemView.findViewById(R.id.tvRowText);
                tvSub = itemView.findViewById(R.id.tvRowSub);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnView = itemView.findViewById(R.id.btnView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
