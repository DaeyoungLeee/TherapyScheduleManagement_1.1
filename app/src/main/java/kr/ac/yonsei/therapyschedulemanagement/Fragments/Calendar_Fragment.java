package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.ac.yonsei.therapyschedulemanagement.Activities.Popup_Activity;
import kr.ac.yonsei.therapyschedulemanagement.Adatpers.CalendarDaySchdule_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Calendar_Fragment extends Fragment implements CalendarDaySchdule_Adapter.OnItemClickedListener {

    private static String TAG = "Fragment2";
    // UI elements
    private FloatingActionButton btn_add_schedule;
    private CompactCalendarView compactCalendarView;
    private SlidingUpPanelLayout sliding_layout_calendar;
    private RecyclerView recyclerView;
    private TextView backslide;
    private ProgressDialog dialog;
    private long milliTime;
    public static String userEmail;
    private TextView txt_calendar_date;

    // floating button 이동 관련
    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.

    private float downRawX, downRawY;
    private float dX, dY;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public static FirebaseDatabase mDatabase;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        btn_add_schedule = view.findViewById(R.id.btn_add);
        sliding_layout_calendar = view.findViewById(R.id.sliding_layout_calendar);
        recyclerView = view.findViewById(R.id.recycler_frag2);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        txt_calendar_date = view.findViewById(R.id.txt_calendar_date);
        dialog = new ProgressDialog(getContext());

        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);

