package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Switch;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.libraries.LibraryManager;
import edu.mit.mitmobile2.libraries.model.MITLibrariesXmlObject;
import edu.mit.mitmobile2.libraries.model.xml.touchstone.MITTouchstoneResponse;
import edu.mit.mitmobile2.libraries.model.xml.user.RelayState;
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

        LibraryManager.setUsernameAndPassword(null, null);

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

    private void postUserLoginInfo(final RelayState relayState, MITLibrariesXmlObject object) {
        LibraryManager.changeEndpoint("https://idp.touchstonenetwork.net/");
        LibraryManager.setUsernameAndPassword("mitlibrarytest@gmail.com", "readingrainbow22");

        object.setHeader(null);

        //TODO: Wrap Post call

        LibraryManager.MIT_SECURE_SERVICE._postloginuser(object, new Callback<MITTouchstoneResponse>() {
            @Override
            public void success(MITTouchstoneResponse response, Response response2) {
                LoggingManager.Timber.d("Success!");
                postLoginAuth(relayState, response);
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Timber.e(error.getMessage());
            }
        });

    }

    private void postLoginAuth(RelayState relayState, MITTouchstoneResponse response) {
        DocumentsContract.Document document;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        LibraryManager.changeEndpoint("https://mobile-dev.mit.edu/Shibboleth.sso/");

        LibraryManager.setUsernameAndPassword(null, null);

        response.getHeader().setResponse(null);
        response.getHeader().setRelayState(new edu.mit.mitmobile2.libraries.model.xml.touchstone.RelayState(relayState.getActor(), relayState.getMustUnderstand(), relayState.getValue()));

        //TODO: Wrap Post call

        Serializer serializer = new Persister();
        try {
            serializer.write(response, new File(Environment.getExternalStorageDirectory().getPath() + "/Download/response.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LibraryManager.MIT_SECURE_SERVICE._postloginuser2(response, new Callback<Response>() {
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

    public static void elementToStream(Element element, OutputStream out) {
        try {
            DOMSource source = new DOMSource(element);
            StreamResult result = new StreamResult(out);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
        } catch (Exception ex) {
            LoggingManager.Timber.d("XML element to stream error = " + ex.getMessage());
        }
    }

}
