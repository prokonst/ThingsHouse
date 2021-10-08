package com.prokonst.thingshouse.model;

import android.app.Application;
import android.app.AsyncNotedAppOp;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

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

    public LiveData<List<Thing>> getThings(String namePart) {
        return thingDao.getThings(namePart);
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

    public void updateThing(Thing thing) {
        (new UpdateThingAsyncTask(thingDao)).execute(thing);
    }

    private static  class UpdateThingAsyncTask extends AsyncTask<Thing, Void, Void> {

        private ThingDao thingDao;

        public UpdateThingAsyncTask(ThingDao thingDao) {
            this.thingDao = thingDao;
        }

        @Override
        protected Void doInBackground(Thing... things) {

            thingDao.update(things[0]);

            return null;
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
