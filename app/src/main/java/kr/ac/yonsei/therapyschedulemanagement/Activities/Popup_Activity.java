package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import it.emperor.animatedcheckbox.AnimatedCheckBox;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Popup_Activity extends Activity {

    private static final String SENSORY_PAIN = "SensoryPain";
    private static final String LANGUAGE = "Language";
    private static final String PLAY = "Play";
    private static final String PHYSICAL = "Physical";
    private static final String OCCUPATION = "Occupation";
    private static final String DB_START_TIME = "start_time";
    private static final String DB_END_TIME = "end_time";
    private static final String DB_THERAPY_KIND = "therapy";

    // Firebase 객체 생성
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    // 내가 로그인한 디바이스 이메일 가져오기
    String db_email;

    // UI 객체 생성
    TextView txt_today;
    Button btn_okay, btn_cancel;
    TimePicker time_start, time_end;
    AnimatedCheckBox check_sensory;
    AnimatedCheckBox check_language;
    AnimatedCheckBox check_play;
    AnimatedCheckBox check_physical;
    AnimatedCheckBox check_occupation;

    int save_year;
    int save_month;
    int save_day;
    int start_hour, start_minute;
    int end_hour, end_minute;
    // 현재 시간
    long now = System.currentTimeMillis();
    Date nowDate = new Date(now);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activiry_calendar_popup);
        //Firebase 초기화
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        txt_today = findViewById(R.id.txt_today);
        btn_okay = findViewById(R.id.btn_okay);
        btn_cancel = findViewById(R.id.btn_cancel);
        time_end = findViewById(R.id.time_end);
        time_start = findViewById(R.id.time_start);
        check_sensory = findViewById(R.id.check_sensory);
        check_language = findViewById(R.id.check_language);
        check_play = findViewById(R.id.check_play);
        check_physical = findViewById(R.id.check_physical);
        check_occupation = findViewById(R.id.check_occupation);

        check_sensory.setChecked(false);
        check_language.setChecked(false);
        check_play.setChecked(false);
        check_physical.setChecked(false);
        check_occupation.setChecked(false);

        db_email = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // 날짜 받아와서 뿌려주기
        Intent intent = getIntent();
        String date = intent.getStringExtra("Date");
        txt_today.setText(date);
        save_year = intent.getIntExtra("Year", 2019);
        save_month = intent.getIntExtra("Month", 1);
        save_day = intent.getIntExtra("Day", 1);

        // 초기 시간 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time_start.setHour(nowDate.getHours());
        }

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 처리해주고 종료
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    start_hour = time_start.getHour();
                    start_minute = time_start.getMinute();
                    end_hour = time_end.getHour();
                    end_minute = time_end.getMinute();
                    if (start_hour > end_hour || ((start_hour == end_hour) && start_minute > end_minute)) {
                        Toast.makeText(Popup_Activity.this, "시간 설정이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        String db_date;
                        String db_start_time;
                        String db_end_time;
                        if (save_day < 10) {
                            db_date = save_year + "/" + save_month + "/" + "0" + save_day;
                            db_start_time = start_hour + ":" + start_minute;
                            db_end_time = end_hour + ":" + end_minute;
                        } else {
                            db_date = save_year + "/" + save_month + "/" + save_day;
                            db_start_time = start_hour + ":" + start_minute;
                            db_end_time = end_hour + ":" + end_minute;
                        }


                        // 감각통증
                        if (check_sensory.isChecked()) {
                            // 테라피 종류 저장
                            DB_save(db_date, DB_THERAPY_KIND, SENSORY_PAIN);
                            // 시작시간 저장
                            DB_save(db_date, DB_START_TIME, db_start_time);
                            // 종료시간 저장
                            DB_save(db_date, DB_END_TIME, db_end_time);
                            finish();
                        } else if (check_language.isChecked()) {
                            // 테라피 종류 저장
                            DB_save(db_date, DB_THERAPY_KIND, LANGUAGE);
                            // 시작시간 저장
                            DB_save(db_date, DB_START_TIME, db_start_time);
                            // 종료시간 저장
                            DB_save(db_date, DB_END_TIME, db_end_time);
                            finish();
                        } else if (check_play.isChecked()) {
                            // 테라피 종류 저장
                            DB_save(db_date, DB_THERAPY_KIND, PLAY);
                            // 시작시간 저장
                            DB_save(db_date, DB_START_TIME, db_start_time);
                            // 종료시간 저장
                            DB_save(db_date, DB_END_TIME, db_end_time);
                            finish();
                        } else if (check_physical.isChecked()) {
                            // 테라피 종류 저장
                            DB_save(db_date, DB_THERAPY_KIND, PHYSICAL);
                            // 시작시간 저장
                            DB_save(db_date, DB_START_TIME, db_start_time);
                            // 종료시간 저장
                            DB_save(db_date, DB_END_TIME, db_end_time);
                            finish();
                        } else if (check_occupation.isChecked()) {
                            // 테라피 종류 저장
                            DB_save(db_date, DB_THERAPY_KIND, OCCUPATION);
                            // 시작시간 저장
                            DB_save(db_date, DB_START_TIME, db_start_time);
                            // 종료시간 저장
                            DB_save(db_date, DB_END_TIME, db_end_time);
                            finish();
                        } else {
                            Toast.makeText(Popup_Activity.this, "일정을 체크해주세요!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Popup_Activity.this, "마시멜로버전 이상 가능", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 저장
    private void DB_save(String date, String kind, String time) {
        mDatabase.getReference(db_email)
                .child("Calendar")
                .child(date)
                .child("Therapy_schedule")
                .child(kind)
                .push()
                .child(kind)
                .setValue(time);
    }

    public void onclick1(View view) {
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        if (check_sensory.isChecked()) {
            check_language.setChecked(false, true);
            check_play.setChecked(false, true);
            check_physical.setChecked(false, true);
            check_occupation.setChecked(false, true);
        }
    }
}
