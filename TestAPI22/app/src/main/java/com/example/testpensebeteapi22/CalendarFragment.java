package com.example.testpensebeteapi22;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class CalendarFragment extends Fragment {

    CalendarView simpleCalendarView;

    private Spinner helped_spinner;
    private Button new_helped;

    private TextView title;
    private EditText email;
    ArrayList<String> aides;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        View rootView = inflater.inflate(R.layout.helperfragment,container, false);
        new_helped = rootView.findViewById(R.id.add_new_helped);
        title = rootView.findViewById(R.id.helper_fragment_title);
        helped_spinner = rootView.findViewById(R.id.spinner_helped);
        email = rootView.findViewById(R.id.email_edit_text);

        //super.onCreate(savedInstanceState);
        simpleCalendarView = (CalendarView) rootView.findViewById(R.id.simpleCalendarView); // get the reference of CalendarView
        simpleCalendarView.setUnfocusedMonthDateColor(Color.BLUE); // set the yellow color for the dates of an unfocused month
        simpleCalendarView.setFocusedMonthDateColor(Color.RED); // set the red color for the dates of  focused month
        simpleCalendarView.setSelectedWeekBackgroundColor(Color.RED); // red color for the selected week's background
        simpleCalendarView.setWeekSeparatorLineColor(Color.GREEN); // green color for the week separator line **/
        // perform setOnDateChangeListener event on CalendarView

        CalendarFragment.this.listeAides();
        ArrayList<String> l = CalendarFragment.this.getAides();
        //System.out.println("La liste est :" + l.toString());

        /**helped_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(l);
                System.out.println(l);
                System.out.println(l);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, l);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                helped_spinner.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println(l);
                System.out.println(l);
                System.out.println(l);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, l);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                helped_spinner.setAdapter(adapter);
            }
        }); **/

        new_helped.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                database.child("aidés").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // On vérifie si un email correspond
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String id = childSnapshot.getKey();
                            String getEmail = snapshot.child(id).child("email").getValue(String.class);
                            System.out.println(getEmail);
                            if (getEmail.equals(email.getText().toString())) {
                                String id_helper = readConfig();
                                System.out.println("Trouvé");
                                database.child("aidants").child(id_helper).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        List l = new ArrayList();
                                        if (snapshot.exists()) {
                                            // Si la liste existe déjà, récupérez-la d'abord
                                            l = snapshot.child("list").getValue(new GenericTypeIndicator<List<String>>() {});
                                            if(l == null){
                                                l = new ArrayList<>();
                                            }
                                        }
                                        l.add(id);
                                        database.child("aidants").child(id_helper).child("list").setValue(l);
                                        Toast.makeText(rootView.getContext(), "Liste modifiée", Toast.LENGTH_LONG).show();

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
                // display the selected date by using a toast
                Toast.makeText(rootView.getContext(), dayOfMonth + "/" + (month + 1) + "/" + year, Toast.LENGTH_LONG).show();
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
            return prop.getProperty("id");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }
    public void listeAides(){
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        String id_helper = readConfig();
        database.child("aidants").child(id_helper).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> l = new ArrayList<>();
                if (snapshot.exists()) {
                    l = snapshot.child("list").getValue(new GenericTypeIndicator<ArrayList<String>>() {
                    });
                    if (l == null) {
                        l = new ArrayList<>();
                        System.out.println("null");
                    }
                    else{
                        System.out.println(l.toString());
                        CalendarFragment.this.setAides(l);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, CalendarFragment.this.getAides());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                helped_spinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setAides(ArrayList<String> aides) {
        this.aides = aides;
    }

    public ArrayList<String> getAides() {
        return aides;
    }
}


