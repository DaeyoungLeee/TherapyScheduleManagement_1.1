package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
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
import java.util.HashMap;
import java.util.Map;

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
    ProgressDialog dialog;

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
        dialog = new ProgressDialog(getContext());


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

        // 현재 날짜 (초기화)
        long now = System.currentTimeMillis();
        Date dates = new Date(now);
        year = dates.getYear() - 100 + 2000;
        month = dates.getMonth() + 1;
        date = dates.getDate();
        /** 처음 화면 뜰 때 리스트 현재 날짜 보여주기 */
        Log.d(TAG, "onCreateView: " + year + "/" + month + "/" + date);

        /** 데이터 받아오기 */
        /** 데이터 받아오기 */
        ArrayList<String> therapyList1 = new ArrayList<>();
        ArrayList<String> startList1 = new ArrayList<>();
        ArrayList<String> endList1 = new ArrayList<>();

        // 테라피 종류 가져오기
        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("therapy")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String therapy = data.get("therapy").toString();
                        Log.d(TAG, "onChildAdded:_th " + therapy);
                        therapyList1.add(therapy);
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

        //시작시간 가져오기
        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("start_time")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String startTime = data.get("start_time").toString();
                        Log.d(TAG, "onChildAdded:_st " + startTime);
                        startList1.add(startTime);
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

        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("end_time")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String endTime = data.get("end_time").toString();
                        Log.d(TAG, "onChildAdded: " + endTime);
                        endList1.add(endTime);

                        if (therapyList1.size() == startList1.size() && therapyList1.size() == endList1.size()) {
                            startList1.size();
                            endList1.size();
                            /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                            ArrayList<CardItem> cardItemsList = new ArrayList<>();
                            calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            calendarDaySchduleAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onChildAdded: " + therapyList1.size() + "/" + startList1.size() + "/ " + endList1.size());
                            for (int j = 0; j < endList1.size(); j++) {
                                final CardItem cardItem = new CardItem(therapyList1.get(j), startList1.get(j), endList1.get(j));
                                cardItemsList.add(cardItem);
                                recyclerView.removeAllViewsInLayout();
                                recyclerView.setAdapter(calendarDaySchduleAdapter);
                                dialog.dismiss();
                            }
                            dialog.dismiss();
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


        /** 캘린더 날짜 선택했을 때 동작 리스너 */
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int selected_year, int selected_month, int selected_dayOfMonth) {
                year = selected_year;
                month = selected_month + 1;
                date = selected_dayOfMonth;

                Log.d(TAG, "onSelectedDayChange: " + year + "/" + month + "/" + date);
                showProgressDialog();

                /** 데이터 받아오기 */
                ArrayList<String> therapyList1 = new ArrayList<>();
                ArrayList<String> startList1 = new ArrayList<>();
                ArrayList<String> endList1 = new ArrayList<>();

                // 테라피 종류 가져오기
                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("therapy")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                String therapy = data.get("therapy").toString();
                                Log.d(TAG, "onChildAdded_th: " + therapy);
                                therapyList1.add(therapy);
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

                //시작시간 가져오기
                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("start_time")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                String startTime = data.get("start_time").toString();
                                Log.d(TAG, "onChildAdded:_st " + startTime);
                                startList1.add(startTime);
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

                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("end_time")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                String endTime = data.get("end_time").toString();
                                Log.d(TAG, "onChildAdded: " + endTime);
                                endList1.add(endTime);
                                /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                                ArrayList<CardItem> cardItemsList = new ArrayList<>();
                                calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                calendarDaySchduleAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onChildAdded: " + startList1.size() + "/ " + endList1.size());
                                for (int j = 0; j < endList1.size(); j++) {
                                    final CardItem cardItem = new CardItem(therapyList1.get(j), startList1.get(j), endList1.get(j));
                                    cardItemsList.add(cardItem);
                                    recyclerView.removeAllViewsInLayout();
                                    recyclerView.setAdapter(calendarDaySchduleAdapter);
                                    dialog.dismiss();
                                }
                                dialog.dismiss();
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

                // 아무 내용이 없으면 보여주지말기
                if (therapyList1.size() == 0 && startList1.size() == 0 && endList1.size() == 0) {
                    /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                    ArrayList<CardItem> cardItemsList = new ArrayList<>();
                    calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    calendarDaySchduleAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onChildAdded: " + startList1.size() + "/ " + endList1.size());
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.setAdapter(calendarDaySchduleAdapter);
                    dialog.dismiss();
                }
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
            }
        });

        /** 슬라이딩뷰 동작 결과에 따른 뷰 위치 */
        sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
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

        return view;

    }

    private void showProgressDialog() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("데이터를 가져오는 중입니다");
        dialog.show();
    }
}
