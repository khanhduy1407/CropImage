package tk.nkduy.cropimage.config;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;

import tk.nkduy.cropimage.image.CropImageBitmapManager;

public class CropImageSaveConfig {

    private Bitmap.CompressFormat compressFormat;
    private int quality;
    private int width, height;
    private Uri dstUri;

    public CropImageSaveConfig(Uri dstPath) {
        this.dstUri = dstPath;
        this.compressFormat = Bitmap.CompressFormat.PNG;
        this.width = CropImageBitmapManager.SIZE_UNSPECIFIED;
        this.height = CropImageBitmapManager.SIZE_UNSPECIFIED;
        this.quality = 90;
    }

    public Bitmap.CompressFormat getCompressFormat() {
        return compressFormat;
    }

    public int getQuality() {
        return quality;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Uri getDstUri() {
        return dstUri;
    }

    public static class Builder {

        private CropImageSaveConfig saveConfig;

        public Builder(Uri dstPath) {
            saveConfig = new CropImageSaveConfig(dstPath);
        }

        public Builder setSize(int width, int height) {
            saveConfig.width = width;
            saveConfig.height = height;
            return this;
        }

        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            saveConfig.compressFormat = compressFormat;
            return this;
        }

        public Builder setQuality(@IntRange(from = 0, to = 100) int quality) {
            saveConfig.quality = quality;
            return this;
        }

        public Builder saveToFile(Uri uri) {
            saveConfig.dstUri = uri;
            return this;
        }

        public CropImageSaveConfig build() {
            return saveConfig;
        }
    }


}
