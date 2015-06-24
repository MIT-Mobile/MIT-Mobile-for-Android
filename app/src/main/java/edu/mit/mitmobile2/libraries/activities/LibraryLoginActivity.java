package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.LibraryManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

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

        LibraryManager.setUsernameAndPassword(null, null);

        if (saveLoginSwitch.isChecked()) {
            // TODO: Save login info in AccountUtils?
        }

        LibraryManager.getLoginAuth(this, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LoggingManager.Timber.d("Success!");

                List<Header> headers = response.getHeaders();
                for (Header header : headers) {
                    if (header.getName() != null && header.getName().equals("Content-Type")) {
                        if (header.getValue().equals("application/vnd.paos+xml")) {
                            String xml = getStringFromBody(response.getBody());

                            int i = xml.indexOf("<ecp:RelayState");
                            int k = xml.indexOf("</S:Header>");

                            String relayState = xml.substring(i, k);
                            relayState = relayState.replaceAll(" S:", " soap11:");

                            TypedString typedString = alterXmlString(xml, "<S:Header>", "<S:Body>", "");
                            postUserLoginInfo(relayState, typedString);
                            return;
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


    private void postUserLoginInfo(final String relayState, final TypedString body) {
        LibraryManager.changeEndpoint("https://idp.touchstonenetwork.net/");
        LibraryManager.setUsernameAndPassword("mitlibrarytest@gmail.com", "readingrainbow22");

        LibraryManager.postLoginToIdp(body, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                LoggingManager.Timber.d("Success!");

                String xml = getStringFromBody(response.getBody());

                TypedString typedString = alterXmlString(xml, "<ecp:Response", "</soap11:Header>", relayState);
                postLoginAuth(typedString);
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Timber.e(error.getMessage());
            }
        });

    }

    private void postLoginAuth(TypedString response) {
        LibraryManager.changeEndpoint("https://mobile-dev.mit.edu/Shibboleth.sso/");

        LibraryManager.setUsernameAndPassword(null, null);

        LibraryManager.postAuthToShibboleth(response, new Callback<Response>() {
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

    private TypedString alterXmlString(String xml, String start, String end, String substitution) {
        int i = xml.indexOf(start);
        int k = xml.indexOf(end);

        String before = xml.substring(0, i);
        String after = xml.substring(k, xml.length());

        String postString = before + substitution + after;
        return new TypedString(postString);
    }

    private String getStringFromBody(TypedInput body) {
        TypedByteArray byteArray = (TypedByteArray) body;
        return new String(byteArray.getBytes());
    }

}
