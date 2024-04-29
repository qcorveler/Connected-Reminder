package com.example.testpensebeteapi22;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    Button helperInterface;
    Button helpedInterface;
    int idConnexion;

    // Menu principal permettant de choisir quel mode on souhaite utiliser (Helper / Helped)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        helpedInterface = findViewById(R.id.helped_button);
        helperInterface = findViewById(R.id.helper_button);
        idConnexion = getIntent().getIntExtra("idConnexion", -1); // -1 est une valeur par défaut si l'extra n'est pas trouvé
        setIdConnexion(idConnexion); // il faudra propager cet id pour savoir de quel utilisateur on parle

        helperInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent helperActivity = new Intent(getApplicationContext(), HelperActivity.class);
                startActivity(helperActivity);
                finish();
            }
        });

        helpedInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent helpedActivity = new Intent(getApplicationContext(), HelpedActivity.class);
                startActivity(helpedActivity);
                finish();
            }
        });
    }


    public int getIdConnexion() {
        return idConnexion;
    }

    public void setIdConnexion(int idConnexion) {
        this.idConnexion = idConnexion;
    }
}
