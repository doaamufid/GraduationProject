package com.example.graduationproject.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.models.MyFutureMsg;
import com.example.graduationproject.models.MyFutureMsgRepository;
import com.example.graduationproject.util.MyFutureMsgDateUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Date;

public class MyFutureMsgComposeBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String myFutureMsgArgEditId = "edit_id";

    private enum When { NONE, CUSTOM, WEEK, MONTH, THREE_MONTHS, YEAR }

    private When myFutureMsgSelected = When.NONE;
    private Date myFutureMsgCustomDate;
    private Long myFutureMsgEditingId;

    private EditText myFutureMsgEtText;
    private TextView myFutureMsgTvSheetTitle;
    private TextView myFutureMsgTvCustomDate;
    private TextView myFutureMsgTvSaveLabel;
    private TextView myFutureMsgChipCustom, myFutureMsgChipWeek, myFutureMsgChipMonth, myFutureMsgChip3m, myFutureMsgChipYear;
    private FrameLayout myFutureMsgBtnSave;

    public static MyFutureMsgComposeBottomSheetFragment newInstanceForAdd() {
        return new MyFutureMsgComposeBottomSheetFragment();
    }

    public static MyFutureMsgComposeBottomSheetFragment newInstanceForEdit(long myFutureMsgId) {
        MyFutureMsgComposeBottomSheetFragment myFutureMsgFragment = new MyFutureMsgComposeBottomSheetFragment();
        Bundle myFutureMsgArgs = new Bundle();
        myFutureMsgArgs.putLong(myFutureMsgArgEditId, myFutureMsgId);
        myFutureMsgFragment.setArguments(myFutureMsgArgs);
        return myFutureMsgFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle myFutureMsgSavedInstanceState) {
        BottomSheetDialog myFutureMsgDialog = (BottomSheetDialog) super.onCreateDialog(myFutureMsgSavedInstanceState);
        myFutureMsgDialog.setOnShowListener(myFutureMsgD -> {
            FrameLayout myFutureMsgBottomSheet = myFutureMsgDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (myFutureMsgBottomSheet != null) {
                myFutureMsgBottomSheet.setBackgroundColor(0);
                BottomSheetBehavior<FrameLayout> myFutureMsgBehavior = BottomSheetBehavior.from(myFutureMsgBottomSheet);
                myFutureMsgBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                myFutureMsgBehavior.setSkipCollapsed(true);
            }
        });
        return myFutureMsgDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater myFutureMsgInflater, @Nullable ViewGroup myFutureMsgContainer, @Nullable Bundle myFutureMsgSavedInstanceState) {
        return myFutureMsgInflater.inflate(R.layout.my_future_msg_fragment_compose, myFutureMsgContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View myFutureMsgView, @Nullable Bundle myFutureMsgSavedInstanceState) {
        super.onViewCreated(myFutureMsgView, myFutureMsgSavedInstanceState);
        if (getArguments() != null && getArguments().containsKey(myFutureMsgArgEditId)) {
            myFutureMsgEditingId = getArguments().getLong(myFutureMsgArgEditId);
        }

        myFutureMsgEtText = myFutureMsgView.findViewById(R.id.my_future_msg_et_text);
        myFutureMsgTvSheetTitle = myFutureMsgView.findViewById(R.id.my_future_msg_tv_sheet_title);
        myFutureMsgTvCustomDate = myFutureMsgView.findViewById(R.id.my_future_msg_tv_custom_date);
        myFutureMsgTvSaveLabel = myFutureMsgView.findViewById(R.id.my_future_msg_tv_save_label);
        myFutureMsgChipCustom = myFutureMsgView.findViewById(R.id.my_future_msg_chip_custom);
        myFutureMsgChipWeek = myFutureMsgView.findViewById(R.id.my_future_msg_chip_week);
        myFutureMsgChipMonth = myFutureMsgView.findViewById(R.id.my_future_msg_chip_month);
        myFutureMsgChip3m = myFutureMsgView.findViewById(R.id.my_future_msg_chip_3m);
        myFutureMsgChipYear = myFutureMsgView.findViewById(R.id.my_future_msg_chip_year);
        myFutureMsgBtnSave = myFutureMsgView.findViewById(R.id.my_future_msg_btn_save);

        myFutureMsgView.findViewById(R.id.my_future_msg_btn_close_sheet).setOnClickListener(myFutureMsgV -> dismiss());

        MyFutureMsg myFutureMsgExisting = null;
        if (myFutureMsgEditingId != null) {
            myFutureMsgExisting = MyFutureMsgRepository.myFutureMsgGetInstance().myFutureMsgFindById(myFutureMsgEditingId);
        }

        if (myFutureMsgExisting != null) {
            myFutureMsgTvSheetTitle.setText(R.string.my_future_msg_compose_title_edit);
            myFutureMsgTvSaveLabel.setText(R.string.my_future_msg_save_edit);
            myFutureMsgEtText.setText(myFutureMsgExisting.myFutureMsgText);
            myFutureMsgSelected = When.CUSTOM;
            myFutureMsgCustomDate = myFutureMsgExisting.myFutureMsgTargetDate;
            myFutureMsgTvCustomDate.setText(MyFutureMsgDateUtils.myFutureMsgFormatDate(myFutureMsgCustomDate));
            myFutureMsgTvCustomDate.setVisibility(View.VISIBLE);
        } else {
            myFutureMsgTvSheetTitle.setText(R.string.my_future_msg_compose_title_new);
            myFutureMsgTvSaveLabel.setText(R.string.my_future_msg_save_new);
        }

        myFutureMsgChipCustom.setOnClickListener(myFutureMsgV -> {
            myFutureMsgSelected = When.CUSTOM;
            myFutureMsgTvCustomDate.setVisibility(View.VISIBLE);
            myFutureMsgOpenDatePicker();
            myFutureMsgUpdateChipStyles();
            myFutureMsgUpdateSaveEnabled();
        });
        myFutureMsgChipWeek.setOnClickListener(myFutureMsgV -> myFutureMsgChooseRelative(When.WEEK));
        myFutureMsgChipMonth.setOnClickListener(myFutureMsgV -> myFutureMsgChooseRelative(When.MONTH));
        myFutureMsgChip3m.setOnClickListener(myFutureMsgV -> myFutureMsgChooseRelative(When.THREE_MONTHS));
        myFutureMsgChipYear.setOnClickListener(myFutureMsgV -> myFutureMsgChooseRelative(When.YEAR));
        myFutureMsgTvCustomDate.setOnClickListener(myFutureMsgV -> myFutureMsgOpenDatePicker());

        myFutureMsgEtText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence myFutureMsgS, int myFutureMsgStart, int myFutureMsgCount, int myFutureMsgAfter) {}
            @Override public void onTextChanged(CharSequence myFutureMsgS, int myFutureMsgStart, int myFutureMsgBefore, int myFutureMsgCount) {}
            @Override public void afterTextChanged(Editable myFutureMsgS) { myFutureMsgUpdateSaveEnabled(); }
        });

        myFutureMsgBtnSave.setOnClickListener(myFutureMsgV -> myFutureMsgSave());

        myFutureMsgUpdateChipStyles();
        myFutureMsgUpdateSaveEnabled();
    }

    private void myFutureMsgChooseRelative(When myFutureMsgWhenVal) {
        myFutureMsgSelected = myFutureMsgWhenVal;
        myFutureMsgTvCustomDate.setVisibility(View.GONE);
        myFutureMsgCustomDate = null;
        myFutureMsgUpdateChipStyles();
        myFutureMsgUpdateSaveEnabled();
    }

    private void myFutureMsgOpenDatePicker() {
        Calendar myFutureMsgMin = Calendar.getInstance();
        myFutureMsgMin.add(Calendar.DAY_OF_MONTH, 1);
        Calendar myFutureMsgCurrent = Calendar.getInstance();
        if (myFutureMsgCustomDate != null) myFutureMsgCurrent.setTime(myFutureMsgCustomDate);
        else myFutureMsgCurrent.setTime(myFutureMsgMin.getTime());

        DatePickerDialog myFutureMsgDpd = new DatePickerDialog(requireContext(), (myFutureMsgView, myFutureMsgYear, myFutureMsgMonth, myFutureMsgDayOfMonth) -> {
            Calendar myFutureMsgC = Calendar.getInstance();
            myFutureMsgC.set(myFutureMsgYear, myFutureMsgMonth, myFutureMsgDayOfMonth, 0, 0, 0);
            myFutureMsgCustomDate = myFutureMsgC.getTime();
            myFutureMsgTvCustomDate.setText(MyFutureMsgDateUtils.myFutureMsgFormatDate(myFutureMsgCustomDate));
            myFutureMsgTvCustomDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.my_future_msg_text_main));
            myFutureMsgUpdateSaveEnabled();
        }, myFutureMsgCurrent.get(Calendar.YEAR), myFutureMsgCurrent.get(Calendar.MONTH), myFutureMsgCurrent.get(Calendar.DAY_OF_MONTH));
        myFutureMsgDpd.getDatePicker().setMinDate(myFutureMsgMin.getTimeInMillis());
        myFutureMsgDpd.show();
    }

    private void myFutureMsgUpdateChipStyles() {
        myFutureMsgSetChipState(myFutureMsgChipCustom, myFutureMsgSelected == When.CUSTOM);
        myFutureMsgSetChipState(myFutureMsgChipWeek, myFutureMsgSelected == When.WEEK);
        myFutureMsgSetChipState(myFutureMsgChipMonth, myFutureMsgSelected == When.MONTH);
        myFutureMsgSetChipState(myFutureMsgChip3m, myFutureMsgSelected == When.THREE_MONTHS);
        myFutureMsgSetChipState(myFutureMsgChipYear, myFutureMsgSelected == When.YEAR);
    }

    private void myFutureMsgSetChipState(TextView myFutureMsgChip, boolean myFutureMsgActive) {
        int myFutureMsgColor = myFutureMsgActive ? 0xFFFFFFFF : ContextCompat.getColor(requireContext(), R.color.my_future_msg_text_soft);
        myFutureMsgChip.setBackgroundResource(myFutureMsgActive ? R.drawable.my_future_msg_bg_chip_selected : R.drawable.my_future_msg_bg_chip_unselected);
        myFutureMsgChip.setTextColor(myFutureMsgColor);
        if (myFutureMsgChip.getCompoundDrawables()[0] != null) {
            myFutureMsgChip.setCompoundDrawableTintList(ColorStateList.valueOf(myFutureMsgColor));
        }
    }

    private boolean myFutureMsgIsDateChosen() {
        if (myFutureMsgSelected == When.NONE) return false;
        if (myFutureMsgSelected == When.CUSTOM) return myFutureMsgCustomDate != null;
        return true;
    }

    private void myFutureMsgUpdateSaveEnabled() {
        boolean myFutureMsgHasText = myFutureMsgEtText.getText() != null && myFutureMsgEtText.getText().toString().trim().length() > 0;
        boolean myFutureMsgEnabled = myFutureMsgHasText && myFutureMsgIsDateChosen();
        myFutureMsgBtnSave.setEnabled(myFutureMsgEnabled);
        myFutureMsgBtnSave.setBackgroundResource(myFutureMsgEnabled ? R.drawable.my_future_msg_bg_button_primary : R.drawable.my_future_msg_bg_button_primary_disabled);
        myFutureMsgBtnSave.setAlpha(1.0f);
    }

    private void myFutureMsgSave() {
        String myFutureMsgText = myFutureMsgEtText.getText().toString().trim();
        if (myFutureMsgText.isEmpty() || !myFutureMsgIsDateChosen()) return;

        Date myFutureMsgTarget;
        if (myFutureMsgSelected == When.CUSTOM) {
            myFutureMsgTarget = myFutureMsgCustomDate;
        } else {
            int myFutureMsgDays = 0;
            switch (myFutureMsgSelected) {
                case WEEK: myFutureMsgDays = 7; break;
                case MONTH: myFutureMsgDays = 30; break;
                case THREE_MONTHS: myFutureMsgDays = 90; break;
                case YEAR: myFutureMsgDays = 365; break;
            }
            myFutureMsgTarget = MyFutureMsgDateUtils.myFutureMsgAddDays(new Date(), myFutureMsgDays);
        }

        MyFutureMsgRepository myFutureMsgRepo = MyFutureMsgRepository.myFutureMsgGetInstance();
        if (myFutureMsgEditingId != null) {
            myFutureMsgRepo.myFutureMsgUpdateMessage(myFutureMsgEditingId, myFutureMsgText, myFutureMsgTarget);
        } else {
            myFutureMsgRepo.myFutureMsgAddMessage(myFutureMsgText, myFutureMsgTarget);
        }
        dismiss();
    }
}
