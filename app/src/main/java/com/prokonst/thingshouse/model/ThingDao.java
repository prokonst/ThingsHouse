package com.prokonst.thingshouse.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ThingDao {
    @Insert
    void insert(Thing thing);

    @Update
    void update(Thing thing);

    @Delete
    void delete(Thing thing);

    //@Query("select * from things order by name")
    @Query("select * from things")
    LiveData<List<Thing>> getAllThings();

    //@Query("select * from things where name like '%' || :namePart || '%'  order by name")
    @Query("select * from things where name like '%' || :namePart || '%'")
    LiveData<List<Thing>> getThings(String namePart);
}
