package org.meruvian.midas.showcase.fragment.news;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.entity.Category;
import org.meruvian.midas.showcase.entity.News;
import org.meruvian.midas.showcase.task.category.CategoryGet;
import org.meruvian.midas.showcase.task.news.NewsPost;

import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class NewsSubmitFragment extends DefaultFragment implements TaskService<Object> {

    @InjectViews({R.id.field_title, R.id.field_category, R.id.field_content})
    List<EditText> fields;
//    @InjectView(R.id.submit_btn)
//    Button submit;
    private ProgressDialog progressDialog;

    private NewsPost newsPost;

    private ArrayAdapter<Category> categoryAdapter;

    @Override
    protected int layout() {
        return R.layout.fragment_news_post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                newsPost = new NewsPost(NewsSubmitFragment.this, getActivity());
//                newsPost.execute(
//                        fields.get(0).getText().toString(),
//                        ((Category) fields.get(1).getTag()).getId(),
//                        fields.get(2).getText().toString()
//                );
//            }
//        });

        new CategoryGet(getActivity(), this).execute("");

        fields.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            newsPost = new NewsPost(NewsSubmitFragment.this, getActivity());
            newsPost.execute(
                    fields.get(0).getText().toString(),
                    ((Category) fields.get(1).getTag()).getId(),
                    fields.get(2).getText().toString()
            );
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExecute(int code) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (newsPost != null) {
                    newsPost.cancel(true);
                }
            }
        });
        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, Object obj) {
        progressDialog.dismiss();

        if (obj != null) {
            if (code == GlobalVariable.NEWS_POST_TASK) {
                Toast.makeText(getActivity(), getString(R.string.success_post_news), Toast.LENGTH_LONG).show();
                finish();
            } else if (code == GlobalVariable.CATEGORY_GET_TASK) {
                categoryAdapter = new ArrayAdapter<Category>(getActivity(), android.R.layout.simple_list_item_single_choice);
                categoryAdapter.addAll((List<Category>) obj);
            }
        }
    }

    @Override
    public void onError(int code, String message) {
        if (message != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        progressDialog.dismiss();
    }

    @Override
    public void onCancel(int code, String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.category));
        builder.setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fields.get(1).setTag(categoryAdapter.getItem(i));
                fields.get(1).setText(categoryAdapter.getItem(i).getName());

                fields.get(2).requestFocus();
            }
        });

        builder.create().show();
    }
}
