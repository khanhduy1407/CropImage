package tk.nkduy.cropimage.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import tk.nkduy.cropimage.config.CropImageSaveConfig;
import tk.nkduy.cropimage.shape.CropImageShapeMask;
import tk.nkduy.cropimage.util.CropImageUtils;

import java.io.IOException;
import java.io.OutputStream;

class CropImageTask extends AsyncTask<Void, Void, Throwable> {

    private Context context;
    private CropArea cropArea;
    private CropImageShapeMask mask;
    private Uri srcUri;
    private CropImageSaveConfig saveConfig;

    public CropImageTask(
            Context context, CropArea cropArea, CropImageShapeMask mask,
            Uri srcUri, CropImageSaveConfig saveConfig) {
        this.context = context;
        this.cropArea = cropArea;
        this.mask = mask;
        this.srcUri = srcUri;
        this.saveConfig = saveConfig;
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {
            Bitmap bitmap = CropImageBitmapManager.get().loadToMemory(
                    context, srcUri, saveConfig.getWidth(),
                    saveConfig.getHeight());

            if (bitmap == null) {
                return new NullPointerException("Failed to load bitmap");
            }

            Bitmap cropped = cropArea.applyCropTo(bitmap);

            cropped = mask.applyMaskTo(cropped);

            Uri dst = saveConfig.getDstUri();
            OutputStream os = context.getContentResolver().openOutputStream(dst);
            cropped.compress(saveConfig.getCompressFormat(), saveConfig.getQuality(), os);
            CropImageUtils.closeSilently(os);

            bitmap.recycle();
            cropped.recycle();
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if (throwable == null) {
            CropImageResultReceiver.onCropCompleted(context, saveConfig.getDstUri());
        } else {
            CropImageResultReceiver.onCropFailed(context, throwable);
        }
    }
}