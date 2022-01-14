package com.prokonst.thingshouse;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.prokonst.thingshouse.databinding.ActivityMainBinding;
import com.prokonst.thingshouse.model.AppRepository;
import com.prokonst.thingshouse.model.Authorization;
import com.prokonst.thingshouse.model.SyncronizerDBs;
import com.prokonst.thingshouse.model.ThingsFireBase;
import com.prokonst.thingshouse.model.tables.Storage;
import com.prokonst.thingshouse.model.tables.Thing;
import com.prokonst.thingshouse.tools.CaptureCameraImage;

import android.view.Menu;
import android.view.MenuItem;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_sync) {

            SyncronizerDBs syncronizerStorages = SyncronizerDBs.getInstance(MainActivity.this);
            syncronizerStorages.sync();

            return  true;
        } else if(id == R.id.action_example) {
            AppRepository appRepository = AppRepository.getInstance(this.getApplication());

            String idT1 = "3D01C18A-6E2F-4E6F-AD8C-7BD429774DED";
            Thing thing1 = new Thing(idT1, "шт", idT1, "Вещь 1", "");
            thing1.calculateHash();
            appRepository.insertThing(thing1);

            String idT2 = "0ACE3316-E3D4-4383-B9EA-EBFDB240400F";
            Thing thing2 = new Thing(idT2, "шт", idT2, "Вещь 2", "");
            thing2.calculateHash();
            appRepository.insertThing(thing2);

            String idB1 = "347C8DCE-93FB-43D0-A186-A77DA2F4B974";
            Thing box1 = new Thing(idB1, "шт", idB1, "Ящик 1", "");
            box1.calculateHash();
            appRepository.insertThing(box1);

            String idB2 = "41CDBB0C-8FF3-4D29-89D5-4F041BF32EB3";
            Thing box2 = new Thing(idB2, "шт", idB2, "Ящик 2", "");
            box2.calculateHash();
            appRepository.insertThing(box2);

            String idS1 = "24F9466F-9FC1-49F6-8A0B-68BD15CE7BBC";
            Storage storage_thing1_in_box1 = new Storage(idS1, box1.getId(), thing1.getId(), 3.0);
            storage_thing1_in_box1.calculateHash();
            appRepository.insertStorage(storage_thing1_in_box1);

            String idS2 = "1CFA31CB-5D14-43F5-BB20-E1D1A5005ED3";
            Storage storage_thing2_in_box1 = new Storage(idS2, box1.getId(), thing2.getId(), 5.0);
            storage_thing2_in_box1.calculateHash();
            appRepository.insertStorage(storage_thing2_in_box1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}