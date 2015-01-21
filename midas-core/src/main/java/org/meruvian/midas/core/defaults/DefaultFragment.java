package org.meruvian.midas.core.defaults;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import org.meruvian.midas.core.R;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class DefaultFragment extends Fragment {

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content_frame, fragment, tag).addToBackStack(null).commit();
    }

    public void finish() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }
}
