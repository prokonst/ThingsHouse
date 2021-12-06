package com.prokonst.thingshouse.model;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
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
        return thingDao.getThingById(thingId, Authorization.getCurrentUser().getUid());
    }

    public LiveData<List<Thing>> getThings() {
        return thingDao.getAllThings(Authorization.getCurrentUser().getUid());
    }
/*
    public List<Thing> getThingsByBarCode(String barCode) {
        return thingDao.getThingsByBarCode(barCode);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertThing(Thing thing) {
        (new AsyncTaskCUD(application,
                () -> {
                    thing.calculateHash();
                    thingDao.insert(thing);
                    return null;
                }
        )).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateThing(Thing thing) {
        (new AsyncTaskCUD(application,
                () -> {
                    thing.calculateHash();
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
        return storageDao.getAllStorages(Authorization.getCurrentUser().getUid());
    }

    public LiveData<List<Storage>> getStoragesByParentId(String parentId) {
        return storageDao.getStoragesByParentId(parentId, Authorization.getCurrentUser().getUid());
    }

    public LiveData<List<Storage>> getStoragesByChildId(String childId) {
        return storageDao.getStoragesByChildId(childId, Authorization.getCurrentUser().getUid());
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByParentId(String parentId) {
        return storageDao.getStorageRecordsByParentId(parentId, Authorization.getCurrentUser().getUid());
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByChildId(String childId) {
        return storageDao.getStorageRecordsByChildId(childId, Authorization.getCurrentUser().getUid());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertStorage(Storage storage) {
        (new AsyncTaskCUD(application,
                () -> {
                    storage.calculateHash();
                    storageDao.insert(storage);
                    return null;
                }
        )).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateStorage(Storage storage) {
        (new AsyncTaskCUD(application,
                () -> {
                    storage.calculateHash();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addQuantityToStorageByParentId(String parentId, String childId, double quantity) {
        (new AsyncTaskCUD(application,
                () -> {
                    Storage storage = storageDao.getStorage(parentId, childId, Authorization.getCurrentUser().getUid());
                    if(storage == null) {
                        storage = new Storage(Utils.generateUUIDStr(), parentId, childId, quantity);
                        storage.calculateHash();
                        storageDao.insert(storage);
                    } else {
                        storage.setQuantity(storage.getQuantity() + quantity);
                        storage.calculateHash();
                        storageDao.update(storage);
                    }
                    return null;
                }
        )).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addQuantityToStorageByBarcode(String barcode, String childId, double quantity) {
        (new AsyncTaskCUD(application,
                () -> {
                    Thing parentThing = thingDao.getThingByBarCode(barcode, Authorization.getCurrentUser().getUid());
                    if(parentThing == null)
                        throw new Exception("Not found thing with barcode: " + barcode);

                    Storage storage = storageDao.getStorage(parentThing.getId(), childId, Authorization.getCurrentUser().getUid());
                    if(storage == null) {
                        storage = new Storage(Utils.generateUUIDStr(), parentThing.getId(), childId, quantity);
                        storage.calculateHash();
                        storageDao.insert(storage);
                    } else {
                        storage.setQuantity(storage.getQuantity() + quantity);
                        storage.calculateHash();
                        storageDao.update(storage);
                    }
                    return null;
                }
        )).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void moveStorageByBarcode(String barcode, StorageRecord storageRecord) {
        (new AsyncTaskCUD(application,
                () -> {
                    Thing newParentThing = thingDao.getThingByBarCode(barcode, Authorization.getCurrentUser().getUid());
                    if(newParentThing == null)
                        throw new Exception("Not found thing with barcode: " + barcode);

                    moveStorage(storageRecord.getParentId(),
                            storageRecord.getChildId(),
                            newParentThing.getId());

                    return null;
                }
        )).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void moveStorage(String oldParentThingId,
                             String movingThingId,
                             String newParentThingId) throws Exception {

        if(newParentThingId.equals(oldParentThingId))
            throw new Exception("Is moving to the same parent");

        if(newParentThingId.equals(movingThingId))
            throw new Exception("Error: apply to self");

        Storage oldStorage = storageDao.getStorage(oldParentThingId, movingThingId, Authorization.getCurrentUser().getUid());
        if(oldStorage == null)
            throw new Exception("oldStorage is NULL" + oldParentThingId + "/" + movingThingId);

        if(oldStorage.getQuantity() == 0.0)
            throw new Exception("Moving quantity is 0.0");

        Storage newStorage = storageDao.getStorage(newParentThingId, movingThingId, Authorization.getCurrentUser().getUid());
        if(newStorage == null) {
            newStorage = new Storage(Utils.generateUUIDStr(), newParentThingId, movingThingId, oldStorage.getQuantity());
            newStorage.calculateHash();
            storageDao.insert(newStorage);
        } else {
            newStorage.setQuantity(newStorage.getQuantity() + oldStorage.getQuantity());
            newStorage.calculateHash();
            storageDao.update(newStorage);
        }

        oldStorage.setQuantity(0);
        oldStorage.calculateHash();
        storageDao.update(oldStorage);
    }
}
