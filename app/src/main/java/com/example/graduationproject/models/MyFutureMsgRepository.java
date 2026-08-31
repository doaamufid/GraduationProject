package com.example.graduationproject.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.graduationproject.util.MyFutureMsgDateUtils;

public class MyFutureMsgRepository {
    private static MyFutureMsgRepository myFutureMsgInstance;
    private final List<MyFutureMsg> myFutures;
    private final List<Listener> myFutureMsgListeners;
    private long myFutureMsgNextId;

    public interface Listener {
        void myFutureMsgOnMessagesChanged();
    }

    private MyFutureMsgRepository() {
        myFutures = new ArrayList<>();
        myFutureMsgListeners = new ArrayList<>();
        myFutureMsgNextId = 1000L;
        myFutureMsgSeed();
    }

    public static synchronized MyFutureMsgRepository myFutureMsgGetInstance() {
        if (myFutureMsgInstance == null) {
            myFutureMsgInstance = new MyFutureMsgRepository();
        }
        return myFutureMsgInstance;
    }

    private void myFutureMsgSeed() {
        Date myFutureMsgNow = new Date();
        myFutures.add(new MyFutureMsg(1, "لو حسيت اليوم إنه ثقيل، تذكر إن كل يوم صعب مررت فيه قبل، خلصته. أنت أقوى مما تتخيل.",
            "منذ شهر", MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, -30), myFutureMsgNow, true));

        myFutures.add(new MyFutureMsg(2, "لا تقلق بشأن الغد، فكل شيء سيكون بخير في الوقت المناسب.",
            "منذ ٤ أيام", MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, -4), MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, 23), false));

        myFutures.add(new MyFutureMsg(3, "تذكر دائماً سبب بدئك. النجاح ليس بعيداً كما تظن.",
            "منذ يومين", MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, -2), MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, 210), false));

        myFutures.add(new MyFutureMsg(4, "ملاحظة لنفسي: أنتِ قاعدة تبلين بلاءً حسناً، استمري في تمارين التنفس.",
            "اليوم", myFutureMsgNow, MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, 7), false));

        myFutures.add(new MyFutureMsg(5, "رسالة تشجيع ليوم الامتحان: لا تتوترين، سويتي اللي عليك والباقي على الله.",
            "منذ ساعة", myFutureMsgNow, MyFutureMsgDateUtils.myFutureMsgAddDays(myFutureMsgNow, 15), false));
    }

    public List<MyFutureMsg> myFutureMsgGetMessages() {
        return myFutures;
    }

    public void myFutureMsgAddMessage(String myFutureMsgText, Date myFutureMsgTargetDate) {
        MyFutureMsg myFuture = new MyFutureMsg(myFutureMsgNextId++, myFutureMsgText, "الآن", new Date(), myFutureMsgTargetDate, false);
        myFutures.add(0, myFuture);
        myFutureMsgNotifyListeners();
    }

    public void myFutureMsgUpdateMessage(long myFutureMsgId, String myFutureMsgText, Date myFutureMsgTargetDate) {
        for (MyFutureMsg myFuture : myFutures) {
            if (myFuture.myFutureMsgId == myFutureMsgId) {
                myFuture.myFutureMsgText = myFutureMsgText;
                myFuture.myFutureMsgTargetDate = myFutureMsgTargetDate;
                break;
            }
        }
        myFutureMsgNotifyListeners();
    }

    public void myFutureMsgDeleteMessage(long myFutureMsgId) {
        Iterator<MyFutureMsg> myFutureMsgIt = myFutures.iterator();
        while (myFutureMsgIt.hasNext()) {
            if (myFutureMsgIt.next().myFutureMsgId == myFutureMsgId) {
                myFutureMsgIt.remove();
                break;
            }
        }
        myFutureMsgNotifyListeners();
    }

    public MyFutureMsg myFutureMsgFindById(long myFutureMsgId) {
        for (MyFutureMsg myFuture : myFutures) {
            if (myFuture.myFutureMsgId == myFutureMsgId) return myFuture;
        }
        return null;
    }

    public void myFutureMsgAddListener(Listener myFutureMsgL) {
        if (!myFutureMsgListeners.contains(myFutureMsgL)) {
            myFutureMsgListeners.add(myFutureMsgL);
        }
    }

    public void myFutureMsgRemoveListener(Listener myFutureMsgL) {
        myFutureMsgListeners.remove(myFutureMsgL);
    }

    private void myFutureMsgNotifyListeners() {
        for (Listener myFutureMsgL : new ArrayList<>(myFutureMsgListeners)) {
            myFutureMsgL.myFutureMsgOnMessagesChanged();
        }
    }
}
