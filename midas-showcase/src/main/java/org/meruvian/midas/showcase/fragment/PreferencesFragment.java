package org.meruvian.midas.showcase.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import org.meruvian.midas.core.fragment.ThemePreference;
import org.meruvian.midas.showcase.R;

/**
 * Created by ludviantoovandi on 25/07/14.
 */
public class PreferencesFragment extends android.preference.PreferenceFragment {
    private Preference themes, rate, version;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        themes = (Preference) findPreference("pref_theme");
        rate = (Preference) findPreference("pref_rate");
        version = (Preference) findPreference("pref_version");

        themes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new ThemePreference()).addToBackStack(null).commit();
            return true;
            }
        });

        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=org.mutiaraiman.droid")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=org.mutiaraiman.droid")));
            }
            return false;
            }
        });

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setSummary("Version : " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
