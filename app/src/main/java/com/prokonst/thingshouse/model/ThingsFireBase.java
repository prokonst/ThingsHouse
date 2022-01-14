package com.prokonst.thingshouse.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Synced;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.DataComparer;
import com.prokonst.thingshouse.tools.Utils;

import java.util.List;

public class ThingsFireBase {
    public static final String THINGS_NODE_KEY = "things";
    public static final String STORAGES_NODE_KEY = "storages";

    public static ThingsFireBase thingsFireBase = null;

    FirebaseDatabase database;
    DatabaseReference rootNodeReference;

    FirebaseStorage imgStorage;

    public static ThingsFireBase getInstance() {
        if(ThingsFireBase.thingsFireBase == null) {
            ThingsFireBase.thingsFireBase = new ThingsFireBase();
        }

        return ThingsFireBase.thingsFireBase;
    }

    private ThingsFireBase() {
        this.database = FirebaseDatabase.getInstance();
        this.rootNodeReference = this.database.getReference("things_data");

        this.imgStorage = FirebaseStorage.getInstance();
    }

    public DatabaseReference getCurrentUserNode() {
        DatabaseReference currentUserNode = this.rootNodeReference.child(getCurrentUserId());
        return currentUserNode;
    }

    private void writeObject(Synced syncedObj, String nodeKey, Context context){
        DatabaseReference objNode = this.getCurrentUserNode().child(nodeKey).child(syncedObj.getId().toLowerCase());
        objNode
            .setValue(syncedObj)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(context, "Uploaded syncedObj to FireBase: " + syncedObj.getId(),
//                                Toast.LENGTH_SHORT).show();
                    } else {
                        Exception exception = task.getException();
//                        Toast.makeText(context, "Uploaded syncedObj failed.\n" + exception.getMessage() + "\n" + exception.getClass(),
//                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


    public void writeThing(Thing thing, Context context){
        writeObject(thing, THINGS_NODE_KEY, context);
    }

    public void writeStorage(Storage storage, Context context){
        writeObject(storage, STORAGES_NODE_KEY, context);
    }

    public void saveImageToFireBase(String imageId, Context context){
        if(imageId == null || imageId.isEmpty())
            return;

        String targetImageFileName = Utils.getBaseImageFileName(imageId);
        Log.d("SyncronizerDBs", "IMG targetImageFileName: " + targetImageFileName);

        String imageBasePath = Utils.getImageBasePath(imageId);
        Log.d("SyncronizerDBs", "IMG imageBasePath: " + imageBasePath);

        Uri fileUri = Utils.getFileProviderUri(context, imageBasePath);
        Log.d("SyncronizerDBs", "IMG fileUri: " + fileUri.getPath());

        //Upload file to user node
        this.imgStorage
                .getReference()
                .child(getCurrentUserId() + "/" + targetImageFileName)
                .putFile(fileUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Uploaded file: " + targetImageFileName,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Exception exception = task.getException();
                            Toast.makeText(context, "Uploaded file failed.\n" + exception.getMessage() + "\n" + exception.getClass(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
/*
        //Get list images in current user node
        this.imgStorage
                .getReference()
                .child(getCurrentUserId())
                .listAll()
                .addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        List<StorageReference> items = task.getResult().getItems();
                        for(StorageReference curStorageReference : items){
                            Log.d("Item", curStorageReference.getName());
                        }
                        Log.d("Item", "-------------------------------------");
                    }
                });*/

/*
        //Download file from storage
        Uri downLoadUri = Uri.parse(Utils.getBatchDirectoryPath() + "/TEST.jpg");
        this.imgStorage
                .getReference()
                .child(getCurrentUserId() + "/" + "TEST.jpg")
                .getFile(downLoadUri)
                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("downLoadUri", downLoadUri.toString());
                        } else {
                            String address = ThingsFireBase.this.imgStorage
                                    .getReference()
                                    .child(getCurrentUserId() + "/" + "TEST.jpg").getPath();

                            Exception exception = task.getException();
                            Log.d("downLoadUri", address + "\nUploaded file failed.\n" + exception.getMessage());
                        }


                    }
                });*/

    }

    private static String getCurrentUserId(){
        return Authorization.getCurrentUser().getUid().toLowerCase();
    }
}
