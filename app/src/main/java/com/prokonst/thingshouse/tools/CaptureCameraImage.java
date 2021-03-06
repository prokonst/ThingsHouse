package com.prokonst.thingshouse.tools;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.prokonst.thingshouse.BuildConfig;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Thing;

import java.io.File;
import java.io.IOException;

public class CaptureCameraImage {

    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private AppRepository appRepository;

    private String mImageUUID;
    private Uri mOutputFileProviderUri;
    private Uri mOutputFileUri;
    private File mImageFile;

    private FragmentActivity mActivity;
    Thing mThing;

    private ActivityResultLauncher<Intent> startCaptureImageActivityResultLauncher;

    private static CaptureCameraImage captureCameraImage = null;

    public static void setCaptureCameraImage(FragmentActivity activity) {
        if(captureCameraImage == null){
            captureCameraImage = new CaptureCameraImage(activity);
        }
        else {
            captureCameraImage.setmActivity(activity);
        }
    }


    public static CaptureCameraImage getCaptureCameraImage(){

        return captureCameraImage;
    }


    public CaptureCameraImage(FragmentActivity activity) {
        setmActivity(activity);

        appRepository = new AppRepository(activity.getApplication());

        //Request For Premissions
        if(isNotAllPermissionsGranted()){
            ActivityCompat.requestPermissions(mActivity, REQUIRED_PERMISSIONS, 1000);
        }

        //???????????????????? ???????????????? ???????????????????? ???? ???????????? ???????????? ???? ?????????????? ????????????????????
        //????. ???????????? https://trendoceans.com/how-to-fix-exposed-beyond-app-through-clipdata-item-geturi/
        //???????? ?????????????????????????????????? ?????? ?? ???????????? FileProvider.getUriForFile() ???????????????????????? ???????????? Uri.fromFile(file)
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());


    }

    public void setmActivity(FragmentActivity mActivity) {
        this.mActivity = mActivity;

        startCaptureImageActivityResultLauncher = mActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    if(isCaptureImageFileExists() && mThing != null){
                        mThing.setMainPhotoId(mImageUUID);
                        appRepository.updateThing(mThing);
                    }
                });
    }


    private void setDefaultValues(){
        mImageUUID = null;
        mOutputFileProviderUri = null;
        mOutputFileUri = null;
        mImageFile = null;
    }

    public void capture(Thing thing){
        setDefaultValues();

        mThing = thing;
        mImageUUID = Utils.generateUUIDStr();
        try {
            mImageFile = new File(Utils.getBatchDirectoryPathAndCreate(), mImageUUID + ".jpg");
        } catch (IOException ex) {
            showMessage("EXCEPTION: " + ex.getMessage());
            return;
        }

        mOutputFileUri = Uri.fromFile(mImageFile);

        mOutputFileProviderUri = FileProvider.getUriForFile(
                mActivity,
                BuildConfig.APPLICATION_ID + "." + mActivity.getLocalClassName() + ".provider",
                mImageFile);

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileProviderUri);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startCaptureImageActivityResultLauncher.launch(intent);
        }
        catch (Exception ex){
            showMessage("EXCEPTION: " + ex.getMessage());
        }
    }


    public boolean isCaptureImageFileExists(){
        return mImageFile != null && mImageFile.exists() && mImageFile.length() > 0;
    }



    private void showMessage(String message){
        Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
    }

    private boolean isNotAllPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }
}
