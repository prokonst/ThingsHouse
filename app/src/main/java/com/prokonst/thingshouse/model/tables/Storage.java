package com.prokonst.thingshouse.model.tables;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import com.prokonst.thingshouse.model.tables.Thing;

import java.io.Serializable;

@Entity(tableName = "storages",
        foreignKeys = {
                @ForeignKey(entity = Thing.class, parentColumns = "thing_id", childColumns = "parent_id", onDelete = CASCADE),
                @ForeignKey(entity = Thing.class, parentColumns = "thing_id", childColumns = "child_id", onDelete = CASCADE)
        }, indices = {
                @Index(value = {"parent_id"}),
                @Index(value = {"child_id"})
        }
)
public class Storage extends BaseObservable implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "storage_id")
    private String storageId;

    @ColumnInfo(name = "parent_id")
    private String parentId;

    @ColumnInfo(name = "child_id")
    private String childId;

    private double quantity;

    public Storage(@NonNull String storageId, String parentId, String childId, double quantity) {
        this.storageId = storageId;
        this.parentId = parentId;
        this.childId = childId;
        this.quantity = quantity;
    }

    @Bindable
    @NonNull
    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(@NonNull String storageId) {
        this.storageId = storageId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.storageId);
    }

    @Bindable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.parentId);
    }

    @Bindable
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
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
}
