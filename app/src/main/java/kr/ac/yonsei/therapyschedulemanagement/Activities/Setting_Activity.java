package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
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
    private EditTextPreference edt_red, edt_blue, edt_yellow, edt_green, edt_orange;
    private EditTextPreference edt_kidName, edt_kidAge;

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
        edt_red = (EditTextPreference) findPreference("edtpref_red");
        edt_blue = (EditTextPreference) findPreference("edtpref_blue");
        edt_yellow = (EditTextPreference) findPreference("edtpref_yellow");
        edt_green = (EditTextPreference) findPreference("edtpref_green");
        edt_orange = (EditTextPreference) findPreference("edtpref_orange");
        edt_kidName = (EditTextPreference) findPreference("edtpref_kidName");
        edt_kidAge = (EditTextPreference) findPreference("edtpref_kidAge");

        chartColorList.setOnPreferenceChangeListener(this);
        alertList.setOnPreferenceChangeListener(this);
        alertNoteList.setOnPreferenceChangeListener(this);
        pushSwitch.setSwitchTextOn("푸시알람이 울립니다.");
        pushSwitch.setOnPreferenceChangeListener(this);
        edt_red.setOnPreferenceChangeListener(this);
        edt_blue.setOnPreferenceChangeListener(this);
        edt_yellow.setOnPreferenceChangeListener(this);
        edt_green.setOnPreferenceChangeListener(this);
        edt_orange.setOnPreferenceChangeListener(this);
        edt_kidName.setOnPreferenceChangeListener(this);
        edt_kidAge.setOnPreferenceChangeListener(this);

        chartColorList.setSummary(chartColorList.getValue());
        alertList.setSummary(alertList.getValue());
        alertNoteList.setSummary(alertNoteList.getValue());
        edt_red.setSummary(edt_red.getText());
        edt_blue.setSummary(edt_blue.getText());
        edt_yellow.setSummary(edt_yellow.getText());
        edt_green.setSummary(edt_green.getText());
        edt_orange.setSummary(edt_orange.getText());
        edt_kidName.setSummary(edt_kidName.getText());
        edt_kidAge.setSummary(edt_kidAge.getText());

        String getmail = mAuth.getCurrentUser().getEmail();//사용자 메일 받아옴

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
                        if (confirm1.getText().toString().equals("delete")) {

                            mDatabase.getReference(getmail.replace(".", "_")).removeValue(); //데이터 삭제
                            mDatabase.getReference(getmail.replace(".", "_")).child("dbvalue").setValue("0");
                            mDatabase.getReference(getmail.replace(".", "_")).child("login_count").setValue(9);
                            Toast.makeText(Setting_Activity.this, "모든 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
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
                AlertDialog.Builder confirmlogout = new AlertDialog.Builder(Setting_Activity.this);
                confirmlogout.setTitle("정말로 로그아웃 하시겠습니까?");
                confirmlogout.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Setting_Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        // 완전종료
                        mAuth.signOut();
                        finishAffinity();
                        Intent intent_login = new Intent(getApplicationContext(), LogIn_Activity.class);
                        startActivity(intent_login);

                    }
                });
                confirmlogout.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                confirmlogout.show();
                //open browser or intent here
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
        }else if (preference == edt_red) {
            edt_red.setSummary(String.valueOf(newValue));
        }else if (preference == edt_blue) {
            edt_blue.setSummary(String.valueOf(newValue));
        }else if (preference == edt_yellow) {
            edt_yellow.setSummary(String.valueOf(newValue));
        }else if (preference == edt_green) {
            edt_green.setSummary(String.valueOf(newValue));
        }else if (preference == edt_orange) {
            edt_orange.setSummary(String.valueOf(newValue));
        }else if (preference == edt_kidName) {
            edt_kidName.setSummary(String.valueOf(newValue));
        }else if (preference == edt_kidAge) {
            edt_kidAge.setSummary(String.valueOf(newValue));
        }

        return true;
    }
}
