package com.prokonst.thingshouse.model;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.DataComparer;

import java.util.Collection;
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
    private int countSyncObj;

    private SyncronizerDBs(AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
        this.thingsFireBase = ThingsFireBase.getInstance();
        this.appRepository = AppRepository.getInstance(appCompatActivity.getApplication());
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        this.countSyncObj = 0;
    }

    public void sync(){
        DataComparer.prepare();
        readThingsFromFireBase();
        DataComparer.finish();
    }

    public void decrementCountSyncObj(String message){
        this.countSyncObj --;
        //Log.d("SyncronizerDBs", "SYNC COUNT: " + this.countSyncObj + " [" + message + "]");

        if(this.countSyncObj > 0)
            return;

        try {
            SyncronizerDBs.this.finalize();
            Toast.makeText(SyncronizerDBs.this.appCompatActivity, "Sync success!", Toast.LENGTH_LONG).show();
            //Log.d("SyncronizerDBs", "Sync success!");
        }
        catch (Throwable ex){
            //Log.d("SyncronizerDBs", ex.getMessage());
            Toast.makeText(SyncronizerDBs.this.appCompatActivity, "Sync failed!\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
            //Log.d("SyncronizerDBs", "Sync failed!\n" + ex.getMessage());
        }
    }

    private void readThingsFromFireBase(){
        FirebaseUser currentUser = Authorization.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this.appCompatActivity, "You are not authorized", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference node = this.thingsFireBase.getCurrentUserNode().child(ThingsFireBase.THINGS_NODE_KEY);
        node.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot curDS : snapshot.getChildren()){
                    Thing curObj = curDS.getValue(Thing.class);
                    DataComparer.addObjToMap(curObj, DataComparer.DbType.FIRE_BASE_OBJ, DataComparer.ObjType.THING);
                }

                SyncronizerDBs.this.readStoragesFromFireBase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readStoragesFromFireBase(){
        FirebaseUser currentUser = Authorization.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this.appCompatActivity, "You are not authorized", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference node = this.thingsFireBase.getCurrentUserNode().child(ThingsFireBase.STORAGES_NODE_KEY);
        node.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot curDS : snapshot.getChildren()){
                    Storage curObj = curDS.getValue(Storage.class);
                    DataComparer.addObjToMap(curObj, DataComparer.DbType.FIRE_BASE_OBJ, DataComparer.ObjType.STORAGE);
                }

                SyncronizerDBs.this.readThingsFromLocalDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readThingsFromLocalDB(){

        this.lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);

        this.appRepository.getAllThings().observe(this, new Observer<List<Thing>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<Thing> things) {
                for(Thing curThing : things) {
                    DataComparer.addObjToMap(curThing, DataComparer.DbType.LOCAL_OBJ, DataComparer.ObjType.THING);
                }

                SyncronizerDBs.this.readStoragesFromLocalDB();
            }
        });


    }

    private void readStoragesFromLocalDB(){

        this.appRepository.getAllStorages().observe(this, new Observer<List<Storage>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<Storage> storages) {
                for(Storage curStorage : storages) {
                    DataComparer.addObjToMap(curStorage, DataComparer.DbType.LOCAL_OBJ, DataComparer.ObjType.STORAGE);
                }

                SyncronizerDBs.this.lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
                SyncronizerDBs.this.compareObjects();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void compareObjects(){

        StringBuilder sb = new StringBuilder();
        sb.append("\nLocal: " + DataComparer.getCountByObjType(DataComparer.DbType.LOCAL_OBJ));
        sb.append("\nFire: " + DataComparer.getCountByObjType(DataComparer.DbType.FIRE_BASE_OBJ));
        sb.append("\nIntersect: " + DataComparer.getCountIntersect());
        Log.d("DC", sb.toString());

        DataComparer.compareAll();

        Collection<DataComparer> allDataComparers = DataComparer.getAllDataComparers();
        this.countSyncObj += allDataComparers.size();

        Collection<String> imagesIdToFireBase = DataComparer.getImagesIdToFireBase();
        this.countSyncObj += imagesIdToFireBase.size();

        Collection<String> imagesIdToLocalDB = DataComparer.getImagesIdToLocalDB();
        this.countSyncObj += imagesIdToLocalDB.size();


        for(DataComparer curDC: allDataComparers){
            changeThing(curDC);
            this.decrementCountSyncObj(curDC.getObjId());
        }

        this.thingsFireBase.saveImagesToFireBase(imagesIdToFireBase, this, this.appCompatActivity);

        this.thingsFireBase.saveFilesToLocalDisc(imagesIdToLocalDB, this, this.appCompatActivity);



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeThing(DataComparer dataComparer){
        if(dataComparer.getIsHandled())
            return;

        StringBuilder sb = new StringBuilder();
        sb.append("\n" + dataComparer.getObjId() + ":\n" + dataComparer.getActionType() + "  " + dataComparer.getObjType());
        sb.append("\nL: " + (dataComparer.getLocalObj() != null) + "   F: " + (dataComparer.getFireBaseObj() != null));

        sb.append("\n");
        Log.d("DC", sb.toString());

        if(dataComparer.getObjType().equals(DataComparer.ObjType.THING)){
            switch (dataComparer.getActionType()){
                case LOCAL_INSERT:
                    if(!dataComparer.getFireBaseObj().getIsDeleted())
                        this.appRepository.insertThing((Thing)dataComparer.getFireBaseObj());
                    break;
                case FIRE_BASE_INSERT:
                    if(!dataComparer.getLocalObj().getIsDeleted())
                        this.thingsFireBase.getInstance().writeThing((Thing)dataComparer.getLocalObj(), this.appCompatActivity);
                    break;
                case LOCAL_UPDATE:
                    this.appRepository.updateThing((Thing)dataComparer.getFireBaseObj());
                    break;
                case FIRE_BASE_UPDATE:
                    this.thingsFireBase.getInstance().writeThing((Thing)dataComparer.getLocalObj(), this.appCompatActivity);
                    break;
                case LOCAL_DELETE_PHYSICALLY:
                    //TODO
                    break;
                case FIRE_BASE_DELETE_PHYSICALLY:
                    //TODO
                    break;
                case BOTH_DELETE_PHYSICALLY:
                    //TODO
                    break;
            }
        }
        else if(dataComparer.getObjType().equals(DataComparer.ObjType.STORAGE)){
            switch (dataComparer.getActionType()){
                case LOCAL_INSERT:
                    if(!dataComparer.getFireBaseObj().getIsDeleted()) {
                        Storage fbStorage = (Storage) dataComparer.getFireBaseObj();

                        DataComparer childDC = DataComparer.getDataComparerById(fbStorage.getChildId());
                        if(childDC != null)
                            changeThing(childDC);

                        DataComparer parentDC = DataComparer.getDataComparerById(fbStorage.getParentId());
                        if(parentDC != null)
                            changeThing(parentDC);

                        this.appRepository.insertStorage(fbStorage);
                    }
                    break;
                case FIRE_BASE_INSERT:
                    if(!dataComparer.getLocalObj().getIsDeleted())
                        this.thingsFireBase.getInstance().writeStorage((Storage) dataComparer.getLocalObj(), this.appCompatActivity);
                    break;
                case LOCAL_UPDATE:
                    this.appRepository.updateStorage((Storage) dataComparer.getFireBaseObj());
                    break;
                case FIRE_BASE_UPDATE:
                    this.thingsFireBase.getInstance().writeStorage((Storage)dataComparer.getLocalObj(), this.appCompatActivity);
                    break;
                case LOCAL_DELETE_PHYSICALLY:
                    //TODO
                    break;
                case FIRE_BASE_DELETE_PHYSICALLY:
                    //TODO
                    break;
                case BOTH_DELETE_PHYSICALLY:
                    //TODO
                    break;
            }
        }

        dataComparer.setIsHandled(true);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        SyncronizerDBs.syncronizerDBs = null;
    }
}
