package com.prokonst.thingshouse;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prokonst.thingshouse.databinding.ActivityMainBinding;
import com.prokonst.thingshouse.databinding.FragmentShowThingsListBinding;
import com.prokonst.thingshouse.model.Thing;
import com.prokonst.thingshouse.model.ThingsDataBase;
import com.prokonst.thingshouse.viewmodel.ThingsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShowThingsListFragment extends Fragment {

    private FragmentShowThingsListBinding fragmentShowThingsListBinding;

    private ThingsViewModel thingsViewModel;
    private ArrayList<Thing> thingArrayList;
    private RecyclerView thingRecyclerView;
    private ThingAdapter thingAdapter;

    private ThingsListClickHandlers thingsListClickHandlers;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        fragmentShowThingsListBinding = FragmentShowThingsListBinding.inflate(inflater, container, false);

        thingsViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(ThingsViewModel.class);

        thingsListClickHandlers = new ThingsListClickHandlers();
        fragmentShowThingsListBinding.setThingsListClickHandlers(thingsListClickHandlers);



        return fragmentShowThingsListBinding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentShowThingsListBinding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ShowThingsListFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        loadThingsInArrayList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentShowThingsListBinding = null;
    }



    private void loadThingsInArrayList(){
        thingsViewModel.getThings(/*namePart*/).observe(this.getViewLifecycleOwner(), new Observer<List<Thing>>() {
            @Override
            public void onChanged(List<Thing> things) {
                thingArrayList = (ArrayList<Thing>) things;
                loadRecyclerView();
            }
        });
    }

    private void loadRecyclerView(){
        thingRecyclerView = fragmentShowThingsListBinding.recyclerview;
        thingRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        thingRecyclerView.setHasFixedSize(true);

        thingAdapter = new ThingAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            thingAdapter.setThingArrayList(thingArrayList);
        }
        thingRecyclerView.setAdapter(thingAdapter);

        thingAdapter.setOnItemClickListener(new ThingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Thing thing) {
                //CaptureCameraImage.getCaptureCameraImage().capture(thing);

                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedThing", thing);
                NavHostFragment.findNavController(ShowThingsListFragment.this)
                        .navigate(R.id.action_SecondFragment_to_thingDataFragment, bundle);
            }
        });

        thingAdapter.getFilter().filter("");
    }

    public class ThingsListClickHandlers {
        public void onFabClicked(View view) {
            Toast.makeText(view.getContext(), "But is cl", Toast.LENGTH_SHORT).show();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
            //loadThingsInArrayList(s.toString());
            thingAdapter.getFilter().filter(s.toString());
        }

        public void onAddClicked(View view) {

            TextInputEditText textInputEditText = getView().findViewById(R.id.textInputEditText);
            String newName = textInputEditText.getText().toString();

            ThingsDataBase.AddTestThing(newName );
            thingAdapter.getFilter().filter(newName);

            Toast.makeText(view.getContext(), "Created: " + newName, Toast.LENGTH_SHORT).show();
        }
    }

}