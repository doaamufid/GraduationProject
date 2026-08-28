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
import com.example.graduationproject.adapters.kidsCalmCardAdapter;
import com.example.graduationproject.dialogs.kidsCalmCardEditDialog;
import com.example.graduationproject.models.kidsCalmKidCardModel;
import com.example.graduationproject.util.kidsCalmAppState;
import com.example.graduationproject.view.kidsCalmKidCardView;
import com.example.graduationproject.view.kidsCalmMascotView;

/** Mirrors the React <GalleryTab>. */
public class kidsCalmGalleryFragment extends Fragment {

    public interface Host {
        void showToast(String message);
    }

    private Host host;
    private kidsCalmMascotView mascot;
    private kidsCalmKidCardView activeCardView;
    private android.widget.TextView activeCardLabel;
    private android.widget.TextView emptyState;
    private RecyclerView recycler;
    private kidsCalmCardAdapter adapter;
    private long confirmDeleteId = -1;

    public void setHost(Host host) { this.host = host; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kids_calm_fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mascot = v.findViewById(R.id.galleryMascot);
        mascot.setText(getString(R.string.kids_calm_gallery_hint));
        mascot.setEmojiSize(30);

        activeCardLabel = v.findViewById(R.id.activeCardLabel);
        activeCardView = v.findViewById(R.id.activeKidCard);
        emptyState = v.findViewById(R.id.emptyStateText);

        recycler = v.findViewById(R.id.cardsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new kidsCalmCardAdapter(kidsCalmAppState.get().cards, new kidsCalmCardAdapter.Listener() {
            @Override
            public void onActivate(kidsCalmKidCardModel card) {
                kidsCalmAppState.get().activateCard(card.id);
                if (host != null) host.showToast(getString(R.string.kids_calm_toast_card_selected));
                refresh();
            }

            @Override
            public void onEdit(kidsCalmKidCardModel card) {
                kidsCalmCardEditDialog dialog = kidsCalmCardEditDialog.newInstanceEdit(card.id);
                dialog.setListener(() -> {
                    if (host != null) host.showToast(getString(R.string.kids_calm_toast_card_saved));
                    refresh();
                });
                dialog.show(getChildFragmentManager(), "edit_card");
            }

            @Override
            public void onRequestDelete(long id) {
                confirmDeleteId = id;
                adapter.setConfirmDeleteId(id);
            }

            @Override
            public void onConfirmDelete(long id) {
                kidsCalmAppState.get().deleteCard(id);
                confirmDeleteId = -1;
                if (host != null) host.showToast(getString(R.string.kids_calm_toast_card_deleted));
                refresh();
            }

            @Override
            public void onCancelDelete() {
                confirmDeleteId = -1;
                adapter.setConfirmDeleteId(-1);
            }
        });
        recycler.setAdapter(adapter);

        v.findViewById(R.id.newCardButton).setOnClickListener(x -> {
            kidsCalmCardEditDialog dialog = kidsCalmCardEditDialog.newInstanceCreate();
            dialog.setListener(() -> {
                if (host != null) host.showToast(getString(R.string.kids_calm_toast_card_new));
                refresh();
            });
            dialog.show(getChildFragmentManager(), "new_card");
        });

        refresh();
    }

    public void refresh() {
        if (getView() == null) return;
        kidsCalmKidCardModel active = kidsCalmAppState.get().getActiveCard();
        boolean hasActive = active != null;
        activeCardLabel.setVisibility(hasActive ? View.VISIBLE : View.GONE);
        activeCardView.setVisibility(hasActive ? View.VISIBLE : View.GONE);
        emptyState.setVisibility(hasActive ? View.GONE : View.VISIBLE);
        if (hasActive) {
            activeCardView.setContent(active.phrase, active.sticker, active.photoUri);
            activeCardView.setBig(false);
            activeCardView.startStickerWiggle();
        }
        adapter.setConfirmDeleteId(confirmDeleteId);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
