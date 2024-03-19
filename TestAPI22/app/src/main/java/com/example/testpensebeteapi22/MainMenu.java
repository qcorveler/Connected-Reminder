package com.example.testpensebeteapi22;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    Button helperInterface;
    Button helpedInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        helpedInterface = findViewById(R.id.helped_button);
        helperInterface = findViewById(R.id.helper_button);

        helperInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent helperActivity = new Intent(getApplicationContext(), Helper.class);
                startActivity(helperActivity);
                finish();
            }
        });

        helpedInterface.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent helpedActivity = new Intent(getApplicationContext(), Home.class);
                startActivity(helpedActivity);
                finish();
            }
        });
    }
}
