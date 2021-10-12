package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.Thing;

public class ShowThingsListParameters {
    private boolean isClearFilter;
    private String title;
    private String actionType;
    private Thing sourceThing;
    private Thing targetThing;
    private double quantity;

    public ShowThingsListParameters() {
        this.isClearFilter = false;
        this.title = "Unknown title";
        this.actionType = "Unknown action type";
        this.sourceThing = null;
        this.targetThing = null;
        this.quantity = 0;
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

    public Thing getSourceThing() {
        return this.sourceThing;
    }

    public ShowThingsListParameters setSourceThing(Thing sourceThing) {
        this.sourceThing = sourceThing;
        return this;
    }

    public Thing getTargetThing() {
        return this.targetThing;
    }

    public ShowThingsListParameters setTargetThing(Thing targetThing) {
        this.targetThing = targetThing;
        return this;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public ShowThingsListParameters setQuantity(String quantity) {
        if(quantity != null) {
            this.quantity = Double.parseDouble(quantity);
        }
        else {
            this.quantity = 0;
        }
        return this;
    }
}
