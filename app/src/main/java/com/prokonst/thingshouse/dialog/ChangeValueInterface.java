package com.prokonst.thingshouse.dialog;

import java.io.Serializable;

public interface ChangeValueInterface {

    public interface ChangeValueCallback<T> {
        void onChangeValue(T newValue);
    }

    public interface ChangeValueCallbackWithAction<T> {
        void onChangeValue(T newValue, ActionType actionType);
    }

    public interface GetValueCallback<T> {
        T onGetValue();
    }

    public enum ActionType implements Serializable {
        Select,
        Scan
    }
}
