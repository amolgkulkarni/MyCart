package eci.officeshopper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import eci.officeshopper.adapter.GridAdapter;
import eci.officeshopper.util.Config;
import eci.officeshopper.util.ExpandableGridView;
import eci.officeshopper.widgets.FooterFragment;
import eci.officeshopper.util.ImageDownloader;

public class HomeActivity extends Activity {
    private static final String TAG = "OfficeShopper";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        this.mContext = this;

        // Download from stored splash url in case cache is cleared
        SharedPreferences preferences = getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);
        String dealerLogoUrl = preferences.getString(Config.DEALER_LOGO_URL, null);
        ImageDownloader imageDownloader = new ImageDownloader(this, (ImageView) findViewById(R.id.dealer_logo),
                Config.DEALER_LOGO_IMG, R.drawable.logo, 0);
        imageDownloader.execute(dealerLogoUrl);

        // Update User TextView
        TextView userView = (TextView) findViewById(R.id.user);
        String userName = null;
        try {
            // Get from Intent if available
            userName = getIntent().getExtras().getString(Config.USER_NAME);
        } catch (Exception e) {
            userName = preferences.getString(Config.USER_NAME, "User");
        }
        userView.setText("Hello, " + userName);

        // load Grid items
        String[] gridTitles = getResources().getStringArray(R.array.homescreen_items);
        TypedArray gridIcons = getResources().obtainTypedArray(R.array.homescreen_icons);

        ExpandableGridView gridView = (ExpandableGridView) findViewById(R.id.gridview);

        gridView.setOnItemClickListener(new SlideMenuClickListener());

        // setting Grid adapter
        GridAdapter adapter = new GridAdapter(getApplicationContext(),
                gridTitles, gridIcons, false);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        gridView.setColumnWidth(metrics.widthPixels/Config.HOMESCREEN_NUM_COLUMNS);
        gridView.setNumColumns(Config.HOMESCREEN_NUM_COLUMNS);

        // Initialize footer
        FooterFragment footer = new FooterFragment(this, findViewById(R.id.footer));
    }

    private class SlideMenuClickListener implements
            GridView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (position) {
                case 0:
                case 3:
                    Toast.makeText(getApplicationContext(), "Showing " + position,
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                case 2:
                case 4:
                    Intent i = new Intent(mContext, FragmentHolderActivity.class);
                    i.putExtra("ItemIndex", position);
                    startActivity(i);
                    break;
                case 5:
                    logoffUser();
                    break;
                default:
                    break;
            }
        }
    }
    private void logoffUser () {
        SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Config.DEALER_AUTH_STATE, Config.AuthState.STATE_AUTHENTICATED.ordinal());
        editor.remove(Config.USER_NAME);
        editor.remove(Config.COOKIE_DOMAIN);
        boolean isCommited = editor.commit();

        // Todo: Remove cookies as well

        if (isCommited) {
            Intent i = new Intent(mContext, LoginActivity.class);
            startActivity(i);
            // close this activity
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Could not logoff. TryAgain!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
