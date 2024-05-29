package com.example.testpensebeteapi22;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** La classe EventFragment est utilisée pour paramétrer le fragment qui sert à afficher un évènement en grand à l'écran.
 *  @extends: {@link Fragment}
 *  @author Méline, Pauline, Jérémy et Quentin
 *  @version 1.1 */
public class EventFragment extends Fragment {

    //region Attributs

    private float textSize;
    /** Context d'utilisation du fragment : <p> dans l'application, l'activity {@link HelpedActivity} </p>*/
    private Context context;

    /** Évènement relié au fragment */
    private Event event;

    /** Layout principal du fragment */
    private ConstraintLayout parent_layout;
    /** Layout d'information du fragment */
    private ConstraintLayout info_layout;

    /** Emplacement du titre de l'évènement sur le fragment */
    private TextView title;
    /** Emplacement du contexte horaire de l'évènement sur le fragment
     * @exemple: <code>"<b>Dans 20 minutes</b>"</code>, <code>"<b>Dans 1 heure</b>"</code> sont des contextes horaires */
    private TextView hour_context;
    /** Emplacement de l'heure de l'évènement sur le fragment */
    private TextView hour;
    /** Emplacement du sous-titre de l'évènement sur le fragment */
    private TextView subtitle;
    /** Emplacement des informations de l'évènement sur le fragment */
    private TextView informations;

    /** Emplacement de l'icone de l'évènement sur le fragment */
    private ImageView icon;

    /** Emplacement de la checkbox de confirmation sur le fragment */
    private CheckBox confirmation_check_box;
    /** Emplacement de la checkbox "Oublié" sur le fragment */
    private CheckBox forgotten_check_box;

    /** Emplacement du bouton de retour arrière */
    private ImageButton back_button;
    //endregion

