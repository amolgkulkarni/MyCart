package eci.officeshopper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import eci.officeshopper.widgets.Actionbar;
import eci.officeshopper.widgets.ListenerFrameLayout;

public class FragmentHolderActivity extends Activity implements DrawerFragment.OnItemSelectedListener{
    private Context mContext;
    private boolean mDrawerOpen;
    private boolean mDualPane;
    private Fragment mDrawerFragment;
    private ListenerFrameLayout mDetailsFragment;
    private  Actionbar mActionbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentholder);
        this.mContext = this;
        this.mDrawerOpen = false;
        this.mDualPane = false;
        this.mDetailsFragment = (ListenerFrameLayout)findViewById(R.id.details_container);
        this.mDrawerFragment = new DrawerFragment();

        // Show selected Fragment
        Integer index = getIntent().getExtras().getInt("ItemIndex", 0);

        // Listen Events on Details fragment for closing drawer if open
        mDetailsFragment.setOnClickListener(new viewClickListener());

        // Initialize ActionBar
        this.mActionbar = new Actionbar(this, findViewById(R.id.header),
                getResources().getStringArray(R.array.homescreen_items)[index],
                R.drawable.menu_icon, R.drawable.shopping_cart){
            @Override
            public void onBackAction(View view) {
                openDrawer();
            }
            @Override
            public void onCmdAction(View view) {
            }
        };

        // Add Drawer Fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.drawer_container, mDrawerFragment).commit();

        ((DrawerFragment)mDrawerFragment).setSelectedItem(index);
        onItemPicked(index);
        // Keep Drawer invisible in beginning
        findViewById(R.id.drawer_container).setVisibility(View.INVISIBLE);
    }

    // Implement DrawingFragment interface
    @Override
    public void onItemPicked(int position)
    {
        closeDrawer();
        Fragment fragment = null;

        switch (position) {
            case 1:
                fragment = new ScanFragment();
                break;
            case 2:
                fragment = new CartFragment();
                break;
            case 4:
                fragment = new AccountsFragment();
                break;
            default:
                break;
        }

        if (null != fragment) {
            String[] items = getResources().getStringArray(R.array.homescreen_items);
            mActionbar.setTitle(items[position]);
            try {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.details_container, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage (String type, String msg) {
        if (type.equals("CmdInfo")) {
            mActionbar.setCommandInfo(msg);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        closeDrawer();
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            switch(keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mDrawerOpen) {
                        closeDrawer();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class viewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            closeDrawer();
        }
    }

    private void closeDrawer () {
        if (mDrawerOpen) {
            final FrameLayout v = (FrameLayout)findViewById(R.id.drawer_container);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            final int newWidth = (int)(metrics.widthPixels * 3/4);
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.LayoutParams params = (v).getLayoutParams();
                    params.width = newWidth + 1 - (int)(newWidth * interpolatedTime);
                    v.setLayoutParams(params);
                }
            };
            a. setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    v.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                }
            });
            a.setDuration(500); // in ms
            v.startAnimation(a);
        }
        mDrawerOpen = false;
        mDetailsFragment.consumeEvents(false);
    }

    private void openDrawer () {
        if (!mDrawerOpen) {
            final FrameLayout v = (FrameLayout)findViewById(R.id.drawer_container);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            final int newWidth = (metrics.widthPixels*3)/4;
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.LayoutParams params = (v).getLayoutParams();
                    params.width = (int)(newWidth * interpolatedTime);
                    v.setLayoutParams(params);
                }
            };

            a. setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                    v.setVisibility(View.VISIBLE);
                }
            });
            a.setDuration(500); // in ms
            v.startAnimation(a);
        }
        mDrawerOpen = true;
        mDetailsFragment.consumeEvents(true);
    }
}
