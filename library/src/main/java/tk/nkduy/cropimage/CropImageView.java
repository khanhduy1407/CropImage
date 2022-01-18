package tk.nkduy.cropimage;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import tk.nkduy.cropimage.config.ConfigChangeListener;
import tk.nkduy.cropimage.config.CropImgViewConfig;
import tk.nkduy.cropimage.config.CropImageOverlayConfig;
import tk.nkduy.cropimage.config.CropImageSaveConfig;
import tk.nkduy.cropimage.image.CropArea;
import tk.nkduy.cropimage.image.CropImageBitmapManager;
import tk.nkduy.cropimage.image.CropImageResultReceiver;
import tk.nkduy.cropimage.util.LoadBitmapCommand;
import tk.nkduy.cropimage.shape.CropImageShapeMask;
import tk.nkduy.cropimage.util.CropImageLog;

public class CropImageView extends FrameLayout {

    private CropImgView imageView;
    private CropImageOverlayView overlayView;

    private CropImageOverlayConfig overlayConfig;
    private CropImgViewConfig imageConfig;

    private CropImgView.GestureProcessor gestureDetector;

    private Uri imageUri;
    private LoadBitmapCommand loadBitmapCommand;

    private ErrorListener errorListener;
    private CropSaveCompleteListener cropSaveCompleteListener;

    private CropImageResultReceiver cropImageResultReceiver;

    public CropImageView(Context context) {
        super(context);
        init(null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        imageConfig = CropImgViewConfig.createFromAttributes(getContext(), attrs);
        initImageView();

        overlayConfig = CropImageOverlayConfig.createFromAttributes(getContext(), attrs);
        overlayConfig.addConfigChangeListener(new ReInitOverlayOnResizeModeChange());
        initOverlayView();

        cropImageResultReceiver = new CropImageResultReceiver();
        cropImageResultReceiver.register(getContext());
        cropImageResultReceiver.setListener(new CropResultRouter());
    }

    private void initImageView() {
        if (imageConfig == null) {
            throw new IllegalStateException("imageConfig must be initialized before calling this method");
        }
        imageView = new CropImgView(getContext(), imageConfig);
        imageView.setBackgroundColor(Color.BLACK);
        gestureDetector = imageView.getImageTransformGestureDetector();
        addView(imageView);
    }

    private void initOverlayView() {
        if (imageView == null || overlayConfig == null) {
            throw new IllegalStateException("imageView and overlayConfig must be initialized before calling this method");
        }
        overlayView = overlayConfig.isDynamicCrop() ?
                new CropImageDynamicOverlayView(getContext(), overlayConfig) :
                new CropImageOverlayView(getContext(), overlayConfig);
        overlayView.setNewBoundsListener(imageView);
        imageView.setImagePositionedListener(overlayView);
        addView(overlayView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (loadBitmapCommand != null) {
            loadBitmapCommand.setDimensions(w, h);
            loadBitmapCommand.tryExecute(getContext());
        }
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //I think this "redundant" if statements improve code readability
        try {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            gestureDetector.onDown(ev);
            return false;
        }
        if (overlayView.isResizing() || overlayView.isDraggingCropArea()) {
            return false;
        }
        return true;
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            gestureDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(widthMeasureSpec, heightMeasureSpec);
        overlayView.measure(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
        imageView.notifyImagePositioned();
        setMeasuredDimension(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
    }

    @Override
    public void invalidate() {
        imageView.invalidate();
        overlayView.invalidate();
    }

    public CropImageOverlayConfig configureOverlay() {
        return overlayConfig;
    }

    public CropImgViewConfig configureImage() {
        return imageConfig;
    }

    public void setImageUri(Uri uri) {
        imageUri = uri;
        loadBitmapCommand = new LoadBitmapCommand(
                uri, getWidth(), getHeight(),
                new BitmapLoadListener());
        loadBitmapCommand.tryExecute(getContext());
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        overlayView.setDrawOverlay(true);
    }

    public void crop(CropImageSaveConfig saveConfig) {
        CropArea cropArea = CropArea.create(
                imageView.getImageRect(),
                imageView.getImageRect(),
                overlayView.getCropRect());
        CropImageShapeMask mask = overlayConfig.getCropShape().getMask();
        CropImageBitmapManager.get().crop(
                getContext(), cropArea, mask,
                imageUri, saveConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (imageUri != null) {
            CropImageBitmapManager loader = CropImageBitmapManager.get();
            loader.unregisterLoadListenerFor(imageUri);
            loader.removeIfCached(imageUri);
        }
        if (cropImageResultReceiver != null) {
            cropImageResultReceiver.unregister(getContext());
        }
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setCropSaveCompleteListener(CropSaveCompleteListener cropSaveCompleteListener) {
        this.cropSaveCompleteListener = cropSaveCompleteListener;
    }

    private class BitmapLoadListener implements CropImageBitmapManager.BitmapLoadListener {

        @Override
        public void onBitmapLoaded(Uri imageUri, Bitmap bitmap) {
            setImage(bitmap);
        }

        @Override
        public void onLoadFailed(Throwable e) {
            CropImageLog.e("Crop Image loading from [" + imageUri + "] failed", e);
            overlayView.setDrawOverlay(false);
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    private class CropResultRouter implements CropImageResultReceiver.Listener {

        @Override
        public void onCropSuccess(Uri croppedUri) {
            if (cropSaveCompleteListener != null) {
                cropSaveCompleteListener.onCroppedRegionSaved(croppedUri);
            }
        }

        @Override
        public void onCropFailed(Throwable e) {
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    private class ReInitOverlayOnResizeModeChange implements ConfigChangeListener {

        @Override
        public void onConfigChanged() {
            if (shouldReInit()) {
                overlayConfig.removeConfigChangeListener(overlayView);
                boolean shouldDrawOverlay = overlayView.isDrawn();
                removeView(overlayView);

                initOverlayView();
                overlayView.setDrawOverlay(shouldDrawOverlay);

                invalidate();
            }
        }

        private boolean shouldReInit() {
            return overlayConfig.isDynamicCrop() != (overlayView instanceof CropImageDynamicOverlayView);
        }
    }

    public interface CropSaveCompleteListener {
        void onCroppedRegionSaved(Uri bitmapUri);
    }

    public interface ErrorListener {
        void onError(Throwable e);
    }
}
