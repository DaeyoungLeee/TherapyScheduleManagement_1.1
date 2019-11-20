package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Chart_Fragment extends Fragment {
    private static final String TAG = "Chart_Fragment";

    private PieChart mPiechart;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public static Chart_Fragment newInstance(){
        Chart_Fragment f = new Chart_Fragment();
        return f;
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mPiechart = view.findViewById(R.id.pieChart);
        mPiechart.setUsePercentValues(true);
        mPiechart.getDescription().setEnabled(false);
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

        mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                .child("Diary")
                .child("2019/11")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Map<String, Object> data = (Map<String, Object>)dataSnapshot.getValue();
                        String state = data.get("status").toString();
                        if (state.equals("매우좋음")) {
                            verygoodList.add(state);
                        }else if (state.equals("좋음")) {
                            goodList.add(state);
                        }else if (state.equals("보통")) {
                            normalList.add(state);
                        }else if (state.equals("나쁨")) {
                            badList.add(state);
                        }else if (state.equals("매우나쁨")) {
                            verybadList.add(state);
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

        // 데이터 입력
        ArrayList<PieEntry> yValues = new ArrayList<>();

        // y 축에 데이터 값, 이름 설정
        Log.d(TAG, "onCreateView: " + verybadList.size());

        yValues.add(new PieEntry(verybadList.size(), "매우나쁨"));
        yValues.add(new PieEntry(badList.size(), "나쁨"));
        yValues.add(new PieEntry(normalList.size(), "보통"));
        yValues.add(new PieEntry(badList.size(), "좋음"));
        yValues.add(new PieEntry(verygoodList.size(), "매우좋음"));

        //설명란
        Description description = new Description();
        description.setText("행동 특성 통계.");
        description.setTextSize(15);
        mPiechart.setDescription(description);

        //mPiechart.animateY(1000, Easing.EasingOption.EaseInCubic);       //애니메이션 효과

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data2 = new PieData(dataSet);

        data2.setValueTextColor(Color.YELLOW);
        data2.setValueTextSize(10f);

        mPiechart.setData(data2);


        return view;
    }
}
