package com.example.testpensebeteapi22;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Properties;

public class HelpedViewEventFragment extends Fragment {

    //region Attributs

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
    /** Emplacement du bouton de retour arrière */
    private ImageButton back_button;

    private ImageButton supp_button;
    //endregion

    /** Constructeur permettant d'associer le fragment avec un contexte et son évènement
     * @param context contexte du fragment :<p> L'activity {@link HelpedActivity} est un contexte </p>
     * @param event l'évènement à relier à ce fragment
     */
    public HelpedViewEventFragment(Context context, Event event){
        this.context = context;
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_root = inflater.inflate(R.layout.helped_view_event_fragment, container, false);

        title = view_root.findViewById(R.id.event_info_title);
        hour_context = view_root.findViewById(R.id.event_info_hour_context);
        hour = view_root.findViewById(R.id.event_info_hour);
        subtitle = view_root.findViewById(R.id.event_info_subtitle);
        informations = view_root.findViewById(R.id.event_info_informations);

        icon = view_root.findViewById(R.id.event_info_icon);

        parent_layout = view_root.findViewById(R.id.event_info_parent_layout);
        info_layout = view_root.findViewById(R.id.event_info_info_layout);


        back_button = view_root.findViewById(R.id.navigation_back_button);
        supp_button = view_root.findViewById(R.id.supp_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();

                fragmentManager.popBackStack();
            }

        });

        supp_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();

                fragmentManager.popBackStack();

                String id = readConfig();

                DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                DatabaseReference helpedRef = database.child("aidés").child(id).child("events").child(String.valueOf(event.getId_event()));
                helpedRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            System.err.println("ça n'a pas marché");
                        }else{
                            System.out.println("ça a marché");
                        }
                    }
                });
            }
        });

        configureEvent();

        return view_root;
    }

    /** Configure le fragment pour que ses champs soient remplis avec les informations de l'évènement.
     * <p><b> Attention : </b> les attributs doivent être instanciés (non null) </p>*/
    private void configureEvent(){

        double screenInches = DimensionsUtil.getScreenInches(context);
        float textSize = screenInches > 0 ? (float) screenInches / 8 : 1;

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


        //gestion de la couleur du texte en fonction de la luminance
        double luminance = event.getLuminance();
        if (luminance < Event.LUMINANCE_ICON_LIMIT){
            icon.setColorFilter(Color.WHITE);
        }

        if(luminance < Event.LUMINANCE_TEXT_LIMIT){
            hour.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            hour_context.setTextColor(Color.WHITE);
            back_button.getBackground().setTint(Color.WHITE);
        }

        luminance = event.getLightLuminance();
        if(luminance < Event.LUMINANCE_TEXT_LIMIT){
            subtitle.setTextColor(Color.WHITE);
            informations.setTextColor(Color.WHITE);
        }
    }

    private HelpedViewEventFragment getFragment(){
        return this;
    }

    private String readConfig() {
        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        // Créer un objet Properties
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
            // Charger les propriétés à partir du fichier
            prop.load(input);

            // Récupérer l'identifiant à partir des propriétés
            return prop.getProperty("idSelectionne");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }
}
