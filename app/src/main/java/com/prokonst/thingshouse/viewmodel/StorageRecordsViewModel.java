package com.prokonst.thingshouse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.dataview.StorageRecord;

import java.util.List;

public class StorageRecordsViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<List<StorageRecord>> storageRecords;

    private Application application;


    public StorageRecordsViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        appRepository = new AppRepository(application);
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByParentId(String parentId) {
        storageRecords = appRepository.getStorageRecordsByParentId(parentId);
        return storageRecords;
    }

    public LiveData<List<StorageRecord>> getStorageRecordsByChildId(String parentId) {
        storageRecords = appRepository.getStorageRecordsByChildId(parentId);
        return storageRecords;
    }

    public void addNewStorageRecord(StorageRecord storageRecord){
        appRepository.insertStorage(storageRecord.createStorage());
    }

    public void updateStorageRecord(StorageRecord storageRecord){
        appRepository.updateStorage(storageRecord.createStorage());
    }

    public void deleteStorageRecord(StorageRecord storageRecord){
        appRepository.deleteStorage(storageRecord.createStorage());
    }

}
