package org.meruvian.midas.core.defaults;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.meruvian.midas.core.R;

import butterknife.ButterKnife;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public abstract class DefaultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout(), container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    protected abstract int layout();

    @Override
    public abstract void onViewCreated(View view, Bundle savedInstanceState);

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
