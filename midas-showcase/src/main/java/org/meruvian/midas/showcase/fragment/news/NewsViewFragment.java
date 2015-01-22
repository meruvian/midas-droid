package org.meruvian.midas.showcase.fragment.news;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.adapter.NewsAdapter;
import org.meruvian.midas.showcase.entity.Category;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.task.category.CategoryGet;
import org.meruvian.midas.showcase.task.news.NewsGet;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsViewFragment extends DefaultFragment implements TaskService<Object> {

    @InjectView(R.id.container)
    ViewGroup container;

    @InjectView(R.id.list_news)
    ListView newsList;

    @InjectView(R.id.btn_create)
    ImageButton createNews;

    private ProgressDialog progressDialog;

    private NewsGet newsGet;

    private SharedPreferences preferences;

    private SearchView searchView;

    private ArrayAdapter<Category> categoryAdapter;
    private NewsAdapter newsAdapter;

    private boolean showSearch = false;

    @Override
    protected int layout() {
        return R.layout.fragment_news_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        newsAdapter = new NewsAdapter(getActivity(), R.layout.adapter_news_view, new ArrayList<News>());

        categoryAdapter = new ArrayAdapter<Category>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        createNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.contains("mervid") || preferences.contains("manual")) {
                    replaceFragment(new NewsSubmitFragment(), "post news");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.warning_login_mervid), Toast.LENGTH_SHORT).show();
                }
            }
        });

        newsList.setAdapter(newsAdapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("news_id", newsAdapter.getItem(position).getId());

                Fragment fragment = new DetailNewsFragment();
                fragment.setArguments(bundle);

                replaceFragment(fragment, null);
            }
        });

        newsGet = new NewsGet(getActivity(), this);
        newsGet.execute("", "10", "0");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                newsGet = new NewsGet(getActivity(), NewsViewFragment.this);
                newsGet.execute(s, "10", "0");

                if (container.getChildCount() != 0) {
                    container.removeViewAt(0);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            newsGet = new NewsGet(getActivity(), this);
            newsGet.execute("", "10", "0");

            if (container.getChildCount() != 0) {
                container.removeViewAt(0);
            }
        } else if (item.getItemId() == R.id.advanced_search) {
            searchView.clearFocus();

            if (categoryAdapter.isEmpty()) {
                new CategoryGet(getActivity(), this).execute("");
            } else {
                advancedSearch();
            }
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
    public void onSuccess(int code, Object objects) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        if (objects != null) {
            if (code == GlobalVariable.NEWS_GET_TASK) {
                newsAdapter.addAll((List<News>) objects);
            } else if (code == GlobalVariable.CATEGORY_GET_TASK) {
                categoryAdapter.addAll((List<Category>) objects);

                advancedSearch();
            }
        }
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

    private void advancedSearch() {
        ViewGroup group = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_advanced_search, container, false);
        Button submit = (Button) group.findViewById(R.id.button_submit);
        final AutoCompleteTextView category = (AutoCompleteTextView) group.findViewById(R.id.edit_category);
        final EditText name = (EditText) group.findViewById(R.id.edit_name);

        category.setAdapter(categoryAdapter);
        category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    category.showDropDown();
                }
            }
        });
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category.setTag(categoryAdapter.getItem(i).getId());
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param1 = name.getText().toString() != null ? name.getText().toString() : "";
                String param2 = category.getTag() != null ? category.getTag().toString() : "";
//                new TenantListTask(TenantActivity.this, TenantActivity.this).execute(param1, param2, 0, 0);
                newsGet = new NewsGet(getActivity(), NewsViewFragment.this);
                newsGet.execute(param1, "10", "0");
            }
        });

        if (!showSearch) {
            container.addView(group, 0);
            showSearch = true;
        } else {
            container.removeViewAt(0);
            showSearch = false;
        }
    }
}
