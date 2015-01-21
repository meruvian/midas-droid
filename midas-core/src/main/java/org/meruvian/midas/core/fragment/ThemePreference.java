package org.meruvian.midas.core.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.meruvian.midas.core.R;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public class ThemePreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference blue, red, orange, green, purple, midas;

    SharedPreferences sessionPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_theme);

        sessionPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());

        blue = (CheckBoxPreference) findPreference("themes_blue");
        red = (CheckBoxPreference) findPreference("themes_red");
        orange = (CheckBoxPreference) findPreference("themes_orange");
        green = (CheckBoxPreference) findPreference("themes_green");
        purple = (CheckBoxPreference) findPreference("themes_purple");
        midas = (CheckBoxPreference) findPreference("themes_midas");

        blue.setOnPreferenceChangeListener(this);
        red.setOnPreferenceChangeListener(this);
        orange.setOnPreferenceChangeListener(this);
        purple.setOnPreferenceChangeListener(this);
        green.setOnPreferenceChangeListener(this);
        midas.setOnPreferenceChangeListener(this);

        initialize();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        SharedPreferences.Editor editor = sessionPreference.edit();

        if (preference.getKey().equals("themes_blue")) {
            editor.putInt("themes", R.style.DarkTheme_Blue);

            blue.setChecked(true);
            red.setChecked(false);
            orange.setChecked(false);
            green.setChecked(false);
            purple.setChecked(false);
            midas.setChecked(false);
        } else if (preference.getKey().equals("themes_red")) {
            editor.putInt("themes", R.style.DarkTheme_Red);

            blue.setChecked(false);
            red.setChecked(true);
            orange.setChecked(false);
            green.setChecked(false);
            purple.setChecked(false);
            midas.setChecked(false);
        } else if (preference.getKey().equals("themes_orange")) {
            editor.putInt("themes", R.style.DarkTheme_Orange);

            blue.setChecked(false);
            red.setChecked(false);
            orange.setChecked(true);
            green.setChecked(false);
            purple.setChecked(false);
            midas.setChecked(false);
        } else if (preference.getKey().equals("themes_green")) {
            editor.putInt("themes", R.style.DarkTheme_Green);

            blue.setChecked(false);
            red.setChecked(false);
            orange.setChecked(false);
            green.setChecked(true);
            purple.setChecked(false);
            midas.setChecked(false);
        } else if (preference.getKey().equals("themes_purple")) {
            editor.putInt("themes", R.style.DarkTheme_Purple);

            blue.setChecked(false);
            red.setChecked(false);
            orange.setChecked(false);
            green.setChecked(false);
            purple.setChecked(true);
            midas.setChecked(false);
        } else {
//            editor.putInt("themes", R.style.DarkTheme_Midas);

            blue.setChecked(false);
            red.setChecked(false);
            orange.setChecked(false);
            green.setChecked(false);
            midas.setChecked(true);
        }
        editor.commit();

        getActivity().finish();
        Intent intent = new Intent(getActivity(), getActivity().getClass());
        intent.putExtra("themes", true);
        getActivity().startActivity(intent);

        return true;
    }

    private void initialize() {
//        if (sessionPreference.getInt("themes", R.style.DarkTheme_Midas) == R.style.DarkTheme_Blue) {
//            blue.setChecked(true);
//            red.setChecked(false);
//            orange.setChecked(false);
//            green.setChecked(false);
//            purple.setChecked(false);
//        } else if (sessionPreference.getInt("themes", R.style.DarkTheme_Midas) == R.style.DarkTheme_Red) {
//            blue.setChecked(false);
//            red.setChecked(true);
//            orange.setChecked(false);
//            green.setChecked(false);
//            purple.setChecked(false);
//        } else if (sessionPreference.getInt("themes", R.style.DarkTheme_Midas) == R.style.DarkTheme_Orange){
//            blue.setChecked(false);
//            red.setChecked(false);
//            orange.setChecked(true);
//            green.setChecked(false);
//            purple.setChecked(false);
//        } else if (sessionPreference.getInt("themes", R.style.DarkTheme_Midas) == R.style.DarkTheme_Green){
//            blue.setChecked(false);
//            red.setChecked(false);
//            orange.setChecked(false);
//            green.setChecked(true);
//            purple.setChecked(false);
//        } else if (sessionPreference.getInt("themes", R.style.DarkTheme_Midas) == R.style.DarkTheme_Purple){
//            blue.setChecked(false);
//            red.setChecked(false);
//            orange.setChecked(false);
//            green.setChecked(false);
//            purple.setChecked(true);
//        } else {
//            midas.setChecked(true);
//            blue.setChecked(false);
//            red.setChecked(false);
//            orange.setChecked(false);
//            green.setChecked(false);
//            purple.setChecked(false);
//        }
    }
}
