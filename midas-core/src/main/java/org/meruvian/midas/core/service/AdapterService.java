package org.meruvian.midas.core.service;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ludviantoovandi on 09/09/14.
 */
public interface AdapterService<L, H> {
    public void add(L object);

    public void addAll(List<L> objects);

    public void clear();
}
