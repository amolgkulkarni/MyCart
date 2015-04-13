package eci.officeshopper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import eci.officeshopper.util.Config;

public class AccountsFragment extends Fragment {
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        // Set show/collapse listeners
        rootView.findViewById(R.id.user_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(rootView.findViewById(R.id.user_details));
            }
        });
        rootView.findViewById(R.id.contact_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(rootView.findViewById(R.id.contact_details));
            }
        });
        rootView.findViewById(R.id.device_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(rootView.findViewById(R.id.device_details));
            }
        });

        SharedPreferences preferences = mContext.getSharedPreferences(Config.PREF_MODULE, Activity.MODE_PRIVATE);
        String userInfo = preferences.getString(Config.USER_INFO, null);
        if (null != userInfo) {
            try {
                JSONObject json = new JSONObject(userInfo);
                TextView loginComp = (TextView) rootView.findViewById(R.id.login_comp_name);
                TextView loginDept = (TextView) rootView.findViewById(R.id.login_dept_name);
                TextView loginUser = (TextView) rootView.findViewById(R.id.login_username);
                TextView userName = (TextView) rootView.findViewById(R.id.username_value);
                TextView compName = (TextView) rootView.findViewById(R.id.comp_name);
                TextView address = (TextView) rootView.findViewById(R.id.address_value);
                TextView phone = (TextView) rootView.findViewById(R.id.phone_value);
                TextView email = (TextView) rootView.findViewById(R.id.email_value);

                loginComp.setText(json.getString("LoginCompName"));
                loginDept.setText(json.getString("LoginDeptName"));
                loginUser.setText(json.getString("loginEmpName"));
                userName.setText(json.getString("Username"));

                JSONObject company = json.getJSONObject("Company");
                compName.setText(company.getString("Name"));
                phone.setText(company.getString("Phone"));
                email.setText(company.getString("Email"));
                address.setText(company.getString("AddressLine1"));
                address.append(" \n" + company.getString("AddressLine2"));
                address.append(" \n" + company.getString("City"));
                address.append(", " + company.getString("State"));
                address.append(", " + company.getString("Country") + "- " + company.getString("ZipCode"));
            } catch(JSONException e) {
                e.printStackTrace();
            }

            TextView model = (TextView) rootView.findViewById(R.id.model_value);
            TextView dimension = (TextView) rootView.findViewById(R.id.dimension_value);
            TextView webkit = (TextView) rootView.findViewById(R.id.webkit_value);
            TextView osVersion = (TextView) rootView.findViewById(R.id.os_value);


            DisplayMetrics metrics = getResources().getDisplayMetrics();
            model.setText(Build.MODEL);
            dimension.setText(metrics.widthPixels + " X " + metrics.heightPixels);
            String userAgent = new WebView(mContext).getSettings().getUserAgentString();
            webkit.setText(userAgent.split("WebKit/")[1].split(" ")[0]);
            //webkit.setText(System.getProperty("http.agent"));
            osVersion.setText(Build.VERSION.RELEASE);
        }

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void toggleContents(View v){

        if(v.isShown()){
            //slide(mContext, v, true);
            v.setVisibility(View.GONE);
        }
        else{
            v.setVisibility(View.VISIBLE);
            slide(mContext, v, false);
        }
    }

    public static void slide(Context ctx, View v, boolean up) {
        Animation anim;
        if (up) {
            anim = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        } else {
            anim = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        }
        if (anim != null) {
            anim.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(anim);
            }
        }
    }

}
