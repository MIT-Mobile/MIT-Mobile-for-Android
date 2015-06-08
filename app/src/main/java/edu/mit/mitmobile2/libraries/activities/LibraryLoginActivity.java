package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.LibraryManager;
import edu.mit.mitmobile2.libraries.model.MITLibrariesXmlObject;
import edu.mit.mitmobile2.libraries.model.RelayState;
import edu.mit.mitmobile2.libraries.model.XmlHeader;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.SimpleXMLConverter;

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

        ButterKnife.inject(this);
    }

    @OnClick(R.id.login_button)
    void login() {
        String user = String.valueOf(username.getText());
        String pwd = String.valueOf(password.getText());

        // TODO: Connect with touchstone somehow


        if (saveLoginSwitch.isChecked()) {
            // TODO: Save login info in AccountUtils?
        }

        LibraryManager.getLoginAuth(this, new Callback<MITLibrariesXmlObject>() {
            @Override
            public void success(MITLibrariesXmlObject mitLibrariesXmlObject, Response response) {
                LoggingManager.Timber.d("Success!");
                List<Header> headers = response.getHeaders();
                for (Header header : headers) {
                    if (header.getName().equals("Content-Type")) {
                        if (header.getValue().equals("application/vnd.paos+xml")) {
                            // Continue with auth
                            postUserLoginInfo(mitLibrariesXmlObject.getHeader().getRelayState(), mitLibrariesXmlObject);
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Timber.e(error.getMessage());
            }
        });


    }

    private void postUserLoginInfo(RelayState relayState, MITLibrariesXmlObject object) {
        LibraryManager.changeEndpoint("https://idp.mit.edu/idp/profile/SAML2/SOAP/ECP/");
        LibraryManager.setUsernameAndPassword("mitlibrarytest@gmail.com", "readingrainbow22");

        object.setHeader(null);

        LibraryManager.MIT_SECURE_SERVICE._postloginuser(new SimpleXMLConverter().toBody(object), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LoggingManager.Timber.d("Success!");
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Timber.e(error.getMessage());
            }
        });

    }

}
