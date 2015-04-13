package eci.officeshopper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Creates Bitmap from image url in background thread.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "OfficeShopper";
    private ImageView mBmImage;
    private Context mContext;
    private String mFileName;
    private Integer mPlaceholder;
    private Integer position;
    private boolean enableCaching = true;

    public ImageDownloader(Context context, ImageView bmImage, String fileName, Integer placeholder, Integer position) {
        this.mBmImage = bmImage;
        this.mContext = context;
        this.mFileName = fileName;
        this.position = position;
        this.mPlaceholder = placeholder;
        if (null != bmImage) {
            bmImage.setTag(position);
            bmImage.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), mPlaceholder));
        }
    }

    public ImageDownloader(Context context, ImageView bmImage, Integer placeholder, boolean enableCaching, Integer position) {
        this.mBmImage = bmImage;
        this.mContext = context;
        this.position = position;
        this.mPlaceholder = placeholder;
        this.enableCaching = enableCaching;

        if (null != bmImage) {
            bmImage.setTag(position);
            bmImage.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), mPlaceholder));
        }
    }

    protected Bitmap doInBackground(String... urls) {

        String url = urls[0];
        Bitmap mIcon = null;
        File imageFile = null;
        if (enableCaching) {
            imageFile = new File(mContext.getCacheDir(), this.mFileName);
        }

        if (enableCaching && imageFile.exists()) {
            // Get from Cache
            mIcon = BitmapFactory.decodeFile(imageFile.getPath());
        } else {
            try {
                mIcon = BitmapFactory.decodeStream(new java.net.URL(url).openStream());
                if(!enableCaching) {
                    // Don't cache if caching is disabled;
                    return mIcon;
                }

                // Save to Cache
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(mContext.getCacheDir(), this.mFileName));
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.toString(), e);
                }
                //if the file couldn't be saved
                if(!mIcon.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                    Log.e(TAG, "The image could not be saved: " + this.mFileName);
                    mIcon = BitmapFactory.decodeResource(mContext.getResources(), mPlaceholder);
                }

                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        boolean setImage = onExecute(result);
        if (setImage && (null != result) && (null != mBmImage)) {
            // Update only for required position (For ListView optimization)
            if (((Integer)mBmImage.getTag()).equals(position)) {
                mBmImage.setImageBitmap(result);
            }
        }
    }

    public boolean onExecute(Bitmap result) {
        return true;
    }
}

