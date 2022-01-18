package tk.nkduy.cropimage.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import tk.nkduy.cropimage.config.CropImageOverlayConfig;

public class CropImageRectShape extends CropImageShape {

    public CropImageRectShape(CropImageOverlayConfig config) {
        super(config);
    }

    @Override
    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawRect(cropBounds, clearPaint);
    }

    @Override
    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawRect(cropBounds, paint);
    }

    @Override
    public CropImageShapeMask getMask() {
        return new RectShapeMask();
    }

    private static class RectShapeMask implements CropImageShapeMask {
        @Override
        public Bitmap applyMaskTo(Bitmap croppedRegion) {
            //Nothing to do
            return croppedRegion;
        }
    }
}

