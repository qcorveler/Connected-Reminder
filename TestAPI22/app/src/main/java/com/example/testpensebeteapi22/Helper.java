package com.example.testpensebeteapi22;

import android.annotation.SuppressLint;
import android.app.appsearch.ReportSystemUsageRequest;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Helper extends AppCompatActivity implements AddFragment.OnEventReturnedListener {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    public ArrayList<Event> evenements;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helper_layout);

        evenements = new ArrayList<Event>();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        frameLayout = findViewById(R.id.frameLayout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menu){

                int itemId = menu.getItemId();

                if(itemId == R.id.navigaton_home){
                    loadFragment(new HomeFragment(),false);
                }
                else if(itemId == R.id.navigaton_parameters){
                    loadFragment(new ParametersFragment(),false);
                }
                else{
                    AddFragment f = new AddFragment();
                    f.setOnEventReturnedListener(Helper.this);
                    loadFragment(f,false);
                }
                return true;
            }
        });
        loadFragment(new HomeFragment(),true);
    }
    public void loadFragment(Fragment fragment, boolean isAppInitialized){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        }
        else{
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();

    }

    public ArrayList<Event> getEvenements(){
        return evenements;
    }
    @Override
    public void onEventReturned(Event e){
        evenements.add(e);
        System.out.println("Event ajouté : " + e.toString());
    }
}

