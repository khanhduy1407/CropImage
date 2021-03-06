package tk.nkduy.cropimage.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import tk.nkduy.cropimage.image.CropImageResultReceiver;
import tk.nkduy.cropimage.sample.adapter.CropGalleryAdapter;
import tk.nkduy.cropimage.sample.data.CropGallery;
import tk.nkduy.cropimage.sample.fragment.ChooseImageForCropFragment;
import tk.nkduy.cropimage.sample.fragment.ConfirmDeletePhotoFragment;

public class CropGalleryActivity extends AppCompatActivity implements CropImageResultReceiver.Listener,
    CropGalleryAdapter.Listener, ConfirmDeletePhotoFragment.Listener {

    private static final String TAG_CHOOSE_IMAGE_FRAGMENT = "choose_image";
    private static final String TAG_CONFIRM_DELETE_IMAGE = "confirm_delete";

    private CropImageResultReceiver cropResultReceiver;
    private CropGalleryAdapter cropGalleryAdapter;

    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_gallery);

        container = findViewById(R.id.container);

        cropGalleryAdapter = new CropGalleryAdapter();
        cropGalleryAdapter.setListener(this);
        cropGalleryAdapter.addImages(CropGallery.getCroppedImageUris());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(cropGalleryAdapter);

        cropResultReceiver = new CropImageResultReceiver();
        cropResultReceiver.setListener(this);
        cropResultReceiver.register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_crop_gallery);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onCropSuccess(Uri croppedUri) {
        cropGalleryAdapter.addImage(croppedUri);
    }

    @Override
    public void onNewCropButtonClicked() {
        ChooseImageForCropFragment fragment = new ChooseImageForCropFragment();
        fragment.show(getSupportFragmentManager(), TAG_CHOOSE_IMAGE_FRAGMENT);
    }

    @Override
    public void onLongPressOnImage(Uri image) {
        ConfirmDeletePhotoFragment fragment = new ConfirmDeletePhotoFragment();
        fragment.setListener(this, image);
        fragment.show(getSupportFragmentManager(), TAG_CONFIRM_DELETE_IMAGE);
    }

    @Override
    public void onDeleteConfirmed(Uri image) {
        cropGalleryAdapter.removeImage(image);
        CropGallery.removeFromGallery(image);
    }

    @Override
    public void onCropFailed(Throwable e) {
        Snackbar.make(container,
                getString(R.string.msg_crop_failed, e.getMessage()),
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cropResultReceiver.unregister(this);
    }
}
