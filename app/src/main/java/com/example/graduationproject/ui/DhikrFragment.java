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
import com.example.graduationproject.adapters.DhikrAdapter;
import com.example.graduationproject.data.AppRepository;
import com.example.graduationproject.models.CalmDhikrItem;

/**
 * Java equivalent of the JS <DhikrTab/>: list of dhikr with a favorite
 * heart toggle and, once favorited, an inline duration-chip row. A dashed
 * "+ أضيفي ذكرك الخاص" button opens DhikrEditDialogFragment.
 */
public class DhikrFragment extends Fragment implements AppRepository.Listener, DhikrAdapter.Callback {

    private final AppRepository repo = AppRepository.get();

    private RecyclerView recyclerView;
    private DhikrAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dhikr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dhikrRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DhikrAdapter(requireContext(), repo.getDhikrList(), this);
        recyclerView.setAdapter(adapter);

        View btnAddDhikr = view.findViewById(R.id.btnAddDhikr);
        btnAddDhikr.setOnClickListener(v ->
                new DhikrEditDialogFragment().show(getParentFragmentManager(), "dhikr_create"));
    }

    @Override
    public void onStart() {
        super.onStart();
        repo.addListener(this);
        adapter.notifyDataSetChanged();
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).refreshHeader();
    }

    @Override
    public void onStop() {
        super.onStop();
        repo.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
        if (getActivity() instanceof AppHost) ((AppHost) getActivity()).refreshHeader();
    }

    @Override
    public void onToggleFavorite(CalmDhikrItem item) {
        repo.toggleFavoriteDhikr(item.id);
    }

    @Override
    public void onChangeMinutes(CalmDhikrItem item, int minutes) {
        repo.setDhikrMinutes(item.id, minutes);
    }
}
