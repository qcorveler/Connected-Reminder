package com.example.testpensebeteapi22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        TextView registerNow = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();

                if(emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(Login.this, "Veuillez entre votre email ou mot de passe", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // On vérifie si un id correspond
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String id = childSnapshot.getKey();
                                String getPwd = snapshot.child(id).child("password").getValue(String.class);
                                String getEmail = snapshot.child(id).child("email").getValue(String.class);
                                if(getEmail.equals(emailTxt) && getPwd.equals(passwordTxt)){
                                    Toast.makeText(Login.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, MainMenuActivity.class);
                                    int idTransmis = Integer.parseInt(id);
                                    intent.putExtra("idConnexion",idTransmis);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                                else{
                                    Toast.makeText(Login.this, "Email/Password inconnus", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre la page pour créer un compte
                startActivity(new Intent(Login.this, Register.class));
            }
        });

    }
}