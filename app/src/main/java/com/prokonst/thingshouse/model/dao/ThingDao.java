package com.prokonst.thingshouse.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.prokonst.thingshouse.model.tables.Thing;

import java.util.List;

@Dao
public interface ThingDao {
    @Insert
    void insert(Thing thing);

    @Update
    void update(Thing thing);

    @Delete
    void delete(Thing thing);

    @Query("select * from things where userId = :userId")
    LiveData<List<Thing>> getAllPhysicalThings(String userId);

    //@Query("select * from things order by name")
    @Query("select * from things where userId = :userId and isDeleted = 0")
    LiveData<List<Thing>> getActualThings(String userId);

    @Query("select * from things where userId = :userId")
    LiveData<List<Thing>> getAllThings(String userId);
/*
    //@Query("select * from things where name like '%' || :namePart || '%'  order by name")
    @Query("select * from things where name like '%' || :namePart || '%'")
    LiveData<List<Thing>> getThings(String namePart);*/

    @Query("select * from things where barCode = :barCode and userId = :userId and isDeleted = 0")
    Thing getThingByBarCode(String barCode, String userId);

    @Query("select * from things where thing_id = :thingId and userId = :userId and isDeleted = 0")
    LiveData<Thing> getThingById(String thingId, String userId);
}
