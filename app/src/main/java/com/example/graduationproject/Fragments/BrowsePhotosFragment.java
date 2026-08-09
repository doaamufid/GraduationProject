package com.example.graduationproject.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.PhotoItem;
import com.example.graduationproject.models.SurvivalBoxRepository;
import com.example.graduationproject.ui.TopBarHelper;
import com.example.graduationproject.dialogs.AddPhotoDialogFragment;

public class BrowsePhotosFragment extends Fragment {

    private static final int SPAN_COUNT = 3;

    private final SurvivalBoxRepository repo = SurvivalBoxRepository.getInstance();
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private PhotoAdapter adapter;

    private View groupDetail;
    private ImageView ivDetail;
    private PhotoItem selected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_browse_photos, container, false);

        ImageButton btnAdd = new ImageButton(requireContext());
        btnAdd.setBackgroundResource(R.drawable.bg_icon_circle_primary);
        btnAdd.setImageResource(R.drawable.ic_plus);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        btnAdd.setPadding(pad, pad, pad, pad);
        btnAdd.setLayoutParams(new ViewGroup.LayoutParams(
                (int) (34 * getResources().getDisplayMetrics().density),
                (int) (34 * getResources().getDisplayMetrics().density)));
        btnAdd.setOnClickListener(v ->
                AddPhotoDialogFragment.newInstance().show(getParentFragmentManager(), "add_photo"));

        TopBarHelper.bind(root, getString(R.string.browse_photos_title),
                () -> requireActivity().getSupportFragmentManager().popBackStack(), btnAdd);

        recyclerView = root.findViewById(R.id.recyclerView);
        tvEmpty = root.findViewById(R.id.tvEmpty);
        tvEmpty.setText(getString(R.string.empty_photos));
        groupDetail = root.findViewById(R.id.groupDetail);
        ivDetail = root.findViewById(R.id.ivDetail);

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), SPAN_COUNT));
        adapter = new PhotoAdapter();
        recyclerView.setAdapter(adapter);

        root.findViewById(R.id.btnBackToGrid).setOnClickListener(v -> {
            selected = null;
            render();
        });
        root.findViewById(R.id.btnDeleteDetail).setOnClickListener(v -> {
            if (selected != null) {
                repo.removePhoto(selected.id);
                selected = null;
            }
            render();
        });

        getParentFragmentManager().setFragmentResultListener(
                AddPhotoDialogFragment.REQUEST_KEY, this, (key, bundle) -> {
                    repo.addPhoto(bundle.getString(AddPhotoDialogFragment.KEY_URI),
                            bundle.getString(AddPhotoDialogFragment.KEY_CAPTION));
                    render();
                });

        render();
        return root;
    }

    private void render() {
        boolean showDetail = selected != null;
        boolean empty = repo.getPhotos().isEmpty();

        groupDetail.setVisibility(showDetail ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(!showDetail && empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(!showDetail && !empty ? View.VISIBLE : View.GONE);

        if (showDetail) {
            ivDetail.setImageURI(android.net.Uri.parse(selected.uri));
        }
        adapter.notifyDataSetChanged();
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_thumb, parent, false);
            int columnWidth = parent.getMeasuredWidth() / SPAN_COUNT;
            if (columnWidth > 0) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                lp.height = columnWidth - dp(6);
                v.setLayoutParams(lp);
            }
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            PhotoItem item = repo.getPhotos().get(position);
            holder.ivThumb.setImageURI(android.net.Uri.parse(item.uri));
            holder.itemView.setOnClickListener(v -> {
                selected = item;
                render();
            });
        }

        @Override
        public int getItemCount() {
            return repo.getPhotos().size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivThumb;

            VH(@NonNull View itemView) {
                super(itemView);
                ivThumb = itemView.findViewById(R.id.ivThumb);
            }
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
