package tk.nkduy.cropimage.sample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.nkduy.cropimage.sample.R;

public class ConfirmDeletePhotoFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Uri image;
    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_delete).setOnClickListener(this);
    }

    public void setListener(Listener listener, Uri image) {
        this.listener = listener;
        this.image = image;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            if (listener != null) {
                listener.onDeleteConfirmed(image);
            }
            dismiss();
        }
    }

    public interface Listener {
        void onDeleteConfirmed(Uri image);
    }
}
