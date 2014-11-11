package org.meruvian.midas.showcase.fragment.news;

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
import android.widget.EditText;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.task.news.NewsPost;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class PostNewsFragment extends DefaultFragment implements TaskService<Boolean> {
    private EditText titleEdit, contentEdit;
    private ProgressDialog progressDialog;

    private NewsPost newsPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_post, container, false);

        titleEdit = (EditText) view.findViewById(R.id.edit_title);
        contentEdit = (EditText) view.findViewById(R.id.edit_content);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.news_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_post_news) {
            newsPost = new NewsPost(this, getActivity());
            newsPost.execute(titleEdit.getText().toString(), contentEdit.getText().toString());
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
                newsPost.cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, Boolean obj) {
        Toast.makeText(getActivity(), "Success submit news", Toast.LENGTH_LONG).show();

        progressDialog.dismiss();
        finish();
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
}
