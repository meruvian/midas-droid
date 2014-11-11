package org.meruvian.midas.showcase.fragment.news;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.adapter.NewsAdapter;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.task.news.NewsGet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class ViewNewsFragment extends DefaultFragment implements TaskService<List<News>> {
    private ListView newsList;
    private ProgressDialog progressDialog;

    private NewsAdapter newsAdapter;
    private NewsGet newsGet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        newsAdapter = new NewsAdapter(getActivity(), R.layout.adapter_news_view, new ArrayList<News>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_view, container, false);

        newsList = (ListView) view.findViewById(R.id.list_news);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        newsList.setAdapter(newsAdapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("news_id", newsAdapter.getItem(position).getId());

                Fragment fragment = new DetailNewsFragment();
                fragment.setArguments(bundle);

                replaceFragment(R.id.content_frame, fragment, true);
            }
        });

        newsGet = new NewsGet(this, getActivity());
        newsGet.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.news_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            new NewsGet(this, getActivity()).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExecute(int code) {
        newsAdapter.clear();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.waiting));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                newsGet.cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, List<News> newses) {
        newsAdapter.addAll(newses);

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onError(int code, String message) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(int code, String message) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
