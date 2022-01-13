package com.prokonst.thingshouse.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.model.tables.Storage;

import java.util.List;

public class StorageRecordsViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<List<StorageRecord>> storageRecords;

    private Application application;


    public StorageRecordsViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        appRepository = AppRepository.getInstance(application);
    }

    public AppRepository getAppRepository() {
        return appRepository;
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByParentId(String parentId) {
        storageRecords = appRepository.getStorageRecordsByParentId(parentId);
        return storageRecords;
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByChildId(String parentId) {
        storageRecords = appRepository.getStorageRecordsByChildId(parentId);
        return storageRecords;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewStorageRecord(StorageRecord storageRecord){
        appRepository.insertStorage(storageRecord.createStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateStorageRecord(StorageRecord storageRecord){
        appRepository.updateStorage(storageRecord.createStorage());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteStorageRecord(StorageRecord storageRecord){
        Storage storage = storageRecord.createStorage();
        storage.setIsDeleted(true);
        appRepository.updateStorage(storage);
    }

}
