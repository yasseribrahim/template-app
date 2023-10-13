package com.getit.app.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.getit.app.Constants;
import com.getit.app.R;
import com.getit.app.databinding.ActivityProfileEditBinding;
import com.getit.app.models.User;
import com.getit.app.persenters.user.UsersCallback;
import com.getit.app.persenters.user.UsersPresenter;
import com.getit.app.ui.fragments.ProgressDialogFragment;
import com.getit.app.utilities.ToastUtils;
import com.getit.app.utilities.UIUtils;
import com.getit.app.utilities.helpers.StorageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class ProfileEditActivity extends BaseActivity implements UsersCallback, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private static final int REQUEST_CODE_CAMERA_AND_STORAGE = 100;
    private static final String TAG = ProfileEditActivity.class.getSimpleName();
    private ActivityProfileEditBinding binding;
    private User user;
    private UsersPresenter presenter;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new UsersPresenter(this);
        user = StorageHelper.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                try {
                    bind();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        binding.btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        binding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodRequiresTwoPermission();
            }
        });

        bind();
    }

    private void takePhoto() {
        if (hasCameraPermission() && hasStoragePermission()) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
        } else {
            requestPermissions();
        }
    }

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }

    private boolean hasStoragePermission() {
        String[] permissions = getPermissionsStorage();
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private void requestPermissions() {
        if (!hasCameraPermission()) {
            requestCameraPermission();
        } else {
            requestStoragePermission();
        }
    }

    private void requestCameraPermission() {
        String[] permissions = {Manifest.permission.CAMERA};
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, REQUEST_CODE_CAMERA_AND_STORAGE, permissions)
                        .setRationale(R.string.str_message_request_camera_and_storage)
                        .setPositiveButtonText(R.string.str_ok)
                        .setNegativeButtonText(R.string.str_cancel)
                        .build());
    }

    private String[] getPermissionsStorage() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        return permissions;
    }

    private void requestStoragePermission() {
        String[] permissions = getPermissionsStorage();
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, REQUEST_CODE_CAMERA_AND_STORAGE, permissions)
                        .setRationale(R.string.str_message_request_camera_and_storage)
                        .setPositiveButtonText(R.string.str_ok)
                        .setNegativeButtonText(R.string.str_cancel)
                        .build());
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_AND_STORAGE)
    private void methodRequiresTwoPermission() {
        if (hasCameraPermission() && hasStoragePermission()) {
            takePhoto();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = result.getUri();
                if (CropImage.isReadExternalStoragePermissionsRequired(this, uri)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    upload(uri);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                ToastUtils.longToast(error.getMessage());
            }
        }
    }

    private void bind() {
        binding.username.setText("@" + user.getUsername());
        binding.type.setText(UIUtils.getAccountType(user.getType()));
        binding.name.setText(user.getUsername());
        binding.email.setText(user.getUsername());
        binding.phone.setText(user.getPhone());
        binding.address.setText(user.getAddress());

        Glide.with(this).load(user.getImageProfile()).placeholder(R.drawable.ic_account_circle).into(binding.profileImage);
    }

    private void save() {
        String email = binding.email.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        String address = binding.address.getText().toString().trim();
        String name = binding.name.getText().toString().trim();

        if (email.isEmpty()) {
            binding.email.setError(getString(R.string.str_email_invalid));
            binding.email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError(getString(R.string.str_email_invalid));
            binding.email.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            binding.name.setError(getString(R.string.str_full_name_invalid));
            binding.name.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            binding.phone.setError(getString(R.string.str_phone_invalid));
            binding.phone.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            binding.address.setError(getString(R.string.str_address_invalid));
            binding.address.requestFocus();
            return;
        }

        user.setFullName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);

        presenter.save(user);
    }

    @Override
    public void onSaveUserComplete() {
        ToastUtils.longToast(R.string.str_message_updated_successfully);
        finish();
    }

    private void upload(Uri uri) {
        ProgressDialogFragment.show(getSupportFragmentManager());
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(Constants.NODE_NAME_IMAGES + "/" + user.getId());
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ToastUtils.longToast(R.string.str_message_updated_successfully);
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;

                        user.setImageProfile(downloadUrl.toString());
                        userReference.setValue(user);
                        ProgressDialogFragment.hide(getSupportFragmentManager());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ToastUtils.longToast("Error: " + e.getMessage());
                ProgressDialogFragment.hide(getSupportFragmentManager());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.i("ProfileEditActivity", "Uploaded  " + (int) progress + "%");
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        takePhoto();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied:" + requestCode);
    }
}