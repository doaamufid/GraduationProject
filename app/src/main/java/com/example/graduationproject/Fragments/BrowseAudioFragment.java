package com.example.graduationproject.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.AudioItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.ui.TopBarHelper;
import com.example.graduationproject.dialogs.AddAudioDialogFragment;

import java.util.Locale;

public class BrowseAudioFragment extends Fragment {

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private AudioAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_browse_list, container, false);

        ImageButton btnAdd = new ImageButton(requireContext());
        btnAdd.setBackgroundResource(R.drawable.bg_icon_circle_primary);
        btnAdd.setImageResource(R.drawable.ic_plus);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        btnAdd.setPadding(pad, pad, pad, pad);
        btnAdd.setLayoutParams(new ViewGroup.LayoutParams(
                (int) (34 * getResources().getDisplayMetrics().density),
                (int) (34 * getResources().getDisplayMetrics().density)));
        btnAdd.setOnClickListener(v ->
                AddAudioDialogFragment.newInstance().show(getParentFragmentManager(), "add_audio"));

        TopBarHelper.bind(root, getString(R.string.browse_audio_title),
                () -> requireActivity().getSupportFragmentManager().popBackStack(), btnAdd);

        recyclerView = root.findViewById(R.id.recyclerView);
        tvEmpty = root.findViewById(R.id.tvEmpty);
        tvEmpty.setText(getString(R.string.empty_audio));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AudioAdapter();
        recyclerView.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener(
                AddAudioDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addAudio(bundle.getString(AddAudioDialogFragment.KEY_LABEL),
                            bundle.getInt(AddAudioDialogFragment.KEY_DURATION));
                    render();
                });

        render();
        return root;
    }

    private void render() {
        boolean empty = repo.getAudio().isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            AudioItem item = repo.getAudio().get(position);
            holder.tvLabel.setText(item.label);
            holder.tvDuration.setText(String.format(Locale.US, "%02d:%02d",
                    item.durationSeconds / 60, item.durationSeconds % 60));
            // Playback is a UI affordance in the original mockup (no real audio
            // engine there either) - toggle the icon to acknowledge the tap.
            holder.btnPlay.setOnClickListener(v ->
                    holder.btnPlay.setImageResource(R.drawable.ic_play_light));
            holder.btnDelete.setOnClickListener(v -> {
                repo.removeAudio(item.id);
                render();
            });
        }

        @Override
        public int getItemCount() {
            return repo.getAudio().size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageButton btnPlay, btnDelete;
            TextView tvLabel, tvDuration;

            VH(@NonNull View itemView) {
                super(itemView);
                btnPlay = itemView.findViewById(R.id.btnPlay);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                tvLabel = itemView.findViewById(R.id.tvLabel);
                tvDuration = itemView.findViewById(R.id.tvDuration);
            }
        }
    }
}
