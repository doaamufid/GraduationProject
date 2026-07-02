package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.SurvivalItemModel;
import java.util.List;

public class SurvivalAdapter extends RecyclerView.Adapter<SurvivalAdapter.SurvivalViewHolder> {

    private List<SurvivalItemModel> itemList;

    public SurvivalAdapter(List<SurvivalItemModel> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SurvivalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survival_car, parent, false);
        return new SurvivalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurvivalViewHolder holder, int position) {
        SurvivalItemModel item = itemList.get(position);
        holder.tvTag.setText(item.getTag());
        holder.tvTitle.setText(item.getTitle());
        holder.btnAction.setText(item.getButtonText());

        // الأكشن الخاص بالزر التفاعلي عند النقر
        holder.btnAction.setOnClickListener(v -> {
            // ضعي هنا الأكشن المناسب لكل كارد
        });
    }

    @Override
    public int getItemCount() { return itemList.size(); }

    static class SurvivalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTitle;
        Button btnAction;

        public SurvivalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvItemTag);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            btnAction = itemView.findViewById(R.id.btnItemAction);
        }
    }
}