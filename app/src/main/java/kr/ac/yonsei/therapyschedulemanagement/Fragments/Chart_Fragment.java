package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import kr.ac.yonsei.therapyschedulemanagement.Adatpers.ChartData_Adapter;
import kr.ac.yonsei.therapyschedulemanagement.Chart_CardItem;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Chart_Fragment extends Fragment {
    private static final String TAG = "Chart_Fragment";

    private PieChart mPiechart;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener childEventListener;

    private LinearLayout linear_nothing;
    private MaterialSpinner spinner_month, spinner_year;
    private RecyclerView recyclerView;
    private ChartData_Adapter chartDataAdapter;
    private String setYear, setMonth;
    private long now = System.currentTimeMillis();
    private Date date = new Date(now);
    public static String staticYear, staticMonth;
    private Animation anim_fromBottom, anim_fromRight;
    private Switch switch_score;
    private BarChart mBarChart;

    int[] colorSet;

    public static Chart_Fragment newInstance() {
        Chart_Fragment f = new Chart_Fragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recycler_chart);
        linear_nothing = view.findViewById(R.id.linear_chart_nothong);
        anim_fromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom);
        anim_fromRight = AnimationUtils.loadAnimation(getContext(), R.anim.from_right);
        switch_score = view.findViewById(R.id.switch_score);
        mBarChart = view.findViewById(R.id.barChart);
        mPiechart = view.findViewById(R.id.pieChart);
        mPiechart.setUsePercentValues(true);
        mPiechart.getDescription().setEnabled(false);

        // Bar Chart 설정
        // 오른쪽 y축 없앰
        mBarChart.getAxisRight().setEnabled(false);
        // y축 최대 최소 보여주는 값 설정
        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setAxisMaximum(5.0f);
        yAxis.setAxisMinimum(0f);


        // 스피너 관련
        spinner_month = view.findViewById(R.id.spinner_chart_month);
        spinner_year = view.findViewById(R.id.spinner_chart_year);
        spinner_month.setItems("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월");
        spinner_year.setItems("2018년", "2019년", "2020년", "2021년");

        //checkGraphshow();

        //offset 설정
        mPiechart.setExtraOffsets(5, 10, 5, 5);

        mPiechart.setDragDecelerationFrictionCoef(10f);               //돌아가는 부드러움 설정

        mPiechart.setDrawHoleEnabled(false);                             //여러 효과
        mPiechart.setHoleColor(Color.WHITE);
        mPiechart.setTransparentCircleRadius(61f);

        ArrayList<String> verygoodList = new ArrayList<>();
        ArrayList<String> goodList = new ArrayList<>();
        ArrayList<String> normalList = new ArrayList<>();
        ArrayList<String> badList = new ArrayList<>();
        ArrayList<String> verybadList = new ArrayList<>();
        ArrayList<String> mdateList = new ArrayList<>();
        ArrayList<String> mstatusList = new ArrayList<>();
        ArrayList<String> mcontentsList = new ArrayList<>();

        // 설정해놓은 값 받아오기
        SharedPreferences chart_color = PreferenceManager.getDefaultSharedPreferences(getContext());
        String chartColor = chart_color.getString("chart_color", "JOYFUL_COLORS");
        if (chartColor.equals("JOYFUL_COLORS")) {
            colorSet = ColorTemplate.JOYFUL_COLORS;
        } else if (chartColor.equals("LIBERTY_COLORS")) {
            colorSet = ColorTemplate.LIBERTY_COLORS;
        } else if (chartColor.equals("MATERIAL_COLORS")) {
            colorSet = ColorTemplate.MATERIAL_COLORS;
        } else if (chartColor.equals("COLORFUL_COLORS")) {
            colorSet = ColorTemplate.COLORFUL_COLORS;
        } else if (chartColor.equals("PASTEL_COLORS")) {
            colorSet = ColorTemplate.PASTEL_COLORS;
        } else if (chartColor.equals("VORDIPLOM_COLORS")) {
            colorSet = ColorTemplate.VORDIPLOM_COLORS;
        } else {
            colorSet = ColorTemplate.VORDIPLOM_COLORS;
        }

        // 현재 날짜
        int nowYear = date.getYear() + 2000 - 100;
        int nowMonth = date.getMonth() + 1;

        setYear = String.valueOf(nowYear);
        setMonth = String.valueOf(nowMonth);

        staticYear = setYear;
        staticMonth = setMonth;

        // 처음 스피너 날짜 선택
        int index = nowYear - 2018;
        spinner_year.setSelectedIndex(index);
        spinner_month.setSelectedIndex(nowMonth - 1);
        spinner_year.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                setYear = item.toString().replace("년", "");
                recyclerView.setVisibility(View.INVISIBLE);
                mPiechart.setVisibility(View.INVISIBLE);
                linear_nothing.setVisibility(View.VISIBLE);
                selectedSpinnerDB();

                staticYear = setYear;

                // 연평균 데이터
                ArrayList<BarEntry> bar_yEntry = new ArrayList<>();

                int selectedYear;
                selectedYear = spinner_year.getSelectedIndex() + 2018;

                barMonthChartYdataa(selectedYear, 1, bar_yEntry, 1);
                barMonthChartYdataa(selectedYear, 2, bar_yEntry, 2);
                barMonthChartYdataa(selectedYear, 3, bar_yEntry, 3);
                barMonthChartYdataa(selectedYear, 4, bar_yEntry, 4);
                barMonthChartYdataa(selectedYear, 5, bar_yEntry, 5);
                barMonthChartYdataa(selectedYear, 6, bar_yEntry, 6);
                barMonthChartYdataa(selectedYear, 7, bar_yEntry, 7);
                barMonthChartYdataa(selectedYear, 8, bar_yEntry, 8);
                barMonthChartYdataa(selectedYear, 9, bar_yEntry, 9);
                barMonthChartYdataa(selectedYear, 10, bar_yEntry, 10);
                barMonthChartYdataa(selectedYear, 11, bar_yEntry, 11);
                barMonthChartYdataa(selectedYear, 12, bar_yEntry, 12);
            }
        });
        spinner_month.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                int monthIndex = Integer.parseInt(item.replace("월", ""));
                setMonth = String.valueOf(monthIndex);
                recyclerView.setVisibility(View.INVISIBLE);
                mPiechart.setVisibility(View.INVISIBLE);
                linear_nothing.setVisibility(View.VISIBLE);
                selectedSpinnerDB();

                staticMonth = setMonth;
            }
        });

        // 처음 화면
        // 체크 안 됐을 때
        mBarChart.setVisibility(View.INVISIBLE);
        mPiechart.setVisibility(View.VISIBLE);
        mRef = mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(nowYear + "/" + nowMonth);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    String state = data.get("status").toString();
                    String mStatus = state;
                    String mDate = data.get("date").toString();
                    String mContents = data.get("contents").toString();

                    mstatusList.add(mStatus);
                    mdateList.add(mDate);
                    mcontentsList.add(mContents);

                    if (state.equals("매우좋음")) {
                        verygoodList.add(state);
                        Log.d(TAG, "onChildAdded: " + verygoodList.size());
                    } else if (state.equals("좋음")) {
                        goodList.add(state);
                    } else if (state.equals("보통")) {
                        normalList.add(state);
                    } else if (state.equals("나쁨")) {
                        badList.add(state);
                    } else if (state.equals("매우나쁨")) {
                        verybadList.add(state);
                    }

                    // 데이터 입력
                    ArrayList<PieEntry> yValues = new ArrayList<>();

                    yValues.add(new PieEntry(verybadList.size(), "매우나쁨"));
                    yValues.add(new PieEntry(badList.size(), "나쁨"));
                    yValues.add(new PieEntry(normalList.size(), "보통"));
                    yValues.add(new PieEntry(goodList.size(), "좋음"));
                    yValues.add(new PieEntry(verygoodList.size(), "매우좋음"));

                    //설명란
                    Description description = new Description();
                    description.setText("단위 : %");
                    description.setTextSize(15);
                    mPiechart.setDescription(description);
                    mPiechart.setDrawHoleEnabled(true);                             //여러 효과
                    mPiechart.setHoleColor(Color.WHITE);
                    mPiechart.setTransparentCircleRadius(61f);
                    mPiechart.setCenterText("행동 특성");
                    mPiechart.setCenterTextSize(16);
                    mPiechart.setCenterTextColor(Color.BLACK);

                    PieDataSet dataSet = new PieDataSet(yValues, "");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(colorSet);

                    PieData data2 = new PieData(dataSet);

                    data2.setValueTextColor(Color.YELLOW);
                    data2.setValueTextSize(10f);

                    mPiechart.animateY(600, Easing.EaseInCubic);
                    mPiechart.invalidate();
                    mPiechart.setData(data2);

                    // 어댑터 연결 세팅
                    ArrayList<Chart_CardItem> chartCardItems = new ArrayList<>();
                    chartDataAdapter = new ChartData_Adapter(chartCardItems);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAnimation(anim_fromRight); // 애니메이션 추가(밑에서 위로)
                    chartDataAdapter.notifyDataSetChanged();

                    for (int i = 0; i < mdateList.size(); i++) {
                        Chart_CardItem cardItem = new Chart_CardItem(mdateList.get(i), mstatusList.get(i), mcontentsList.get(i));
                        chartCardItems.add(cardItem);
                        recyclerView.removeAllViews();
                        recyclerView.setAdapter(chartDataAdapter);
                    }
                    linear_nothing.setVisibility(View.INVISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
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
        };
        mRef.addChildEventListener(childEventListener);

        // 파이차트 클릭 리스너
        mPiechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: highlight " + h.getDataIndex());
                int highlightIndex = (int) h.getX();

                //리스트 내용 비우기
                mstatusList.clear();
                mdateList.clear();
                mcontentsList.clear();

                if (highlightIndex == 0) {
                    // 매우나쁨
                    showStatusList("매우나쁨");
                } else if (highlightIndex == 1) {
                    //나쁨
                    showStatusList("나쁨");
                } else if (highlightIndex == 2) {
                    //보통
                    showStatusList("보통");
                } else if (highlightIndex == 3) {
                    //좋음
                    showStatusList("좋음");
                } else if (highlightIndex == 4) {
                    //매우좋음
                    showStatusList("매우좋음");
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        switch_score.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBarChart.setVisibility(View.VISIBLE);
                    mPiechart.setVisibility(View.INVISIBLE);

                    // Bar 차트 x축 설정
                    String[] xData = new String[]{"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
                    XAxis xAxis = mBarChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(xData));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    ArrayList<BarEntry> bar_yEntry = new ArrayList<>();

                    int selectedYear;
                    selectedYear = spinner_year.getSelectedIndex() + 2018;

                    barMonthChartYdataa(selectedYear, 1, bar_yEntry, 1);
                    barMonthChartYdataa(selectedYear, 2, bar_yEntry, 2);
                    barMonthChartYdataa(selectedYear, 3, bar_yEntry, 3);
                    barMonthChartYdataa(selectedYear, 4, bar_yEntry, 4);
                    barMonthChartYdataa(selectedYear, 5, bar_yEntry, 5);
                    barMonthChartYdataa(selectedYear, 6, bar_yEntry, 6);
                    barMonthChartYdataa(selectedYear, 7, bar_yEntry, 7);
                    barMonthChartYdataa(selectedYear, 8, bar_yEntry, 8);
                    barMonthChartYdataa(selectedYear, 9, bar_yEntry, 9);
                    barMonthChartYdataa(selectedYear, 10, bar_yEntry, 10);
                    barMonthChartYdataa(selectedYear, 11, bar_yEntry, 11);
                    barMonthChartYdataa(selectedYear, 12, bar_yEntry, 12);



                } else {
                    // 체크 안 됐을 때
                    mBarChart.setVisibility(View.INVISIBLE);
                    mPiechart.setVisibility(View.VISIBLE);
                    mRef = mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                            .child("Diary")
                            .child(nowYear + "/" + nowMonth);

                    childEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            try {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                String state = data.get("status").toString();
                                String mStatus = state;
                                String mDate = data.get("date").toString();
                                String mContents = data.get("contents").toString();

                                mstatusList.add(mStatus);
                                mdateList.add(mDate);
                                mcontentsList.add(mContents);

                                if (state.equals("매우좋음")) {
                                    verygoodList.add(state);
                                    Log.d(TAG, "onChildAdded: " + verygoodList.size());
                                } else if (state.equals("좋음")) {
                                    goodList.add(state);
                                } else if (state.equals("보통")) {
                                    normalList.add(state);
                                } else if (state.equals("나쁨")) {
                                    badList.add(state);
                                } else if (state.equals("매우나쁨")) {
                                    verybadList.add(state);
                                }

                                // 데이터 입력
                                ArrayList<PieEntry> yValues = new ArrayList<>();

                                yValues.add(new PieEntry(verybadList.size(), "매우나쁨"));
                                yValues.add(new PieEntry(badList.size(), "나쁨"));
                                yValues.add(new PieEntry(normalList.size(), "보통"));
                                yValues.add(new PieEntry(goodList.size(), "좋음"));
                                yValues.add(new PieEntry(verygoodList.size(), "매우좋음"));

                                //설명란
                                Description description = new Description();
                                description.setText("단위 : %");
                                description.setTextSize(15);
                                mPiechart.setDescription(description);
                                mPiechart.setDrawHoleEnabled(true);                             //여러 효과
                                mPiechart.setHoleColor(Color.WHITE);
                                mPiechart.setTransparentCircleRadius(61f);
                                mPiechart.setCenterText("행동 특성");
                                mPiechart.setCenterTextSize(16);
                                mPiechart.setCenterTextColor(Color.BLACK);

                                PieDataSet dataSet = new PieDataSet(yValues, "");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(colorSet);

                                PieData data2 = new PieData(dataSet);

                                data2.setValueTextColor(Color.YELLOW);
                                data2.setValueTextSize(10f);

                                mPiechart.animateY(600, Easing.EaseInCubic);
                                mPiechart.invalidate();
                                mPiechart.setData(data2);

                                linear_nothing.setVisibility(View.INVISIBLE);

                            } catch (Exception e) {
                                e.printStackTrace();
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
                    };
                    mRef.addChildEventListener(childEventListener);

                    // 파이차트 클릭 리스너
                    mPiechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            Log.d(TAG, "onValueSelected: highlight " + h.getDataIndex());
                            int highlightIndex = (int) h.getX();

                            //리스트 내용 비우기
                            mstatusList.clear();
                            mdateList.clear();
                            mcontentsList.clear();

                            if (highlightIndex == 0) {
                                // 매우나쁨
                                showStatusList("매우나쁨");
                            } else if (highlightIndex == 1) {
                                //나쁨
                                showStatusList("나쁨");
                            } else if (highlightIndex == 2) {
                                //보통
                                showStatusList("보통");
                            } else if (highlightIndex == 3) {
                                //좋음
                                showStatusList("좋음");
                            } else if (highlightIndex == 4) {
                                //매우좋음
                                showStatusList("매우좋음");
                            }
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });
                }
            }
        });

        return view;
    }

    private void showStatusList(String status) {
        ArrayList<String> mdateList = new ArrayList<>();
        ArrayList<String> mstatusList = new ArrayList<>();
        ArrayList<String> mcontentsList = new ArrayList<>();

        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(setYear + "/" + setMonth)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {

                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            String state = data.get("status").toString();
                            String mStatus = state;
                            String mDate = data.get("date").toString();
                            String mContents = data.get("contents").toString();

                            if (state.equals(status)) {
                                mstatusList.add(mStatus);
                                mdateList.add(mDate);
                                mcontentsList.add(mContents);
                            }

                            // 어댑터 연결 세팅
                            ArrayList<Chart_CardItem> chartCardItems = new ArrayList<>();
                            chartDataAdapter = new ChartData_Adapter(chartCardItems);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            chartDataAdapter.notifyDataSetChanged();

                            for (int i = 0; i < mdateList.size(); i++) {
                                Chart_CardItem cardItem = new Chart_CardItem(mdateList.get(i), mstatusList.get(i), mcontentsList.get(i));
                                chartCardItems.add(cardItem);
                                recyclerView.removeAllViews();
                                recyclerView.setAdapter(chartDataAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
    }

    private void selectedSpinnerDB() {
        // 설정해놓은 값 받아오기
        recyclerView.setAnimation(anim_fromBottom);
        SharedPreferences chart_color = PreferenceManager.getDefaultSharedPreferences(getContext());
        String chartColor = chart_color.getString("chart_color", "JOYFUL_COLORS");
        if (chartColor.equals("JOYFUL_COLORS")) {
            colorSet = ColorTemplate.JOYFUL_COLORS;
        } else if (chartColor.equals("LIBERTY_COLORS")) {
            colorSet = ColorTemplate.LIBERTY_COLORS;
        } else if (chartColor.equals("MATERIAL_COLORS")) {
            colorSet = ColorTemplate.MATERIAL_COLORS;
        } else if (chartColor.equals("COLORFUL_COLORS")) {
            colorSet = ColorTemplate.COLORFUL_COLORS;
        } else if (chartColor.equals("PASTEL_COLORS")) {
            colorSet = ColorTemplate.PASTEL_COLORS;
        } else if (chartColor.equals("VORDIPLOM_COLORS")) {
            colorSet = ColorTemplate.VORDIPLOM_COLORS;
        } else {
            colorSet = ColorTemplate.VORDIPLOM_COLORS;
        }
        // 데이터 받아오기
        ArrayList<String> verygoodList = new ArrayList<>();
        ArrayList<String> goodList = new ArrayList<>();
        ArrayList<String> normalList = new ArrayList<>();
        ArrayList<String> badList = new ArrayList<>();
        ArrayList<String> verybadList = new ArrayList<>();
        ArrayList<String> mdateList = new ArrayList<>();
        ArrayList<String> mstatusList = new ArrayList<>();
        ArrayList<String> mcontentsList = new ArrayList<>();

        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(setYear + "/" + setMonth)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            Log.d(TAG, "onChildAdded: data! " + data);
                            String state = data.get("status").toString();
                            String mStatus = state;
                            String mDate = data.get("date").toString();
                            String mContents = data.get("contents").toString();

                            if (state.equals("매우좋음")) {
                                verygoodList.add(state);
                                Log.d(TAG, "onChildAdded: " + verygoodList.size());
                            } else if (state.equals("좋음")) {
                                goodList.add(state);
                            } else if (state.equals("보통")) {
                                normalList.add(state);
                            } else if (state.equals("나쁨")) {
                                badList.add(state);
                            } else if (state.equals("매우나쁨")) {
                                verybadList.add(state);
                            }

                            // 데이터 입력
                            ArrayList<PieEntry> yValues = new ArrayList<>();

                            yValues.add(new PieEntry(verybadList.size(), "매우나쁨"));
                            yValues.add(new PieEntry(badList.size(), "나쁨"));
                            yValues.add(new PieEntry(normalList.size(), "보통"));
                            yValues.add(new PieEntry(goodList.size(), "좋음"));
                            yValues.add(new PieEntry(verygoodList.size(), "매우좋음"));

                            //설명란
                            Description description = new Description();
                            description.setText("단위 : %");
                            description.setTextSize(15);
                            mPiechart.setDescription(description);
                            mPiechart.setDrawHoleEnabled(true);                             //여러 효과
                            mPiechart.setHoleColor(Color.WHITE);
                            mPiechart.setTransparentCircleRadius(61f);
                            mPiechart.setCenterText("행동 특성");
                            mPiechart.setCenterTextSize(16);
                            mPiechart.setCenterTextColor(Color.BLACK);

                            PieDataSet dataSet = new PieDataSet(yValues, "");
                            dataSet.setSliceSpace(3f);
                            dataSet.setSelectionShift(5f);
                            dataSet.setColors(colorSet);

                            PieData data2 = new PieData(dataSet);

                            data2.setValueTextColor(Color.YELLOW);
                            data2.setValueTextSize(10f);

                            mPiechart.invalidate();
                            mPiechart.setData(data2);

                            mstatusList.add(mStatus);
                            mdateList.add(mDate);
                            mcontentsList.add(mContents);

                            // 어댑터 연결 세팅
                            ArrayList<Chart_CardItem> chartCardItems = new ArrayList<>();
                            chartDataAdapter = new ChartData_Adapter(chartCardItems);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            chartDataAdapter.notifyDataSetChanged();

                            for (int i = 0; i < mdateList.size(); i++) {
                                Chart_CardItem cardItem = new Chart_CardItem(mdateList.get(i), mstatusList.get(i), mcontentsList.get(i));
                                chartCardItems.add(cardItem);
                                recyclerView.removeAllViews();
                                recyclerView.setAdapter(chartDataAdapter);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            mPiechart.setVisibility(View.VISIBLE);
                            linear_nothing.setVisibility(View.INVISIBLE);

                            checkGraphshow();

                        } catch (Exception e) {
                            e.printStackTrace();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedSpinnerDB();
    }

    private void barMonthChartYdataa(int year, int month, ArrayList<BarEntry> barEntry, int xMonth) {
        // 점수 변환을 위한 변수 지정
        ArrayList<Integer> scoreList = new ArrayList<>();

        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child(year + "/" + month)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot dataSnapshotKey : dataSnapshot.getChildren()) {
                                Map<String, Object> data = (Map<String, Object>) dataSnapshotKey.getValue();
                                String state = data.get("status").toString();
                                Log.d(TAG, "onDataChange: 2222222" + data);
                                if (state.equals("매우좋음")) {
                                    scoreList.add(5);
                                } else if (state.equals("좋음")) {
                                    scoreList.add(4);
                                } else if (state.equals("보통")) {
                                    scoreList.add(3);
                                } else if (state.equals("나쁨")) {
                                    scoreList.add(2);
                                } else if (state.equals("매우나쁨")) {
                                    scoreList.add(1);
                                } else {
                                    barEntry.add(new BarEntry(xMonth - 1, 0f));
                                }
                            }

                            float sum = 0.0f;
                            float average;

                            for (int i = 0; i < scoreList.size(); i++) {
                                sum = sum + scoreList.get(i);
                            }
                            average = sum / scoreList.size();

                            Log.d(TAG, "onDataChange: 평평" + average);

                            if (Float.isNaN(average)) {
                                barEntry.add(new BarEntry(xMonth - 1, 0.0f));
                            }else {
                                barEntry.add(new BarEntry(xMonth - 1, average));

                            }

                            BarData barData = new BarData();

                            BarDataSet barDataSet = new BarDataSet(barEntry, "평균데이터");
                            barDataSet.setColors(colorSet);
                            barDataSet.setValueFormatter(new MyValueFormatter());

                            barData.addDataSet(barDataSet);
                            barData.setValueTextSize(12f);

                            mBarChart.invalidate();
                            mBarChart.animateY(600, Easing.EaseInCubic);
                            mBarChart.setData(barData);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // 어떤 그래프( 바 그래프, 파이 차트 )를 보여줄지 결정해주는 메소드
    private void checkGraphshow() {
        // 스위치 상태에 따른 그래프 보여주기
        if (switch_score.isChecked()) {
            mBarChart.setVisibility(View.VISIBLE);
            mPiechart.setVisibility(View.INVISIBLE);
        } else {
            mBarChart.setVisibility(View.INVISIBLE);
            mPiechart.setVisibility(View.VISIBLE);
        }
    }


    // 소숫점 한 자리까지 보이기 위한 Formatter
    public class MyValueFormatter extends ValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }
}
