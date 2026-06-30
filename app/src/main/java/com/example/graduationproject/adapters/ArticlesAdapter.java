package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.R;
import com.example.graduationproject.models.ArticleModel;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private List<ArticleModel> articlesList;

    public ArticlesAdapter(List<ArticleModel> articlesList) {
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_card, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleModel article = articlesList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTag.setText(article.getTag());
        holder.tvTitle.setText(article.getTitle());

        holder.badgesContainer.removeAllViews();

        if (article.getBadges() != null) {
            for (String badgeText : article.getBadges()) {
                // إنشاء Button تفاعلي حقيقي لكل عنصر
                Button dynamicButton = new Button(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        intToDp(context, 32)
                );
                params.setMargins(0, 0, intToDp(context, 8), 0);
                dynamicButton.setLayoutParams(params);

                dynamicButton.setText(badgeText);
                dynamicButton.setTextSize(11);
                dynamicButton.setTextColor(Color.WHITE);
                dynamicButton.setTransformationMethod(null);
                dynamicButton.setPadding(intToDp(context, 14), 0, intToDp(context, 14), 0);

                // تلوين الأزرار باللون الأزرق المعتمد
                dynamicButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3A74B8")));

                holder.badgesContainer.addView(dynamicButton);
            }
        }
    }

    private int intToDp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTitle;
        LinearLayout badgesContainer; // سيتعرف عليها الآن بنجاح بدون أخطاء

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvCardTag);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            badgesContainer = itemView.findViewById(R.id.badgesContainer);
        }
    }
}