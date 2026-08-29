package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.MyFutureMsg;
import com.example.graduationproject.util.MyFutureMsgDateUtils;

import java.util.List;

public class MyFutureMsgAdapter extends RecyclerView.Adapter<MyFutureMsgAdapter.VH> {
    private final List<MyFutureMsg> myFutureMsgData;
    private final OnMessageClick myFutureMsgListener;

    public interface OnMessageClick {
        void myFutureMsgOnClick(MyFutureMsg myFuture);
    }

    public MyFutureMsgAdapter(List<MyFutureMsg> myFutureMsgData, OnMessageClick myFutureMsgListener) {
        this.myFutureMsgData = myFutureMsgData;
        this.myFutureMsgListener = myFutureMsgListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup myFutureMsgParent, int myFutureMsgViewType) {
        View myFutureMsgV = LayoutInflater.from(myFutureMsgParent.getContext()).inflate(R.layout.my_future_msg_item_message_timeline, myFutureMsgParent, false);
        return new VH(myFutureMsgV);
    }

    @Override
    public void onBindViewHolder(@NonNull VH myFutureMsgHolder, int myFutureMsgPosition) {
        MyFutureMsg myFuture = myFutureMsgData.get(myFutureMsgPosition);
        Context myFutureMsgCtx = myFutureMsgHolder.itemView.getContext();

        myFutureMsgHolder.myFutureMsgTvCardText.setText(myFuture.myFutureMsgArrived ? myFuture.myFutureMsgText : myFutureMsgCtx.getString(R.string.my_future_msg_locked_card_text));
        myFutureMsgHolder.myFutureMsgTvStatCreatedNum.setText(MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgDaysSince(myFuture.myFutureMsgCreatedDate)));

