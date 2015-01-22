package org.meruvian.midas.showcase.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.meruvian.midas.core.drawer.Navigation;
import org.meruvian.midas.core.drawer.NavigationDrawer;
import org.meruvian.midas.core.drawer.NavigationDrawerAdapter;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.fragment.category.CategorySubmitFragment;
import org.meruvian.midas.showcase.fragment.news.NewsSubmitFragment;
import org.meruvian.midas.showcase.fragment.PreferencesFragment;
import org.meruvian.midas.showcase.fragment.news.NewsViewFragment;
import org.meruvian.midas.showcase.gcm.RegisterGcmDevice;
import org.meruvian.midas.showcase.activity.social.SocialLoginActivity;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class MainActivity extends NavigationDrawer {
    private RegisterGcmDevice registerGcmDevice;

    private SharedPreferences preferences;

    @Override
    public void onViewCreated() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        registerGcmDevice = new RegisterGcmDevice() {
            @Override
            protected void storeRegistrationId(String id) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("gcm", id);
                editor.commit();
            }

            @Override
            public boolean hasRegister() {
                return preferences.contains("gcm");
            }

            @Override
            public void storeInServer(String id) {

            }
        };
        registerGcmDevice.register(this);
    }

    @Override
    public int iconHome() {
        return R.drawable.ic_home;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerGcmDevice.register(this);
    }

    @Override
    public Fragment mainFragment() {
        return new NewsViewFragment();
    }

    @Override
    public void navigationAdapter(NavigationDrawerAdapter adapter) {
        adapter.addNavigation(new Navigation("Dashboard", Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Create News", Navigation.NavigationType.MENU));
//        adapter.addNavigation(new Navigation("Create Category", Navigation.NavigationType.MENU));
        adapter.addNavigation(new Navigation("Social Login", Navigation.NavigationType.MENU));
    }

    @Override
    public void selectedItem(int position) {
        if (position == 0) {
            int backStack = getFragmentManager().getBackStackEntryCount();
            while (backStack > 0) {
                getFragmentManager().popBackStack();
            }
        } else if (position == 1) {
            if (preferences.contains("mervid") || preferences.contains("manual")) {
                replaceFragment(new NewsSubmitFragment(), "post news");
            } else {
                Toast.makeText(this, getString(R.string.warning_login_mervid), Toast.LENGTH_SHORT).show();
            }
//        } else if (position == 2) {
//            if (preferences.contains("mervid") || preferences.contains("manual")) {
//                replaceFragment(new CategorySubmitFragment(), "post news");
//            } else {
//                Toast.makeText(this, getString(R.string.warning_login_mervid), Toast.LENGTH_SHORT).show();
//            }
        } else if (position == 2) {
            startActivity(new Intent(this, SocialLoginActivity.class));
        }

        closeDrawer();
    }

    @Override
    public void onClickPreference() {
        replaceFragment(new PreferencesFragment(), "preference");
        closeDrawer();
    }

}
