package com.prokonst.thingshouse.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prokonst.thingshouse.R;
import com.prokonst.thingshouse.databinding.FragmentShowThingsListBinding;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.ScanBarCodeLauncher;
import com.prokonst.thingshouse.tools.ShowThingsListParameters;
import com.prokonst.thingshouse.tools.Utils;
import com.prokonst.thingshouse.viewmodel.ThingAdapter;
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

    private TextInputEditText textInputEditText;

    private String filter = "";
    private ShowThingsListParameters fragmentInputParams;

    private ActivityResultLauncher<Intent> startBarCodeScannerActivityResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        ShowThingsListFragmentArgs args = ShowThingsListFragmentArgs.fromBundle(getArguments());
        fragmentInputParams = args.getShowThingsListParameters();

        if(fragmentInputParams.getIsClearFilter()) {
            filter = "";
        }

        setTitle(fragmentInputParams.getTitle());

        if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.ViewThings) {

        }
        else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.AddThingTo) {

        }
        else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.MoveTo) {

        }
        else {
            setTitle("Unknown action type");
        }




        fragmentShowThingsListBinding = FragmentShowThingsListBinding.inflate(inflater, container, false);

        thingsViewModel = new ViewModelProvider(this).get(ThingsViewModel.class);
                //.AndroidViewModelFactory(getActivity().getApplication())
                //.create();

        thingsListClickHandlers = new ThingsListClickHandlers();
        fragmentShowThingsListBinding.setThingsListClickHandlers(thingsListClickHandlers);



        startBarCodeScannerActivityResultLauncher = ShowThingsListFragment.this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {

                    try {
                        String barCode = ScanBarCodeLauncher.getBarCode(result);
                        applyFilter(barCode);
                        textInputEditText.setText(barCode);


                    }catch (Exception ex) {
                        Toast.makeText(ShowThingsListFragment.this.getActivity(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
        );

        textInputEditText = fragmentShowThingsListBinding.getRoot().findViewById(R.id.textInputEditText);

        return fragmentShowThingsListBinding.getRoot();

    }

    private void setTitle(String title) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentShowThingsListBinding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ShowThingsListFragment.this)
                        .navigate(R.id.action_ShowThingsListFragment_to_FirstFragment);
            }
        });

        thingRecyclerView = fragmentShowThingsListBinding.recyclerview;
        thingRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        thingRecyclerView.setHasFixedSize(true);

        thingAdapter = new ThingAdapter();
        thingRecyclerView.setAdapter(thingAdapter);

        thingAdapter.setOnItemClickListener(
                (selectedThing) -> {
                    AppRepository appRepository = new AppRepository(ShowThingsListFragment.this.getActivity().getApplication());
                    NavController navController = NavHostFragment.findNavController(ShowThingsListFragment.this);
                    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

                    if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.ViewThings) {
                        NavDirections action = ShowThingsListFragmentDirections.actionShowThingsListFragmentToThingDataFragment(selectedThing);
                        NavHostFragment.findNavController(ShowThingsListFragment.this)
                                .navigate(action);
                    }
                    else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.AddThingTo) {
                        ShowThingsListParameters.ThingIdInterface newParentThing = selectedThing;
                        ShowThingsListParameters.ThingIdInterface movingThing = fragmentInputParams.getSourceThing();

                        if(newParentThing.getThingId().equals(movingThing.getThingId())) {
                            Toast.makeText(view.getContext(), "Error: apply to self", Toast.LENGTH_LONG).show();
                        }
                        else {
                            appRepository.addQuantityToStorageByParentId(newParentThing.getThingId(), movingThing.getThingId(), fragmentInputParams.getQuantity());
                        }
                        // Do not working. When have many actions we are have exceptions
                        // Moving to nav_graph
                        // navController.popBackStack(R.id.thingDataFragment, true);

                        //NavDirections action = ShowThingsListFragmentDirections.actionShowThingsListFragmentToThingDataFragment((Thing) fragmentInputParams.getSourceThing());
                        //navController.navigate(action);

                        NavigationUI.navigateUp(navController, appBarConfiguration);

                    }else if(fragmentInputParams.getActionType() == ShowThingsListParameters.ActionType.MoveTo) {

                        ShowThingsListParameters.ThingIdInterface newParentThing = selectedThing;
                        ShowThingsListParameters.ThingIdInterface movingThing = fragmentInputParams.getSourceThing();
                        ShowThingsListParameters.ThingIdInterface oldParentThing = fragmentInputParams.getTargetThing();

                        appRepository.moveStorage(oldParentThing, movingThing, newParentThing);

                        NavigationUI.navigateUp(navController, appBarConfiguration);

                    } else {
                        Toast.makeText(view.getContext(), "Unknown action type", Toast.LENGTH_LONG).show();
                    }
                 });


        thingsViewModel.getThings(/*namePart*/).observe(this.getViewLifecycleOwner(), new Observer<List<Thing>>() {
            @Override
            public void onChanged(List<Thing> things) {
                thingArrayList = (ArrayList<Thing>) things;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    thingAdapter.setThingArrayList(thingArrayList);
                }
                thingAdapter.getFilter().filter(filter);
            }
        });

        textInputEditText.setText(filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentShowThingsListBinding = null;
    }



    private void applyFilter(String newFilter){
        filter = newFilter;
        thingAdapter.getFilter().filter(filter);
    }

    public class ThingsListClickHandlers {
        public void onFabClicked(View view) {
            Toast.makeText(view.getContext(), "But is cl", Toast.LENGTH_SHORT).show();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            applyFilter(s.toString());
        }

        public void onAddClicked(View view) {
            String newName = textInputEditText.getText().toString();
            if(newName == null || newName.length() == 0) {
                Toast.makeText(ShowThingsListFragment.this.getContext(), "Input new name", Toast.LENGTH_LONG).show();
                return;
            }
            String newThingId = Utils.generateUUIDStr();
            Thing newThing = new Thing(newThingId, "шт", newThingId, newName, "");
            thingsViewModel.addNewThing(newThing);
            applyFilter(newName);

            Toast.makeText(ShowThingsListFragment.this.getContext(), "CREATE: <" + newName + ">", Toast.LENGTH_LONG).show();
        }

        public void onScanClicked(View view) {
            ScanBarCodeLauncher.startScanBarCodeLauncher(ShowThingsListFragment.this.getActivity(), startBarCodeScannerActivityResultLauncher);
        }
    }

}