package org.meruvian.midas.showcase.fragment.category;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultFragment;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.entity.Category;
import org.meruvian.midas.showcase.task.category.CategoryPost;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class CategorySubmitFragment extends DefaultFragment implements TaskService<Category> {

    @InjectViews({R.id.field_name, R.id.field_description})
    List<EditText> fields;
    @InjectView(R.id.submit_btn)
    Button submit;

    private ProgressDialog progressDialog;

    private CategoryPost categoryPost;

    @Override
    protected int layout() {
        return R.layout.fragment_category_post;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPost = new CategoryPost(getActivity(), CategorySubmitFragment.this);
                categoryPost.execute(
                        fields.get(0).getText().toString(),
                        fields.get(1).getText().toString()
                );

                Log.e("name", fields.get(0).getText().toString() + " asda");
            }
        });
    }

    @Override
    public void onExecute(int code) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                categoryPost.cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    public void onSuccess(int code, Category category) {
        progressDialog.dismiss();

        if (category != null) {
            Toast.makeText(getActivity(), getString(R.string.success_post_category), Toast.LENGTH_LONG).show();

            finish();
        }
    }

    @Override
    public void onError(int code, String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onCancel(int code, String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
