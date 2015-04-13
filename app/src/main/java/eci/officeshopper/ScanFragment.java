package eci.officeshopper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eci.officeshopper.model.CartListItem;

public class ScanFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "OfficeShopper";
    private Context mContext;
    private ListView mScanList;
    private TextView mScanResultView;
    public static final int REQUEST_CODE = 0x0ba7c0de;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        this.mScanList = (ListView)rootView.findViewById(R.id.scan_list);

        String[] items = {"", "", "", "", "", "", ""};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.scanned_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.scanned_item, null);

                }

                View countText = convertView.findViewById(R.id.scan_count);

                View inc = convertView.findViewById(R.id.inc_count);
                inc.setTag(countText);
                inc.setOnClickListener(ScanFragment.this);

                View dec = convertView.findViewById(R.id.dec_count);
                dec.setTag(countText);
                dec.setOnClickListener(ScanFragment.this);

                View scanText = convertView.findViewById(R.id.scan_text);
                scanText.setTag(countText);
                scanText.setOnClickListener(ScanFragment.this);

                return convertView;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        };
        this.mScanList.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        TextView cv;
        Integer val;
        switch(v.getId()) {
            case R.id.inc_count:
                cv = (TextView) v.getTag();
                try {
                    val = Integer.parseInt(cv.getText().toString());
                    cv.setText("" + (val + 1));
                } catch (NumberFormatException nfe) {
                    Log.e(TAG, "Invalid entry: " + nfe);
                }
                break;
            case R.id.dec_count:
                cv = (TextView) v.getTag();
                try {
                    val = Integer.parseInt(cv.getText().toString());
                    if (val > 0) {
                        cv.setText("" + (val - 1));
                    }
                } catch (NumberFormatException nfe) {
                    Log.e(TAG, "Invalid entry: " + nfe);
                }
                break;
            case R.id.scan_text:
                mScanResultView = (TextView)v;
                Intent intentScan = new Intent(mContext, CaptureActivity.class);
                intentScan.addCategory(Intent.CATEGORY_DEFAULT);
                intentScan.setAction("eci.officeshopper.barcodescanner.SCAN");
                intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE, ONE_D_MODE");

                startActivityForResult(intentScan, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("TEXT", intent.getStringExtra("SCAN_RESULT"));
                    obj.put("FORMAT", intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    obj.put("CANCELLED", false);
                    mScanResultView.setText(intent.getStringExtra("SCAN_RESULT"));

                    TextView cv = (TextView) mScanResultView.getTag();
                    cv.setText("0");
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("TEXT", "");
                    obj.put("FORMAT", "");
                    obj.put("CANCELLED", true);
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
            } else {
                Log.d(TAG, "Invalid Results");
            }
        }
    }

}
