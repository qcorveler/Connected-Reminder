package com.example.testpensebeteapi22;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainMenuActivity extends AppCompatActivity {

    Button helperInterface;
    Button helpedInterface;

    // Menu principal permettant de choisir quel mode on souhaite utiliser (Helper / Helped)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        helpedInterface = findViewById(R.id.helped_button);
        helperInterface = findViewById(R.id.helper_button);
        helperInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                writeConfig("1");
                Intent helperActivity = new Intent(getApplicationContext(), Login.class);
                startActivity(helperActivity);
                finish();
            }
        });

        helpedInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                writeConfig("2");
                Intent helpedActivity = new Intent(getApplicationContext(), Login.class);
                startActivity(helpedActivity);
                finish();
            }

        });
    }
    private void writeConfig(String id){
        // Créer un objet Properties
        Properties prop = new Properties();
        // Définir les propriétés
        prop.setProperty("mode", id); // le mode 1 sera l'aidant et le mode 2 sera l'aidé

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
            //Toast.makeText(MainMenuActivity.this, "Mode de connexion enregistré avec succès", Toast.LENGTH_SHORT).show();
        } catch (IOException io) {
            io.printStackTrace();
            //Toast.makeText(MainMenuActivity.this, "Erreur lors de l'enregistrement du mode de connexion", Toast.LENGTH_SHORT).show();
        }
    }
}
