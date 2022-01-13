package com.prokonst.thingshouse.tools;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Synced;

import java.util.ArrayList;
import java.util.List;

public class LiveObjectListExtractor implements LifecycleOwner {

    private LifecycleRegistry lifecycleRegistry;

    private LiveObjectListExtractor(){
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }


    public static <T> ArrayList<T> extsactArrayList(LiveData<List<T>> listLiveData, ArrayList<T> result, AfterExtract afterExtract){

        LiveObjectListExtractor liveObjectListExtractor = new LiveObjectListExtractor();

        return liveObjectListExtractor.extsactAndAddToList(listLiveData, result, afterExtract);
    }


    private <T> ArrayList<T> extsactAndAddToList(LiveData<List<T>> listLiveData, ArrayList<T> result, AfterExtract afterExtract){

        this.lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);

        listLiveData.observe(this, new Observer<List<T>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<T> objects) {
                for(T curObj : objects) {
                    result.add(curObj);
                }

                afterExtract.ActionAfterExtract();

                LiveObjectListExtractor.this.lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
            }
        });

        return result;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }
}
