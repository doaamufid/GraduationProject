package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyFutureMsgRowAdapter extends RecyclerView.Adapter<MyFutureMsgRowAdapter.VH> {
    private final List<MyFutureMsg> myFutures;
    private final Callbacks myFutureMsgCallbacks;
    private final Set<Long> myFutureMsgArmedIds;
    private final Handler myFutureMsgHandler;

    public interface Callbacks {
        void myFutureMsgOnView(MyFutureMsg myFuture);
        void myFutureMsgOnEdit(MyFutureMsg myFuture);
        void myFutureMsgOnDelete(long myFutureMsgId);
    }

    public MyFutureMsgRowAdapter(List<MyFutureMsg> myFutureMsgData, Callbacks myFutureMsgCallbacks) {
        this.myFutures = new ArrayList<>();
        this.myFutureMsgArmedIds = new HashSet<>();
        this.myFutureMsgHandler = new Handler(Looper.getMainLooper());
        this.myFutureMsgCallbacks = myFutureMsgCallbacks;
        myFutureMsgSetData(myFutureMsgData);
    }

    public void myFutureMsgSetData(List<MyFutureMsg> myFutureMsgData) {
        myFutures.clear();
        myFutures.addAll(myFutureMsgData);
        Collections.sort(myFutures, Comparator.comparingLong(myFuture -> myFuture.myFutureMsgTargetDate.getTime()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup myFutureMsgParent, int myFutureMsgViewType) {
        View myFutureMsgV = LayoutInflater.from(myFutureMsgParent.getContext()).inflate(R.layout.my_future_msg_item_message_row, myFutureMsgParent, false);
        return new VH(myFutureMsgV);
    }

    @Override
    public void onBindViewHolder(@NonNull VH myFutureMsgHolder, int myFutureMsgPosition) {
        MyFutureMsg myFuture = myFutures.get(myFutureMsgPosition);
        boolean myFutureMsgArmed = myFutureMsgArmedIds.contains(myFuture.myFutureMsgId);
        Context myFutureMsgCtx = myFutureMsgHolder.itemView.getContext();

        int myFutureMsgAccent = ContextCompat.getColor(myFutureMsgCtx, R.color.my_future_msg_accent);
        int myFutureMsgSoftText = ContextCompat.getColor(myFutureMsgCtx, R.color.my_future_msg_text_soft);

        if (myFuture.myFutureMsgArrived) {
            myFutureMsgHolder.myFutureMsgTvRowText.setText(myFuture.myFutureMsgText);
            myFutureMsgHolder.myFutureMsgTvRowSub.setText("وصلت بتاريخ " + MyFutureMsgDateUtils.myFutureMsgFormatDate(myFuture.myFutureMsgTargetDate));
            myFutureMsgHolder.myFutureMsgCircleIcon.setBackgroundResource(R.drawable.my_future_msg_bg_circle_icon_arrived);
            myFutureMsgHolder.myFutureMsgIvRowIcon.setImageResource(R.drawable.my_future_msg_ic_heart);
            myFutureMsgHolder.myFutureMsgIvRowIcon.setImageTintList(ColorStateList.valueOf(0xFFFFFFFF));
            myFutureMsgHolder.myFutureMsgBtnEditRow.setVisibility(View.GONE);
        } else {
            myFutureMsgHolder.myFutureMsgTvRowText.setText(R.string.my_future_msg_locked_row_text);
            myFutureMsgHolder.myFutureMsgTvRowSub.setText("ستفتح بتاريخ " + MyFutureMsgDateUtils.myFutureMsgFormatDate(myFuture.myFutureMsgTargetDate) + " (باقي " + MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgDaysLeft(myFuture.myFutureMsgTargetDate)) + " يوم)");
            myFutureMsgHolder.myFutureMsgCircleIcon.setBackgroundResource(R.drawable.my_future_msg_bg_circle_icon_locked);
            myFutureMsgHolder.myFutureMsgIvRowIcon.setImageResource(R.drawable.my_future_msg_ic_lock);
            myFutureMsgHolder.myFutureMsgIvRowIcon.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
            myFutureMsgHolder.myFutureMsgBtnEditRow.setVisibility(View.VISIBLE);
        }

        if (myFutureMsgArmed) {
            myFutureMsgHolder.myFutureMsgDeleteChipContent.setBackgroundResource(R.drawable.my_future_msg_bg_chip_delete_armed);
            myFutureMsgHolder.myFutureMsgTvDeleteLabel.setText(R.string.my_future_msg_confirm_delete);
            myFutureMsgHolder.myFutureMsgTvDeleteLabel.setTextColor(myFutureMsgAccent);
            myFutureMsgHolder.myFutureMsgIvDeleteIcon.setImageTintList(ColorStateList.valueOf(myFutureMsgAccent));
        } else {
            myFutureMsgHolder.myFutureMsgDeleteChipContent.setBackgroundColor(0);
            myFutureMsgHolder.myFutureMsgTvDeleteLabel.setText(R.string.my_future_msg_delete);
            myFutureMsgHolder.myFutureMsgTvDeleteLabel.setTextColor(myFutureMsgSoftText);
            myFutureMsgHolder.myFutureMsgIvDeleteIcon.setImageTintList(ColorStateList.valueOf(myFutureMsgSoftText));
        }

        myFutureMsgHolder.myFutureMsgBtnViewRow.setOnClickListener(myFutureMsgV -> myFutureMsgCallbacks.myFutureMsgOnView(myFuture));
        myFutureMsgHolder.myFutureMsgBtnEditRow.setOnClickListener(myFutureMsgV -> myFutureMsgCallbacks.myFutureMsgOnEdit(myFuture));
        myFutureMsgHolder.myFutureMsgBtnDeleteRow.setOnClickListener(myFutureMsgV -> {
            if (myFutureMsgArmedIds.contains(myFuture.myFutureMsgId)) {
                myFutureMsgArmedIds.remove(myFuture.myFutureMsgId);
                myFutureMsgCallbacks.myFutureMsgOnDelete(myFuture.myFutureMsgId);
            } else {
                myFutureMsgArmedIds.add(myFuture.myFutureMsgId);
                notifyItemChanged(myFutureMsgHolder.getBindingAdapterPosition());
                myFutureMsgHandler.postDelayed(() -> {
                    if (myFutureMsgArmedIds.remove(myFuture.myFutureMsgId)) {
                        int myFutureMsgIdx = myFutures.indexOf(myFuture);
                        if (myFutureMsgIdx >= 0) notifyItemChanged(myFutureMsgIdx);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myFutures.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView myFutureMsgTvRowText, myFutureMsgTvRowSub, myFutureMsgTvDeleteLabel;
        ImageView myFutureMsgIvRowIcon, myFutureMsgIvDeleteIcon;
        FrameLayout myFutureMsgBtnViewRow, myFutureMsgBtnEditRow, myFutureMsgBtnDeleteRow, myFutureMsgCircleIcon;
        LinearLayout myFutureMsgDeleteChipContent;

        public VH(@NonNull View myFutureMsgItemView) {
            super(myFutureMsgItemView);
            myFutureMsgTvRowText = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_row_text);
            myFutureMsgTvRowSub = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_row_sub);
            myFutureMsgTvDeleteLabel = myFutureMsgItemView.findViewById(R.id.my_future_msg_tv_delete_label);
            myFutureMsgIvRowIcon = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_row_icon);
            myFutureMsgIvDeleteIcon = myFutureMsgItemView.findViewById(R.id.my_future_msg_iv_delete_icon);
            myFutureMsgBtnViewRow = myFutureMsgItemView.findViewById(R.id.my_future_msg_btn_view_row);
            myFutureMsgBtnEditRow = myFutureMsgItemView.findViewById(R.id.my_future_msg_btn_edit_row);
            myFutureMsgBtnDeleteRow = myFutureMsgItemView.findViewById(R.id.my_future_msg_btn_delete_row);
            myFutureMsgCircleIcon = myFutureMsgItemView.findViewById(R.id.my_future_msg_circle_icon);
            myFutureMsgDeleteChipContent = myFutureMsgItemView.findViewById(R.id.my_future_msg_delete_chip_content);
        }
    }
}
