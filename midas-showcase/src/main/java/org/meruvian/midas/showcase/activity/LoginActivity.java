package org.meruvian.midas.showcase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.meruvian.midas.core.defaults.DefaultActivity;
import org.meruvian.midas.core.service.TaskService;
import org.meruvian.midas.showcase.GlobalVariable;
import org.meruvian.midas.showcase.R;
import org.meruvian.midas.showcase.activity.social.SocialLoginActivity;
import org.meruvian.midas.showcase.task.LoginTask;

/**
 * Created by ludviantoovandi on 19/11/14.
 */
public class LoginActivity extends DefaultActivity implements TaskService {
    private EditText username, password;
    private Button login;
    private TextView skip;

    @Override
    public int layout() {
        return R.layout.activity_login;
    }

    @Override
    public void onViewCreated() {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.field_username);
        password = (EditText) findViewById(R.id.field_password);
        login = (Button) findViewById(R.id.button_login);
        skip = (TextView) findViewById(R.id.text_skip);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask(LoginActivity.this, LoginActivity.this).execute(username.getText().toString(), password.getText().toString());
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SocialLoginActivity.class));
        finish();
    }

    @Override
    public void onExecute(int code) {
        login.setText(getString(R.string.loging_in));
        login.setEnabled(false);
    }

    @Override
    public void onSuccess(int code, Object result) {
        if (result != null) {
            if (code == GlobalVariable.LOGIN_TASK) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onCancel(int code, String message) {

    }

    @Override
    public void onError(int code, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        login.setText(getString(R.string.login));
        login.setEnabled(true);
    }
}
