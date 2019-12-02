package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class FinishAdvertisement_Activity extends Activity {

    private AdView adView;
    private Button btn_cancel, btn_finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_front_advertisement);

        adView = findViewById(R.id.ad_adview_front);
        btn_cancel = findViewById(R.id.btn_ad_cancel);
        btn_finish = findViewById(R.id.btn_ad_finish);

        MobileAds.initialize(this, "a-app-pub-6270688884891981~9770535273");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.runFinalization();
                finish();
            }
        });
    }

}
