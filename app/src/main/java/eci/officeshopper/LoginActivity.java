package eci.officeshopper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eci.officeshopper.util.AsyncRestTask;
import eci.officeshopper.util.Config;
import eci.officeshopper.widgets.FooterFragment;
import eci.officeshopper.util.ImageDownloader;
import eci.officeshopper.util.NetworkUtils;

public class LoginActivity extends Activity {
    private static final String TAG = "OfficeShopper";
    private Context mContext;
    private EditText mUserView;
    private EditText mPwdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mContext = this;
        this.mUserView = (EditText) findViewById(R.id.login_username);
        this.mPwdView = (EditText) findViewById(R.id.login_password);

        // Download from stored splash url in case cache is cleared
        SharedPreferences preferences = getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
        String dealerLogoUrl = preferences.getString(Config.DEALER_LOGO_URL, null);
        ImageDownloader imageDownloader = new ImageDownloader(this, (ImageView)findViewById(R.id.dealer_logo),
                Config.DEALER_LOGO_IMG, R.drawable.logo, 0);
        imageDownloader.execute(dealerLogoUrl);

        // forgot_pwd has links specified by putting <a> tags in the string
        // resource.  By default these links will appear but not
        // respond to user input.  To make them active, you need to
        // call setMovementMethod() on the TextView object.
        TextView t2 = (TextView) findViewById(R.id.forgot_pwd);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        // To make text underline
        //mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Initialize footer
        FooterFragment footer = new FooterFragment(this, findViewById(R.id.footer));
    }

    public void onShowPassword (View v) {
        if (((CheckBox)v).isChecked()) {
            ((EditText)findViewById(R.id.login_password)).setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            // 129 corresponds to masked password (not documented)
            ((EditText)findViewById(R.id.login_password)).setInputType(129);
        }
    }

    public void onLogin (View v) {
        NetworkUtils nwUtils = new NetworkUtils(mContext);
        boolean isConnected = nwUtils.checkConnectivity();
        if (isConnected) {
            // Get BaseUrl
            SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
            String baseUrl = preferences.getString(Config.DEALER_BASE_URL, null);
            if (null != baseUrl) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", mUserView.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("password", mPwdView.getText().toString()));
                AsyncRestTask loginUser = new AsyncRestTask(this, "GET", nameValuePairs){
                    @Override
                    public void onExecute(String result) {
                        onLoginExecute(result);
                    }
                };
                loginUser.execute("http://" + baseUrl + Config.DEALER_LOGIN_API);
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong. Authenticate again!",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, AuthActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        } else {
            nwUtils.showAlert(R.string.network_alert_title, R.string.network_alert_message, R.string.network_tryagain);
        }
    }

    private void onLoginExecute (String result) {
        if (null != result) {
            try {
                JSONObject json = new JSONObject(result);
                boolean success = json.getBoolean("Success");
                if (success) {
                    SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_LOGGEDIN.ordinal());
                    editor.putString(Config.USER_NAME, json.getString("loginEmpName"));
                    editor.putString(Config.USER_INFO,result);
                    editor.apply();
                    Intent i = new Intent(mContext, HomeActivity.class);
                    i.putExtra(Config.USER_NAME, json.getString("loginEmpName"));
                    startActivity(i);
                    // close this activity
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Authentication failure! Code: " + json.getInt("StatusCode") + " Msg: " + json.getString("Message"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Authentication failure!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
