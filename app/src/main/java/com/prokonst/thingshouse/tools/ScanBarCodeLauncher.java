package com.prokonst.thingshouse.tools;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanBarCodeLauncher {
    public static void startScanBarCodeLauncher(Activity activity, ActivityResultLauncher<Intent> launcher){
        IntentIntegrator intentIntegrator = createIntentIntegrator(activity, launcher);
        launcher.launch(intentIntegrator.createScanIntent());
    }

    private static IntentIntegrator createIntentIntegrator(Activity activity, ActivityResultLauncher<Intent> launcher) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(activity);

        intentIntegrator.setPrompt("For flash use volume up key");
        intentIntegrator.setBeepEnabled(false);
        //intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        return intentIntegrator;
    }

    public static String getBarCode(ActivityResult result) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                IntentIntegrator.REQUEST_CODE, result.getResultCode(), result.getData()
        );
        String contents = intentResult.getContents();
        return contents;
    }

}
