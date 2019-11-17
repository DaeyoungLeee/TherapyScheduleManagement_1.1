package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import kr.ac.yonsei.therapyschedulemanagement.Activities.Popup_Activity;
import kr.ac.yonsei.therapyschedulemanagement.Adatpers.CalendarDaySchdule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Calendar_Fragment extends Fragment implements CalendarDaySchdule_Adapter.OnItemClickedListener {

    private static String TAG = "Fragment2";
    // UI elements
    private FloatingActionButton btn_add_schedule;
    private CalendarView calendarView;
    Calendar calendar;
    private SlidingUpPanelLayout sliding_layout_calendar;
    private RecyclerView recyclerView;
    private TextView backslide;
    private ProgressDialog dialog;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    // Adapter
    CalendarDaySchdule_Adapter calendarDaySchduleAdapter;

    //ArrayList<String> allDataList = new ArrayList<>();
    ArrayList<String> keyList = new ArrayList<>();

    int date, month, year;
    String dayName;

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
        sliding_layout_calendar = view.findViewById(R.id.sliding_layout_calendar);
        recyclerView = view.findViewById(R.id.recycler_frag2);
        dialog = new ProgressDialog(getContext());


        // 슬라이딩 올라와있는 상태에서 외부 Fade쪽 클릭하면 다시 내려오는 동작
        backslide = view.findViewById(R.id.backSlide);
        backslide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_layout_calendar.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
        dayName = doDayOfWeek();
        /** 처음 화면 뜰 때 리스트 현재 날짜 보여주기 */
        Log.d(TAG, "onCreateView: " + year + "/" + month + "/" + date);

        /** 처음 화면에서 클릭 안했을 때 어댑터 보여주기 */    
        ArrayList<String> allDataList = new ArrayList<>();
        ArrayList<String> allDataList2 = new ArrayList<>();

        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("data_save")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d(TAG, "onChildAdded: REAL");

                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String dataAll = data.get("data_save").toString();

                        final String[] splitData = dataAll.split(":");

                        allDataList.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);

                        Collections.sort(allDataList);
                        for (int i = 0; i < allDataList.size(); i++) {
                            if (!allDataList2.contains(allDataList.get(i))) {
                                allDataList2.add(allDataList.get(i));
                            }
                        }

                        Log.d(TAG, "onDataChange: " + allDataList.size());

                        /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                        ArrayList<CardItem> cardItemsList = new ArrayList<>();
                        calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                        calendarDaySchduleAdapter.setOnItemClickedListener(Calendar_Fragment.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        calendarDaySchduleAdapter.notifyDataSetChanged();

                        for (int j = 0; j < allDataList2.size(); j++) {
                            try {
                                final CardItem cardItem = new CardItem(allDataList2.get(j).substring(16),
                                        allDataList2.get(j).substring(8, 10) + ":" + allDataList2.get(j).substring(10, 12),
                                        allDataList2.get(j).substring(12, 14) + ":" + allDataList2.get(j).substring(14, 16));
                                cardItemsList.add(cardItem);
                                recyclerView.removeAllViewsInLayout();
                                recyclerView.setAdapter(calendarDaySchduleAdapter);
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved: REAL");
                        refreshAdapter();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // 아무 내용이 없으면 보여주지말기
        if (allDataList.size() == 0) {
            /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
            ArrayList<CardItem> cardItemsList = new ArrayList<>();
            calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
            calendarDaySchduleAdapter.setOnItemClickedListener(Calendar_Fragment.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            calendarDaySchduleAdapter.notifyDataSetChanged();
            recyclerView.removeAllViewsInLayout();
            recyclerView.setAdapter(calendarDaySchduleAdapter);
            dialog.dismiss();
        }

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

                ArrayList<String> allDataList = new ArrayList<>();
                ArrayList<String> allDataList2 = new ArrayList<>();

                mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                        .child("Calendar")
                        .child(year + "/" + month + "/" + date)
                        .child("Therapy_schedule")
                        .child("data_save")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Log.d(TAG, "onChildAdded: REAL1");
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                String dataAll = data.get("data_save").toString();

                                final String[] splitData = dataAll.split(":");

                                allDataList.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);
                                Collections.sort(allDataList);

                                /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                                for (int i = 0; i < allDataList.size(); i++) {
                                    if (!allDataList2.contains(allDataList.get(i))) {
                                        allDataList2.add(allDataList.get(i));
                                    }
                                }

                                Log.d(TAG, "onDataChange: " + allDataList.size());

                                /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                                ArrayList<CardItem> cardItemsList = new ArrayList<>();
                                calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                                calendarDaySchduleAdapter.setOnItemClickedListener(Calendar_Fragment.this);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                calendarDaySchduleAdapter.notifyDataSetChanged();

                                for (int j = 0; j < allDataList2.size(); j++) {
                                    try {
                                        final CardItem cardItem = new CardItem(allDataList2.get(j).substring(16),
                                                allDataList2.get(j).substring(8, 10) + ":" + allDataList2.get(j).substring(10, 12),
                                                allDataList2.get(j).substring(12, 14) + ":" + allDataList2.get(j).substring(14, 16));
                                        cardItemsList.add(cardItem);
                                        recyclerView.removeAllViewsInLayout();
                                        recyclerView.setAdapter(calendarDaySchduleAdapter);
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onChildRemoved: REAL1");
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                // 아무 내용이 없으면 보여주지말기
                if (allDataList.size() == 0) {
                    /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                    ArrayList<CardItem> cardItemsList = new ArrayList<>();
                    calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                    calendarDaySchduleAdapter.setOnItemClickedListener(Calendar_Fragment.this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    calendarDaySchduleAdapter.notifyDataSetChanged();
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
        sliding_layout_calendar.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // 슬라이딩 뷰가 올라오면 Parent뷰로 만들고, 내려가면 캘린더뷰가 Parent 뷰가 되도록
                if (newState.toString().equals("DRAGGING")) {
                    sliding_layout_calendar.bringToFront();
                }
                if (newState.toString().equals("EXPANDED")) {
                    sliding_layout_calendar.bringToFront();
                } else if (newState.toString().equals("COLLAPSED")) {
                    calendarView.bringToFront();
                }
            }
        });

        return view;

    }

    // 요일 알아내기
    private String doDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        String strWeek = null;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = "일요일";
        } else if (nWeek == 2) {
            strWeek = "월요일";
        } else if (nWeek == 3) {
            strWeek = "화요일";
        } else if (nWeek == 4) {
            strWeek = "수요일";
        } else if (nWeek == 5) {
            strWeek = "목요일";
        } else if (nWeek == 6) {
            strWeek = "금요일";
        } else if (nWeek == 7) {
            strWeek = "토요일";
        }

        return strWeek;
    }

    private void showProgressDialog() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("데이터를 가져오는 중입니다");
        dialog.show();
    }

    @Override
    public void onItemClick(CalendarDaySchdule_Adapter.CustomViewHolder holder, View view, int position) {

    }

    // 삭제!!
    @Override
    public void onDeleteButtonClick(int position) {
        // 그리고 키값 다시 받아오기
        keyList.clear();
        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("data_save")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotKey : dataSnapshot.getChildren()) {
                            keyList.add(dataSnapshotKey.getKey());
                        }
                        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                                .child("Calendar")
                                .child(year + "/" + month + "/" + date)
                                .child("Therapy_schedule")
                                .child("data_save")
                                .child(keyList.get(position))
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                refreshAdapter();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void refreshAdapter() {
        ArrayList<String> allDataList = new ArrayList<>();
        ArrayList<String> allDataList2 = new ArrayList<>();

        mDatabase.getReference(mUser.getEmail().replace(".", "_"))
                .child("Calendar")
                .child(year + "/" + month + "/" + date)
                .child("Therapy_schedule")
                .child("data_save")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d(TAG, "onChildAdded: REAL");

                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String dataAll = data.get("data_save").toString();

                        final String[] splitData = dataAll.split(":");

                        allDataList.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);

                        Collections.sort(allDataList);
                        for (int i = 0; i < allDataList.size(); i++) {
                            if (!allDataList2.contains(allDataList.get(i))) {
                                allDataList2.add(allDataList.get(i));
                            }
                        }

                        Log.d(TAG, "onDataChange: " + allDataList.size());

                        /** 데이터가, 바뀔때마다 리사이클러뷰 업데이트! */
                        ArrayList<CardItem> cardItemsList = new ArrayList<>();
                        calendarDaySchduleAdapter = new CalendarDaySchdule_Adapter(cardItemsList);
                        calendarDaySchduleAdapter.setOnItemClickedListener(Calendar_Fragment.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        calendarDaySchduleAdapter.notifyDataSetChanged();

                        for (int j = 0; j < allDataList2.size(); j++) {
                            try {
                                final CardItem cardItem = new CardItem(allDataList2.get(j).substring(16),
                                        allDataList2.get(j).substring(8, 10) + ":" + allDataList2.get(j).substring(10, 12),
                                        allDataList2.get(j).substring(12, 14) + ":" + allDataList2.get(j).substring(14, 16));
                                cardItemsList.add(cardItem);
                                recyclerView.removeAllViewsInLayout();
                                recyclerView.setAdapter(calendarDaySchduleAdapter);
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved: REAL");

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
