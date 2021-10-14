package com.prokonst.thingshouse.model;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.Utils;
import com.prokonst.thingshouse.model.dao.StorageDao;
import com.prokonst.thingshouse.model.dao.ThingDao;
import com.prokonst.thingshouse.model.dataview.Delete_StorageWithThings;
import com.prokonst.thingshouse.model.dataview.StorageItem;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;

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

    public LiveData<List<Delete_StorageWithThings>> getStoragesWithTingsByParentId(String parentId) {
        return storageDao.getStoragesWithTingsByParentId(parentId);
    }

    public LiveData<List<Delete_StorageWithThings>> getStoragesWithTingsByChildId(String childId) {
        return storageDao.getStoragesWithTingsByChildId(childId);
    }

    public LiveData<List<StorageItem>> getStorageItemsByParentId(String parentId) {
        return storageDao.getStorageItemsByParentId(parentId);
    }

    public LiveData<List<StorageItem>> getStorageItemsByChildId(String childId) {
        return storageDao.getStorageItemsByChildId(childId);
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
                    Thing parentThing = thingDao.getThingsByBarCode(barcode);
                    if(parentThing == null)
                        throw new Exception("Not found thing with barcode: " + barcode);

                    Storage storage = storageDao.getStorage(parentThing.getThingId(), childId);
                    if(storage == null) {
                        storage = new Storage(Utils.generateUUIDStr(), parentThing.getThingId(), childId, quantity);
                        storageDao.insert(storage);
                    } else {
                        storage.setQuantity(storage.getQuantity() + quantity);
                        storageDao.update(storage);
                    }
                    return null;
                }
        )).execute();
    }
}
