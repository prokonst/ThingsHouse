package com.prokonst.thingshouse.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "storages",
        foreignKeys = {
                @ForeignKey(entity = Thing.class, parentColumns = "id", childColumns = "parentId", onDelete = CASCADE),
                @ForeignKey(entity = Thing.class, parentColumns = "id", childColumns = "childId", onDelete = CASCADE)
        }, indices = {
                @Index(value = {"parentId"}, unique = true),
                @Index(value = {"childId"}, unique = true)
        }
)
public class Storage extends BaseObservable implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;
    private String parentId;
    private String childId;
    private double quantity;

    public Storage(@NonNull String id, String parentId, String childId, double quantity) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.quantity = quantity;
    }

    @Bindable
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
        notifyPropertyChanged(BR.parentId);
    }

    @Bindable
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
        notifyPropertyChanged(BR.childId);
    }

    @Bindable
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }
}
