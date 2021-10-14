package com.prokonst.thingshouse;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.prokonst.thingshouse.databinding.ActivityMainBinding;
import com.prokonst.thingshouse.fragments.ThingDataFragment;
import com.prokonst.thingshouse.fragments.ThingDataFragmentDirections;
import com.prokonst.thingshouse.tools.CaptureCameraImage;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CaptureCameraImage.setCaptureCameraImage(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
/*
        @SuppressLint("RestrictedApi")
        Deque<NavBackStackEntry> backStack = navController.getBackStack();

        List<NavBackStackEntry> listNavBackStackEntry = new ArrayList<NavBackStackEntry>( backStack );

        if(listNavBackStackEntry.size() > 2) {
            NavBackStackEntry prevNavBackStackEntry = listNavBackStackEntry.get(listNavBackStackEntry.size() - 2);
            Bundle arguments = prevNavBackStackEntry.getArguments();
            if(arguments != null && arguments.containsKey("ActionType")) {
                if(arguments.getString("ActionType").equals("AddThingTo")) {
                    backStack.remove(navController.getCurrentBackStackEntry());
                    backStack.remove(prevNavBackStackEntry);

//                    int size1 = backStack.size();
//                    boolean result =
//                    int size2 = navController.getBackStack().size();
//                    Toast.makeText(this, ""+size1 + "\n" + size2, Toast.LENGTH_SHORT).show();
                }
            }
        }
*/

/*
        @SuppressLint("RestrictedApi")
        List<NavBackStackEntry> listNavBackStackEntry = new ArrayList<NavBackStackEntry>( navController.getBackStack() );

        if(listNavBackStackEntry.size() > 2) {
            NavBackStackEntry prevNavBackStackEntry = listNavBackStackEntry.get(listNavBackStackEntry.size() - 2);
            Bundle arguments = prevNavBackStackEntry.getArguments();
            if(arguments != null && arguments.containsKey("ActionType")) {
                if(arguments.getString("ActionType").equals("AddThingTo")) {

                    NavDirections action = ThingDataFragmentDirections.actionThingDataFragmentToShowThingListFragment(
                            false, "Browse things", "ViewThings", null, null, null );
                    navController.navigate(action);
                    return true;
                }
            }

        }*/

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}