//        btn_add_schedule.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                View viewParent;
//
//                switch (event.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//                        downRawX = event.getRawX();
//                        downRawY = event.getRawY();
//                        dX = v.getX() - downRawX;
//                        dY = v.getY() - downRawY;
//
//                        return true; // Consumed
//
//                    case MotionEvent.ACTION_MOVE:
//                        int viewWidth = v.getWidth();
//                        int viewHeight = v.getHeight();
//
//                        viewParent = (View) v.getParent();
//                        int parentWidth = viewParent.getWidth();
//                        int parentHeight = viewParent.getHeight();
//
//                        float newX = event.getRawX() + dX;
//                        newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
//                        newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent
//
//                        float newY = event.getRawY() + dY;
//                        newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
//                        newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent
//
//                        v.animate()
//                                .x(newX)
//                                .y(newY)
//                                .setDuration(0)
//                                .start();
//                        return true; // Consumed
//
//                    case MotionEvent.ACTION_UP:
//
//
//                        float upRawX = event.getRawX();
//                        float upRawY = event.getRawY();
//
//                        float upDX = upRawX - downRawX;
//                        float upDY = upRawY - downRawY;
//
//                        if((Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE))
//                            return true;
//
//                        View viewParent2 = (View) v.getParent();
//                        float borderY,borderX;
//                        float oldX=v.getX(), oldY=v.getY();
//                        float finalX,finalY;
//
//                        borderY = Math.min(v.getY()-viewParent2.getTop(),viewParent2.getBottom()-v.getY());
//                        borderX = Math.min(v.getX()-viewParent2.getLeft(),viewParent2.getRight()-v.getX());
//
//                        //You can set your dp margin from dimension resources (Suggested)
//                        //float fab_margin= getResources().getDimension(R.dimen.fab_margin);
//                        float fab_margin=15;
//
//                        //check if is nearest Y o X
//                        if(borderX>borderY) {
//                            if(v.getY()>viewParent2.getHeight()/2) { //view near Bottom
//                                finalY = viewParent2.getBottom() - v.getHeight();
//                                finalY = Math.min(viewParent2.getHeight() - v.getHeight(), finalY) - fab_margin; // Don't allow the FAB past the bottom of the parent
//                            }
//                            else {  //view vicina a Top
//                                finalY = viewParent2.getTop();
//                                finalY = Math.max(0, finalY) + fab_margin; // Don't allow the FAB past the top of the parent
//                            }
//                            //check if X it's over fab_margin
//                            finalX=oldX;
//                            if(v.getX()+viewParent2.getLeft()<fab_margin)
//                                finalX=viewParent2.getLeft()+fab_margin;
//                            if(viewParent2.getRight()-v.getX()-v.getWidth()<fab_margin)
//                                finalX=viewParent2.getRight()- v.getWidth()-fab_margin;
//                        }
//                        else {  //view near Right
//                            if(v.getX()>viewParent2.getWidth()/2) {
//                                finalX = viewParent2.getRight() - v.getWidth();
//                                finalX = Math.max(0, finalX) - fab_margin; // Don't allow the FAB past the left hand side of the parent
//                            }
//                            else {  //view near Left
//                                finalX = viewParent2.getLeft();
//                                finalX = Math.min(viewParent2.getWidth() - v.getWidth(), finalX) + fab_margin; // Don't allow the FAB past the right hand side of the parent
//                            }
//                            //check if Y it's over fab_margin
//                            finalY=oldY;
//                            if(v.getY()+viewParent2.getTop()<fab_margin)
//                                finalY=viewParent2.getTop()+fab_margin;
//                            if(viewParent2.getBottom()-v.getY()-v.getHeight()<fab_margin)
//                                finalY=viewParent2.getBottom()-v.getHeight()-fab_margin;
//                        }
//
//                        v.animate()
//                                .x(finalX)
//                                .y(finalY)
//                                .setDuration(400)
//                                .start();
//
//                        return false;
//
//                    // A drag consumed
//                    default:
//                        return true;
//                }
//
//
//            }
//        });

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
        userEmail = mUser.getEmail().replace(".", "_");

        // 현재 날짜 (초기화)
        long now = System.currentTimeMillis();
        Date dates = new Date(now);
        year = dates.getYear() - 100 + 2000;
        month = dates.getMonth() + 1;
        date = dates.getDate();
        dayName = doDayOfWeek();
        setMonthSchedule(userEmail, year, month);

        txt_calendar_date.setText(year + "년 " + month + "월");

        /** 처음 화면 뜰 때 리스트 현재 날짜 보여주기 */
        Log.d(TAG, "onCreateView: " + year + "/" + month + "/" + date);

        /** 처음 화면에서 클릭 안했을 때 어댑터 보여주기 */
        ArrayList<String> allDataList = new ArrayList<>();
        ArrayList<String> allDataList2 = new ArrayList<>();

        try {
            mDatabase.getReference(userEmail)
                    .child("Calendar")
                    .child(year + "/" + month + "/" + date)
                    .child("Therapy_schedule")
                    .child("data_save")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d(TAG, "onChildAdded: REAL");

                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            Log.d(TAG, "onChildAdded: ?????" + data);
                            String dataAll = data.get("data_save").toString();

                            final String[] splitData = dataAll.split(":");

                            allDataList.add(splitData[0] + splitData[1] + splitData[2] + splitData[3] + splitData[4] + splitData[5] + splitData[6] + splitData[7]);
                            Collections.sort(allDataList);
                            for (int i = 0; i < allDataList.size(); i++) {
                                if (!allDataList2.contains(allDataList.get(i)))
                                    allDataList2.add(allDataList.get(i));
                            }

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

            /** Compact CalendarView 라이브러리 클릭 리스너 */
            compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                @Override
                public void onDayClick(Date dateClicked) {

                    year = dateClicked.getYear() + 2000 - 100;
                    month = dateClicked.getMonth() + 1;
                    date = dateClicked.getDate();
                    milliTime = dateClicked.getTime();

                    Log.d(TAG, "onSelectedDayChange: " + year + "/" + month + "/" + date);
                    showProgressDialog();

                    /** 데이터 받아오기 */

                    ArrayList<String> allDataList = new ArrayList<>();
                    ArrayList<String> allDataList2 = new ArrayList<>();

                    mDatabase.getReference(userEmail)
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
                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    Log.d(TAG, "onMonthScroll: ");
                    int monthSlide = firstDayOfNewMonth.getMonth() + 1;
                    int yearSlide = firstDayOfNewMonth.getYear() + 2000 - 100;
                    setMonthSchedule(userEmail, yearSlide, monthSlide);
                    txt_calendar_date.setText(yearSlide + "년 " + monthSlide + "월");

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
                    intent.putExtra("MilliTime", milliTime);
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
                        compactCalendarView.bringToFront();
                    }
                }
            });

        } catch (Exception e) {

        }
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
        mDatabase.getReference(userEmail)
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
        ArrayList<String> dotList = new ArrayList<>();

        mDatabase.getReference(userEmail)
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

    // 일정 있으면 표시
    private void setMonthSchedule(String email, int year, int month) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date dateEvent = new Date(now);

                ArrayList<String> dotList = new ArrayList<>();
                ArrayList<String> therapyList = new ArrayList<>();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            mDatabase.getReference(email)
                                    .child("Calendar_dot")
                                    .removeValue();

                            mDatabase.getReference(userEmail)
                                    .child("Calendar")
                                    .child(year + "/" + month)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int k = 0;

                                            for (DataSnapshot dataSnapshotKey : dataSnapshot.getChildren()) {
                                                Map<String, Object> data = (Map<String, Object>) dataSnapshotKey.getValue();
                                                Map<String, Object> dataAll = (Map<String, Object>) data.get("Therapy_schedule");
                                                Map<String, Object> dataSave = (Map<String, Object>) dataAll.get("data_save");

                                                mDatabase.getReference(email)
                                                        .child("Calendar_dot")
                                                        .child(String.valueOf(k))
                                                        .setValue(dataSave);

                                                mDatabase.getReference(userEmail)
                                                        .child("Calendar_dot")
                                                        .child(String.valueOf(k))
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot dataSnapshotKey : dataSnapshot.getChildren()) {

                                                                    final String[] splitData = dataSnapshotKey.getValue().toString()
                                                                            .replace("{data_save", "").replace("}", "")
                                                                            .split(":");

                                                                    try {

                                                                        dotList.add(splitData[8]);
                                                                        therapyList.add(splitData[7]);

                                                                        Log.d(TAG, "onDataChange: !!!!!" + dotList);

                                                                        List<Event> eventArrayList = compactCalendarView.getEvents(dateEvent);

                                                                        for (int i = 0; i < dotList.size(); i++) {
                                                                            if (therapyList.get(i).equals("1")) {
                                                                                eventArrayList.add(new Event(Color.RED, Long.parseLong(dotList.get(i))));
                                                                            } else if (therapyList.get(i).equals("2")) {
                                                                                eventArrayList.add(new Event(Color.BLUE, Long.parseLong(dotList.get(i))));
                                                                            } else if (therapyList.get(i).equals("3")) {
                                                                                eventArrayList.add(new Event(Color.YELLOW, Long.parseLong(dotList.get(i))));
                                                                            } else if (therapyList.get(i).equals("4")) {
                                                                                eventArrayList.add(new Event(Color.GREEN, Long.parseLong(dotList.get(i))));
                                                                            } else if (therapyList.get(i).equals("5")) {
                                                                                eventArrayList.add(new Event(Color.MAGENTA, Long.parseLong(dotList.get(i))));
                                                                            }
                                                                            compactCalendarView.removeAllEvents();
                                                                            compactCalendarView.addEvents(eventArrayList);
                                                                        }
                                                                        dialog.dismiss();

                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                k = k + 1;

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }
}
