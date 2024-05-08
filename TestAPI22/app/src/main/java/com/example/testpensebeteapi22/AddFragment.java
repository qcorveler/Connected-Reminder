package com.example.testpensebeteapi22;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

public class AddFragment extends Fragment {
    EditText title;
    EditText subtitle;
    EditText date;
    EditText informations;
    EditText hour;
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
        hour = rootView.findViewById(R.id.heure);
        date = rootView.findViewById(R.id.date);
        String id = readConfig(); // On lit l'id de la personne aidée dans le fichier config
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("aidés");
                System.out.println("Id séléctionné : " + id);
                findId(id, new MaxIdCallback() {
                    @Override
                    public void onMaxIdFound(String idEvent) {
                        // Utilisez idEvent une fois qu'il est disponible
                        Date dateEvent = DateAuBonFormat(date.getText().toString(), hour.getText().toString());
                        Event e = new Event(Integer.parseInt(idEvent), title.getText().toString(), subtitle.getText().toString(), "MED", "111;136;255", informations.getText().toString(), dateEvent, 50, 10);
                        myRef.child(id).child("events").child(idEvent).setValue(e);
                        title.setText("");
                        subtitle.setText("");
                        informations.setText("");
                        date.setText("");
                        hour.setText("");
                    }
                });
                if(eventReturnedListener != null) {
                    //TODO Gestion des entrées pour la création d'un event (date, couleur, icon...)
                    Toast.makeText(rootView.getContext(), "Pense-Bête ajouté avec succès !", Toast.LENGTH_LONG).show();
                }



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
            System.out.println(prop.getProperty("idSelectionne"));
            return prop.getProperty("idSelectionne");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public void findId(String id, MaxIdCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        database.child("aidés").child(id).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            int max;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                max = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String idEventExistant = childSnapshot.getKey();
                    if (Integer.parseInt(idEventExistant) > max) {
                        max = Integer.parseInt(idEventExistant);
                    }
                }
                // Appel du rappel avec la valeur maximale
                callback.onMaxIdFound(String.valueOf(++max));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'annulation si nécessaire
            }
        });
    }

    public interface MaxIdCallback {
        void onMaxIdFound(String id);
    }


    public Date DateAuBonFormat(String date, String heure){
        String[] e = date.split("/");
        String[] h = heure.split(":");

        // Construire une chaîne de caractères dans le format ISO 8601
        String formattedDateTime = String.format(Locale.getDefault(), "%s-%s-%sT%s:%s:00", e[2], e[1], e[0], h[0], h[1]);

        // Utiliser LocalDateTime.parse() avec un formateur DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

        // Retourner la date et l'heure parsées
        return new Date(parsedDateTime);
    }

}
