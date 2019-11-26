package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.yonsei.therapyschedulemanagement.R;

import static kr.ac.yonsei.therapyschedulemanagement.Fragments.Chart_Fragment.staticMonth;
import static kr.ac.yonsei.therapyschedulemanagement.Fragments.Chart_Fragment.staticYear;
import static kr.ac.yonsei.therapyschedulemanagement.Fragments.Diary_Fragment.linear_empty;

public class ContentsPopup_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "DiaryPopup";

    String save_year;
    String save_month;
    String save_day;
    private TextView txt_today;
    private Button btn_okay, btn_cancel, btn_delete;
    private EditText edt_coments;
    private RadioButton radio_very_bad, radio_bad, radio_soso, radio_good, radio_very_good;
    private String status = null;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_diary_popup);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        txt_today = findViewById(R.id.txt_diary_date);
        btn_cancel = findViewById(R.id.btn_diary_cancel);
        btn_okay = findViewById(R.id.btn_diary_okay);
        btn_delete = findViewById(R.id.btn_diary_delete);
        edt_coments = findViewById(R.id.edt_conments);
        radio_very_good = findViewById(R.id.radio_very_good);
        radio_good = findViewById(R.id.radio_good);
        radio_soso = findViewById(R.id.radio_soso);
        radio_bad = findViewById(R.id.radio_bad);
        radio_very_bad = findViewById(R.id.radio_very_bad);

        radio_very_good.setOnClickListener(this);
        radio_good.setOnClickListener(this);
        radio_soso.setOnClickListener(this);
        radio_bad.setOnClickListener(this);
        radio_very_bad.setOnClickListener(this);

        Intent intent = getIntent();

        // 날짜 받아와서 뿌려주기
        save_year = staticYear;
        save_month = staticMonth;
        save_day = intent.getStringExtra("CHART_DAY");
        txt_today.setText(save_year + "년 " + save_month + "월 " + save_day + "일");

        try {
            // 기존 내용이 있으면 보여주기
            mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                    .child("Diary")
                    .child(save_year + "/" + save_month + "/" + save_day)
                    .child("status")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String status2 = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "onDataChange: " + status);
                            if (status2 != null) {
                                if (status2.equals("매우좋음")) {
                                    radio_very_good.setChecked(true);
                                    status = "매우좋음";
                                } else if (status2.equals("좋음")) {
                                    radio_good.setChecked(true);
                                    status = "좋음";
                                } else if (status2.equals("보통")) {
                                    radio_soso.setChecked(true);
                                    status = "보통";
                                } else if (status2.equals("나쁨")) {
                                    radio_bad.setChecked(true);
                                    status = "나쁨";
                                } else if (status2.equals("매우나쁨")) {
                                    radio_very_bad.setChecked(true);
                                    status = "매우나쁨";
                                } else {

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                    .child("Diary")
                    .child(save_year + "/" + save_month + "/" + save_day)
                    .child("contents")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            edt_coments.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {

        }

        // 취소버튼
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 삭제버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ContentsPopup_Activity.this);
                builder.setTitle("경고");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    // 삭제
                                    mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                                            .child("Diary")
                                            .child(save_year + "/" + save_month + "/" + save_day)
                                            .removeValue();
                                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
                                    linear_empty.setVisibility(View.VISIBLE);
                                } catch (Exception e) {

                                }

                                finish();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();

            }
        });
        // 저장버튼
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + status);
                if (status != null) {
                    // 데이터베이스 일별 저장
                    mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                            .child("Diary")
                            .child(save_year + "/" + save_month + "/" + save_day)
                            .child("date")
                            .setValue(save_day);

                    mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                            .child("Diary")
                            .child(save_year + "/" + save_month + "/" + save_day)
                            .child("status")
                            .setValue(status);

                    mDatabase.getReference(mAuth.getCurrentUser().getEmail().replace(".", "_"))
                            .child("Diary")
                            .child(save_year + "/" + save_month + "/" + save_day)
                            .child("contents")
                            .setValue(edt_coments.getText().toString());

                    Toast.makeText(ContentsPopup_Activity.this, "소중한 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ContentsPopup_Activity.this, "상태를 체크해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.radio_very_good:
                radio_very_bad.setChecked(false);
                radio_bad.setChecked(false);
                radio_good.setChecked(false);
                radio_soso.setChecked(false);
                status = "매우좋음";
                break;
            case R.id.radio_good:
                radio_very_bad.setChecked(false);
                radio_bad.setChecked(false);
                radio_soso.setChecked(false);
                radio_very_good.setChecked(false);
                status = "좋음";
                break;
            case R.id.radio_soso:
                radio_very_bad.setChecked(false);
                radio_bad.setChecked(false);
                radio_good.setChecked(false);
                radio_very_good.setChecked(false);
                status = "보통";
                break;
            case R.id.radio_bad:
                radio_very_bad.setChecked(false);
                radio_good.setChecked(false);
                radio_soso.setChecked(false);
                radio_very_good.setChecked(false);
                status = "나쁨";
                break;
            case R.id.radio_very_bad:
                radio_bad.setChecked(false);
                radio_good.setChecked(false);
                radio_soso.setChecked(false);
                radio_very_good.setChecked(false);
                status = "매우나쁨";
                break;
        }
        Log.d(TAG, "onClick: status" + status);
    }
}
