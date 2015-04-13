package eci.officeshopper.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    Context mContext;

    public NetworkUtils (Context context) {
        this.mContext = context;
    }

    public boolean checkConnectivity () {
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void onAction() {

    }

    public void showAlert (final Integer titleId, final Integer messageId, final Integer textId) {
        new AlertDialog.Builder(mContext).setMessage(messageId)
                .setTitle(titleId)
                .setCancelable(false)
                .setNeutralButton(textId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        onAction();
                    }
                })
                .show();
    }
}
