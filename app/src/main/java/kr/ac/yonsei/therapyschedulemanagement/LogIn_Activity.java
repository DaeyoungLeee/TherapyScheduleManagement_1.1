package kr.ac.yonsei.therapyschedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LogIn_Activity extends AppCompatActivity {
    TextView txt_signin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_signin = findViewById(R.id.txt_signin);

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signin = new Intent(getApplicationContext(), SignIn_Activity.class);
                startActivity(intent_signin);
            }
        });
    }
}
