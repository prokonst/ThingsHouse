package com.prokonst.thingshouse.model;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.tools.Utils;
import com.prokonst.thingshouse.model.dao.StorageDao;
import com.prokonst.thingshouse.model.dao.ThingDao;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.AsyncTaskCUD;

import java.util.List;

public class AppRepository {

    private static AppRepository appRepository;

    private  Application application;
    private ThingDao thingDao;
    private StorageDao storageDao;

    private LiveData<List<Thing>> things;

    public static AppRepository getInstance(Application application) {
        if(AppRepository.appRepository == null)
            AppRepository.appRepository = new AppRepository(application);

        return AppRepository.appRepository;
    }

    private AppRepository(Application application) {
        this.application = application;
        ThingsDataBase database = ThingsDataBase.getInstance(application);
        thingDao = database.getThingDao();
        storageDao = database.getStorageDao();
    }

    //CRUD For Thing

    public LiveData<Thing> getThingById(String thingId) {
        return thingDao.getThingById(thingId);
    }

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

    public LiveData<List<StorageRecord>> getStorageRecordsByParentId(String parentId) {
        return storageDao.getStorageRecordsByParentId(parentId);
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByChildId(String childId) {
        return storageDao.getStorageRecordsByChildId(childId);
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

    public void addQuantityToStorageByParentId(String parentId, String childId, double quantity) {
        (new AsyncTaskCUD(application,
                () -> {
                    Storage storage = storageDao.getStorage(parentId, childId);
                    if(storage == null) {
                        storage = new Storage(Utils.generateUUIDStr(), parentId, childId, quantity);
                        storageDao.insert(storage);
                    } else {
                        storage.setQuantity(storage.getQuantity() + quantity);
                        storageDao.update(storage);
                    }
                    return null;
                }
        )).execute();
    }

    public void addQuantityToStorageByBarcode(String barcode, String childId, double quantity) {
        (new AsyncTaskCUD(application,
                () -> {
                    Thing parentThing = thingDao.getThingByBarCode(barcode);
                    if(parentThing == null)
                        throw new Exception("Not found thing with barcode: " + barcode);

                    Storage storage = storageDao.getStorage(parentThing.getId(), childId);
                    if(storage == null) {
                        storage = new Storage(Utils.generateUUIDStr(), parentThing.getId(), childId, quantity);
                        storageDao.insert(storage);
                    } else {
                        storage.setQuantity(storage.getQuantity() + quantity);
                        storageDao.update(storage);
                    }
                    return null;
                }
        )).execute();
    }

    public void moveStorageByBarcode(String barcode, StorageRecord storageRecord) {
        (new AsyncTaskCUD(application,
                () -> {
                    Thing newParentThing = thingDao.getThingByBarCode(barcode);
                    if(newParentThing == null)
                        throw new Exception("Not found thing with barcode: " + barcode);

                    moveStorage(storageRecord.getParentId(),
                            storageRecord.getChildId(),
                            newParentThing.getId());

                    return null;
                }
        )).execute();
    }

    public void moveStorage(ShowThingsListParameters.ThingIdInterface oldParentThing,
                            ShowThingsListParameters.ThingIdInterface movingThing,
                            ShowThingsListParameters.ThingIdInterface newParentThing) {
        (new AsyncTaskCUD(application,
                () -> {
                    moveStorage(oldParentThing.getThingId(),
                            movingThing.getThingId(),
                            newParentThing.getThingId());

                    return null;
                }
        )).execute();
    }

    private void moveStorage(String oldParentThingId,
                             String movingThingId,
                             String newParentThingId) throws Exception {

        if(newParentThingId.equals(oldParentThingId))
            throw new Exception("Is moving to the same parent");

        if(newParentThingId.equals(movingThingId))
            throw new Exception("Error: apply to self");

        Storage oldStorage = storageDao.getStorage(oldParentThingId, movingThingId);
        if(oldStorage == null)
            throw new Exception("oldStorage is NULL" + oldParentThingId + "/" + movingThingId);

        if(oldStorage.getQuantity() == 0.0)
            throw new Exception("Moving quantity is 0.0");

        Storage newStorage = storageDao.getStorage(newParentThingId, movingThingId);
        if(newStorage == null) {
            newStorage = new Storage(Utils.generateUUIDStr(), newParentThingId, movingThingId, oldStorage.getQuantity());
            storageDao.insert(newStorage);
        } else {
            newStorage.setQuantity(newStorage.getQuantity() + oldStorage.getQuantity());
            storageDao.update(newStorage);
        }

        oldStorage.setQuantity(0);
        storageDao.update(oldStorage);
    }
}
