package com.example.testpensebeteapi22;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;
import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class CalendarFragment extends Fragment {

    CalendarView simpleCalendarView;
    private Spinner helped_spinner;
    private String selected_helped; // Id de la personne aidée sélectionnée par le spinner
    private Button new_helped;
    private TextView title;
    private EditText email;
    ArrayList<String> aidesId;
    ArrayList<String> aidesNoms;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        View rootView = inflater.inflate(R.layout.helperfragment, container, false);
        new_helped = rootView.findViewById(R.id.add_new_helped);
        title = rootView.findViewById(R.id.helper_fragment_title);
        helped_spinner = rootView.findViewById(R.id.spinner_helped);
        email = rootView.findViewById(R.id.email_edit_text);

        simpleCalendarView = (CalendarView) rootView.findViewById(R.id.simpleCalendarView); // get the reference of CalendarView
        simpleCalendarView.setUnfocusedMonthDateColor(Color.BLUE); // set the yellow color for the dates of an unfocused month
        simpleCalendarView.setFocusedMonthDateColor(Color.RED); // set the red color for the dates of focused month
        simpleCalendarView.setSelectedWeekBackgroundColor(Color.RED); // red color for the selected week's background
        simpleCalendarView.setWeekSeparatorLineColor(Color.GREEN); // green color for the week separator line **/

        listeAides();

        helped_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Enregistre l'id de la personne séléctionnée dans le spinner et fait les mises à jours nécessaires
                selected_helped = CalendarFragment.this.getAidesNoms().get(position);
                System.out.println(selected_helped);
                String id_selected = getIdSelectionne(selected_helped);
                System.out.println(id_selected);
                writeConfig(id_selected);
                nomPersonne(id_selected);
                //Toast.makeText(getContext(), selected_helped + " Séléctionné", Toast.LENGTH_SHORT).show();
                selected_helped = id_selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setEnabled(false);
                helped_spinner.setVisibility(View.GONE);
            }
        });

        new_helped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("aidés").addListenerForSingleValueEvent(new ValueEventListener() {
                    String emailEntre = email.getText().toString();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // On vérifie si un email correspond
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String id = childSnapshot.getKey();
                            //System.out.println("Id " + id );
                            //System.out.println("test");
                            String getEmail = snapshot.child(id).child("email").getValue(String.class);
                            String nom = snapshot.child(id).child("name").getValue(String.class);
                            //System.out.println("Email :" + getEmail);
                            //System.out.println("Email entré " + email.getText().toString());
                            if (getEmail.equals(emailEntre)) {
                                String id_helper = GlobalData.id;
                                //System.out.println("Trouvé");
                                database.child("aidants").child(id_helper).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList l = new ArrayList();
                                        ArrayList noms = new ArrayList();
                                        //System.out.println("Liste créée");
                                        if (snapshot.exists()) {
                                            // Si la liste existe déjà, récupérez-la d'abord
                                            l = snapshot.child("list").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                                            });
                                            noms = snapshot.child("listNoms").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                                            });
                                            if (l == null) {
                                                l = new ArrayList<>();
                                                noms = new ArrayList();
                                                System.out.println("c'était null");
                                            }
                                        }
                                        //System.out.println("Avant ajout : ID " + id);
                                        l.add(id);
                                        noms.add(nom);
                                        database.child("aidants").child(id_helper).child("list").setValue(l);
                                        database.child("aidants").child(id_helper).child("listNoms").setValue(noms);
                                        //listeAides();
                                        Toast.makeText(rootView.getContext(), "Utilisateur ajouté !", Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            email.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // TODO → afficher l'interface de la personne aidée correspondant à la date
                Date date = new Date();
                date = new Date(date.getDate().withMonth(month+1).withDayOfMonth(dayOfMonth).withYear(year));
                openHelpedViewFragment(date, selected_helped);


            }
        });
        return rootView;
    }

    private void openHelpedViewFragment(Date date, String selected_helped) {
        // Créer une instance du nouveau fragment
        HelpedViewFragment newFragment = new HelpedViewFragment(date, selected_helped, this.getContext());

        // Commencer une transaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Remplacer le contenu actuel par le nouveau fragment
        transaction.replace(R.id.frameLayout, newFragment);

        // Ajouter la transaction à la pile arrière
        transaction.addToBackStack(null);

        // Valider la transaction
        transaction.commit();
    }

    public void listeAides() {
        // Affiche la liste des personnes aidées par l'aidant dans le spinner
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String id_helper = GlobalData.id;
        database.child("aidants").child(id_helper).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> l = new ArrayList<>();
                ArrayList<String> l2 = new ArrayList<>();
                if (snapshot.exists()) {
                    l = snapshot.child("list").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                    });
                    l2 = snapshot.child("listNoms").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                    });

                    if (l == null) {
                        l = new ArrayList<>();
                        l2 = new ArrayList<>();
                    }
                    CalendarFragment.this.setAidesId(l);
                    CalendarFragment.this.setAidesNoms(l2);

                }
                if(l.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, CalendarFragment.this.getAidesNoms());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    helped_spinner.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setAidesId(ArrayList<String> aides) {
        this.aidesId = aides;
    }

    public ArrayList<String> getAidesId() {
        return aidesId;
    }

    public ArrayList<String> getAidesNoms() {
        return aidesNoms;
    }

    public void setAidesNoms(ArrayList<String> aidesNoms) {
        this.aidesNoms = aidesNoms;
    }


    private void writeConfig(String id){
        //Ecrit dans un fichier config l'id de la personne aidée que l'on a séléctionné
        // Créer un objet Properties
        Properties prop = new Properties();
        // Définir les propriétés
        prop.setProperty("idSelectionne", id);

        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
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
            //Toast.makeText(getContext(), "Mode de connexion enregistré avec succès", Toast.LENGTH_SHORT).show();
        } catch (IOException io) {
            io.printStackTrace();
            //Toast.makeText(getContext(), "Erreur lors de l'enregistrement du mode de connexion", Toast.LENGTH_SHORT).show();
        }
    }

    public void nomPersonne(String idSelectionne){
        // Cherche le nom de la personne séléctionnée pour afficher le titre
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        database.child("aidés").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    String key = childSnapshot.getKey();
                    if(key.equals(idSelectionne)){
                        String nom = snapshot.child(key).child("name").getValue(String.class);
                        title.setText("Planning de " + nom);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getIdSelectionne(String nom){
        // Cherche l'id associé au nom
        ArrayList<String> noms = getAidesNoms();
        int k = 0;
        for(int i=0; i<noms.size() ; i++){
            if(noms.get(i).equals(nom)){
                k = i;
            }
        }
        System.out.println("Liste Id : " + getAidesId());
        return getAidesId().get(k);
    }
}
