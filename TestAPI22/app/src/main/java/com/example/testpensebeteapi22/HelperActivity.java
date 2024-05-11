package com.example.testpensebeteapi22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

//HelperActivity est l'activité principale du Helper avec la barre de navigation sur laquelle on rajoute un fragment
public class HelperActivity extends AppCompatActivity  {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    public String idTest;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);
        idTest = readConfig();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menu){

                int itemId = menu.getItemId();

                if(itemId == R.id.navigaton_home){
                    loadFragment(new CalendarFragment(),false);
                }
                else if(itemId == R.id.navigaton_parameters){
                    loadFragment(new ParametersFragment(),false);
                }
                else{
                    AddFragment f = new AddFragment();
                    loadFragment(f,false);
                }
                return true;
            }
        });
        loadFragment(new CalendarFragment(),true);


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

    private void writeConfig(String id){
        // Créer un objet Properties
        Properties prop = new Properties();
        // Définir les propriétés
        prop.setProperty("id", id);

        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            // Enregistrer les propriétés dans le fichier
            prop.store(output, "Fichier de configuration");
            //Toast.makeText(getApplicationContext(), "Id enregistré", Toast.LENGTH_SHORT).show();
        } catch (IOException io) {
            io.printStackTrace();
           // Toast.makeText(getApplicationContext(), "Erreur lors de l'enregistrement du mode de connexion", Toast.LENGTH_SHORT).show();
        }
    }

    private String readConfig() {
        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        // Créer un objet Properties
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
            // Charger les propriétés à partir du fichier
            prop.load(input);

            // Récupérer l'identifiant à partir des propriétés
            System.out.println(prop.getProperty("idSelectionne"));
            return prop.getProperty("id");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }
}

