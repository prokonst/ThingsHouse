package com.prokonst.thingshouse.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AppRepository {

    private  Application application;
    private ThingDao thingDao;

    private LiveData<List<Thing>> things;

    public AppRepository(Application application) {
        this.application = application;
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
        (new InsertThingAsyncTask(application, thingDao)).execute(thing);
    }

    private static  class InsertThingAsyncTask extends AsyncTaskEnhanced<Thing, Void, Void> {

        public InsertThingAsyncTask(Application application, ThingDao thingDao) {
            super(application, thingDao);
        }

        @Override
        protected Void doInBackgroundWithFaultTolerance(Thing... things) throws Exception {
            thingDao.insert(things[0]);
            return null;
        }
    }

    public void updateThing(Thing thing) {
        (new UpdateThingAsyncTask(application, thingDao)).execute(thing);
    }

    private static class UpdateThingAsyncTask extends AsyncTaskEnhanced<Thing, Void, Void> {

        public UpdateThingAsyncTask(Application application, ThingDao thingDao) {
            super(application, thingDao);
        }

        @Override
        protected Void doInBackgroundWithFaultTolerance(Thing... things) throws Exception {
            thingDao.update(things[0]);
            return null;
        }
    }

    public void deleteThing(Thing thing) {
        (new DeleteThingAsyncTask(application, thingDao)).execute(thing);
    }

    private static class DeleteThingAsyncTask extends AsyncTaskEnhanced<Thing, Void, Void> {

        public DeleteThingAsyncTask(Application application, ThingDao thingDao) {
            super(application, thingDao);
        }

        @Override
        protected Void doInBackgroundWithFaultTolerance(Thing... things) throws Exception {
            thingDao.delete(things[0]);
            return null;
        }
    }
}
