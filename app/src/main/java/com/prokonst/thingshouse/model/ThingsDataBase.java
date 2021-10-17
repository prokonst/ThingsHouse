package com.prokonst.thingshouse.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.prokonst.thingshouse.model.dao.StorageDao;
import com.prokonst.thingshouse.model.dao.ThingDao;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;

@Database(entities = {Thing.class, Storage.class}, version = 1, exportSchema = false)
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

            String idT1 = "3D01C18A-6E2F-4E6F-AD8C-7BD429774DED";
            Thing thing1 = new Thing(idT1, "шт", idT1, "Вещь 1", "");
            thingDao.insert(thing1);

            String idT2 = "0ACE3316-E3D4-4383-B9EA-EBFDB240400F";
            Thing thing2 = new Thing(idT2, "шт", idT2, "Вещь 2", "");
            thingDao.insert(thing2);

            String idB1 = "347C8DCE-93FB-43D0-A186-A77DA2F4B974";
            Thing box1 = new Thing(idB1, "шт", idB1, "Ящик 1", "");
            thingDao.insert(box1);

            String idB2 = "41CDBB0C-8FF3-4D29-89D5-4F041BF32EB3";
            Thing box2 = new Thing(idB2, "шт", idB2, "Ящик 2", "");
            thingDao.insert(box2);

            String idS1 = "24F9466F-9FC1-49F6-8A0B-68BD15CE7BBC";
            Storage storage_thing1_in_box1 = new Storage(idS1, box1.getThingId(), thing1.getThingId(), 3.0);
            storageDao.insert(storage_thing1_in_box1);

            String idS2 = "1CFA31CB-5D14-43F5-BB20-E1D1A5005ED3";
            Storage storage_thing2_in_box1 = new Storage(idS2, box1.getThingId(), thing2.getThingId(), 5.0);
            storageDao.insert(storage_thing2_in_box1);

            return null;
        }
    }
}
