package tk.nkduy.cropimage.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import tk.nkduy.cropimage.AspectRatio;
import tk.nkduy.cropimage.R;
import tk.nkduy.cropimage.shape.CropImageOvalShape;
import tk.nkduy.cropimage.shape.CropImageRectShape;
import tk.nkduy.cropimage.shape.CropImageShape;
import tk.nkduy.cropimage.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

public class CropImageOverlayConfig {

    private static final float DEFAULT_CROP_SCALE = 0.8f;

    public static CropImageOverlayConfig createDefault(Context context) {
        ResUtil r = new ResUtil(context);
        CropImageOverlayConfig config = new CropImageOverlayConfig()
                .setBorderColor(r.color(R.color.cropimg_default_border_color))
                .setCornerColor(r.color(R.color.cropimg_default_corner_color))
                .setGridColor(r.color(R.color.cropimg_default_grid_color))
                .setOverlayColor(r.color(R.color.cropimg_default_overlay_color))
                .setBorderStrokeWidth(r.dimen(R.dimen.cropimg_default_border_stroke_width))
                .setCornerStrokeWidth(r.dimen(R.dimen.cropimg_default_corner_stroke_width))
                .setCropScale(DEFAULT_CROP_SCALE)
                .setGridStrokeWidth(r.dimen(R.dimen.cropimg_default_grid_stroke_width))
                .setMinWidth(r.dimen(R.dimen.cropimg_default_min_width))
                .setMinHeight(r.dimen(R.dimen.cropimg_default_min_height))
                .setAspectRatio(new AspectRatio(2, 1))
                .setShouldDrawGrid(true)
                .setDynamicCrop(true);
        CropImageShape shape = new CropImageRectShape(config);
        config.setCropShape(shape);
        return config;
    }

    public static CropImageOverlayConfig createFromAttributes(Context context, AttributeSet attrs) {
        CropImageOverlayConfig c = CropImageOverlayConfig.createDefault(context);
        if (attrs == null) {
            return c;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropImgView);
        try {
            c.setMinWidth(ta.getDimensionPixelSize(
                    R.styleable.CropImgView_ci_min_crop_width,
                    c.getMinWidth()));
            c.setMinHeight(ta.getDimensionPixelSize(
                    R.styleable.CropImgView_ci_min_crop_height,
                    c.getMinHeight()));
            c.setAspectRatio(new AspectRatio(
                    ta.getInteger(R.styleable.CropImgView_ci_aspect_ratio_w, 1),
                    ta.getInteger(R.styleable.CropImgView_ci_aspect_ratio_h, 1)));
            c.setCropScale(ta.getFloat(
                    R.styleable.CropImgView_ci_crop_scale,
                    c.getCropScale()));
            c.setBorderColor(ta.getColor(
                    R.styleable.CropImgView_ci_border_color,
                    c.getBorderColor()));
            c.setBorderStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropImgView_ci_border_width,
                    c.getBorderStrokeWidth()));
            c.setCornerColor(ta.getColor(
                    R.styleable.CropImgView_ci_corner_color,
                    c.getCornerColor()));
            c.setCornerStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropImgView_ci_corner_width,
                    c.getCornerStrokeWidth()));
            c.setGridColor(ta.getColor(
                    R.styleable.CropImgView_ci_grid_color,
                    c.getGridColor()));
            c.setGridStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropImgView_ci_grid_width,
                    c.getGridStrokeWidth()));
            c.setShouldDrawGrid(ta.getBoolean(
                    R.styleable.CropImgView_ci_draw_grid,
                    c.shouldDrawGrid()));
            c.setOverlayColor(ta.getColor(
                    R.styleable.CropImgView_ci_overlay_color,
                    c.getOverlayColor()));
            c.setCropShape(ta.getInt(R.styleable.CropImgView_ci_crop_shape, 0) == 0 ?
                    new CropImageRectShape(c) :
                    new CropImageOvalShape(c));
            c.setDynamicCrop(ta.getBoolean(
                    R.styleable.CropImgView_ci_dynamic_aspect_ratio,
                    c.isDynamicCrop()));
        } finally {
            ta.recycle();
        }
        return c;
    }

    private int overlayColor;

    private int borderColor;
    private int cornerColor;
    private int gridColor;
    private int borderStrokeWidth;

    private int cornerStrokeWidth;
    private int gridStrokeWidth;

    private int minHeight;
    private int minWidth;

    private AspectRatio aspectRatio;

    private float cropScale;

    private boolean isDynamicCrop;
    private boolean shouldDrawGrid;
    private CropImageShape cropShape;

    private List<ConfigChangeListener> listeners;
    private List<ConfigChangeListener> iterationList;

    public CropImageOverlayConfig() {
        listeners = new ArrayList<>();
        iterationList = new ArrayList<>();
    }

    public int getOverlayColor() {
        return overlayColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public int getCornerColor() {
        return cornerColor;
    }

    public int getBorderStrokeWidth() {
        return borderStrokeWidth;
    }

    public int getCornerStrokeWidth() {
        return cornerStrokeWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getGridColor() {
        return gridColor;
    }

    public int getGridStrokeWidth() {
        return gridStrokeWidth;
    }

    public boolean shouldDrawGrid() {
        return shouldDrawGrid;
    }

    public CropImageShape getCropShape() {
        return cropShape;
    }

    public boolean isDynamicCrop() {
        return isDynamicCrop;
    }

    public float getCropScale() {
        return cropScale;
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
    }

    public CropImageOverlayConfig setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    public CropImageOverlayConfig setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public CropImageOverlayConfig setCornerColor(int cornerColor) {
        this.cornerColor = cornerColor;
        return this;
    }

    public CropImageOverlayConfig setGridColor(int gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public CropImageOverlayConfig setBorderStrokeWidth(int borderStrokeWidth) {
        this.borderStrokeWidth = borderStrokeWidth;
        return this;
    }

    public CropImageOverlayConfig setCornerStrokeWidth(int cornerStrokeWidth) {
        this.cornerStrokeWidth = cornerStrokeWidth;
        return this;
    }

    public CropImageOverlayConfig setGridStrokeWidth(int gridStrokeWidth) {
        this.gridStrokeWidth = gridStrokeWidth;
        return this;
    }

    public CropImageOverlayConfig setCropScale(@FloatRange(from = 0.01, to = 1f) float cropScale) {
        this.cropScale = cropScale;
        return this;
    }

    public CropImageOverlayConfig setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public CropImageOverlayConfig setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public CropImageOverlayConfig setAspectRatio(AspectRatio ratio) {
        this.aspectRatio = ratio;
        return this;
    }

    public CropImageOverlayConfig setShouldDrawGrid(boolean shouldDrawGrid) {
        this.shouldDrawGrid = shouldDrawGrid;
        return this;
    }

    public CropImageOverlayConfig setCropShape(@NonNull CropImageShape cropShape) {
        if (this.cropShape != null) {
            removeConfigChangeListener(this.cropShape);
        }
        this.cropShape = cropShape;
        return this;
    }

    public CropImageOverlayConfig setDynamicCrop(boolean enabled) {
        this.isDynamicCrop = enabled;
        return this;
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeConfigChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    public void apply() {
        iterationList.addAll(listeners);
        for (ConfigChangeListener listener : iterationList) {
            listener.onConfigChanged();
        }
        iterationList.clear();
    }
}
