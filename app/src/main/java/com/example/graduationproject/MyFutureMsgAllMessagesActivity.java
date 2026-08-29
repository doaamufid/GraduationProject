package com.example.graduationproject;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.Fragments.MyFutureDetailDialogFragment;
import com.example.graduationproject.Fragments.MyFutureMsgComposeBottomSheetFragment;
import com.example.graduationproject.adapters.MyFutureMsgRowAdapter;
import com.example.graduationproject.models.MyFutureMsg;
import com.example.graduationproject.models.MyFutureMsgRepository;

public class MyFutureMsgAllMessagesActivity extends AppCompatActivity implements MyFutureMsgRepository.Listener, MyFutureMsgRowAdapter.Callbacks {
    private MyFutureMsgRepository myFutureMsgRepo;
    private MyFutureMsgRowAdapter myFutureMsgAdapter;
    private RecyclerView myFutureMsgRv;
    private View myFutureMsgEmptyState;

    @Override
    protected void onCreate(@Nullable Bundle myFutureMsgSavedInstanceState) {
        super.onCreate(myFutureMsgSavedInstanceState);
        setContentView(R.layout.my_future_msg_activity_all_messages);

        myFutureMsgRepo = MyFutureMsgRepository.myFutureMsgGetInstance();

        myFutureMsgRv = findViewById(R.id.my_future_msg_rv_all_messages);
        myFutureMsgEmptyState = findViewById(R.id.my_future_msg_empty_all_state);

        myFutureMsgRv.setLayoutManager(new LinearLayoutManager(this));
        myFutureMsgAdapter = new MyFutureMsgRowAdapter(myFutureMsgRepo.myFutureMsgGetMessages(), this);
        myFutureMsgRv.setAdapter(myFutureMsgAdapter);

        findViewById(R.id.my_future_msg_btn_back).setOnClickListener(myFutureMsgV -> finish());
        findViewById(R.id.my_future_msg_btn_add).setOnClickListener(myFutureMsgV -> 
            MyFutureMsgComposeBottomSheetFragment.newInstanceForAdd().show(getSupportFragmentManager(), "compose"));

        myFutureMsgRefresh();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private void myFutureMsgRefresh() {
        myFutureMsgAdapter.myFutureMsgSetData(myFutureMsgRepo.myFutureMsgGetMessages());
        boolean myFutureMsgEmpty = myFutureMsgRepo.myFutureMsgGetMessages().isEmpty();
        myFutureMsgEmptyState.setVisibility(myFutureMsgEmpty ? View.VISIBLE : View.GONE);
        myFutureMsgRv.setVisibility(myFutureMsgEmpty ? View.GONE : View.VISIBLE);
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
    public void myFutureMsgOnMessagesChanged() {
        myFutureMsgRefresh();
    }

    @Override
    public void myFutureMsgOnView(MyFutureMsg myFuture) {
        MyFutureDetailDialogFragment.newInstance(myFuture.myFutureMsgId).show(getSupportFragmentManager(), "detail");
    }

    @Override
    public void myFutureMsgOnEdit(MyFutureMsg myFuture) {
        MyFutureMsgComposeBottomSheetFragment.newInstanceForEdit(myFuture.myFutureMsgId).show(getSupportFragmentManager(), "compose");
    }

    @Override
    public void myFutureMsgOnDelete(long myFutureMsgId) {
        myFutureMsgRepo.myFutureMsgDeleteMessage(myFutureMsgId);
    }
}
