package org.meruvian.midas.core.fragment;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.meruvian.midas.core.R;
import org.meruvian.midas.core.defaults.DefaultFragment;

public abstract class DefaultAboutFragment extends DefaultFragment {
	private TextView version;
	private WebView webView;
    private ImageView logo;

    @Override
    protected int layout() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        version = (TextView) view.findViewById(R.id.version);
        webView = (WebView) view.findViewById(R.id.webview);
        logo = (ImageView) view.findViewById(R.id.logo);

        webView.loadUrl(about());
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(pInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        logo(resourceLogo());
    }

    public abstract int resourceLogo();

    private void logo(int res) {
        if (res == 0) {
            logo.setImageResource(R.drawable.midas_merv);
        } else {
            logo.setImageResource(res);
        }
    }

    private String about() {
        if (fileName() != null) {
            return "file:///android_asset/" + fileName();
        } else {
            return "file:///android_asset/about.html";
        }
    }

    public abstract String fileName();
}
