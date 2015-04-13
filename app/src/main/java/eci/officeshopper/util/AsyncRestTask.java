package eci.officeshopper.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

import eci.officeshopper.R;

public class AsyncRestTask extends AsyncTask<String, Void, String>{

    private static final String TAG = "OfficeShopper";
    private Context mContext;
    private List<NameValuePair> mNameValuePairs;
    private String method;
    private ProgressDialog mProgressDialog;

    public AsyncRestTask(Context context, String method, List<NameValuePair> nameValuePairs) {
        this.mContext = context;
        this.mNameValuePairs = nameValuePairs;
        this.method = method;

        // Create Progress dialog
        this.mProgressDialog = new ProgressDialog(mContext, R.style.SpinnerTheme);
        this.mProgressDialog.setCancelable(false);
        //this.mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onPreExecute () {
        // Todo: Show Spinner
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String retVal = null;
        // Create client
        DefaultHttpClient httpclient = new DefaultHttpClient();

        // Get Cookies if Any
        String[] keyValueSets;
        SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, mContext.MODE_PRIVATE);
        String storedDomain = preferences.getString(Config.COOKIE_DOMAIN, null);

        try {
            keyValueSets= CookieManager.getInstance().getCookie(storedDomain).split(";");
        } catch (Exception e) {
            keyValueSets = new String[0];
        }
        for(String cookie : keyValueSets)
        {
            String[] keyValue = cookie.split("=");
            String key = keyValue[0];
            String value = "";
            if(keyValue.length>1) value = keyValue[1];

            BasicClientCookie c = new BasicClientCookie(key, value);
            c.setDomain(storedDomain);
            httpclient.getCookieStore().addCookie(c);
        }

        try {
            if (method.equals("POST")) {
                HttpPost request = new HttpPost(params[0]);
                request.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
                HttpResponse response = httpclient.execute(request);
                try {
                    retVal = EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Parse Exception " + e + "");
                    retVal = null;
                }
            } else {
                HttpGet request;
                if (null != mNameValuePairs) {
                    String paramsString = URLEncodedUtils.format(mNameValuePairs, "UTF-8");
                    request = new HttpGet(params[0] + "?" + paramsString);
                } else {
                    request = new HttpGet(params[0]);
                }
                ResponseHandler<String> handler = new BasicResponseHandler();
                retVal = httpclient.execute(request, handler);
            }
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                Log.i(TAG, "No cookies stored...");
            } else {
                String domain = null;
                for (Cookie cookie : cookies) {
                    domain = cookie.getDomain();
                    String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + domain;
                    CookieManager.getInstance().setCookie(domain, cookieString);
                }
                CookieSyncManager.getInstance().sync();
                //CookieManager.getInstance().flush();

                // Store cookie domain for further use
                if (null != domain) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Config.COOKIE_DOMAIN, domain);
                    editor.apply();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            retVal = null;
        } catch (IOException e) {
            e.printStackTrace();
            retVal = null;
        }
        httpclient.getConnectionManager().shutdown();
        return retVal;
    }

    @Override
    protected void onPostExecute(String result) {
        // Todo: Hide Spinner
        mProgressDialog.dismiss();
        onExecute(result);
    }

    public void onExecute(String result) {
    }
}
