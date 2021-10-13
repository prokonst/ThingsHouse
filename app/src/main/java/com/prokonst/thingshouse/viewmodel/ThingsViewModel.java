package com.prokonst.thingshouse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.Thing;

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

    public LiveData<List<Thing>> getThings() {
        things = appRepository.getThings();
        return things;
    }
/*
    public LiveData<List<Thing>> getThings(String namePart) {
        things = appRepository.getThings(namePart);
        return things;
    }*/
/*
    public List<Thing> getThingsByBarCode(String barCode) {
        return appRepository.getThingsByBarCode(barCode);
    }*/

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
