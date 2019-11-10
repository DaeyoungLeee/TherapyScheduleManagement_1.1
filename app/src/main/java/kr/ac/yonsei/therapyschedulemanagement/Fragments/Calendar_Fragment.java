package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.ac.yonsei.therapyschedulemanagement.Popup_Activity;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Calendar_Fragment extends Fragment {

    private static String TAG = "Fragment2";
    // UI elements
    FloatingActionButton btn_add_schedule;
    CalendarView calendarView;
    Calendar calendar_date;
    SlidingUpPanelLayout sliding_layout;
    LinearLayout linearLayout;
    int date, month, year;

    public static Calendar_Fragment newInstance() {
        Calendar_Fragment f = new Calendar_Fragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        List<EventDay> events = new ArrayList<>();

        calendarView = view.findViewById(R.id.calendarView);
        btn_add_schedule = view.findViewById(R.id.btn_add);
        sliding_layout = view.findViewById(R.id.sliding_layout);
        linearLayout = view.findViewById(R.id.linearLayout);

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
                Log.d(TAG, "onPanelSlide: ");
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
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                date = eventDay.getCalendar().getTime().getDate();
                year = eventDay.getCalendar().getTime().getYear();
                year = year - 100 + 2000;
                month = eventDay.getCalendar().getTime().getMonth();
                Log.d(TAG, "onDayClick: " + eventDay.getCalendar().getTime().getYear());

            }

        });

        /** Floating 추가 버튼 클릭 동작 */
        btn_add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Popup_Activity.class);
                intent.putExtra("Date", year + "년 " + month + "월 " + date + "일");
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

}
