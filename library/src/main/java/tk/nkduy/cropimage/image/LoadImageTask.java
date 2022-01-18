package tk.nkduy.cropimage.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

class LoadImageTask extends AsyncTask<Void, Void, Throwable> {

    private Context context;
    private Uri uri;
    private int desiredWidth;
    private int desiredHeight;

    private Bitmap result;

    public LoadImageTask(Context context, Uri uri, int desiredWidth, int desiredHeight) {
        this.context = context;
        this.uri = uri;
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {
            result = CropImageBitmapManager.get().loadToMemory(
                    context, uri, desiredWidth,
                    desiredHeight);

            if (result == null) {
                return new NullPointerException("Failed to load bitmap");
            }
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        CropImageBitmapManager.get().notifyListener(uri, result, e);
    }
}