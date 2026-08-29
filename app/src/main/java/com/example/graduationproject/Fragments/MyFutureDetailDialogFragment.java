package com.example.graduationproject.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.example.graduationproject.models.MyFutureMsg;
import com.example.graduationproject.models.MyFutureMsgRepository;
import com.example.graduationproject.util.MyFutureMsgDateUtils;

public class MyFutureDetailDialogFragment extends DialogFragment {
    private static final String myFutureMsgArgId = "my_future_id";

    public static MyFutureDetailDialogFragment newInstance(long myFutureMsgId) {
        MyFutureDetailDialogFragment myFutureMsgFragment = new MyFutureDetailDialogFragment();
        Bundle myFutureMsgArgs = new Bundle();
        myFutureMsgArgs.putLong(myFutureMsgArgId, myFutureMsgId);
        myFutureMsgFragment.setArguments(myFutureMsgArgs);
        return myFutureMsgFragment;
    }

    @Override
    public int getTheme() {
        return R.style.my_future_msg_theme_future_message_dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater myFutureMsgInflater, @Nullable ViewGroup myFutureMsgContainer, @Nullable Bundle myFutureMsgSavedInstanceState) {
        View myFutureMsgV = myFutureMsgInflater.inflate(R.layout.my_future_msg_dialog_message_detail, myFutureMsgContainer, false);
        
        long myFutureMsgId = getArguments() != null ? getArguments().getLong(myFutureMsgArgId) : -1;
        MyFutureMsg myFuture = MyFutureMsgRepository.myFutureMsgGetInstance().myFutureMsgFindById(myFutureMsgId);

        ImageView myFutureMsgIvIcon = myFutureMsgV.findViewById(R.id.my_future_msg_iv_detail_icon);
        TextView myFutureMsgTvTitle = myFutureMsgV.findViewById(R.id.my_future_msg_tv_detail_title);
        TextView myFutureMsgTvSub = myFutureMsgV.findViewById(R.id.my_future_msg_tv_detail_sub);
        TextView myFutureMsgTvBody = myFutureMsgV.findViewById(R.id.my_future_msg_tv_detail_body);
        TextView myFutureMsgTvCloseLabel = myFutureMsgV.findViewById(R.id.my_future_msg_tv_detail_close_label);

        if (myFuture == null) {
            dismissAllowingStateLoss();
            return myFutureMsgV;
        }

        if (myFuture.myFutureMsgArrived) {
            myFutureMsgIvIcon.setImageResource(R.drawable.my_future_msg_ic_mail);
            myFutureMsgIvIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.my_future_msg_accent)));
            myFutureMsgTvTitle.setVisibility(View.GONE);
            myFutureMsgTvSub.setText("كتبت هذه الرسالة " + myFuture.myFutureMsgCreatedLabel);
            myFutureMsgTvBody.setVisibility(View.VISIBLE);
            myFutureMsgTvBody.setText(myFuture.myFutureMsgText);
            myFutureMsgTvCloseLabel.setText(R.string.my_future_msg_close);
            myFutureMsgTvCloseLabel.setBackgroundResource(R.drawable.my_future_msg_bg_button_accent_pill);
            myFutureMsgTvCloseLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_future_msg_white));
        } else {
            myFutureMsgIvIcon.setImageResource(R.drawable.my_future_msg_ic_lock);
            myFutureMsgIvIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.my_future_msg_text_soft)));
            myFutureMsgTvTitle.setVisibility(View.VISIBLE);
            myFutureMsgTvTitle.setText(R.string.my_future_msg_locked_title);
            myFutureMsgTvSub.setText("ستفتح بتاريخ " + MyFutureMsgDateUtils.myFutureMsgFormatDate(myFuture.myFutureMsgTargetDate) + " (باقي " + MyFutureMsgDateUtils.myFutureMsgToAr(MyFutureMsgDateUtils.myFutureMsgDaysLeft(myFuture.myFutureMsgTargetDate)) + " يوم)");
            myFutureMsgTvBody.setVisibility(View.GONE);
            myFutureMsgTvCloseLabel.setText(R.string.my_future_msg_close);
            myFutureMsgTvCloseLabel.setBackgroundResource(R.drawable.my_future_msg_bg_button_neutral_pill);
            myFutureMsgTvCloseLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_future_msg_text_soft));
        }

        myFutureMsgV.findViewById(R.id.my_future_msg_btn_detail_close).setOnClickListener(myFutureMsgV2 -> dismiss());
        myFutureMsgV.setOnClickListener(myFutureMsgV2 -> {});

        return myFutureMsgV;
    }
}
