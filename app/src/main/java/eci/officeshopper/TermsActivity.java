package eci.officeshopper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import eci.officeshopper.util.Config;
import eci.officeshopper.util.NetworkUtils;

public class TermsActivity extends Activity {

    private Integer authState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        final Button acceptButton = (Button) findViewById(R.id.acceptButton);

        SharedPreferences preferences = this.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
        this.authState = preferences.getInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_NONE.ordinal());
        if (this.authState > Config.AuthState.STATE_NONE.ordinal()) {
            // Already Accepted
            acceptButton.setText(R.string.back);
        }
        // Check for network status
        NetworkUtils nwUtils = new NetworkUtils(this){
            @Override
            public void onAction(){
                // Continue showing alert till network is restored
                if(!this.checkConnectivity()) {
                    this.showAlert(R.string.network_alert_title, R.string.network_alert_message,
                            R.string.network_tryagain);
                } else {
                    WebView webView = (WebView) findViewById(R.id.webview);
                    webView.loadUrl("file:///android_asset/www/LicenseAgreementScreen.html");
                    acceptButton.setEnabled(true);
                }

            }
        };

        // Check for connectivity
        boolean isConnected = nwUtils.checkConnectivity();
        if (!isConnected) {
            acceptButton.setEnabled(false);
            nwUtils.showAlert(R.string.network_alert_title, R.string.network_alert_message,
                    R.string.network_tryagain);
        } else {
            WebView webView = (WebView) findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/www/LicenseAgreementScreen.html");
        }
    }

    public void onAccept (View v) {
        if (authState > 0) {
            // We are here for showing Terms. They are already accepted.
            finish();
        } else {
            // Update State to AcceptedTerms
            SharedPreferences preferences = this.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_ACCEPTED.ordinal());
            boolean result = editor.commit();

            if (result) {
                // Show Dealer Auth Screen
                Intent i = new Intent(TermsActivity.this, AuthActivity.class);
                startActivity(i);
                // close this activity
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong Try Again !",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
