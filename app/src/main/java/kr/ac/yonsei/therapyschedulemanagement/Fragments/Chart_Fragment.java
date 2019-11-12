package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Chart_Fragment extends Fragment {

    public static Chart_Fragment newInstance(){
        Chart_Fragment f = new Chart_Fragment();
        return f;
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);


        return view;
    }
}
