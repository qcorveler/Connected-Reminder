package com.example.testpensebeteapi22;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        View rootView = inflater.inflate(R.layout.fragment_parameters,container, false);

        notifications = rootView.findViewById(R.id.notifications);
        standby = rootView.findViewById(R.id.standbymode);
        isPastAccessible = rootView.findViewById(R.id.pastaccessible);
        sounds = rootView.findViewById(R.id.sounds);
        isHourVisible = rootView.findViewById(R.id.hourvisible);
        police_size = rootView.findViewById(R.id.police);
        save = rootView.findViewById(R.id.add);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("parameters");
                Parameters p = new Parameters(notifications.isChecked(), standby.isChecked(), isPastAccessible.isChecked(), isHourVisible.isChecked(),sounds.isChecked(),police_size.getSelectedItem().toString());
                Toast.makeText(rootView.getContext(), "Paramètres sauvegardés", Toast.LENGTH_LONG).show();
                myRef.setValue(p);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Pour ajouter les paramètres
                        Parameters value = dataSnapshot.getValue(Parameters.class);
                        Log.d("Pense-Bête error", "Value is: " + value);
                        System.out.println("Event ajouté !");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Erreur lors de l'ajout à la base de données
                        Log.w("Erreur lors de l'ajout" ,"Failed to read value.", error.toException());
                    }
                });
            }
        });
        return rootView;
    }}
