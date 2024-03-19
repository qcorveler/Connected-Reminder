package com.example.testpensebeteapi22;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parameters,container, false);
        return rootView;
}}
