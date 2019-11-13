package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Intro_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        Handler handler = new Handler(); // 객체생성
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intro_Activity.this, LogIn_Activity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);           // 몇 초간 띄우고 다음 액티비티로 넘어갈지 결정
    }
}
