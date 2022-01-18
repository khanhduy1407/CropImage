package tk.nkduy.cropimage.shape;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface CropImageShapeMask extends Serializable {
    Bitmap applyMaskTo(Bitmap croppedRegion);
}
