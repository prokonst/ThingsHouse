package com.prokonst.thingshouse.model;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public abstract class AsyncTaskEnhanced<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private boolean onPostExecuteCalled = false;
    private Exception exception = null;
    private Application application;
    protected ThingDao thingDao;

    public AsyncTaskEnhanced(Application application, ThingDao thingDao) {
        this.application = application;
        this.thingDao = thingDao;
    }

    @Override
    protected final Result doInBackground(Params... params) {
        try {
            this.doInBackgroundWithFaultTolerance(params);
        } catch (Exception ex) {
            this.exception = ex;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (this.onPostExecuteCalled) {
            return;
        }

        this.onPostExecuteCalled = true;
        super.onPostExecute(result);

        notifyUser(this.exception);
    }

    private void notifyUser(Exception ex) {
        if(ex == null) {
            showAppToast("Success");
            return;
        }

        if(ex instanceof SQLiteConstraintException){
            if(ex.getMessage().contains("barCode")) {
                try {
                    showAppToast("BD Error: Likely bar code is already used");
                } catch (Exception exc)
                {
                    showAppToast(exc.getMessage(), Toast.LENGTH_LONG);
                }
            }
            else {
                showAppToast("BD Error: " + ex.toString(), Toast.LENGTH_LONG);
            }
        } else {
            showAppToast(ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void showAppToast(String message, int duration) {
        Toast toast = Toast.makeText(application, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showAppToast(String message){
        showAppToast(message, Toast.LENGTH_SHORT);
    }


    private void showAlertDialogOk(String message) {
        (new AlertDialog.Builder(application.getApplicationContext()))
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    // handle click
                })
                //.setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    protected abstract Result doInBackgroundWithFaultTolerance(Params... params) throws Exception;
}
