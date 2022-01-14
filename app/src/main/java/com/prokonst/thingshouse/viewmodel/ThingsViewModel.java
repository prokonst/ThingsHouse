package com.prokonst.thingshouse.viewmodel;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.LiveObjectListExtractor;

import java.util.ArrayList;
import java.util.List;

public class ThingsViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<List<Thing>> things;

    private Application application;


    public ThingsViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        appRepository = AppRepository.getInstance(application);
    }

    private static Void ActionAfterExtract() {
        return null;
    }

    public AppRepository getAppRepository() {
        return appRepository;
    }

    public LiveData<Thing> getThingById(String thingId) {

        return appRepository.getThingById(thingId);
    }

    public LiveData<List<Thing>> getThings() {
        things = appRepository.getActualThings();
        return things;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNewThing(Thing thing){
        appRepository.insertThing(thing);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateThing(Thing thing){
        appRepository.updateThing(thing);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void markAsDeletedThing(Thing thing){
        thing.setIsDeleted(true);
        appRepository.updateThing(thing);

        ArrayList<Storage> storages = new ArrayList<>();

        LiveObjectListExtractor.extsactArrayList(appRepository.getStoragesByChildId(thing.getId()), storages,
                () -> {
                    LiveObjectListExtractor.extsactArrayList(ThingsViewModel.this.appRepository.getStoragesByParentId(thing.getId()), storages,
                            () -> {
                                for(Storage curStorage : storages){
                                    curStorage.setIsDeleted(true);
                                    Log.d("Del", curStorage.getId() + "   " + curStorage.getIsDeleted());
                                    appRepository.updateStorage(curStorage);
                                }
                                return null;
                            });
                    return null;
                });
    }

}
