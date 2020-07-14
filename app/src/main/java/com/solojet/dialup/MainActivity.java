package com.solojet.dialup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
}
