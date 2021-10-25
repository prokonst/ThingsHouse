package com.prokonst.thingshouse.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prokonst.thingshouse.BuildConfig;
import com.prokonst.thingshouse.MainActivity;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.Utils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ThingsFireBase {
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

    public void writeThing(Thing thing, Context context){
        DatabaseReference thingNode = this.getCurrentUserNode().child("things").child(thing.getId().toLowerCase());

        thingNode
            .setValue(thing)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Uploaded thing: " + thing.getName(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(context, "Uploaded file failed.\n" + exception.getMessage() + "\n" + exception.getClass(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });


        saveImage(thing, context);
    }

    public void saveImage(Thing thing, Context context){
        if(thing.getMainPhotoId() == null || thing.getMainPhotoId().isEmpty())
            return;

        Uri fileUri = Utils.getFileProviderUri(context, thing.getMainPhotoBaseSrc());
        String targetImageFileName = Utils.getBaseImageFileName(thing.getMainPhotoId());

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
                });


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
                });

    }

    private static String getCurrentUserId(){
        return Authorization.getCurrentUser().getUid().toLowerCase();
    }
}
