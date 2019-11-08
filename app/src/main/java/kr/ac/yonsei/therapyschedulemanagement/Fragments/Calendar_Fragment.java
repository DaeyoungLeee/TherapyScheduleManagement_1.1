package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Calendar_Fragment extends Fragment {

    private static String TAG = "Fragment2";
    // UI elements
    FloatingActionButton btn_add_schedule;
    CalendarView calendarView;
    Calendar calendar_date;
    SlidingUpPanelLayout sliding_layout;
    LinearLayout linearLayout;

    public static Calendar_Fragment newInstance(){
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

        // 슬라이딩 레이아웃 리스너
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
                }else if (newState.toString().equals("COLLAPSED")) {
                    calendarView.bringToFront();
                }
            }
        });

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                calendar_date = eventDay.getCalendar();


            }
        });

        btn_add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events.add(new EventDay(calendar_date, R.drawable.dot_red_icon));
                calendarView.setEvents(events);

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
