package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.ac.yonsei.therapyschedulemanagement.CalendarDaySchdule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.Activities.Popup_Activity;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Calendar_Fragment extends Fragment {

    private static String TAG = "Fragment2";
    // UI elements
    FloatingActionButton btn_add_schedule;
    CalendarView calendarView;
    Calendar calendar;
    SlidingUpPanelLayout sliding_layout;
    RecyclerView recyclerView;
    TextView backslide;



    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    // Adapter
    CalendarDaySchdule_Adapter calendarDaySchduleAdapter;

    int date, month, year;

    public static Calendar_Fragment newInstance() {
        Calendar_Fragment f = new Calendar_Fragment();
        return f;
    }

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        btn_add_schedule = view.findViewById(R.id.btn_add);
        sliding_layout = view.findViewById(R.id.sliding_layout);
        recyclerView = view.findViewById(R.id.recycler_frag2);

        // 슬라이딩 올라와있는 상태에서 외부 Fade쪽 클릭하면 다시 내려오는 동작
        backslide = view.findViewById(R.id.backSlide);
        backslide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ArrayList<CardItem> cardItemsList = new ArrayList<>();

        calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        calendarDaySchduleAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(calendarDaySchduleAdapter);
        Log.d(TAG, "onCreateView: " + year + "/" + month + "/" + date);


        // 현재 날짜 (초기화)
        long now = System.currentTimeMillis();
        Date dates = new Date(now);
        year = dates.getYear() - 100 + 2000;
        month = dates.getMonth();
        date = dates.getDate();

        /** 슬라이딩 레이아웃 리스너 */
        sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.d(TAG, "onPanelStateChanged: " + newState.toString());
                // 슬라이딩 뷰가 올라오면 Parent뷰로 만들고, 내려가면 캘린더뷰가 Parent 뷰가 되도록
                if (newState.toString().equals("DRAGGING")) {
                    sliding_layout.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sliding_layout.bringToFront();
                } else if (newState.toString().equals("COLLAPSED")) {
                    calendarView.bringToFront();
                }
            }
        });

        /** 캘린더 날짜 선택했을 때 동작 리스너 */
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int selected_year, int selected_month, int selected_dayOfMonth) {
                year = selected_year;
                month = selected_month + 1;
                date = selected_dayOfMonth;

                Log.d(TAG, "onSelectedDayChange: " + year + "/" + month + "/" + date);
/*

                ArrayList<String> endTime1 = new ArrayList<>();
                ArrayList<String> endTime2 = new ArrayList<>();
                ArrayList<String> startTime1 = new ArrayList<>();
                ArrayList<String> startTime2 = new ArrayList<>();

                // 종료시간 가져오기
                DatabaseReference listDataRef = mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("end_time");
                listDataRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String end_time = dataSnapshot.getValue().toString(); // 종료시간들
                        Log.d(TAG, "onChildAdded: " + end_time);
                        endTime1.add(end_time);
                       */
/* for (int i = 0; i < endTime1.size(); i++) {
                            if (!endTime1.contains(endTime1.get(i))) {
                                endTime2.add(endTime1.get(i));
                            }
                        }*//*



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

                // 시작시간 가져오기
                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("start_time")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String start_time = dataSnapshot.getValue().toString();
                                startTime1.add(start_time);
                              */
/*  for (int i = 0; i < startTime1.size(); i++) {
                                    if (!startTime1.contains(startTime1.get(i))) {
                                        startTime2.add(startTime1.get(i));
                                    }
                                }*//*


                                for (int j = 0; j < startTime1.size(); j++) {
                                    final CardItem cardItem = new CardItem(startTime1.get(j), endTime1.get(j));
                                    cardItemsList.add(cardItem);
                                    recyclerView.removeAllViewsInLayout();
                                    recyclerView.setAdapter(calendarDaySchduleAdapter);
                                }
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
*/


            }
        });




        /** Floating 추가 버튼 클릭 동작 */
        btn_add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캘린더 날짜 전달
                Intent intent = new Intent(getContext(), Popup_Activity.class);
                intent.putExtra("Date", year + "년 " + month + "월 " + date + "일");
                intent.putExtra("Year", year);
                intent.putExtra("Month", month);
                intent.putExtra("Day", date);
                startActivityForResult(intent, 1);

                /** 캘린더 날짜 선택했을 때 동작 리스너 */

            }
        });

        return view;

    }

}
