package tk.nkduy.cropimage.sample.config;

import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import tk.nkduy.cropimage.AspectRatio;
import tk.nkduy.cropimage.CropImageView;
import tk.nkduy.cropimage.config.ConfigChangeListener;
import tk.nkduy.cropimage.config.CropImageSaveConfig;
import tk.nkduy.cropimage.sample.R;
import tk.nkduy.cropimage.sample.adapter.AspectRatioPreviewAdapter;
import tk.nkduy.cropimage.sample.data.CropGallery;
import tk.nkduy.cropimage.shape.CropImageOvalShape;
import tk.nkduy.cropimage.shape.CropImageRectShape;
import tk.nkduy.cropimage.shape.CropImageShape;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.MaterialSeekBarPreference;
import com.yarolegovich.mp.io.StorageModule;
import com.yarolegovich.mp.util.Utils;

import java.util.Set;

public class CropViewConfigurator implements StorageModule, ConfigChangeListener,
        AspectRatioPreviewAdapter.OnNewSelectedListener {

    private CropImageView cropImageView;
    private CropImageSaveConfig.Builder saveConfig;

    private RecyclerView fixedRatioList;

    private MaterialSeekBarPreference seekBarPreference;

    public CropViewConfigurator(CropImageView cropImageView, MaterialPreferenceScreen screen) {
        this.cropImageView = cropImageView;
        this.saveConfig = new CropImageSaveConfig.Builder(CropGallery.createNewEmptyFile());
        this.seekBarPreference = (MaterialSeekBarPreference) screen.findViewById(R.id.scale_seek_bar);

        AspectRatioPreviewAdapter ratioPreviewAdapter = new AspectRatioPreviewAdapter();
        ratioPreviewAdapter.setListener(this);
        fixedRatioList = (RecyclerView) screen.findViewById(R.id.fixed_ratio_list);
        fixedRatioList.setLayoutManager(new LinearLayoutManager(
                cropImageView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        fixedRatioList.setAdapter(ratioPreviewAdapter);

        cropImageView.configureImage().addConfigChangeListener(this);
    }


    @Override
    public void saveBoolean(String key, boolean value) {
        if (Prefs.keys().KEY_DRAW_GRID.equals(key)) {
            cropImageView.configureOverlay().setShouldDrawGrid(value).apply();
        } else if (Prefs.keys().KEY_ENABLE_TRANSLATE.equals(key)) {
            cropImageView.configureImage().setImageTranslationEnabled(value).apply();
        } else if (Prefs.keys().KEY_ENABLE_SCALE.equals(key)) {
            cropImageView.configureImage().setImageScaleEnabled(value).apply();
        } else if (Prefs.keys().KEY_DYNAMIC_CROP.equals(key)) {
            cropImageView.configureOverlay().setDynamicCrop(value).apply();
            fixedRatioList.setVisibility(value ? View.GONE : View.VISIBLE);
        } else if (Prefs.keys().KEY_DASHED_GRID.equals(key)) {
            int dashLength = Utils.dpToPixels(cropImageView.getContext(), 2);
            int spaceLength = Utils.dpToPixels(cropImageView.getContext(), 4);
            float[] intervals = {dashLength, spaceLength};
            PathEffect effect = value ? new DashPathEffect(intervals, 0) : null;
            getGridPaint().setPathEffect(effect);
            cropImageView.invalidate();
        }
    }

    @Override
    public void saveString(String key, String value) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            saveConfig.setCompressFormat(stringToCompressFormat(value));
        } else if (Prefs.keys().KEY_CROP_SHAPE.equals(key)) {
            cropImageView.configureOverlay().setCropShape(stringToCropShape(value)).apply();
        }
    }

    @Override
    public void saveInt(String key, int value) {
        if (Prefs.keys().KEY_GRID_COLOR.equals(key)) {
            cropImageView.configureOverlay().setGridColor(value).apply();
        } else if (Prefs.keys().KEY_OVERLAY_COLOR.equals(key)) {
            cropImageView.configureOverlay().setOverlayColor(value).apply();
        } else if (Prefs.keys().KEY_CORNER_COLOR.equals(key)) {
            cropImageView.configureOverlay().setCornerColor(value).apply();
        } else if (Prefs.keys().KEY_BORDER_COLOR.equals(key)) {
            cropImageView.configureOverlay().setBorderColor(value).apply();
        } else if (Prefs.keys().KEY_IMAGE_QUALITY.equals(key)) {
            saveConfig.setQuality(value);
        } else if (Prefs.keys().KEY_SCALE.equals(key)) {
            float newScale = value / 100f;
            if (Math.abs(newScale - cropImageView.configureImage().getScale()) > 0.01) {
                cropImageView.configureImage().setScale(value / 100f).apply();
            }
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        if (Prefs.keys().KEY_DRAW_GRID.equals(key)) {
            return cropImageView.configureOverlay().shouldDrawGrid();
        } else if (Prefs.keys().KEY_ENABLE_TRANSLATE.equals(key)) {
            return cropImageView.configureImage().isImageTranslationEnabled();
        } else if (Prefs.keys().KEY_ENABLE_SCALE.equals(key)) {
            return cropImageView.configureImage().isImageScaleEnabled();
        } else if (Prefs.keys().KEY_DYNAMIC_CROP.equals(key)) {
            return cropImageView.configureOverlay().isDynamicCrop();
        } else if (Prefs.keys().KEY_DASHED_GRID.equals(key)) {
            return getGridPaint().getPathEffect() != null;
        }
        return false;
    }

    @Override
    public String getString(String key, String defaultVal) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            return compressFormatToString(saveConfig.build().getCompressFormat());
        } else if (Prefs.keys().KEY_CROP_SHAPE.equals(key)) {
            return cropShapeToString(cropImageView.configureOverlay().getCropShape());
        }
        return "";
    }

    @Override
    public int getInt(String key, int defaultVal) {
        if (Prefs.keys().KEY_GRID_COLOR.equals(key)) {
            return cropImageView.configureOverlay().getGridColor();
        } else if (Prefs.keys().KEY_OVERLAY_COLOR.equals(key)) {
            return cropImageView.configureOverlay().getOverlayColor();
        } else if (Prefs.keys().KEY_CORNER_COLOR.equals(key)) {
            return cropImageView.configureOverlay().getCornerColor();
        } else if (Prefs.keys().KEY_BORDER_COLOR.equals(key)) {
            return cropImageView.configureOverlay().getBorderColor();
        } else if (Prefs.keys().KEY_SCALE.equals(key)) {
            return (int) (cropImageView.configureImage().getScale() * 100);
        } else if (Prefs.keys().KEY_IMAGE_QUALITY.equals(key)) {
            return saveConfig.build().getQuality();
        }
        return 0;
    }

    @Override
    public void onNewAspectRatioSelected(AspectRatio ratio) {
        cropImageView.configureOverlay().setAspectRatio(ratio).apply();
    }

    public CropImageSaveConfig getSelectedSaveConfig() {
        return saveConfig.build();
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return null;
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {

    }

    @Override
    public void onConfigChanged() {
        int scale = (int) Math.max(1, cropImageView.configureImage().getScale() * 100);
        seekBarPreference.setValue(scale);
    }

    private Paint getGridPaint() {
        return cropImageView.configureOverlay().getCropShape().getGridPaint();
    }

    private static Bitmap.CompressFormat stringToCompressFormat(String str) {
        return Bitmap.CompressFormat.valueOf(str.toUpperCase());
    }

    private static String compressFormatToString(Bitmap.CompressFormat format) {
        return format.name();
    }

    private CropImageShape stringToCropShape(String str) {
        if ("rectangle".equals(str.toLowerCase())) {
            return new CropImageRectShape(cropImageView.configureOverlay());
        } else if ("oval".equals(str.toLowerCase())) {
            return new CropImageOvalShape(cropImageView.configureOverlay());
        }
        throw new IllegalArgumentException("Unknown shape");
    }

    private static String cropShapeToString(CropImageShape shape) {
        if (shape instanceof CropImageRectShape) {
            return "Rectangle";
        } else if (shape instanceof CropImageOvalShape) {
            return "Oval";
        }
        throw new IllegalArgumentException("Instance of unknown class");
    }
}
