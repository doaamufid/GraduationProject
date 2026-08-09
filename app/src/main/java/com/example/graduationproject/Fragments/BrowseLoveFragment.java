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
import com.example.graduationproject.models.LoveItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.ui.TopBarHelper;
import com.example.graduationproject.dialogs.AddLoveDialogFragment;

public class BrowseLoveFragment extends Fragment {

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private LoveAdapter adapter;

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
                AddLoveDialogFragment.newInstance().show(getParentFragmentManager(), "add_love"));

        TopBarHelper.bind(root, getString(R.string.browse_love_title),
                () -> requireActivity().getSupportFragmentManager().popBackStack(), btnAdd);

        recyclerView = root.findViewById(R.id.recyclerView);
        tvEmpty = root.findViewById(R.id.tvEmpty);
        tvEmpty.setText(getString(R.string.empty_love));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LoveAdapter();
        recyclerView.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener(
                AddLoveDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addLove(bundle.getString(AddLoveDialogFragment.KEY_TEXT),
                            bundle.getString(AddLoveDialogFragment.KEY_SOURCE));
                    render();
                });

        render();
        return root;
    }

    private void render() {
        boolean empty = repo.getLove().isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_love, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            LoveItem item = repo.getLove().get(position);
            holder.tvText.setText(item.text);
            holder.tvSourceBadge.setText(LoveItem.SOURCE_SELF.equals(item.source)
                    ? getString(R.string.tag_self) : getString(R.string.tag_other));
            holder.btnDelete.setOnClickListener(v -> {
                repo.removeLove(item.id);
                render();
            });
        }

        @Override
        public int getItemCount() {
            return repo.getLove().size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageButton btnDelete;
            TextView tvText, tvSourceBadge;

            VH(@NonNull View itemView) {
                super(itemView);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                tvText = itemView.findViewById(R.id.tvText);
                tvSourceBadge = itemView.findViewById(R.id.tvSourceBadge);
            }
        }
    }
}
