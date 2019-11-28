package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import kr.ac.yonsei.therapyschedulemanagement.Activities.DiaryPopup_Activity;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Diary_Fragment extends Fragment {
    private static final String TAG = "Diary_Fragment";

    // UI elements
    public static HorizontalCalendar horizontalCalendar;
    private Button btn_add;
    private TextView txt_status, txt_contents;
    public static LinearLayout linear_empty;

    // 현재 날짜
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    int year = date.getYear() + 2000 - 100;
    int month = date.getMonth() + 1;
    int day = date.getDate();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public static Diary_Fragment newInstance() {
        Diary_Fragment f = new Diary_Fragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary, container, false);

        btn_add = view.findViewById(R.id.btn_add_diary);
        txt_contents = view.findViewById(R.id.txt_diary);
        txt_status = view.findViewById(R.id.txt_status);
        linear_empty = view.findViewById(R.id.linear_empty);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(year + "/" + month + "/" + day)
                .child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        updateDiary();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(year + "/" + month + "/" + day)
                .child("contents")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        updateDiary();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        try {
           updateDiary();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /** 캘린더뷰 관련 */
        /* starts before 1 month from now */
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -12);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 12);

        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView_horizon)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .showBottomText(true)
                .end()
                .build();

        // 캘린더 날짜 선택시
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                year = date.getTime().getYear() + 2000 - 100;
                month = date.getTime().getMonth() + 1;
                day = date.getTime().getDate();
                mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                        .child("Diary")
                        .child(year + "/" + month + "/" + day)
                        .child("status")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                updateDiary();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                        .child("Diary")
                        .child(year + "/" + month + "/" + day)
                        .child("contents")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                updateDiary();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                try {
                    updateDiary();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 추가 및 수정 버튼 클릭
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DiaryPopup_Activity.class);
                intent.putExtra("Date", year + "년 " + month + "월 " + date + "일");
                intent.putExtra("Year", year);
                intent.putExtra("Month", month);
                intent.putExtra("Day", day);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    private void updateDiary() {
        // 데이터가 저장돼있으면 불러오기
        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(year + "/" + month + "/" + day)
                .child("status")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status != null) {
                            txt_status.setText(status);
                            linear_empty.setVisibility(View.INVISIBLE);
                        }else {
                            linear_empty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(year + "/" + month + "/" + day)
                .child("contents")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String diary = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "onChildAdded: " + diary);
                        txt_contents.setText(diary);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
