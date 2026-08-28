package com.example.graduationproject.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.kidsCalmWordAdapter;
import com.example.graduationproject.dialogs.kidsCalmWordEditDialog;
import com.example.graduationproject.models.kidsCalmWordModel;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.view.kidsCalmMascotView;

/** Mirrors the React <WordsTab>. */
public class kidsCalmWordsFragment extends Fragment {

    public interface Host {
        void showToast(String message);
    }

    private Host host;
    private RecyclerView recycler;
    private kidsCalmWordAdapter adapter;

    public void setHost(Host host) { this.host = host; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kids_calm_fragment_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        kidsCalmMascotView mascot = v.findViewById(R.id.wordsMascot);
        mascot.setText(getString(R.string.kids_calm_words_hint));
        mascot.setEmojiSize(30);

        recycler = v.findViewById(R.id.wordsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new kidsCalmWordAdapter(kidsCalmAppState.get().words, new kidsCalmWordAdapter.Listener() {
            @Override
            public void onToggleFavorite(kidsCalmWordModel w) {
                w.favorite = !w.favorite;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChangeDuration(kidsCalmWordModel w, String durKey) {
                w.durKey = durKey;
                adapter.notifyDataSetChanged();
            }
        });
        recycler.setAdapter(adapter);

        v.findViewById(R.id.newWordButton).setOnClickListener(x -> {
            kidsCalmWordEditDialog dialog = kidsCalmWordEditDialog.newInstance();
            dialog.setListener(() -> {
                if (host != null) host.showToast(getString(R.string.kids_calm_toast_word_saved));
                adapter.notifyDataSetChanged();
            });
            dialog.show(getChildFragmentManager(), "new_word");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