        if (myFuture.myFutureMsgArrived) {
            myFutureMsgHolder.myFutureMsgTvStatTargetLabel.setText("يوم منذ الوصول");
            myFutureMsgHolder.myFutureMsgTvStatTargetNum.setText(MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgDaysSince(myFuture.myFutureMsgTargetDate)));
        } else {
            myFutureMsgHolder.myFutureMsgTvStatTargetLabel.setText("يوم متبقي");
            myFutureMsgHolder.myFutureMsgTvStatTargetNum.setText(MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgDaysLeft(myFuture.myFutureMsgTargetDate)));
        }

        myFutureMsgHolder.myFutureMsgTvStatWords.setText(MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgWordCount(myFuture.myFutureMsgText)));

        int myFutureMsgAccent = ContextCompat.getColor(myFutureMsgCtx, R.color.my_future_msg_accent);
        int myFutureMsgMainText = ContextCompat.getColor(myFutureMsgCtx, R.color.my_future_msg_text_main);
        int myFutureMsgSoftText = ContextCompat.getColor(myFutureMsgCtx, R.color.my_future_msg_text_soft);

        if (myFuture.myFutureMsgArrived) {
            myFutureMsgHolder.myFutureMsgCardContainer.setBackgroundResource(R.drawable.my_future_msg_bg_card_arrived);
            myFutureMsgHolder.myFutureMsgCardContainer.setAlpha(1.0f);
            int myFutureMsgColor = myFutureMsgAccent;
            myFutureMsgHolder.myFutureMsgTvStatCreatedNum.setTextColor(myFutureMsgColor);
            myFutureMsgHolder.myFutureMsgTvStatTargetNum.setTextColor(myFutureMsgColor);
            myFutureMsgHolder.myFutureMsgTvStatWords.setTextColor(myFutureMsgColor);
            myFutureMsgHolder.myFutureMsgIvStatCreated.setImageTintList(ColorStateList.valueOf(myFutureMsgColor));
            myFutureMsgHolder.myFutureMsgIvStatTarget.setImageTintList(ColorStateList.valueOf(myFutureMsgColor));
            myFutureMsgHolder.myFutureMsgIvStatWords.setImageTintList(ColorStateList.valueOf(myFutureMsgColor));
            myFutureMsgHolder.myFutureMsgRowReadAgain.setVisibility(View.VISIBLE);
            myFutureMsgHolder.myFutureMsgTabContainer.setBackgroundResource(R.drawable.my_future_msg_bg_tab_arrived);
            myFutureMsgHolder.myFutureMsgIvTabIcon.setImageResource(R.drawable.my_future_msg_ic_heart);
            myFutureMsgHolder.myFutureMsgIvTabIcon.setImageTintList(ColorStateList.valueOf(myFutureMsgAccent));
            myFutureMsgHolder.myFutureMsgTvTabLabel.setText("وصلت");
            myFutureMsgHolder.myFutureMsgTvTabLabel.setTextColor(myFutureMsgAccent);
        } else {
            myFutureMsgHolder.myFutureMsgCardContainer.setBackgroundResource(R.drawable.my_future_msg_bg_card_locked);
            myFutureMsgHolder.myFutureMsgCardContainer.setAlpha(0.7f);
            myFutureMsgHolder.myFutureMsgTvStatCreatedNum.setTextColor(myFutureMsgMainText);
            myFutureMsgHolder.myFutureMsgTvStatTargetNum.setTextColor(myFutureMsgMainText);
            myFutureMsgHolder.myFutureMsgTvStatWords.setTextColor(myFutureMsgMainText);
            myFutureMsgHolder.myFutureMsgIvStatCreated.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
            myFutureMsgHolder.myFutureMsgIvStatTarget.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
            myFutureMsgHolder.myFutureMsgIvStatWords.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
            myFutureMsgHolder.myFutureMsgRowReadAgain.setVisibility(View.GONE);
            myFutureMsgHolder.myFutureMsgTabContainer.setBackgroundResource(R.drawable.my_future_msg_bg_tab_locked);
            myFutureMsgHolder.myFutureMsgIvTabIcon.setImageResource(R.drawable.my_future_msg_ic_lock);
            myFutureMsgHolder.myFutureMsgIvTabIcon.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
            myFutureMsgHolder.myFutureMsgTvTabLabel.setText("مقفلة");
            myFutureMsgHolder.myFutureMsgTvTabLabel.setTextColor(myFutureMsgSoftText);
        }

        myFutureMsgHolder.myFutureMsgCardTouch.setOnClickListener(myFutureMsgV -> myFutureMsgListener.myFutureMsgOnClick(myFuture));

        myFutureMsgHolder.itemView.clearAnimation();
        Animation myFutureMsgAnim = AnimationUtils.loadAnimation(myFutureMsgCtx, R.anim.fade_in);
        myFutureMsgAnim.setStartOffset(myFutureMsgPosition * 40L);
        myFutureMsgHolder.itemView.startAnimation(myFutureMsgAnim);
    }

    @Override
    public int getItemCount() {
        return myFutureMsgData.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView myFutureMsgTvCardText, myFutureMsgTvStatCreatedNum, myFutureMsgTvStatTargetLabel, myFutureMsgTvStatTargetNum, myFutureMsgTvStatWords, myFutureMsgTvTabLabel;
        ImageView myFutureMsgIvStatCreated, myFutureMsgIvStatTarget, myFutureMsgIvStatWords, myFutureMsgIvTabIcon;
        LinearLayout myFutureMsgCardContainer, myFutureMsgRowReadAgain, myFutureMsgTabContainer;
        FrameLayout myFutureMsgCardTouch;

        public VH(@NonNull View myFutureMsgItemView) {
            super(myFutureMsgItemView);
            myFutureMsgTvCardText = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_card_text);
            myFutureMsgTvStatCreatedNum = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_stat_created_num);
            myFutureMsgTvStatTargetLabel = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_stat_target_label);
            myFutureMsgTvStatTargetNum = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_stat_target_num);
            myFutureMsgTvStatWords = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_stat_words);
            myFutureMsgTvTabLabel = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_tab_label);
            myFutureMsgIvStatCreated = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_stat_created);
            myFutureMsgIvStatTarget = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_stat_target);
            myFutureMsgIvStatWords = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_stat_words);
            myFutureMsgIvTabIcon = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_tab_icon);
            myFutureMsgCardContainer = myFutureMsgItemView.findViewById(R.id.my_future_msg_card_container);
            myFutureMsgRowReadAgain = myFutureMsgItemView.findViewById(R.id.my_future_msg_row_read_again);
            myFutureMsgTabContainer = myFutureMsgItemView.findViewById(R.id.my_future_msg_tab_container);
            myFutureMsgCardTouch = myFutureMsgItemView.findViewById(R.id.my_future_msg_card_touch);
        }
    }
}
