package com.prokonst.thingshouse.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.prokonst.thingshouse.Utils;

@Database(entities = {Thing.class, Storage.class}, version = 1)
public abstract class ThingsDataBase extends RoomDatabase {

    private static ThingsDataBase instance;

    public abstract ThingDao getThingDao();
    public abstract StorageDao getStorageDao();

    public static synchronized ThingsDataBase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ThingsDataBase.class, "thingsDB")
                    //.allowMainThreadQueries() //HACK
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return  instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new InitialDataAsyncTask(instance).execute();
        }
    };

    private static class InitialDataAsyncTask extends AsyncTask<Void, Void, Void>{

        private ThingDao thingDao;
        private StorageDao storageDao;

        public InitialDataAsyncTask(ThingsDataBase database) {
            thingDao = database.getThingDao();
            storageDao = database.getStorageDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Thing thing1 = new Thing(Utils.generateUUIDStr(), "шт", "barCode1", "Вещь 1", "");
            thingDao.insert(thing1);

            Thing thing2 = new Thing(Utils.generateUUIDStr(), "шт", "barCode2", "Вещь 2", "");
            thingDao.insert(thing2);

            Thing box1 = new Thing(Utils.generateUUIDStr(), "шт", "barCode3", "Ящик 1", "");
            thingDao.insert(box1);

            Thing box2 = new Thing(Utils.generateUUIDStr(), "шт", "barCode4", "Ящик 2", "");
            thingDao.insert(box2);

            Storage storage_thing1_in_box1 = new Storage(Utils.generateUUIDStr(), box1.getThingId(), thing1.getThingId(), 3.0);
            storageDao.insert(storage_thing1_in_box1);

            Storage storage_thing2_in_box1 = new Storage(Utils.generateUUIDStr(), box1.getThingId(), thing2.getThingId(), 5.0);
            storageDao.insert(storage_thing2_in_box1);
/*
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode11", "Test_Болт1", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode21", "Test_Гайка1", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode31", "Test_Шайба1", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode41", "Test_Гровер1", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode51", "Test_Стопор1", ""));

            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode12", "Test_Болт2", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode22", "Test_Гайка2", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode32", "Test_Шайба2", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode42", "Test_Гровер2", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode52", "Test_Стопор2", ""));

            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode13", "Test_Болт3", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode23", "Test_Гайка3", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode33", "Test_Шайба3", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode43", "Test_Гровер3", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode53", "Test_Стопор3", ""));

            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode14", "Test_Болт4", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode24", "Test_Гайка4", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode34", "Test_Шайба4", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode44", "Test_Гровер4", ""));
            thingDao.insert(new Thing(Utils.generateUUIDStr(), "шт", "barCode54", "Test_Стопор4", ""));*/


            return null;
        }
    }
}
