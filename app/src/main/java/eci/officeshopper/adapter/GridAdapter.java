package eci.officeshopper.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import eci.officeshopper.R;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] mTitleValues;
    private final TypedArray mIconValues;
    private boolean mShowAsList = false;

    public GridAdapter(Context context, String[] titleValues, TypedArray iconValues, boolean asList) {
        this.mContext = context;
        this.mTitleValues = titleValues;
        this.mIconValues = iconValues;
        this.mShowAsList = asList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mShowAsList) {
                convertView = inflater.inflate(R.layout.drawer_list_item, null);
            } else {
                convertView = inflater.inflate(R.layout.homescreen_item, null);
            }
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.grid_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.grid_title);

        imgIcon.setImageResource(mIconValues.getResourceId(position,-1));
        txtTitle.setText(mTitleValues[position]);

        return convertView;
    }

    @Override
    public int getCount() {
        return mTitleValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
