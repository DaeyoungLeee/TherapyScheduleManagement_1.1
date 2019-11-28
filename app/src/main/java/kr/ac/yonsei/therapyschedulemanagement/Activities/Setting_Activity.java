package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.prefs.Preferences;

import kr.ac.yonsei.therapyschedulemanagement.PushAlert.JobSchedulerStart;
import kr.ac.yonsei.therapyschedulemanagement.R;

public class Setting_Activity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ListPreference chartColorList, alertList, alertNoteList;
    private SwitchPreference pushSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        chartColorList = (ListPreference) findPreference("chart_color");
        alertList = (ListPreference) findPreference("alert_time");
        pushSwitch = (SwitchPreference) findPreference("push_alert");
        alertNoteList = (ListPreference) findPreference("activity_time");

        chartColorList.setOnPreferenceChangeListener(this);
        alertList.setOnPreferenceChangeListener(this);
        alertNoteList.setOnPreferenceChangeListener(this);
        pushSwitch.setSwitchTextOn("푸시알람이 울립니다.");
        pushSwitch.setOnPreferenceChangeListener(this);

        chartColorList.setSummary(chartColorList.getValue());
        alertList.setSummary(alertList.getValue());
        alertNoteList.setSummary(alertNoteList.getValue());

        String getmail =  mAuth.getCurrentUser().getEmail();//사용자 메일 받아옴

        Preference alldelete = (Preference) findPreference("all_delete");
        alldelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder rdelete = new AlertDialog.Builder(Setting_Activity.this);

                rdelete.setTitle("정말로 지우시려면 'delete'를 입력해주세요");
                final EditText confirm1 = new EditText(Setting_Activity.this);
                rdelete.setView(confirm1);
                rdelete.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                rdelete.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(confirm1.getText().toString().equals("delete")) {


                            mDatabase.getReference(getmail.replace(".", "_")).removeValue(); //데이터 삭제
                            mDatabase.getReference(getmail.replace(".", "_")).child("dbvalue").setValue("0");

                            Toast.makeText(Setting_Activity.this, "모든 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Setting_Activity.this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                            confirm1.setText(" ");
                        }
                    }
                });
                rdelete.show();

                return true;
            }
        });











        Preference myPref = (Preference) findPreference("log_out");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                Toast.makeText(Setting_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                // 완전종료
                mAuth.signOut();
                finishAffinity();
                Intent intent_login = new Intent(getApplicationContext(), LogIn_Activity.class);
                startActivity(intent_login);
                return true;
            }
        });

    }

    /** 설정 하면 설정값 summary 보여주기 */
    private static void bindSummaryValue(Preference preference){
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(),""));

    }
    //설정 하면 설정값 summary 보여주기 listener
    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            // List Preference에 대한 설정
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                //set summary to reflect new value
                preference.setSummary(index > 0
                        ? listPreference.getEntries()[index]
                        :null);
            }


            return true;
        }
    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == chartColorList){
            chartColorList.setSummary(String.valueOf(newValue));
        }else if (preference == alertList) {
            alertList.setSummary(String.valueOf(newValue));
        }else if (preference == pushSwitch) {
            if (!pushSwitch.isChecked()) {
                JobSchedulerStart.start(getApplicationContext());
                Toast.makeText(this, "설정 시간에 푸시알람이 울립니다.", Toast.LENGTH_SHORT).show();
            }else {
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
                dispatcher.cancelAll();
                Toast.makeText(this, "푸시알람이 해지되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }else if (preference == alertNoteList) {
            alertNoteList.setSummary(String.valueOf(newValue));
        }

        return true;
    }
}
