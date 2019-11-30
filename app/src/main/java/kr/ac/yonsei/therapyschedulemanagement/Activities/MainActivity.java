package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.ac.yonsei.therapyschedulemanagement.Fragments.Calendar_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Chart_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Diary_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Home_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private FrameLayout fragment;
    private long backBtnTime = 0;
    private static int ONE_MINUTE = 5626;
    private AdView adView1;

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-6270688884891981~9770535273");
        AdRequest adRequest = new AdRequest.Builder().build();

        mDatabase = FirebaseDatabase.getInstance();
        mAuth =FirebaseAuth.getInstance();
        String email =  mAuth.getCurrentUser().getEmail();

        adView1 = findViewById(R.id.ad_adview1);
        adView1.loadAd(adRequest);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragment = findViewById(R.id.frame);
        int forlogincount =1; //2번 이상 로그인 시 별점 평가 요청
        int firstlogin= 0;
        mDatabase.getReference(email.replace(".","_")).child("login_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int count=dataSnapshot.getValue(Integer.class);
                    count++; //로그인 할때마다 카운트 증가
                    mDatabase.getReference(email.replace(".","_")).child("login_count").setValue(count);
                }
                catch (NullPointerException E) {
                    mDatabase.getReference(email.replace(".", "_")).child("login_count").setValue(0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.getReference(email.replace(".","_")).child("login_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int logincount = dataSnapshot.getValue(Integer.class);
                    if (forlogincount == logincount) {
                        AlertDialog.Builder givemescoreplz = new AlertDialog.Builder(MainActivity.this);
                        givemescoreplz.setTitle("평가를 해주세요!! 개발자에게 큰 도움이 됩니다.");
                        givemescoreplz.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(getApplicationContext(), "감사합니다~", Toast.LENGTH_SHORT).show();
                                Intent gotomarket = new Intent(Intent.ACTION_VIEW);
                                gotomarket.setData(Uri.parse("market://details?id=kr.ac.yonsei.therapyschedulemanagement"));
                                startActivity(gotomarket);
                            }
                        });
                        givemescoreplz.setNegativeButton("다음에", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "아쉽군요", Toast.LENGTH_SHORT);
                            }
                        });
                        givemescoreplz.show();
                    }


                }
                catch (NullPointerException K){
                    mDatabase.getReference(email.replace(".", "_")).child("login_count").setValue(0);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.getReference(email.replace(".","_")).child("login_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           try{
                int firstcount = dataSnapshot.getValue(Integer.class);
                if (firstlogin == firstcount) {
                    AlertDialog.Builder gotosetting = new AlertDialog.Builder(MainActivity.this);
                    gotosetting.setTitle("처음이시군요! 아이의 이름과 나이를 설정해주세요");
                    gotosetting.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MainActivity.this, Setting_Activity.class);
                            startActivity(intent);
                        }

                    });
                    gotosetting.show();
                }
            }
           catch (NullPointerException E){
               mDatabase.getReference(email.replace(".","_")).child("login_count").setValue(0);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //첫 화면
        if (savedInstanceState == null) {
            showFragment(Home_Fragment.newInstance());
        }
        //바텀 네비게이션 리스너 연결
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    /**
     * navigation bottom 버튼 클릭 시 동작 버튼
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        // 바텀 네비게이션 아이템 클릭 시 이동경로
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottomBarItemOne:
                    if (!item.isChecked())
                        showFragment(Home_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemSecond:
                    if (!item.isChecked())
                        showFragment(Calendar_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemThird:
                    if (!item.isChecked())
                        showFragment(Diary_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemFourth:
                    if (!item.isChecked())
                        showFragment(Chart_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemFifth:
                    Intent intent = new Intent(MainActivity.this, Setting_Activity.class);
                    startActivity(intent);
                    return false;
                default:
                    return false;
            }
        }
    };

    public void showFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
    }

    /**
     * 뒤로가기 버튼이 눌렸을 경우 동작
     */
    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (gapTime >= 0 && gapTime <= 2000) {
            super.onBackPressed();
            finishAffinity();
            System.runFinalization();
            finish();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }

      /* Intent endIntent = new Intent(getApplicationContext(), FinishAdvertisement_Activity.class);
       startActivity(endIntent);*/

    }
}
