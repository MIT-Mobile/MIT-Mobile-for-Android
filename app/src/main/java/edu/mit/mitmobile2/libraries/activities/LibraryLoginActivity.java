package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.R;

public class LibraryLoginActivity extends AppCompatActivity {

    @InjectView(R.id.username_edittext)
    EditText username;

    @InjectView(R.id.password_edittext)
    EditText password;

    @InjectView(R.id.save_login_info_switch)
    Switch saveLoginSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_login);
    }

    @OnClick(R.id.login_button)
    void login() {
        String user = String.valueOf(username.getText());
        String pwd = String.valueOf(password.getText());

        // TODO: Connect with touchstone somehow

        if (saveLoginSwitch.isChecked()) {
            // TODO: Save login info in AccountUtils?
        }
    }

}
