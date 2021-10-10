package com.prokonst.thingshouse;

public interface ChangeValueInterface {

    public interface ChangeValueCallback<T> {
        void onChangeValue(T newValue);
    }

    public interface GetValueCallback<T> {
        T onGetValue();
    }
}
