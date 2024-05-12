package com.example.testpensebeteapi22;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ParametersFragment extends Fragment {
    Switch notifications;
    Switch standby;
    Switch isPastAccessible;
    Switch isHourVisible;
    Switch sounds;
    Spinner police_size;
    Button save;

    // Fragment pour choisir les paramètres désirés sur l'application
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parameters, container, false);

        notifications = rootView.findViewById(R.id.notifications);
        standby = rootView.findViewById(R.id.standbymode);
        isPastAccessible = rootView.findViewById(R.id.pastaccessible);
        sounds = rootView.findViewById(R.id.sounds);
        isHourVisible = rootView.findViewById(R.id.hourvisible);
        police_size = rootView.findViewById(R.id.police);
        save = rootView.findViewById(R.id.add);

        setParameters(); // récupère les paramètres de la base de données s'ils ont déjà été enregistrés
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("aidés").child(readConfig()).child("parameters");
                Parameters p = new Parameters(notifications.isChecked(), standby.isChecked(), isPastAccessible.isChecked(), isHourVisible.isChecked(), sounds.isChecked(), police_size.getSelectedItem().toString());
                Toast.makeText(rootView.getContext(), "Paramètres sauvegardés", Toast.LENGTH_LONG).show();
                myRef.setValue(p);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Pour ajouter les paramètres
                        Parameters value = dataSnapshot.getValue(Parameters.class);
                        Log.d("Pense-Bête error", "Value is: " + value);
                        System.out.println("Paramètres ajoutés !");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Erreur lors de l'ajout à la base de données
                        Log.w("Erreur lors de l'ajout", "Failed to read value.", error.toException());
                    }
                });
            }
        });
        return rootView;
    }

    private String readConfig() {
        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getActivity());
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        // Créer un objet Properties
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
            // Charger les propriétés à partir du fichier
            prop.load(input);

            // Récupérer l'identifiant à partir des propriétés
            System.out.println(prop.getProperty("idSelectionne"));
            return prop.getProperty("idSelectionne");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    private void setParameters() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("aidés").child(readConfig()).child("parameters");
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Parameters parameters = snapshot.getValue(Parameters.class);
                System.out.println(parameters);
                if(parameters !=null ){
                    isPastAccessible.setChecked(parameters.isPastAccessible());
                    System.out.println(parameters.isHourVisible());
                    notifications.setChecked(parameters.isNotifications());
                    sounds.setChecked(parameters.isSounds());
                    isHourVisible.setChecked(parameters.isHourVisible());
                    standby.setChecked(parameters.isStandBy());
                    if(parameters.getPolice_size().equals("Police : Petite")){
                        police_size.setSelection(2);
                    }
                    else if(parameters.getPolice_size().equals("Police : Moyenne")){
                        police_size.setSelection(1);
                    }
                    else{
                        police_size.setSelection(0);
                    }
                }
                else{
                    notifications.setChecked(true);
                    sounds.setChecked(true);
                    standby.setChecked(true);
                    isPastAccessible.setChecked(false);
                    isHourVisible.setChecked(true);
                    police_size.setSelection(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
