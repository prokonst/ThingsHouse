package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.tables.Thing;

import java.io.Serializable;

public class ShowThingsListParameters implements Serializable {
    private boolean isClearFilter;
    private String title;
    private ActionType actionType;
    private ThingIdInterface sourceThing;
    private ThingIdInterface targetThing;
    private double quantity;

    public ShowThingsListParameters(boolean isClearFilter, String title, ActionType actionType,
                                    ThingIdInterface sourceThing, ThingIdInterface targetThing, double quantity) {
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

    public ThingIdInterface getSourceThing() {
        return this.sourceThing;
    }

    public ThingIdInterface getTargetThing() {
        return this.targetThing;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public enum ActionType implements Serializable{
        ViewThings,
        AddThingTo,
        MoveTo,
        Unknown
    }

    public interface ThingIdInterface {
        public String getThingId();
    }
}
