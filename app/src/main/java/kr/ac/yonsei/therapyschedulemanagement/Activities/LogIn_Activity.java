package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class LogIn_Activity extends AppCompatActivity {
    // 로그 태깅
    private static final String TAG = "LOGIN_ACTIVITY";
    // UI 객체
    private TextView txt_signin;
    private ProgressDialog dialog;
    private Button logIn_button;
    private EditText id, pw;
    private CheckBox check_saveId, check_auto_login;
    private String email, password;
    private Animation from_bottom;
    private CardView card_login;
    private SignInButton btn_googleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    // Firebase 객체
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_signin = findViewById(R.id.txt_signin);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);

        id = findViewById(R.id.edt_login_id);
        pw = findViewById(R.id.edt_login_password);

        check_saveId = findViewById(R.id.check_saveId);
        check_auto_login = findViewById(R.id.check_autoLogin);
        card_login = findViewById(R.id.card_login);
        btn_googleLogin = findViewById(R.id.btn_google_login);
        from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);

        card_login.setAnimation(from_bottom);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        // 구글 로그인 버튼
        btn_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // 로그인 변화 리스너
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent_signin = new Intent(LogIn_Activity.this, MainActivity.class);
                    startActivity(intent_signin);
                    finish();
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // 저장된 값 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_STATE", MODE_PRIVATE);
        String saved_email = sharedPreferences.getString("EMAIL", "");
        String saved_password = sharedPreferences.getString("PASSWORD", "");
        boolean id_check = sharedPreferences.getBoolean("CHECKBOX_ID", false);
        boolean auto_login_check = sharedPreferences.getBoolean("CHECKBOX_AUTO_LOGIN", false);

        id.setText(saved_email);
        pw.setText(saved_password);
        check_saveId.setChecked(id_check);
        check_auto_login.setChecked(auto_login_check);

        if (check_auto_login.isChecked()) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }

        //로그인버튼 클릭
        logIn_button = findViewById(R.id.btn_login);
        logIn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 값 가져오기
                email = id.getText().toString();
                password = pw.getText().toString();

                // 입력 값이 null일 때 알려주기
                if (email.equals("")) {
                    Toast.makeText(LogIn_Activity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(LogIn_Activity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (email.equals("") && password.equals("")) {
                    Toast.makeText(LogIn_Activity.this, "입력을 확인해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    // 예외상황이 없으면 로그인 시도!
                    showProgressDialog();
                    dialog.show();
                    log_In();
                }

            }
        });

        // 회원가입 버튼 클릭
        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signin = new Intent(getApplicationContext(), SignIn_Activity.class);
                startActivity(intent_signin);
            }
        });
    }

    private void log_In() {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDatabase = FirebaseDatabase.getInstance();
                            mAuth = FirebaseAuth.getInstance();
                            String getmail = email.replace(".", "_");

                            Intent intent_goMain = new Intent(LogIn_Activity.this, MainActivity.class);
                            startActivity(intent_goMain);
                            Log.d(TAG, "onComplete: 호출");

                            try{
                                mDatabase.getReference(getmail.replace(".","_")).child("login_count");
                            }
                            catch (NullPointerException E ){

                                mDatabase.getReference(getmail.replace(".","_")).child("login_count").setValue(0);
                            }
                            mDatabase.getReference(getmail.replace(".","_")).child("login_count").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override

                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        int count=dataSnapshot.getValue(Integer.class);
                                        count++; //로그인 할때마다 카운트 증가
                                        mDatabase.getReference(getmail.replace(".","_")).child("login_count").setValue(count);
                                    }
                                    catch (NullPointerException K)
                                    {
                                        mDatabase.getReference(getmail.replace(".","_")).child("login_count").setValue(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // 아이디 저장이 체크되어있으면 저장
                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_STATE", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // 아이디 저장에만 체크 되어있을 경우
                            if (check_saveId.isChecked() && !check_auto_login.isChecked()) {
                                editor.putString("EMAIL", id.getText().toString());
                                editor.putBoolean("CHECKBOX_ID", true);
                                editor.putBoolean("CHECKBOX_AUTO_LOGIN", false);
                            }
                            // 자동 로그인만 체크 되어있는 경우
                            else if (!check_saveId.isChecked() && check_auto_login.isChecked()) {
                                editor.putString("EMAIL", id.getText().toString());
                                editor.putString("PASSWORD", pw.getText().toString());
                                editor.putBoolean("CHECKBOX_ID", true);
                                editor.putBoolean("CHECKBOX_AUTO_LOGIN", true);
                            }
                            // 둘 다 체크되어있지 않은 경우
                            else if (!check_auto_login.isChecked() && !check_saveId.isChecked()) {
                                editor.putString("EMAIL", "");
                                editor.putString("PASSWORD", "");
                                editor.putBoolean("CHECKBOX_ID", false);
                                editor.putBoolean("CHECKBOX_AUTO_LOGIN", false);
                            }
                            // 둘 다 체크되어있는 경우
                            else {
                                editor.putString("EMAIL", id.getText().toString());
                                editor.putString("PASSWORD", pw.getText().toString());
                                editor.putBoolean("CHECKBOX_ID", true);
                                editor.putBoolean("CHECKBOX_AUTO_LOGIN", true);
                            }
                            editor.apply();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void showProgressDialog() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("잠시 기다려주세요.");

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 로그인이 성공해서 메인으로 넘어가면 다이얼로그 없애기
        dialog.dismiss();
    }

    // 구글 로그인
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showProgressDialog();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                // mDatabase.getReference().setValue(mUser.getEmail().replace(".", "_"));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                            }

                            // ...
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
