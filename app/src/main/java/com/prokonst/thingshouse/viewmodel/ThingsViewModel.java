package com.prokonst.thingshouse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Thing;

import java.util.List;

public class ThingsViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private LiveData<List<Thing>> things;

    private Application application;


    public ThingsViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        appRepository = new AppRepository(application);
    }


    public LiveData<Thing> getThingById(String thingId) {

        return appRepository.getThingById(thingId);
    }

    public LiveData<List<Thing>> getThings() {
        things = appRepository.getThings();
        return things;
    }

    public void addNewThing(Thing thing){
        appRepository.insertThing(thing);
    }

    public void updateThing(Thing thing){
        appRepository.updateThing(thing);
    }

    public void deleteThing(Thing thing){
        appRepository.deleteThing(thing);
    }

}
