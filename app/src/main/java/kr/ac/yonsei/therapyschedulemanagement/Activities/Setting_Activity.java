package kr.ac.yonsei.therapyschedulemanagement.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import kr.ac.yonsei.therapyschedulemanagement.R;

public class Setting_Activity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

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
}