    /** Constructeur permettant d'associer le fragment avec un contexte et son évènement
     * @param context contexte du fragment :<p> L'activity {@link HelpedActivity} est un contexte </p>
     * @param event l'évènement à relier à ce fragment
     * @param textSize multiplicateur de la taille du texte*/
    public EventFragment(Context context, Event event, float textSize){
        this.context = context;
        this.event = event;
        this.textSize = textSize;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_root = inflater.inflate(R.layout.event_info_fragment, container, false);

        title = view_root.findViewById(R.id.event_info_title);
        hour_context = view_root.findViewById(R.id.event_info_hour_context);
        hour = view_root.findViewById(R.id.event_info_hour);
        subtitle = view_root.findViewById(R.id.event_info_subtitle);
        informations = view_root.findViewById(R.id.event_info_informations);

        icon = view_root.findViewById(R.id.event_info_icon);

        parent_layout = view_root.findViewById(R.id.event_info_parent_layout);
        info_layout = view_root.findViewById(R.id.event_info_info_layout);

        confirmation_check_box = view_root.findViewById(R.id.event_confirmation_checkbox);
        forgotten_check_box = view_root.findViewById(R.id.forgotten_event_checkbox);

        confirmation_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                event.setConfirmed(isChecked);
                if (isChecked){
                    forgotten_check_box.setChecked(false);

                    // Mettre à jour la base de données
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                    DatabaseReference myRef = database.getReference("aidés").child(GlobalData.id).child("events").child(String.valueOf(event.getId_event())).child("confirmed");

                    DatabaseReference myRef2 = database.getReference("aidés").child(GlobalData.id).child("events").child(String.valueOf(event.getId_event())).child("forgotten");

                    myRef.setValue(true);
                    myRef2.setValue(false);

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Pour ajouter les paramètres
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Erreur lors de l'ajout à la base de données
                            Log.w("Erreur lors de l'ajout", "Failed to read value.", error.toException());
                        }
                    });

                    myRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Pour ajouter les paramètres
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Erreur lors de l'ajout à la base de données
                            Log.w("Erreur lors de l'ajout", "Failed to read value.", error.toException());
                        }
                    });
                }
            }
        });
        forgotten_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                event.setForgotten(isChecked);
                if (isChecked){
                    confirmation_check_box.setChecked(false);

                    // Mettre à jour la base de données
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app");
                    DatabaseReference myRef = database.getReference("aidés").child(GlobalData.id).child("events").child(String.valueOf(event.getId_event())).child("forgotten");

                    myRef.setValue(true);

                    DatabaseReference myRef2 = database.getReference("aidés").child(GlobalData.id).child("events").child(String.valueOf(event.getId_event())).child("confirmed");
                    myRef2.setValue(false);

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Pour ajouter les paramètres
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Erreur lors de l'ajout à la base de données
                            Log.w("Erreur lors de l'ajout", "Failed to read value.", error.toException());
                        }
                    });
                    myRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Pour ajouter les paramètres
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Erreur lors de l'ajout à la base de données
                            Log.w("Erreur lors de l'ajout", "Failed to read value.", error.toException());
                        }
                    });

                }
            }
        });

        back_button = view_root.findViewById(R.id.navigation_back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context activity = getActivity();
                if (activity instanceof HelpedActivity) {
                    HelpedActivity home = (HelpedActivity) activity;
                    home.setInStandby(false);

                    Thread backgroundThread = home.getBackgroundPastEvents();

                    if (backgroundThread != null) {
                        try {
                            backgroundThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace(); // TODO → GERER L'EXCEPTION ?
                        }
                    }

                    FragmentTransaction transaction = home.getSupportFragmentManager().beginTransaction();
                    transaction.remove(getFragment());
                    transaction.commit();

                    home.displayDayEvents();
                    home.setVisible_ParametersButton();
                }
            }

        });

        configureEvent();

        return view_root;
    }

    /** Configure le fragment pour que ses champs soient remplis avec les informations de l'évènement.
     * <p><b> Attention : </b> les attributs doivent être instanciés (non null) </p>*/
    private void configureEvent(){

        //gestion des textes
        title.setText(event.getTitle(false));
        title.setTextSize((float)Math.max(30,50*Math.pow(textSize, 2.5)));
        subtitle.setText(event.getSubtitle(false));
        subtitle.setTextSize(35*textSize);
        hour_context.setText(event.getDate().hourContext());
        hour_context.setTextSize((float) Math.max(20,35 * Math.pow(textSize, 2.5)));
        hour.setText(event.getDate().getHourFormat());
        hour.setTextSize(60*textSize);
        informations.setText(event.getInformations());
        informations.setTextSize(25*textSize);

        //gestion de l'image
        int iconId = context.getResources().getIdentifier("icone_"+ event.getIconId(), "drawable", context.getPackageName()) ;
        icon.setImageDrawable(context.getDrawable(iconId));

        // gestion de la couleur du background
        parent_layout.getBackground().setTint(event.getRGBColor(true));
        info_layout.getBackground().setTint(event.getLightRGBColor());

        //gestion des checkbox
        confirmation_check_box.setChecked(event.isConfirmed());
        confirmation_check_box.setTextSize((float)(25*Math.pow(textSize, 1.4)));
        forgotten_check_box.setChecked(event.isForgotten());
        forgotten_check_box.setTextSize((float)(25*Math.pow(textSize, 1.1)));

        if(!event.getDate().sameDate(new Date()) && event.getDate().compareTo(new Date())>0){
            // Si on est dans le futur, on ne peut pas cliquer sur terminé ou oublié
            confirmation_check_box.setVisibility(View.GONE);
            forgotten_check_box.setVisibility(View.GONE);
        }


        //gestion de la couleur du texte en fonction de la luminance
        double luminance = event.getLuminance();
        if (luminance < Event.LUMINANCE_ICON_LIMIT){
            icon.setColorFilter(Color.WHITE);
        }

        if(luminance < Event.LUMINANCE_TEXT_LIMIT){
            hour.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            hour_context.setTextColor(Color.WHITE);
            confirmation_check_box.setTextColor(Color.WHITE);
            forgotten_check_box.setTextColor(Color.WHITE);
            back_button.getBackground().setTint(Color.WHITE);
        }

        luminance = event.getLightLuminance();
        if(luminance < Event.LUMINANCE_TEXT_LIMIT){
            subtitle.setTextColor(Color.WHITE);
            informations.setTextColor(Color.WHITE);
        }
    }

    private EventFragment getFragment(){
        return this;
    }
}
