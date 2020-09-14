package com.solojet.dialup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fragments.FavoriteFragment;
import fragments.HistoryFragment;
import fragments.HomeFragment;

import static models.Commons.FRAG_FAVORITE;
import static models.Commons.FRAG_HISTORY;
import static models.Commons.FRAG_HOME;
import static models.Commons.LAST_FRAGMENT;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    final Fragment fragmentHome  = new HomeFragment();
    final Fragment fragmentFav = new FavoriteFragment();
    final Fragment fragmentHistory = new HistoryFragment();

    private Fragment fragmentActive = fragmentHome;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private final long versionCode = BuildConfig.VERSION_CODE;
    private final String FB_RC_KEY_FORCE_UPDATE_VERSION = "force_update_version";
    private final String FB_RC_KEY_LATEST_VERSION = "latest_version";
    private final HashMap<String, Object> defaultMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        BottomNavigationView navBottom = findViewById(R.id.bottom_navigation);
        navBottom.setOnNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            String tag = savedInstanceState.getString(LAST_FRAGMENT);
            switch (tag){
                case FRAG_HOME:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentHome).commit();
                    fragmentActive = fragmentHome;
                    break;
                case FRAG_FAVORITE:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentFav).commit();
                    fragmentActive = fragmentFav;
                    break;
                case FRAG_HISTORY:
                    getSupportFragmentManager().beginTransaction().hide(fragmentActive).show(fragmentHistory).commit();
                    fragmentActive = fragmentHistory;
                    break;
            }
        }
        else
            loadFragment();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        checkForUpdate();
    }

    private void loadFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container,fragmentHistory, FRAG_HISTORY).hide(fragmentHistory);
        fragmentTransaction.add(R.id.main_container,fragmentFav, FRAG_FAVORITE).hide(fragmentFav);
        fragmentTransaction.add(R.id.main_container,fragmentHome, FRAG_HOME).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                if (fragmentActive != fragmentHome) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentHome).commit();
                    fragmentActive = fragmentHome;
                }
                return true;
            case R.id.nav_favorite:
                if (fragmentActive != fragmentFav) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentFav).commit();
                    fragmentActive = fragmentFav;
                }
                return true;
            case R.id.nav_history:
                if (fragmentActive != fragmentHistory) {
                    fragmentManager.beginTransaction().hide(fragmentActive).show(fragmentHistory).commit();
                    fragmentActive = fragmentHistory;
                }
                return true;

        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(LAST_FRAGMENT, fragmentActive.getTag());
        super.onSaveInstanceState(outState);
    }
    private void checkForUpdate() {
        // Hashmap which contains the default values for all the parameter defined in the remote config server
        defaultMap.put(FB_RC_KEY_FORCE_UPDATE_VERSION, versionCode);
        defaultMap.put(FB_RC_KEY_LATEST_VERSION, versionCode);

        long cacheExpiration = BuildConfig.DEBUG? 60 : TimeUnit.HOURS.toSeconds(12);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration).build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultMap);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {

                boolean updated = task.getResult();
                Log.d("Move", "Config params updated: " + updated);

                boolean visible = true;
                long forceUpdateVersion = mFirebaseRemoteConfig.getLong(FB_RC_KEY_FORCE_UPDATE_VERSION);
                long latestAppVersion = mFirebaseRemoteConfig.getLong(FB_RC_KEY_LATEST_VERSION);
                Log.i("Move", "onComplete: version code: "+ versionCode + "latest code: " + latestAppVersion);
                if (latestAppVersion > versionCode){
                    if(forceUpdateVersion>versionCode)
                        visible = false;
                    updateAlert(visible);
                }
            }
        });
    }
    private void updateAlert(boolean visible){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_alert, null);
        builder.setView(dialogView).setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnUpdate = alertDialog.findViewById(R.id.btnUpdate);
        TextView btnLater = alertDialog.findViewById(R.id.btnLater);
        btnLater.setVisibility(visible? View.VISIBLE : View.GONE);
        btnUpdate.setOnClickListener(view -> rateApp());
        btnLater.setOnClickListener(view -> alertDialog.cancel());
    }

    public void rateApp(){
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }




}
