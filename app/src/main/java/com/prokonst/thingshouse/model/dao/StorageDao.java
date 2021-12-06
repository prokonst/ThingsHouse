package com.prokonst.thingshouse.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.prokonst.thingshouse.model.dataview.StorageRecord;
import com.prokonst.thingshouse.model.tables.Storage;

import java.util.List;

@Dao
public interface StorageDao {
    @Insert
    void insert(Storage storage);

    @Update
    void update(Storage storage);

    @Delete
    void delete(Storage storage);

    @Query("select * from storages where userId = :userId")
    LiveData<List<Storage>> getAllPhysicalStorages(String userId);

    @Query("select * from storages where userId = :userId and isDeleted = 0")
    LiveData<List<Storage>> getAllStorages(String userId);

    @Query("select * from storages where parent_id = :parentId and userId = :userId and isDeleted = 0")
    LiveData<List<Storage>> getStoragesByParentId(String parentId, String userId);

    @Query("select * from storages where child_id = :childId and userId = :userId and isDeleted = 0")
    LiveData<List<Storage>> getStoragesByChildId(String childId, String userId);

    @Query("select * from storages where parent_id = :parentId and child_id = :childId and userId = :userId and isDeleted = 0")
    Storage getStorage(String parentId, String childId, String userId);

    @Query("select storages.storage_id, storages.parent_id, storages.child_id, storages.quantity,"
            + " things.thing_id, things.unit, things.barCode, things.name, things.mainPhotoId"
            + "  from things, storages where storages.parent_id = :parentId and storages.child_id = things.thing_id"
            + " and storages.userId = :userId and storages.isDeleted = 0" )
    LiveData<List<StorageRecord>> getStorageRecordsByParentId(String parentId, String userId);

    @Query("select storages.storage_id, storages.parent_id, storages.child_id, storages.quantity,"
            + " things.thing_id, things.unit, things.barCode, things.name, things.mainPhotoId"
            + "  from things, storages where storages.child_id = :childId and storages.parent_id = things.thing_id"
            + " and storages.userId = :userId and storages.isDeleted = 0" )
    LiveData<List<StorageRecord>> getStorageRecordsByChildId(String childId, String userId);

}
