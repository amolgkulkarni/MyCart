package eci.officeshopper.widgets;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import eci.officeshopper.R;
import eci.officeshopper.TermsActivity;

public class FooterFragment {
    private static final String TAG = "OfficeShopper";
    private Context mContext;
    public FooterFragment(Context context, View view){
        this.mContext = context;

        TextView versionView = (TextView)view.findViewById(R.id.version);
        versionView.setText(versionView.getText().toString() + ": " + getApplicationVersionName(mContext));

        View termsView = view.findViewById(R.id.terms);
        termsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, TermsActivity.class);
                mContext.startActivity(i);
            }
        });
    }

    private String getApplicationVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Package not found");
        } catch(Exception e){
            Log.e(TAG, "VersionName not found");
        }
        return "";
    }

    private int getApplicationVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Package not found");
        } catch(Exception e){
            Log.e(TAG, "VersionCode not found");
        }
        return 0;
    }
}

