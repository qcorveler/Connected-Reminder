package com.example.testpensebeteapi22;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

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