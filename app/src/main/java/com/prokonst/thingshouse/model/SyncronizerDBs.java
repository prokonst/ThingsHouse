package com.prokonst.thingshouse.model;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.DataComparer;

import java.util.List;

public class SyncronizerDBs implements LifecycleOwner {
    private static SyncronizerDBs syncronizerDBs = null;

    public static SyncronizerDBs getInstance(AppCompatActivity appCompatActivity){
        if(SyncronizerDBs.syncronizerDBs == null){
            SyncronizerDBs.syncronizerDBs = new SyncronizerDBs(appCompatActivity);
        }

        return SyncronizerDBs.syncronizerDBs;
    }

    private ThingsFireBase thingsFireBase;
    private AppRepository appRepository;
    private AppCompatActivity appCompatActivity;
    private LifecycleRegistry lifecycleRegistry;

    private SyncronizerDBs(AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
        this.thingsFireBase = ThingsFireBase.getInstance();
        this.appRepository = AppRepository.getInstance(appCompatActivity.getApplication());
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    public void sync(){
        DataComparer.prepare();
        readFromFireBase();
        //readFromLocalDB();
    }

    private void readFromFireBase(){
        FirebaseUser currentUser = Authorization.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this.appCompatActivity, "You are not authorized", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference thingNode = this.thingsFireBase.getCurrentUserNode().child("things");
        thingNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot curDS : snapshot.getChildren()){
                    Thing curThing = curDS.getValue(Thing.class);
                    DataComparer.addObjToMap(curThing, DataComparer.ObjType.FIRE_BASE_OBJ);
                }

                SyncronizerDBs.this.readFromLocalDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFromLocalDB(){

        this.lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);

        this.appRepository.getThings().observe(this, new Observer<List<Thing>>() {
            @Override
            public void onChanged(List<Thing> things) {
                for(Thing curThing : things) {
                    DataComparer.addObjToMap(curThing, DataComparer.ObjType.LOCAL_OBJ);
                }

                SyncronizerDBs.this.compare();
            }
        });
    }

    private void compare(){
        StringBuilder sb = new StringBuilder();
        sb.append("Local: " + DataComparer.getCountByObjType(DataComparer.ObjType.LOCAL_OBJ));
        sb.append("\nFire: " + DataComparer.getCountByObjType(DataComparer.ObjType.FIRE_BASE_OBJ));
        sb.append("\nIntersect: " + DataComparer.getCountIntersect());

        Toast.makeText(SyncronizerDBs.this.appCompatActivity, sb.toString(), Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }
}
