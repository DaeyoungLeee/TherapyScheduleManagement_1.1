package kr.ac.yonsei.therapyschedulemanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn_Activity extends AppCompatActivity {
    // 로깅 태그
    private static String TAG = "SignIn_Activity";

    // Firebase 객체 생성
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    // UI 객체 생성
    private EditText edt_member_userName;
    private EditText edt_member_userEmail;
    private EditText edt_member_userPassword1, edt_member_userPassword2;
    private Button btn_member_finish;
    private RadioGroup radioGroup_sex;
    private RadioButton  radio_man, radio_woman;
    private RelativeLayout birth_data;
    private ProgressDialog dialog;

    // 데이트 피커 관련
    private TextView userBirth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar calendar;
    private int year, month, day;
    private int set_year, set_month, set_day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        edt_member_userName = findViewById(R.id.edt_member_userName);
        edt_member_userEmail = findViewById(R.id.edt_member_userEmail);
        edt_member_userPassword1 = findViewById(R.id.edt_member_userPassword);
        edt_member_userPassword2 = findViewById(R.id.edt_member_userPassword2);
        btn_member_finish = findViewById(R.id.btn_member_finish);
        radioGroup_sex = findViewById(R.id.radioGroup);
        radio_man = findViewById(R.id.radio_man);
        radio_woman = findViewById(R.id.radio_woman);
        birth_data = findViewById(R.id.birth_all);
        userBirth = findViewById(R.id.txt_member_userBirth);

        dialog = new ProgressDialog(this);

        // 비밀번호 첫 번 째 받을 때 검증
        edt_member_userPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = edt_member_userPassword1.getText().toString();
                if (!password.equals("")) {
                    if (password.length() >= 8) {
                        check_validation(password);
                    }else {
                        edt_member_userPassword1.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 비밀번호 두 번째 비교 (첫 번쨰 입력과 같은지)
        edt_member_userPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password1 = edt_member_userPassword1.getText().toString();
                String password2 = edt_member_userPassword2.getText().toString();
                if (password1.equals(password2)) {
                    edt_member_userPassword2.setTextColor(Color.parseColor("#ff6edf"));
                }else {
                    edt_member_userPassword2.setTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 생일 입력
        birth_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //데이트피커 보여줘서 생일 선택
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SignIn_Activity.this,
                        AlertDialog.THEME_HOLO_LIGHT, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        // 데이트 피커 열고 확인했을 때
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                month = month + 1;
                userBirth.setText(year + "년 " + month + "월 " + dayOfMonth  +"일");
                set_year = year;
                set_month = month;
                set_day = dayOfMonth;
            }
        };
        // 회원가입 버튼 클릭시
        btn_member_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_member_userEmail.getText().toString();
                String password = edt_member_userPassword1.getText().toString();
                String password_check = edt_member_userPassword2.getText().toString();
                showProgressDialog();
                if (!password.equals("")) {
                    if (!password.equals(password_check)) {
                        Toast.makeText(SignIn_Activity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else if (password.equals(password_check) && password.length() >= 8) {
                        // 성별 체크
                        if (radio_man.isChecked() || radio_woman.isChecked()) {
                            if (!edt_member_userName.equals("")){
                                if (String.valueOf(set_year) != null){
                                    check_validation_login(email, password);
                                }else {
                                    Toast.makeText(SignIn_Activity.this, "생년월일을 입력해주세요", Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(SignIn_Activity.this, "이름을 입력해주새요", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }else {
                            Toast.makeText(SignIn_Activity.this, "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else if (password.length() < 8) {
                        Toast.makeText(SignIn_Activity.this, "비밀번호는 8글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else {
                    Toast.makeText(SignIn_Activity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }            }
        });
    }

    // 비밀번호 유효성 검사
    private void check_validation(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String regExp_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
        //String regExp_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(regExp_symbol);
        //Pattern pattern_alpha = Pattern.compile(regExp_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
        //Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (matcher_symbol.find()) {
            edt_member_userPassword1.setTextColor(Color.parseColor("#ff6edf"));
        }
    }

    // 비밀번호 유효성 검사
    private void check_validation_login(String email, String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String regExp_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
        //String regExp_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(regExp_symbol);
        //Pattern pattern_alpha = Pattern.compile(regExp_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
        //Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (matcher_symbol.find()) {
            email_signIn(email, password);
        }else {
            Toast.makeText(this, "이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    private void email_signIn(final String email, String password) {
        final EditText edt_name;
        final String userName;

        edt_name = findViewById(R.id.edt_member_userName);
        userName = edt_name.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // token 저장
                            UserData userData = new UserData();
                            userData.fcmToken = FirebaseInstanceId.getInstance().getToken();
                            userData.userName = userName;

                            // RealTime Firebase database에 유저 정보 저장
                            mRef = mDatabase.getReference(email.replace(".", "_"));

                            mRef.child("token").setValue(userData.getFcmToken());
                            mRef.child("user_name").setValue(userData.getUserName());

                            Toast.makeText(SignIn_Activity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(SignIn_Activity.this, "이미 가입된 아이디이거나 이메일 양식이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

    }

    private void showProgressDialog() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("잠시 기다려주세요.");
        dialog.show();
    }

}
