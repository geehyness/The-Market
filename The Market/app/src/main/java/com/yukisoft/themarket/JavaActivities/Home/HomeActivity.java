package com.yukisoft.themarket.JavaActivities.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.yukisoft.themarket.JavaActivities.Home.Fragments.HomeFragment;
import com.yukisoft.themarket.JavaActivities.Home.Fragments.ChatsFragment;
import com.yukisoft.themarket.JavaActivities.Home.Fragments.ProfileFragment;
import com.yukisoft.themarket.JavaActivities.Home.Fragments.SearchFragment;
import com.yukisoft.themarket.JavaActivities.Home.Fragments.SellFragment;
import com.yukisoft.themarket.JavaActivities.UserManagement.SettingsActivity;
import com.yukisoft.themarket.R;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFrag = null;

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFrag = new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFrag = new SearchFragment();
                    break;
                case R.id.nav_sell:
                    selectedFrag = new SellFragment();
                    break;
                /*case R.id.nav_chats:
                    selectedFrag = new ChatsFragment();
                    break;*/
                case R.id.nav_profile:
                    selectedFrag = new ProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFrag).commit();

            return true;
        }
    };
}