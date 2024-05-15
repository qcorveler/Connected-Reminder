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
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFragment extends Fragment {
    EditText title;
    EditText subtitle;
    EditText date;
    EditText informations;
    EditText hour;
    Spinner type;
    Button add;

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
        type = rootView.findViewById(R.id.type);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String id = readConfig(); // On lit l'id de la personne aidée dans le fichier config
                findId(id, new MaxIdCallback() {
                    @Override
                    public void onMaxIdFound(String idEvent) {
                    }
                });
            }
        });
        return rootView;
    }

    public void findId(String id, MaxIdCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference eventsRef = database.child("aidés").child(id).child("events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Vérifiez si la node "events" existe
                if (!snapshot.exists()) {
                    // Si elle n'existe pas, on la crée
                    eventsRef.setValue(true);
                }
                findMaxIdAndAddEvent(eventsRef, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'annulation si nécessaire
            }
        });
    }

    private void findMaxIdAndAddEvent(DatabaseReference eventsRef, MaxIdCallback callback) {
        //Cherche l'id max des events enregistrés en attendant la réponse (car asynchrone)
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int max = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String idEventExistant = childSnapshot.getKey();
                    if (idEventExistant != null) {
                        int eventId = Integer.parseInt(idEventExistant);
                        if (eventId > max) {
                            max = eventId;
                        }
                    }
                }

                // Appel du callback avec la valeur maximale
                callback.onMaxIdFound(String.valueOf(++max));

                // Ajouter l'événement si la node "events" existait déjà
                if (max > 0) {
                    addEvent(eventsRef, max);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer l'annulation si nécessaire
            }
        });
    }

    private void addEvent(DatabaseReference eventsRef, int maxId) {
        // Ajouter l'événement à la node "events" avec l'ID max
        if(DateEstAuBonFormat(date.getText().toString(),hour.getText().toString())) {
            String typeString = type.getSelectedItem().toString();
            ArrayList<String[]> d = DateSplit(date.getText().toString(), hour.getText().toString());
            HashMap<String, Integer> icones = iconesEvents();
            HashMap<String, String> couleurs = couleursEvents();
            HashMap<String, Integer> ranges = rangeEvents();
            System.out.println(typeString);
            Integer icone = icones.containsKey(typeString) ? icones.get(typeString) : Integer.valueOf(18);
            String couleur = couleurs.containsKey(typeString) ? couleurs.get(typeString) : "0;193;200";
            Integer range = ranges.containsKey(typeString) ? ranges.get(typeString) : Integer.valueOf(60);
            Event e = new Event(maxId, title.getText().toString(), subtitle.getText().toString(), typeString, couleur, informations.getText().toString(), null, range, icone);
            eventsRef.child(String.valueOf(maxId)).setValue(e);
            eventsRef.child(String.valueOf(maxId)).child("annee").setValue(d.get(0)[2]);
            eventsRef.child(String.valueOf(maxId)).child("mois").setValue(d.get(0)[1]);
            eventsRef.child(String.valueOf(maxId)).child("jour").setValue(d.get(0)[0]);
            eventsRef.child(String.valueOf(maxId)).child("heure").setValue(d.get(1)[0]);
            eventsRef.child(String.valueOf(maxId)).child("minute").setValue(d.get(1)[1]);
            title.setText("");
            subtitle.setText("");
            informations.setText("");
            date.setText("");
            hour.setText("");
        }
        else {
            Toast.makeText(getContext(), "Veuillez entrer une date et une heure valides", Toast.LENGTH_SHORT).show();
        }
    }

    public interface MaxIdCallback {
        void onMaxIdFound(String id);
    }


    public boolean DateEstAuBonFormat(String date, String heure){
        String regexDate = "\\d{2}/\\d{2}/\\d{4}";
        Pattern patternDate = Pattern.compile(regexDate);
        Matcher matcherDate = patternDate.matcher(date);
        String regexHeure = "\\d{2}:\\d{2}";
        Pattern patternHeure = Pattern.compile(regexHeure);
        Matcher matcherHeure = patternHeure.matcher(heure);

        return matcherHeure.matches() && matcherDate.matches();
    }

    public ArrayList<String[]> DateSplit(String date, String heure){
        String[] e = date.split("/");
        String[] h = heure.split(":");
        ArrayList<String[]> d = new ArrayList<>();
        d.add(e);
        d.add(h);
        return d;
    }

    public HashMap<String, String> couleursEvents(){
        HashMap<String, String> couleurs = new HashMap<>();
        couleurs.put("Medicaments", "253;82;82");
        couleurs.put("Anniversaire","255;235;59");
        couleurs.put("Voeux", "255;235;59");
        couleurs.put("RDV", "111;136;255");
        couleurs.put("Autres", "0;193;255");
        return couleurs;
    }

    public HashMap<String, Integer> iconesEvents(){
        HashMap<String,Integer> icones = new HashMap<>();
        icones.put("Medicaments", 5);
        icones.put("Anniversaire", 72);
        icones.put("Voeux",70);
        icones.put("RDV", 10);
        icones.put("Autres", 62);
        return icones;
    }

    public HashMap<String, Integer> rangeEvents(){
        HashMap<String,Integer> ranges = new HashMap<>();
        ranges.put("Medicaments", 15);
        ranges.put("Anniversaire", 0);
        ranges.put("Voeux",0);
        ranges.put("RDV", 60);
        ranges.put("Autres", 60);
        return ranges;
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
            return prop.getProperty("idSelectionne");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }
}
