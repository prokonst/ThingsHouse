package com.prokonst.thingshouse.tools;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.prokonst.thingshouse.ThingDataFragment;
import com.prokonst.thingshouse.ThingDataFragmentDirections;

public class ShowThingsListParameters {
    private boolean isClearFilter;
    private String title;
    private String actionType;
    private String sourceId;
    private String targetId;

    public ShowThingsListParameters() {
        this.isClearFilter = false;
        this.title = "Unknown title";
        this.actionType = "Unknown action type";
        this.sourceId = "";
        this.targetId = "";
    }

    public boolean getIsClearFilter() {
        return this.isClearFilter;
    }

    public ShowThingsListParameters setIsClearFilter(boolean isClearFilter) {
        this.isClearFilter = isClearFilter;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public ShowThingsListParameters setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getActionType() {
        return this.actionType;
    }

    public ShowThingsListParameters setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public ShowThingsListParameters setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public ShowThingsListParameters setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }

}
