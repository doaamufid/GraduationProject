package com.example.graduationproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.AppHost;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.CardsAdapter;
import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.models.CardItem;
import com.example.graduationproject.view.CalmCardView;

/**
 * Java equivalent of the JS <GalleryTab/>: shows the active card at the top
 * (or an empty-state hint), the full list of cards below with tap-to-activate
 * / edit / inline delete-confirm, and a "+ بطاقة جديدة" button that opens
 * the CardEditDialogFragment.
 */
public class GalleryFragment extends Fragment implements AppRepository.Listener, CardsAdapter.Callback {

    private final AppRepository repo = AppRepository.get();

    private View activeCardSection;
    private CalmCardView activeCalmCard;
    private View noActiveState;
    private RecyclerView recyclerView;
    private View btnNewCard;

    private CardsAdapter adapter;
    private Long confirmingDeleteId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activeCardSection = view.findViewById(R.id.activeCardSection);
        activeCalmCard = view.findViewById(R.id.activeCalmCard);
        noActiveState = view.findViewById(R.id.noActiveState);
        recyclerView = view.findViewById(R.id.cardsRecyclerView);
        btnNewCard = view.findViewById(R.id.btnNewCard);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CardsAdapter(requireContext(), repo.getCards(), this);
        recyclerView.setAdapter(adapter);

        btnNewCard.setOnClickListener(v -> {
            CardEditDialogFragment dialog = CardEditDialogFragment.newInstanceCreate();
            dialog.show(getParentFragmentManager(), "card_create");
        });

        render();
    }

    @Override
    public void onStart() {
        super.onStart();
        repo.addListener(this);
        render();
    }

    @Override
    public void onStop() {
        super.onStop();
        repo.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        render();
    }

    private void render() {
        CardItem active = repo.getActiveCard();
        if (active != null) {
            activeCardSection.setVisibility(View.VISIBLE);
            noActiveState.setVisibility(View.GONE);
            activeCalmCard.setCard(active.photo, active.phrase, false);
        } else {
            activeCardSection.setVisibility(View.GONE);
            noActiveState.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).refreshHeader();
    }

    /* ---- CardsAdapter.Callback ---- */

    @Override
    public void onActivate(CardItem card) {
        repo.activateCard(card.id);
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).showToast(getString(R.string.toast_activated));
    }

    @Override
    public void onEdit(CardItem card) {
        CardEditDialogFragment dialog = CardEditDialogFragment.newInstanceEdit(card.id);
        dialog.show(getParentFragmentManager(), "card_edit");
    }

    @Override
    public void onRequestDelete(long id) {
        confirmingDeleteId = id;
        adapter.setConfirmingDeleteId(confirmingDeleteId);
    }

    @Override
    public void onConfirmDelete(long id) {
        repo.deleteCard(id);
        confirmingDeleteId = null;
        adapter.setConfirmingDeleteId(null);
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).showToast(getString(R.string.toast_deleted_card));
    }

    @Override
    public void onCancelDelete() {
        confirmingDeleteId = null;
        adapter.setConfirmingDeleteId(null);
    }
}
