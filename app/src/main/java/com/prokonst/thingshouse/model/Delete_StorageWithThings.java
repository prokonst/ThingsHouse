package com.prokonst.thingshouse.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Relation;



import java.io.Serializable;

public class Delete_StorageWithThings extends BaseObservable implements Serializable {

    @ColumnInfo(name = "storage_id")
    private String storageId;

    @ColumnInfo(name = "parent_id")
    private String parentId;

    @ColumnInfo(name = "child_id")
    private String childId;

    private double quantity;


    @Relation(parentColumn = "parent_id", entityColumn = "thing_id")
    public Thing parentThing;

    @Relation(parentColumn = "child_id", entityColumn = "thing_id")
    public Thing childThing;

    @Bindable
    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
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

    @Bindable
    public Thing getParentThing() {
        return parentThing;
    }

    public void setParentThing(Thing parentThing) {
        this.parentThing = parentThing;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.parentThing);
    }

    @Bindable
    public Thing getChildThing() {
        return childThing;
    }

    public void setChildThing(Thing childThing) {
        this.childThing = childThing;
        notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.childThing);
    }
}
