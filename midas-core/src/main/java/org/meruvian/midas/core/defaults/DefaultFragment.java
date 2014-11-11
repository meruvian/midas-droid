package org.meruvian.midas.core.defaults;

import android.app.Fragment;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class DefaultFragment extends Fragment {

    public void replaceFragment(int res, Fragment fragment, boolean addBackStack) {
        if (addBackStack) {
            getFragmentManager().beginTransaction().replace(res, fragment).addToBackStack(null).commit();
        } else {
            getFragmentManager().beginTransaction().replace(res, fragment).commit();
        }
    }

    public void finish() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }
}
