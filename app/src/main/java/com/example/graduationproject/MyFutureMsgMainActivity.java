package com.example.graduationproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.Fragments.MyFutureDetailDialogFragment;
import com.example.graduationproject.Fragments.MyFutureMsgComposeBottomSheetFragment;
import com.example.graduationproject.adapters.MyFutureMsgAdapter;
import com.example.graduationproject.models.MyFutureMsg;
import com.example.graduationproject.models.MyFutureMsgRepository;

import java.util.List;

public class MyFutureMsgMainActivity extends AppCompatActivity implements MyFutureMsgRepository.Listener {
    private MyFutureMsgRepository myFutureMsgRepo;
    private MyFutureMsgAdapter myFutureMsgAdapter;
    private RecyclerView myFutureMsgRvTimeline;
    private View myFutureMsgEmptyState;
    private TextView myFutureMsgTvLastArrived;
    private View myFutureMsgFabGlow;
    private AnimatorSet myFutureMsgGlowAnimator;

    @Override
    protected void onCreate(@Nullable Bundle myFutureMsgSavedInstanceState) {
        super.onCreate(myFutureMsgSavedInstanceState);
        setContentView(R.layout.my_future_msg_activity_main);

        myFutureMsgRepo = MyFutureMsgRepository.myFutureMsgGetInstance();

        myFutureMsgRvTimeline = findViewById(R.id.my_future_msg_rv_timeline);
        myFutureMsgEmptyState = findViewById(R.id.my_future_msg_empty_state);
        myFutureMsgTvLastArrived = findViewById(R.id.my_future_msg_tv_last_arrived);
        myFutureMsgFabGlow = findViewById(R.id.my_future_msg_fab_glow);

        View myFutureMsgFabCompose = findViewById(R.id.my_future_msg_fab_compose);
        View myFutureMsgBtnViewAllTop = findViewById(R.id.my_future_msg_btn_view_all_top);
        View myFutureMsgBtnViewAllBottom = findViewById(R.id.my_future_msg_btn_view_all_bottom);

        myFutureMsgRvTimeline.setLayoutManager(new LinearLayoutManager(this));
        myFutureMsgAdapter = new MyFutureMsgAdapter(myFutureMsgRepo.myFutureMsgGetMessages(), this::myFutureMsgShowDetail);
        myFutureMsgRvTimeline.setAdapter(myFutureMsgAdapter);

        myFutureMsgFabCompose.setOnClickListener(myFutureMsgV -> 
            MyFutureMsgComposeBottomSheetFragment.newInstanceForAdd().show(getSupportFragmentManager(), "compose"));

        myFutureMsgBtnViewAllTop.setOnClickListener(myFutureMsgV -> myFutureMsgOpenAllMessages());
        myFutureMsgBtnViewAllBottom.setOnClickListener(myFutureMsgV -> myFutureMsgOpenAllMessages());
        myFutureMsgTvLastArrived.setOnClickListener(myFutureMsgV -> {
            MyFutureMsg myFuture = myFutureMsgLastArrived();
            if (myFuture != null) myFutureMsgShowDetail(myFuture);
        });

        myFutureMsgStartFabGlowPulse();
        myFutureMsgRefresh();
    }

    private void myFutureMsgOpenAllMessages() {
        startActivity(new Intent(this, MyFutureMsgAllMessagesActivity.class));
        overridePendingTransition(R.anim.slide_in_right, 0);
    }

    private void myFutureMsgShowDetail(MyFutureMsg myFuture) {
        MyFutureDetailDialogFragment.newInstance(myFuture.myFutureMsgId).show(getSupportFragmentManager(), "detail");
    }

    private MyFutureMsg myFutureMsgLastArrived() {
        List<MyFutureMsg> myFutures = myFutureMsgRepo.myFutureMsgGetMessages();
        for (MyFutureMsg myFuture : myFutures) {
            if (myFuture.myFutureMsgArrived) return myFuture;
        }
        return null;
    }

    private void myFutureMsgRefresh() {
        List<MyFutureMsg> myFutures = myFutureMsgRepo.myFutureMsgGetMessages();
        boolean myFutureMsgEmpty = myFutures.isEmpty();
        myFutureMsgEmptyState.setVisibility(myFutureMsgEmpty ? View.VISIBLE : View.GONE);
        myFutureMsgRvTimeline.setVisibility(myFutureMsgEmpty ? View.GONE : View.VISIBLE);
        
        MyFutureMsg myFutureMsgLast = myFutureMsgLastArrived();
        if (myFutureMsgLast != null) {
            myFutureMsgTvLastArrived.setText(myFutureMsgLast.myFutureMsgText);
        } else {
            myFutureMsgTvLastArrived.setText(R.string.my_future_msg_no_arrived_yet);
        }
        myFutureMsgAdapter.notifyDataSetChanged();
    }

    private void myFutureMsgStartFabGlowPulse() {
        ObjectAnimator myFutureMsgScaleX = ObjectAnimator.ofFloat(myFutureMsgFabGlow, "scaleX", 1f, 1.4f);
        ObjectAnimator myFutureMsgScaleY = ObjectAnimator.ofFloat(myFutureMsgFabGlow, "scaleY", 1f, 1.4f);
        ObjectAnimator myFutureMsgAlpha = ObjectAnimator.ofFloat(myFutureMsgFabGlow, "alpha", 0.4f, 0f);

        myFutureMsgGlowAnimator = new AnimatorSet();
        myFutureMsgGlowAnimator.playTogether(myFutureMsgScaleX, myFutureMsgScaleY, myFutureMsgAlpha);
        myFutureMsgGlowAnimator.setDuration(800);
        myFutureMsgGlowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        myFutureMsgGlowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator myFutureMsgAnimation) {
                if (myFutureMsgGlowAnimator != null) myFutureMsgGlowAnimator.start();
            }
        });
        myFutureMsgGlowAnimator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myFutureMsgRepo.myFutureMsgAddListener(this);
        myFutureMsgRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myFutureMsgRepo.myFutureMsgRemoveListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myFutureMsgGlowAnimator != null) {
            myFutureMsgGlowAnimator.removeAllListeners();
            myFutureMsgGlowAnimator.cancel();
            myFutureMsgGlowAnimator = null;
        }
    }

    @Override
    public void myFutureMsgOnMessagesChanged() {
        myFutureMsgRefresh();
    }
}
