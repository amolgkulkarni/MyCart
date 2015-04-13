package eci.officeshopper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eci.officeshopper.util.AsyncRestTask;
import eci.officeshopper.util.Config;
import eci.officeshopper.util.ImageDownloader;
import eci.officeshopper.widgets.FooterFragment;
import eci.officeshopper.util.NetworkUtils;

public class AuthActivity extends Activity {

    private static final String TAG = "OfficeShopper";
    EditText authCodeView;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        authCodeView = (EditText)findViewById(R.id.auth_view);
        this.mContext = this;

        // Initialize footer
        FooterFragment footer = new FooterFragment(this, findViewById(R.id.footer));
    }

    public void onProceed (View v) {
        NetworkUtils nwUtils = new NetworkUtils(mContext);
        boolean isConnected = nwUtils.checkConnectivity();
        if (isConnected) {
            String dealerCode = authCodeView.getText().toString();
            if (dealerCode.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter dealer code! ",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Authenticate Dealer
                AsyncRestTask authUser = new AsyncRestTask(mContext, "GET", null){
                    @Override
                    public void onExecute(String result) {
                        onAuthExecute(result);
                    }
                };
                authUser.execute(Config.DEALERS_URL + dealerCode);
            }
        } else {
            nwUtils.showAlert(R.string.network_alert_title, R.string.network_alert_message, R.string.network_tryagain);
        }
    }

    private void onAuthExecute(String result) {
        if (null == result) {
            NetworkUtils nwUtils = new NetworkUtils(mContext);
            nwUtils.showAlert(R.string.error, R.string.auth_error, R.string.positive_button);
        } else {
            try {
                JSONObject json = new JSONObject(result);
                JSONArray dealerItems = json.getJSONArray("items");
                if (dealerItems.length() > 0) {
                    parseResponse(dealerItems.getJSONObject(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseResponse (JSONObject item) {
        try {
            String baseUrl = item.getString("baseUrl");
            String dealerSplashUrl = item.getJSONObject("splashImages")
                    .getJSONObject("medium")
                    .getString("url");
            String dealerLogoUrl = item.getJSONObject("logoImages")
                    .getJSONObject("medium")
                    .getString("url");

            SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_AUTHENTICATED.ordinal());
            editor.putString(Config.DEALER_BASE_URL, baseUrl);

            // Save Image location to reuse in case cache is cleared.
            editor.putString(Config.DEALER_SPLASH_URL, dealerSplashUrl);
            editor.putString(Config.DEALER_LOGO_URL, dealerLogoUrl);
            boolean isCommited = editor.commit();

            if (isCommited) {
                // Download Splash Image
                ImageDownloader splashDownloader = new ImageDownloader(mContext, null, Config.DEALER_SPLASH_IMG, null, 0){
                    @Override
                    public boolean onExecute(Bitmap bm) {
                        return false;
                    }
                };
                splashDownloader.execute(dealerSplashUrl);

                // Download Logo Image
                ImageDownloader logoDownloader = new ImageDownloader(mContext, null, Config.DEALER_LOGO_IMG, null, 0) {
                    @Override
                    public boolean onExecute(Bitmap bm) {
                        if (null != bm) {
                            Intent i = new Intent(AuthActivity.this, LoginActivity.class);
                            startActivity(i);
                            // close this activity
                            finish();
                        }
                        return false;
                    }
                };
                logoDownloader.execute(dealerLogoUrl);
            } else {
                Log.e(TAG, "State could not be updated. Try again!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
