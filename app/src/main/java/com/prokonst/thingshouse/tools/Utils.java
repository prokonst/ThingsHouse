package com.prokonst.thingshouse.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.prokonst.thingshouse.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class Utils {

    public static String generateUUIDStr() {
        return UUID.randomUUID().toString().toLowerCase();
    }

    public static String getBatchDirectoryPath() {
        //String appFolderPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/CaptureImage";
        String appFolderPath = Environment.getExternalStorageDirectory().toString()
                + "/Android/media/"
                //+ activity.getApplicationContext().getPackageName()
                + MainActivity.class.getPackage().getName()
                + "/Images";

        return  appFolderPath;
    }

    private static final String ORIG_SUFF = "";
    private static final String BASE_SUFF = "_base";
    private static final String PREV_SUFF = "_prev";
    private static final int BASE_RESOLUTION = 1280;
    private static final int PREV_RESOLUTION = 400;

    public static String getImageBasePath(String strUUID){
        return  getImagePath(strUUID, ORIG_SUFF, BASE_SUFF, BASE_RESOLUTION);
    }

    public static String getImagePreviewPath(String strUUID){

        //For create BASE img
        getImageBasePath(strUUID);

        return  getImagePath(strUUID, BASE_SUFF, PREV_SUFF, PREV_RESOLUTION);
    }

    public static String getBaseImageFileName(String strUUID) {
        return strUUID.toLowerCase() + BASE_SUFF + ".jpg";
    }

    private static String getImagePath(String strUUID, String baseSuff, String resSuff, int resolution){
        if(strUUID.isEmpty())
            return "";

        String pathResult = getBatchDirectoryPath() + "/" + strUUID + resSuff + ".jpg";
        File filePreview = new File(pathResult);
        if(filePreview.exists())
            return pathResult;

        String pathBase = getBatchDirectoryPath() + "/" + strUUID + baseSuff + ".jpg";
        File fileOrig = new File(pathBase);
        if(!fileOrig.exists())
            return "";

        Bitmap resizedBitmap = resizeBitmap(pathBase, resolution, resolution);

        int rotation = getExifRotateDegree(pathBase);

        Matrix matrix = new Matrix();
        matrix.setRotate((float)rotation, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(),
                matrix, true);

        try (FileOutputStream out = new FileOutputStream(pathResult)) {
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 72, out);
            return pathResult;
        } catch (IOException e) {
            return pathBase;
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

    public static Uri getFileProviderUri(Context context, File file) {
        return FileProvider.getUriForFile(
                context,
                MainActivity.class.getName() + ".provider",
                file);
    }

    public static Uri getFileProviderUri(Context context, String filePath) {
        File file = new File(filePath);
        return Utils.getFileProviderUri(context, file);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String ConvertDoubleToBase64(double val) {
        byte[] bytes = ByteBuffer.allocate(8).putDouble(val).array();
        String base64 = new String(Base64.getEncoder().encode(bytes));
        return base64;
    }
}
