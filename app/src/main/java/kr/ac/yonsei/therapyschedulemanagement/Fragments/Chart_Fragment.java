package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Chart_Fragment extends Fragment {

    private PieChart mPiechart;

    public static Chart_Fragment newInstance(){
        Chart_Fragment f = new Chart_Fragment();
        return f;
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        mPiechart = view.findViewById(R.id.pieChart);
        mPiechart.setUsePercentValues(true);
        mPiechart.getDescription().setEnabled(false);
        //offset 설정
        mPiechart.setExtraOffsets(5, 10, 5, 5);

        mPiechart.setDragDecelerationFrictionCoef(0.95f);               //돌아가는 부드러움 설정

        mPiechart.setDrawHoleEnabled(false);                             //여러 효과
        mPiechart.setHoleColor(Color.WHITE);
        mPiechart.setTransparentCircleRadius(61f);

        // 데이터 입력
        ArrayList<PieEntry> yValues = new ArrayList<>();

        // y 축에 데이터 값, 이름 설정
        yValues.add(new PieEntry(34f, "매우나쁨"));
        yValues.add(new PieEntry(31f, "나쁨"));
        yValues.add(new PieEntry(54f, "보통"));
        yValues.add(new PieEntry(43f, "좋음"));
        yValues.add(new PieEntry(56f, "매우좋음"));

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
