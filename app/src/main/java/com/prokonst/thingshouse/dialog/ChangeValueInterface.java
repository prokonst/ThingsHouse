package com.prokonst.thingshouse.dialog;

public interface ChangeValueInterface {

    public interface ChangeValueCallback<T> {
        void onChangeValue(T newValue);
    }

    public interface ChangeValueCallbackWithAction<T> {
        void onChangeValue(T newValue, String actionNameKey);
    }

    public interface GetValueCallback<T> {
        T onGetValue();
    }
}
