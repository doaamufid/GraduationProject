package com.example.graduationproject.dialogs;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Equivalent of <AddPhotoDialog/>. In the original both "التقط صورة" and
 * "من المعرض" trigger the same hidden <input type="file"> - we mirror
 * that by pointing both buttons at the same system image picker.
 */
public class AddPhotoDialogFragment extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "add_photo_result";
    public static final String KEY_URI = "uri";
    public static final String KEY_CAPTION = "caption";

    public static AddPhotoDialogFragment newInstance() {
        return new AddPhotoDialogFragment();
    }

    private Uri chosenUri;

    private LinearLayout llPickButtons;
    private ImageView ivPreview;
    private EditText edtCaption;
    private TextView btnSave;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                try {
                    requireContext().getContentResolver()
                            .takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // Some providers don't support persistable permissions; the URI still
                    // works for the lifetime of this dialog either way.
                }
                chosenUri = uri;
                render();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add_photo, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        root.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        llPickButtons = root.findViewById(R.id.llPickButtons);
        ivPreview = root.findViewById(R.id.ivPreview);
        edtCaption = root.findViewById(R.id.edtCaption);
        btnSave = root.findViewById(R.id.btnSave);

        View.OnClickListener launchPicker = v -> imagePicker.launch("image/*");
        root.findViewById(R.id.btnTakePhoto).setOnClickListener(launchPicker);
        root.findViewById(R.id.btnFromGallery).setOnClickListener(launchPicker);

        btnSave.setOnClickListener(v -> {
            if (chosenUri == null) return;
            Bundle result = new Bundle();
            result.putString(KEY_URI, chosenUri.toString());
            result.putString(KEY_CAPTION, edtCaption.getText().toString().trim());
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        render();
        return root;
    }

    private void render() {
        boolean hasImage = chosenUri != null;
        llPickButtons.setVisibility(hasImage ? View.GONE : View.VISIBLE);
        ivPreview.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        edtCaption.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        btnSave.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        if (hasImage) {
            ivPreview.setImageURI(chosenUri);
        }
    }

    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_SurvivalBox_BottomSheet;
    }
}
