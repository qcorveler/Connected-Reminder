package com.example.testpensebeteapi22;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ParametersFragment extends Fragment {
    Switch notifications;
    Switch standby;
    Switch isPastAccessible;
    Switch isHourVisible;
    Switch sounds;
    Spinner police_size;
    Button save;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parameters,container, false);

        notifications = rootView.findViewById(R.id.notifications);
        standby = rootView.findViewById(R.id.standbymode);
        isPastAccessible = rootView.findViewById(R.id.pastaccessible);
        isHourVisible = rootView.findViewById(R.id.hourvisible);
        police_size = rootView.findViewById(R.id.police);
        save = rootView.findViewById(R.id.add);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(rootView.getContext(), "Paramètres sauvegardés", Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }}
