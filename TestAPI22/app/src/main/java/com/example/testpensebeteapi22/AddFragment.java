package com.example.testpensebeteapi22;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {
    EditText title;
    EditText subtitle;
    EditText date;
    EditText informations;
    Button add;
    OnEventReturnedListener eventReturnedListener;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_reminder,container, false);

        title = rootView.findViewById(R.id.title);
        subtitle = rootView.findViewById(R.id.subtitle);
        informations = rootView.findViewById(R.id.informations);
        add = rootView.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Event e = new Event(0, title.getText().toString(),subtitle.getText().toString(),"MED", "#DEF", informations.getText().toString(), new Date(), 50,10 );
                if(eventReturnedListener != null) {
                    // Envoyez l'event à l'activité
                    //TODO Gestion des entrées pour la création d'un event (date, couleur, icon...)
                    returnEventToActivity(e);
                    Toast.makeText(rootView.getContext(), "Pense-Bête ajouté avec succès !", Toast.LENGTH_LONG).show();

                }
                title.setText("");
                subtitle.setText("");
                informations.setText("");
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


}
