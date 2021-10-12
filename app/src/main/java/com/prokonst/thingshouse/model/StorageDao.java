package com.prokonst.thingshouse.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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
    LiveData<List<StorageWithThings>> getStoragesWithTingsByParentId(String parentId);

    @Transaction
    @Query("select * from storages where child_id = :childId")
    LiveData<List<StorageWithThings>> getStoragesWithTingsByChildId(String childId);

    @Query("select * from storages where parent_id = :parentId and child_id = :childId")
    Storage getStorage(String parentId, String childId);
}
