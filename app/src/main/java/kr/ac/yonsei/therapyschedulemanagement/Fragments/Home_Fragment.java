package kr.ac.yonsei.therapyschedulemanagement.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Home_Fragment extends Fragment {

    public static Home_Fragment newInstance(){
        Home_Fragment f = new Home_Fragment();
        return f;
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        Intent intent = new Intent(getContext(), Test_Activity.class);
//        startActivity(intent);

        return view;
    }
}
