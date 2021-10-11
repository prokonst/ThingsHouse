package com.prokonst.thingshouse.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
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

    @Query("select * from storages where parentId = :parentId")
    LiveData<List<Storage>> getStoragesByParentId(String parentId);

    @Query("select * from storages where childId = :childId")
    LiveData<List<Storage>> getStoragesByChildId(String childId);
}
