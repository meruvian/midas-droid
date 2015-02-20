package org.meruvian.midas.core.defaults;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by ludviantoovandi on 20/02/15.
 */
public class DefaultHolder {
    public DefaultHolder(View view) {
        ButterKnife.inject(this, view);
    }
}
