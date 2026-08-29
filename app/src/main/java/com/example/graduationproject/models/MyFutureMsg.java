package com.example.graduationproject.models;

import java.util.Date;

public class MyFutureMsg {
    public long myFutureMsgId;
    public String myFutureMsgText;
    public String myFutureMsgCreatedLabel;
    public Date myFutureMsgCreatedDate;
    public Date myFutureMsgTargetDate;
    public boolean myFutureMsgArrived;

    public MyFutureMsg(long myFutureMsgId, String myFutureMsgText, String myFutureMsgCreatedLabel, Date myFutureMsgCreatedDate, Date myFutureMsgTargetDate, boolean myFutureMsgArrived) {
        this.myFutureMsgId = myFutureMsgId;
        this.myFutureMsgText = myFutureMsgText;
        this.myFutureMsgCreatedLabel = myFutureMsgCreatedLabel;
        this.myFutureMsgCreatedDate = myFutureMsgCreatedDate;
        this.myFutureMsgTargetDate = myFutureMsgTargetDate;
        this.myFutureMsgArrived = myFutureMsgArrived;
    }
}
