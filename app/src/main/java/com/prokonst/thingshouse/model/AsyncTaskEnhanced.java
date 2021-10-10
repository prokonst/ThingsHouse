package com.prokonst.thingshouse.model;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
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

        notifyUserAboutException(this.exception);
    }

    private void notifyUserAboutException(Exception ex) {
        if(ex == null)
            return;
        if(ex instanceof SQLiteConstraintException){
            if(ex.getMessage().contains("barCode")) {
                showAppToast("BD Error: Likely bar code is already used");
            }
            else {
                showAppToast("BD Error: " + ex.toString());
            }
        } else {
            showAppToast(ex.getMessage());
        }
    }

    private void showAppToast(String message) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show();
    }

    protected abstract Result doInBackgroundWithFaultTolerance(Params... params) throws Exception;
}
