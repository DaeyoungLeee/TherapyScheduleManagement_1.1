package kr.ac.yonsei.therapyschedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.ac.yonsei.therapyschedulemanagement.Fragments.Calendar_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Chart_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Diary_Fragment;
import kr.ac.yonsei.therapyschedulemanagement.Fragments.Home_Fragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FrameLayout fragment;

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

    /** navigation bottom 버튼 클릭 시 동작 버튼 */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        // 바텀 네비게이션 아이템 클릭 시 이동경로
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottomBarItemOne:
                    showFragment(Home_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemSecond:
                    showFragment(Calendar_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemThird:
                    showFragment(Diary_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemFourth:
                    showFragment(Chart_Fragment.newInstance());
                    return true;
                case R.id.bottomBarItemFifth:

                    return false;
                default:
                    return false;
            }
        }
    };

    public void showFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, f).commit();
    }


}
