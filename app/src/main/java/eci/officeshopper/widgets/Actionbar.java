package eci.officeshopper.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eci.officeshopper.R;

public class Actionbar {
    private static final String TAG = "OfficeShopper";
    private Context mContext;
    private TextView mRightCmdView;
    private ImageView mBackView;
    private TextView mTitleView;

    public Actionbar(Context context, View view, String title, Integer leftId, Integer rightId){
        this.mContext = context;

        this.mBackView = (ImageView) view.findViewById(R.id.left_icon);
        this.mBackView.setImageResource(leftId);
        this.mBackView.setOnClickListener(new menuClickListener());

        this.mRightCmdView = (TextView) view.findViewById(R.id.right_text);
        Drawable leftDrawable = context.getResources().getDrawable(rightId);
        this.mRightCmdView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        this.mRightCmdView.setOnClickListener(new menuClickListener());

        this.mTitleView = (TextView) view.findViewById(R.id.title_text);
        this.mTitleView.setText(title);
    }

    public void setTitle (String title) {
        this.mTitleView.setText(title);
    }

    public void setCommandInfo (String text) {
        this.mRightCmdView.setText(text);
    }

    public void setCommandIcon (Integer imgId) {
        Drawable leftDrawable = this.mContext.getResources().getDrawable(imgId);
        this.mRightCmdView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
    }

    public void setBackImage (Integer imgId) {
        this.mBackView.setImageResource(imgId);
    }

    public void setBackImage (Bitmap bitmap) {
        this.mBackView.setImageBitmap(bitmap);
    }

    public void onBackAction (View view) {
    }

    public void onCmdAction (View view) {
    }

    private class menuClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == mBackView) {
                onBackAction(view);
            } else {
                onCmdAction(view);
            }
        }
    }
}
