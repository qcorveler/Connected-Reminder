package com.example.testpensebeteapi22;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.io.InputStream;
import java.util.Properties;

public class AddFragment extends Fragment {
    EditText title;
    EditText subtitle;
    EditText date;
    EditText informations;
    Button add;
    OnEventReturnedListener eventReturnedListener;

    // Fragment permettant d'ajouter un pense bête à la base de données
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.addfragment,container, false);

        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        informations = rootView.findViewById(R.id.informations);
        add = rootView.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("users");
                Event e = new Event(0, title.getText().toString(),subtitle.getText().toString(),"MED", "111;136;255", informations.getText().toString(), null, 50,10 );
                if(eventReturnedListener != null) {
                    //TODO Gestion des entrées pour la création d'un event (date, couleur, icon...)
                    Toast.makeText(rootView.getContext(), "Pense-Bête ajouté avec succès !", Toast.LENGTH_LONG).show();

                }
                String id = readConfig();
                myRef.child(id).child("events").child(String.valueOf(e.getId_event())).setValue(e);
                title.setText("");
                subtitle.setText("");
                informations.setText("");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Pour ajouter l'event
                        Event value = dataSnapshot.getValue(Event.class);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventReturnedListener) {
            eventReturnedListener = (OnEventReturnedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEventReturnedListener");
        }
    }

    // Je pense qu'on pourra supprimer les 3 méthodes suivantes mais je les garde au cas où
    public interface OnEventReturnedListener{
        void onEventReturned(Event e);
    }

    public void returnEventToActivity(Event e){
        if(eventReturnedListener != null){
            eventReturnedListener.onEventReturned(e);
        }
    }

    public void setOnEventReturnedListener(OnEventReturnedListener listener){
        eventReturnedListener = listener;
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
            return prop.getProperty("id");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

}
