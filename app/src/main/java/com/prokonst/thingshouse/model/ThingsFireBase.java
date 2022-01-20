package com.prokonst.thingshouse.model;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Synced;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.DataComparer;
import com.prokonst.thingshouse.tools.Utils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
                        Log.d("SyncronizerDBs", "SUCCESS UploadData: " + syncedObj.getId() );
                    } else {
                        Exception exception = task.getException();
                        Log.d("SyncronizerDBs", "EXCEPTION UploadData: " + syncedObj.getId() + ":\n" + exception.getMessage());
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

    public void saveImagesToFireBase(Collection<String> imagesId, SyncronizerDBs syncronizerDBs, Context context) {
        if(imagesId == null || imagesId.size() == 0)
            return;

        //Get list images in current user node
        this.imgStorage
                .getReference()
                .child(getCurrentUserId())
                .listAll()
                .addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        List<StorageReference> items = task.getResult().getItems();
                        ArrayList<String> existFileNames = new ArrayList<>();
                        for(StorageReference curStorageReference : items){
                            existFileNames.add(curStorageReference.getName());
                        }

                        for (String curImageId : imagesId){
                            saveImageToFireBase(curImageId, syncronizerDBs, existFileNames, context);
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveImageToFireBase(String imageId, SyncronizerDBs syncronizerDBs, Collection<String> existFileNames, Context context){
        if(imageId == null || imageId.isEmpty()) {
            syncronizerDBs.decrementCountSyncObj("IMGtoFB: Image isEmpty");
            return;
        }

        String targetImageFileName = Utils.getBaseImageFileName(imageId);
        if(existFileNames.contains(targetImageFileName)){
            syncronizerDBs.decrementCountSyncObj("IMGtoFB: Image isExist: " + targetImageFileName);
            return;
        }
        //Log.d("SyncronizerDBs", "IMG targetImageFileName: " + targetImageFileName);

        String imageBasePath = Utils.getImageBasePath(imageId);
        //Log.d("SyncronizerDBs", "IMG imageBasePath: " + imageBasePath);

        Uri fileUri = Utils.getFileProviderUri(context, imageBasePath);
        //Log.d("SyncronizerDBs", "IMG fileUri: " + fileUri.getPath());

        //Upload file to user node
        this.imgStorage
                .getReference()
                .child(getCurrentUserId() + "/" + targetImageFileName)
                .putFile(fileUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("SyncronizerDBs", "SUCCESS UploadFile: " + targetImageFileName );
                        } else {
                            Exception exception = task.getException();
                            Log.d("SyncronizerDBs", "EXCEPTION UploadFile: " + targetImageFileName + ":\n" + exception.getMessage());
                        }
                        //Log.d("SyncronizerDBs", "decrementCountSyncObj");
                        syncronizerDBs.decrementCountSyncObj("IMGtoFB:" + imageId);
                    }
                });
    }

    public void saveFilesToLocalDisc(Collection<String> imagesId, SyncronizerDBs syncronizerDBs, Context context){
        String batchDirPath = Utils.getBatchDirectoryPath();

        File batchDir = new File(batchDirPath);
        if(!batchDir.exists())
            batchDir.mkdirs();

        ArrayList<String> existFileNames = new ArrayList<>();
        for(File curFile : batchDir.listFiles()){
            existFileNames.add(curFile.getName());
        }

        for (String curImageId : imagesId){
            saveImageToLocalDisc(curImageId, syncronizerDBs, existFileNames, context);
        }
    }

    private void saveImageToLocalDisc(String imageId, SyncronizerDBs syncronizerDBs, Collection<String> existFileNames, Context context) {
        if (imageId == null || imageId.isEmpty()) {
            syncronizerDBs.decrementCountSyncObj("IMGfromFB: Image isEmpty");
            return;
        }

        String targetImageFileName = Utils.getBaseImageFileName(imageId);
        if (existFileNames.contains(targetImageFileName)) {
            syncronizerDBs.decrementCountSyncObj("IMGfromFB: Image isExist: " + targetImageFileName);
            return;
        }

        //Download file from storage
        Uri downLoadUri = Uri.parse(Utils.getBatchDirectoryPath() + "/" + targetImageFileName);
        String childPath = getCurrentUserId() + "/" + targetImageFileName;
        this.imgStorage
                .getReference()
                .child(childPath)
                .getFile(downLoadUri)
                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("SyncronizerDBs", "SUCCESS DownloadFile: " + targetImageFileName);
                        } else {
                            String address = ThingsFireBase.this.imgStorage
                                    .getReference()
                                    .child(childPath).getPath();

                            Exception exception = task.getException();
                            Log.d("SyncronizerDBs", "EXCEPTION DownloadFile: " + targetImageFileName + ":\n" + exception.getMessage());
                        }
                        syncronizerDBs.decrementCountSyncObj("IMGfromFB:" + imageId);

                    }
                });

    }

    private static String getCurrentUserId(){
        return Authorization.getCurrentUser().getUid().toLowerCase();
    }
}
