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
import com.example.graduationproject.models.DhikrItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.ui.TopBarHelper;
import com.example.graduationproject.dialogs.AddDhikrDialogFragment;

public class BrowseDhikrFragment extends Fragment {

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private DhikrAdapter adapter;

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
                AddDhikrDialogFragment.newInstance().show(getParentFragmentManager(), "add_dhikr"));

        TopBarHelper.bind(root, getString(R.string.browse_dhikr_title),
                () -> requireActivity().getSupportFragmentManager().popBackStack(), btnAdd);

        recyclerView = root.findViewById(R.id.recyclerView);
        tvEmpty = root.findViewById(R.id.tvEmpty);
        tvEmpty.setText(getString(R.string.empty_dhikr));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DhikrAdapter();
        recyclerView.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener(
                AddDhikrDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.setDhikr(bundle.getStringArrayList(AddDhikrDialogFragment.KEY_TEXTS));
                    render();
                });

        render();
        return root;
    }

    private void render() {
        boolean empty = repo.getDhikr().isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private class DhikrAdapter extends RecyclerView.Adapter<DhikrAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dhikr, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            DhikrItem item = repo.getDhikr().get(position);
            holder.tvText.setText(item.text);
            holder.btnStar.setOnClickListener(v -> {
                repo.removeDhikr(item.text);
                render();
            });
        }

        @Override
        public int getItemCount() {
            return repo.getDhikr().size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageButton btnStar;
            TextView tvText;

            VH(@NonNull View itemView) {
                super(itemView);
                btnStar = itemView.findViewById(R.id.btnStar);
                tvText = itemView.findViewById(R.id.tvText);
            }
        }
    }
}
