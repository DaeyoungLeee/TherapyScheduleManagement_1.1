package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragment = findViewById(R.id.frame);

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

    }
}
