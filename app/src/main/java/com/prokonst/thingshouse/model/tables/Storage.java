package com.prokonst.thingshouse.model.tables;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import com.google.firebase.database.Exclude;
import com.prokonst.thingshouse.tools.Utils;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "storages",
        foreignKeys = {
                @ForeignKey(entity = Thing.class, parentColumns = "thing_id", childColumns = "parent_id", onDelete = CASCADE),
                @ForeignKey(entity = Thing.class, parentColumns = "thing_id", childColumns = "child_id", onDelete = CASCADE)
        }, indices = {
                @Index(value = {"parent_id"}),
                @Index(value = {"child_id"})
        }
)
public class Storage extends BaseObservable implements Serializable, Synced {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "storage_id")
    private String id;

    @ColumnInfo(name = "parent_id")
    private String parentId;

    @ColumnInfo(name = "child_id")
    private String childId;

    private double quantity;

    @TypeConverters({com.prokonst.thingshouse.tools.DateTimeConverter.class})
    private Date dateChange;

    private String dataHash;
    private boolean isDeleted;
    private String userId;

    @Ignore
    public Storage() {
    }

    public Storage(@NonNull String id, String parentId, String childId, double quantity) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.quantity = quantity;
    }

    @Bindable
    @NonNull
    public String getId() {
        return id.toLowerCase();
    }

    public void setId(@NonNull String id) {
        this.id = id.toLowerCase();
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.storageId);
    }

    @Bindable
    public String getParentId() {
        return parentId.toLowerCase();
    }

    public void setParentId(String parentId) {
        this.parentId = parentId.toLowerCase();
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.parentId);
    }

    @Bindable
    public String getChildId() {
        return childId.toLowerCase();
    }

    public void setChildId(String childId) {
        this.childId = childId.toLowerCase();
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.childId);
    }

    @Bindable
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.quantity);
    }

    public Date getDateChange(){
        return this.dateChange;
    }

    public void setDateChange(Date dateChange){
        this.dateChange = dateChange;
    }


    public String getDataHash() {
        return this.dataHash;
    }
    public void setDataHash(String dataHash){
        this.dataHash = dataHash;
    }


    public boolean getIsDeleted() {
        return this.isDeleted;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDataForHash(){

        StringBuilder dataForHash = new StringBuilder();

        dataForHash.append("ParentId: ");
        dataForHash.append(this.parentId);

        dataForHash.append("ChildId: ");
        dataForHash.append(this.childId);

        dataForHash.append("Quantity: ");
        dataForHash.append(Utils.ConvertDoubleToBase64(this.quantity));

        dataForHash.append("IsDeleted: ");
        dataForHash.append(Boolean.toString(this.isDeleted).toLowerCase());

        return dataForHash.toString();
    }
}
