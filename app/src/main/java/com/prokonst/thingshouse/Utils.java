package com.prokonst.thingshouse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Utils {

    private static CaptureCameraImage captureCameraImage;

    public static CaptureCameraImage getCaptureCameraImage() {
        return captureCameraImage;
    }

    public static void setCaptureCameraImage(CaptureCameraImage captureCameraImage) {
        Utils.captureCameraImage = captureCameraImage;
    }

    public static String generateUUIDStr() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    private static String getBatchDirectoryPath() {
        //String appFolderPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/CaptureImage";
        String appFolderPath = Environment.getExternalStorageDirectory().toString()
                + "/Android/media/"
                //+ activity.getApplicationContext().getPackageName()
                + Utils.class.getPackage().getName()
                + "/Images";

        return  appFolderPath;
    }

    public static String getImagePreviewPath(String strUUID){
        if(strUUID.isEmpty())
            return "";

        String pathPreview = getBatchDirectoryPath() + "/" + strUUID + "_prev.jpg";
        File filePreview = new File(pathPreview);
        if(filePreview.exists())
            return pathPreview;

        String pathOrig = getBatchDirectoryPath() + "/" + strUUID + ".jpg";
        File fileOrig = new File(pathOrig);
        if(!fileOrig.exists())
            return "";



        //Bitmap resizedBitmap = decodeSampledBitmapFromFile(pathOrig, 200, 200);
        Bitmap resizedBitmap = resizeBitmap(pathOrig, 400, 400);


//        int rotation = getExifRotateDegree(pathOrig);
        int rotation = 90;
        Matrix matrix = new Matrix();
//        //matrix.postRotate(rotation);
        matrix.setRotate((float)rotation, resizedBitmap.getWidth(), resizedBitmap.getHeight());
//
        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(),
                matrix, true);
//        Bitmap rotatedBitmap = resizedBitmap;

        try (FileOutputStream out = new FileOutputStream(pathPreview)) {
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 72, out);
            return pathPreview;
        } catch (IOException e) {
            return pathOrig;
        }
    }
    private static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;


        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    //Данный код не работает, всегда ориентация - 0
    private static int getExifRotateDegree(String photoPath){
        try {
            ExifInterface exif;
            exif = new ExifInterface(photoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("Orientation", Integer.toString(orientation));
            if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                return 90;
            if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                return 180;
            if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                return 270;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getBatchDirectoryPathAndCreate() throws IOException {
        String appFolderPath = Utils.getBatchDirectoryPath();
        File dir = new File(appFolderPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Can't create: " + appFolderPath);
        }

        return appFolderPath;
    }
}
