package com.prokonst.thingshouse.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.FragmentShowPhotoBinding;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;


public class ShowPhotoFragment extends Fragment {

    private ThingsViewModel thingsViewModel;
    private Thing thing;
    private String thingId;
    FragmentShowPhotoBinding fragmentShowPhotoBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thingId = ShowPhotoFragmentArgs.fromBundle(getArguments()).getSelectedThing().getThingId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thingsViewModel = new ViewModelProvider(this).get(ThingsViewModel.class);

        fragmentShowPhotoBinding = FragmentShowPhotoBinding.inflate(inflater, container, false);


        return fragmentShowPhotoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thingsViewModel.getThingById(thingId).observe(this.getViewLifecycleOwner(), new Observer<Thing>() {
            @Override
            public void onChanged(Thing t) {
                thing = t;
                fragmentShowPhotoBinding.setThing(thing);
                setTitleActionBar();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentShowPhotoBinding = null;
    }

    private void setTitleActionBar(){
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if(appCompatActivity == null)
            return;

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if(actionBar == null)
            return;

        actionBar.setTitle("Photo " + thing.getName());
    }
}