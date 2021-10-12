package com.prokonst.thingshouse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.prokonst.thingshouse.databinding.FragmentFirstBinding;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.StorageWithThings;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action = FirstFragmentDirections.actionFirstFragmentToShowThingsListFragment(
                        false, "Browse things", "ViewThings", "", "");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(action);
            }
        });

        binding.testButton.setOnClickListener((v) -> {
            AppRepository appRepository = new AppRepository(FirstFragment.this.getActivity().getApplication());
            //appRepository.getStoragesWithTingsByParentId("347C8DCE-93FB-43D0-A186-A77DA2F4B974").observe(this.getViewLifecycleOwner(), (listStoragesWithThings) -> {
            appRepository.getStoragesWithTingsByChildId("3D01C18A-6E2F-4E6F-AD8C-7BD429774DED").observe(this.getViewLifecycleOwner(), (listStoragesWithThings) -> {
                for(StorageWithThings curStorageWithThings : listStoragesWithThings) {
                    Log.d("TEST", "--------------------------------");
                    Log.d("TEST", "parentName = " + curStorageWithThings.parentThing.getName());
                    Log.d("TEST", "childName = " + curStorageWithThings.childThing.getName());
                    Log.d("TEST", "quantity = " + Double.toString(curStorageWithThings.getQuantity()));
                    Log.d("TEST", "--------------------------------");
                }
            });


            return;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}