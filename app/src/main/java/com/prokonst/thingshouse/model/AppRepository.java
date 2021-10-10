package com.prokonst.thingshouse.model;

import android.app.Application;
import android.app.AsyncNotedAppOp;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.xml.transform.Result;

public class AppRepository {
    private ThingDao thingDao;

    private LiveData<List<Thing>> things;

    public AppRepository(Application application) {
        ThingsDataBase database = ThingsDataBase.getInstance(application);
        thingDao = database.getThingDao();
    }

    public LiveData<List<Thing>> getThings() {
        return thingDao.getAllThings();
    }

    public List<Thing> getThingsByBarCode(String barCode) {
        return thingDao.getThingsByBarCode(barCode);
    }

    public void insertThing(Thing thing) {
        (new InsertThingAsyncTask(thingDao)).execute(thing);
    }

    private static  class InsertThingAsyncTask extends AsyncTask<Thing, Void, Void> {

        private ThingDao thingDao;

        public InsertThingAsyncTask(ThingDao thingDao) {
            this.thingDao = thingDao;
        }

        @Override
        protected Void doInBackground(Thing... things) {

            thingDao.insert(things[0]);

            return null;
        }
    }

    public void updateThing(Thing thing, Context context) {
        UpdateThingAsyncTask updateThingAsyncTask = new UpdateThingAsyncTask(thingDao, context);
        updateThingAsyncTask.execute(thing);
    }

    private static  class UpdateThingAsyncTask extends AsyncTask<Thing, Void, Void> {

        private boolean onPostExecuteCalled = false;
        private Exception exception = null;
        private Context context;

        private ThingDao thingDao;

        public UpdateThingAsyncTask(ThingDao thingDao, Context context) {
            this.thingDao = thingDao;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Thing... things) {
            try {
                thingDao.update(things[0]);
            } catch (Exception ex) {
                exception = ex;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (this.onPostExecuteCalled) {
                return;
            }

            this.onPostExecuteCalled = true;
            super.onPostExecute(unused);

            if(exception != null) {
                if(exception instanceof SQLiteConstraintException && exception.getMessage().contains("barCode")){
                    Toast.makeText(context, "BD Error: Likely bar code is already used", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void deleteThing(Thing thing) {
        (new DeleteThingAsyncTask(thingDao)).execute(thing);
    }

    private static  class DeleteThingAsyncTask extends AsyncTask<Thing, Void, Void> {

        private ThingDao thingDao;

        public DeleteThingAsyncTask(ThingDao thingDao) {
            this.thingDao = thingDao;
        }

        @Override
        protected Void doInBackground(Thing... things) {

            thingDao.delete(things[0]);

            return null;
        }
    }
}
