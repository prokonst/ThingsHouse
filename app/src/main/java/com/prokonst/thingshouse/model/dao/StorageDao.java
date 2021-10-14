package com.prokonst.thingshouse.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.prokonst.thingshouse.model.dataview.Delete_StorageWithThings;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.dataview.StorageItem;

import java.util.List;

@Dao
public interface StorageDao {
    @Insert
    void insert(Storage storage);

    @Update
    void update(Storage storage);

    @Delete
    void delete(Storage storage);

    @Query("select * from storages")
    LiveData<List<Storage>> getAllStorages();

    @Query("select * from storages where parent_id = :parentId")
    LiveData<List<Storage>> getStoragesByParentId(String parentId);

    @Query("select * from storages where child_id = :childId")
    LiveData<List<Storage>> getStoragesByChildId(String childId);

    @Transaction
    @Query("select * from storages where parent_id = :parentId")
    LiveData<List<Delete_StorageWithThings>> getStoragesWithTingsByParentId(String parentId);

    @Transaction
    @Query("select * from storages where child_id = :childId")
    LiveData<List<Delete_StorageWithThings>> getStoragesWithTingsByChildId(String childId);

    @Query("select * from storages where parent_id = :parentId and child_id = :childId")
    Storage getStorage(String parentId, String childId);

    @Query("select storages.storage_id, storages.parent_id, storages.child_id, storages.quantity,"
            + " things.thing_id, things.unit, things.barCode, things.name, things.mainPhotoId"
            + "  from things, storages where storages.parent_id = :parentId and storages.child_id = things.thing_id" )
    LiveData<List<StorageItem>> getStorageItemsByParentId(String parentId);

    @Query("select storages.storage_id, storages.parent_id, storages.child_id, storages.quantity,"
            + " things.thing_id, things.unit, things.barCode, things.name, things.mainPhotoId"
            + "  from things, storages where storages.child_id = :childId and storages.parent_id = things.thing_id" )
    LiveData<List<StorageItem>> getStorageItemsByChildId(String childId);

}
