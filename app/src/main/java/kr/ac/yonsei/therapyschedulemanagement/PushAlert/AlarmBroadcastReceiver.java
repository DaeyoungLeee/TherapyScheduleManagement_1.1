package kr.ac.yonsei.therapyschedulemanagement.PushAlert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import kr.ac.yonsei.therapyschedulemanagement.Activities.MainActivity;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private final static int NOTICATION_ID = 222;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    String setTime, setTime_note;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 설정 정보 가져오기
        SharedPreferences timeSavedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String alertTime = timeSavedPreference.getString("alert_time", "아침 10시");
        String noteTime = timeSavedPreference.getString("activity_time", "저녁 9시");

        if (alertTime.equals("아침 7시"))
            setTime = "7:0";
        else if (alertTime.equals("아침 8시"))
            setTime = "8:0";
        else if (alertTime.equals("아침 9시"))
            setTime = "9:0";
        else if (alertTime.equals("아침 10시"))
            setTime = "10:0";

        if (noteTime.equals("저녁 7시"))
            setTime_note = "19:0";
        else if (alertTime.equals("저녁 8시"))
            setTime_note = "20:0";
        else if (alertTime.equals("저녁 9시"))
            setTime_note = "21:0";
        else if (alertTime.equals("저녁 10시"))
            setTime_note = "22:0";
        else if (alertTime.equals("저녁 11시"))
            setTime_note = "23:0";

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        int year = date.getYear() + 2000 - 100;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        int hour = date.getHours();
        int minute = date.getMinutes();

        Log.d("date", "onReceive!!!: " + hour + ":" + minute);
        String nowTime = hour + ":" + minute;

        ArrayList<String> list1 = new ArrayList<>();

        // splitData[0] : 연도,
        // splitData[1] : 월,
        // splitData[2] : 일,
        // splitData[3] : 시작시간,
        // splitData[4] : 시작 분,
        // splitData[5] : 끝 시간,
        // splitData[6] : 끝 분,
        // splitData[7] : 치료 종류
        Log.d("TAG", "onReceive: setTime!!! = " + setTime);
        if (nowTime.equals(setTime)) {
            mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                    .child("Calendar")
                    .child(year + "/" + month + "/" + day)
                    .child("Therapy_schedule")
                    .child("data_save")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Map<String, Object> data = (Map<String, Object>)dataSnapshot.getValue();
                            String schedules = data.get("data_save").toString();
                            Log.d("d", "onChildAdded: " + schedules);

                            String[] spliteData = schedules.split(":");

                            String therapyKindInteger;
                            String therapyKind = null;

                            list1.add( spliteData[0]
                                    + spliteData[1]
                                    + spliteData[2]
                                    + spliteData[3]
                                    + spliteData[4]
                                    + spliteData[5]
                                    + spliteData[6]
                                    + spliteData[7]);
                            Collections.sort(list1);

                            StringBuilder comments = new StringBuilder();
                            // 받은 리스트 내용물들 보여주기
                            for (int i = 0; i < list1.size(); i++) {
                                therapyKindInteger = list1.get(i).substring(16);
                                if (therapyKindInteger.equals("1")) {
                                    therapyKind = "감각통증치료";
                                }else if (therapyKindInteger.equals("2")) {
                                    therapyKind = "언 어 치 료";
                                }else if (therapyKindInteger.equals("3")) {
                                    therapyKind = "놀 이 치 료";
                                }else if (therapyKindInteger.equals("4")) {
                                    therapyKind = "물 리 치 료";
                                }else if (therapyKindInteger.equals("5")) {
                                    therapyKind = "작 업 치 료";
                                }
                                comments.append("[" + therapyKind + "]   "
                                        + list1.get(i).substring(8, 10)
                                        + ":" + list1.get(i).substring(10, 12)
                                        + " ~ " + list1.get(i).substring(12, 14)
                                        + ":" + list1.get(i).substring(14, 16) + "\n");
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String channelId ="NOTI_ID"; // 채널 아이디
                                CharSequence channelName = "NOTI_NAME"; //채널 이름
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);
                            }

                            Log.d("milli", "onChildAdded: " + System.currentTimeMillis());

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NOTI_ID")
                                    .setSmallIcon(R.drawable.app_logo_main) //알람 아이콘
                                    .setContentTitle("오늘의 스케줄을 확인해주세요!")  //알람 제목
                                    .setContentText(comments)//알람 내용
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(comments))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); //알람 중요도

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(NOTICATION_ID, builder.build()); //알람 생성
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        Log.d("TAG", "onReceive: setTimeNote!!! = " + setTime_note);

        if (nowTime.equals(setTime_note)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId ="NOTI_ID2"; // 채널 아이디
                CharSequence channelName = "NOTI_NAME2"; //채널 이름
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NOTI_ID2")
                    .setSmallIcon(R.drawable.app_logo_main) //알람 아이콘
                    .setContentTitle("오늘의 내 아이 컨디션을 입력해주세요!")  //알람 제목
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); //알람 중요도

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTICATION_ID, builder.build()); //알람 생성
        }

        Log.d("AlarmBroadcastReceiver", "onReceive");

    }
}

