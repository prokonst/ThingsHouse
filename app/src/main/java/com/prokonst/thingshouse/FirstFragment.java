package com.prokonst.thingshouse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
                        false, "Browse things", "ViewThings", null, null, null);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(action);
            }
        });

        binding.testButton.setOnClickListener((v) -> {
            AppRepository appRepository = new AppRepository(FirstFragment.this.getActivity().getApplication());
            //appRepository.getStoragesWithTingsByParentId("347C8DCE-93FB-43D0-A186-A77DA2F4B974").observe(this.getViewLifecycleOwner(), (listStoragesWithThings) -> {
            appRepository.getStoragesWithTingsByChildId("3D01C18A-6E2F-4E6F-AD8C-7BD429774DED").observe(this.getViewLifecycleOwner(), (listStoragesWithThings) -> {
                StringBuilder sb = new StringBuilder();


                for(StorageWithThings curStorageWithThings : listStoragesWithThings) {

                    sb.append("-----\n");
                    sb.append("parentName = " + curStorageWithThings.parentThing.getName() + "\n");
                    sb.append("childName = " + curStorageWithThings.childThing.getName() + "\n");
                    sb.append("quantity = " + Double.toString(curStorageWithThings.getQuantity()) + "\n");
                    sb.append("-----\n");
                }

                Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
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