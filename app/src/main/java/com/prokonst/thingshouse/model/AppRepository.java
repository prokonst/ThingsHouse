package com.prokonst.thingshouse.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class AppRepository {

    private  Application application;
    private ThingDao thingDao;
    private StorageDao storageDao;

    private LiveData<List<Thing>> things;

    public AppRepository(Application application) {
        this.application = application;
        ThingsDataBase database = ThingsDataBase.getInstance(application);
        thingDao = database.getThingDao();
        storageDao = database.getStorageDao();
    }

    //CRUD For Thing

    public LiveData<List<Thing>> getThings() {
        return thingDao.getAllThings();
    }
/*
    public List<Thing> getThingsByBarCode(String barCode) {
        return thingDao.getThingsByBarCode(barCode);
    }*/

    public void insertThing(Thing thing) {
        (new AsyncTaskCUD(application,
                () -> {
                    thingDao.insert(thing);
                    return null;
                }
        )).execute();
    }

    public void updateThing(Thing thing) {
        (new AsyncTaskCUD(application,
                () -> {
                    thingDao.update(thing);
                    return null;
                }
                )).execute();
    }


    public void deleteThing(Thing thing) {
        (new AsyncTaskCUD(application,
                () -> {
                    thingDao.delete(thing);
                    return null;
                }
        )).execute();
    }


    //CRUD For Storage

    public LiveData<List<Storage>> getStorages() {
        return storageDao.getAllStorages();
    }

    public LiveData<List<Storage>> getStoragesByParentId(String parentId) {
        return storageDao.getStoragesByParentId(parentId);
    }

    public LiveData<List<Storage>> getStoragesByChildId(String childId) {
        return storageDao.getStoragesByChildId(childId);
    }

    public void insertStorage(Storage storage) {
        (new AsyncTaskCUD(application,
                () -> {
                    storageDao.insert(storage);
                    return null;
                }
        )).execute();
    }

    public void updateStorage(Storage storage) {
        (new AsyncTaskCUD(application,
                () -> {
                    storageDao.update(storage);
                    return null;
                }
        )).execute();
    }


    public void deleteStorage(Storage storage) {
        (new AsyncTaskCUD(application,
                () -> {
                    storageDao.delete(storage);
                    return null;
                }
        )).execute();
    }
}
