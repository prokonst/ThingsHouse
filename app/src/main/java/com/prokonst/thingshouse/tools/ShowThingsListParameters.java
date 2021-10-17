package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.tables.Thing;

import java.io.Serializable;

public class ShowThingsListParameters implements Serializable {
    private boolean isClearFilter;
    private String title;
    private ActionType actionType;
    private Thing sourceThing;
    private Thing targetThing;
    private double quantity;

    public ShowThingsListParameters(boolean isClearFilter, String title, ActionType actionType,
                                    Thing sourceThing, Thing targetThing, double quantity) {
        this.isClearFilter = isClearFilter;
        this.title = title;
        this.actionType = actionType;
        this.sourceThing = sourceThing;
        this.targetThing = targetThing;
        this.quantity = quantity;
    }

    public boolean getIsClearFilter() {
        return this.isClearFilter;
    }

    public String getTitle() {
        return this.title;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public Thing getSourceThing() {
        return this.sourceThing;
    }

    public Thing getTargetThing() {
        return this.targetThing;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public enum ActionType implements Serializable{
        ViewThings,
        AddThingTo,
        Unknown
    }
}
