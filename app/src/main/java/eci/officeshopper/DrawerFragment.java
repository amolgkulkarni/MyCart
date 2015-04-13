package eci.officeshopper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import eci.officeshopper.adapter.GridAdapter;
import eci.officeshopper.util.Config;


public class DrawerFragment extends Fragment {
    private static final String TAG = "OfficeShopper";
    private Integer mIndex = 0;
    private Activity activity;
    private ImageView mCurrentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(this.activity, R.style.FullscreenTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View rootView = localInflater.inflate(R.layout.fragment_drawer, container, false);
        SharedPreferences preferences = this.activity.getSharedPreferences(Config.PREF_MODULE, Activity.MODE_PRIVATE);

        ListView listView = (ListView) rootView.findViewById(R.id.drawer_list);
        // Update User TextView
        TextView userView = (TextView) rootView.findViewById(R.id.drawer_username);
        userView.setText("Hello, " + preferences.getString(Config.USER_NAME, "User"));

        // load Drawer items
        String[] drawerTitles = this.activity.getResources().getStringArray(R.array.homescreen_items);
        TypedArray drawerIcons = this.activity.getResources().obtainTypedArray(R.array.homescreen_icons);
        // setting adapter
        GridAdapter adapter = new GridAdapter(this.activity.getApplicationContext(),
                drawerTitles, drawerIcons, true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new SlideMenuClickListener());

        this.mCurrentView = (ImageView) rootView.findViewById(R.id.current_view);
        this.mCurrentView.setImageResource(drawerIcons.getResourceId(this.mIndex, -1));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setSelectedItem (Integer position) {
        this.mIndex = position;
        // Set Image for current Item
        if (null != this.activity) {
            TypedArray drawerIcons = this.activity.getResources().obtainTypedArray(R.array.homescreen_icons);
            this.mCurrentView.setImageResource(drawerIcons.getResourceId(this.mIndex, -1));
        }
    }

    private void selectItem(Integer position){
        try{
            if (null != activity) {
                TypedArray drawerIcons = this.activity.getResources().obtainTypedArray(R.array.homescreen_icons);
                this.mCurrentView.setImageResource(drawerIcons.getResourceId(this.mIndex, -1));
                ((OnItemSelectedListener) activity).onItemPicked(position);
            }
        }catch (ClassCastException cce){
            Log.e(TAG, "OnItemSelectedListener Interface not implemented");
        }
    }

    // Interface for communicating with Activity
    public static interface OnItemSelectedListener{
        public void onItemPicked(int position);
        public void onMessage(String type, String Msg);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            setSelectedItem(position);
            selectItem(position);
        }
    }

}
