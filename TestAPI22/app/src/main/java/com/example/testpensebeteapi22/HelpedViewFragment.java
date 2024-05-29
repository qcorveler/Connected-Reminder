package com.example.testpensebeteapi22;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class HelpedViewFragment extends Fragment {

    private Date display_date;

    private String selected_helped;

    private Context context;
    /**
     * TextView dans lequel on affiche la date d'affichage
     */
    private TextView text_view_display_day;
    /**
     * TextView dans lequel on affiche l'heure d'affichage
     */
    private TextView text_view_display_hour;
    /**
     * TextView dans lequel on affiche le contexte de la date d'affichage par rapport à aujourd'hui
     *
     * @exemple : "Aujourd'hui", "Demain", "Dans 2 jours"...
     */
    private TextView text_view_display_temporality;

    /**
     * liste d'évenements du jour à afficher
     */
    private ArrayList<Event> day_events = new ArrayList<>();

    /**
     * liste de tous les bandeaux prévus pour l'utilisateur
     */
    private ArrayList<Banner> banners_list = new ArrayList<>();

    /**
     * bandeau du jour à afficher
     */
    private Banner day_banner;

    /**
     * Layout de gestion du bandeau
     */
    private ConstraintLayout banner_layout;
    /**
     * TextView dans lequel on affiche la description du bandeau
     */
    private TextView banner_text;
    /**
     * ImageView pour afficher l'icone du bandeau
     */
    private ImageView banner_image;
    /**
     * Layout de gestion des events du jour
     */
    private LinearLayout day_events_layout;
    /**
     * ScrollView contenant les events du jour
     */
    private ScrollView day_events_scrollview;

    private ImageButton back_button;

    private TextView planning_name;
    private String name;

    public HelpedViewFragment(Date date, String selected_helped, Context context, String name){
        this.display_date = date;
        this.selected_helped = selected_helped;
        this.context = context;
        this.name = name;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view_root = inflater.inflate(R.layout.helped_view_fragment, container, false);

        // Affichage de la date :
        Date display_date = this.display_date;

        text_view_display_day = view_root.findViewById(R.id.date); // récupération des TextView qui contiennent la date et l'heure pour l'affichage
        text_view_display_hour = view_root.findViewById(R.id.hour);
        text_view_display_temporality = view_root.findViewById(R.id.temporality);
        planning_name = view_root.findViewById(R.id.planning_name);

        text_view_display_temporality.setText(display_date.dateContext()); // Affichage de la date et de la temporalité au bon endroit
        text_view_display_day.setText(display_date.getDateFormat1());
        text_view_display_hour.setText(this.display_date.getHourFormat());
        text_view_display_day.setTextSize(15);
        text_view_display_hour.setTextSize(30);
        text_view_display_temporality.setTextSize(20);

        day_events_layout = view_root.findViewById(R.id.event_list_layout);
        day_events_scrollview = view_root.findViewById(R.id.event_list_scrollview);

        day_events_layout.removeAllViews();

        planning_name.setText("Planning de " + name);
        //day_events_scrollview.removeAllViews();

        // Récupération des events de la bonne personne
        String id = selected_helped; // id de la personne aidée sélectionnée
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference helpedRef = database.child("aidés").child(id).child("events");
        helpedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String idEvent = childSnapshot.getKey();
                    if (idEvent != null) {
                        eventInDatabase(idEvent, view_root);
                        System.out.println("Event ajouté");
                        System.out.println(day_events.toString());
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Affichage des events
        banner_layout = view_root.findViewById(R.id.day_banner_layout);
        banner_text = view_root.findViewById(R.id.day_banner_text);
        banner_image = view_root.findViewById(R.id.day_banner_imageview);

        banner_layout.removeAllViews();

        // Gestion du bouton de retour
        back_button = view_root.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return view_root;
    }

    public void displayDayEvents(View view_root){

        day_events_layout.removeAllViews();

        if (day_events.isEmpty()) {
            view_root.findViewById(R.id.no_pensebete_layout).setVisibility(View.VISIBLE);
        } else {
            view_root.findViewById(R.id.no_pensebete_layout).setVisibility(View.GONE);
        }

        Collections.sort(day_events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.compareTo(o2);
            }
        });

        // Affichage des events de la liste de jour
        for (Event e : day_events) {
            System.out.println(e.toString());
            // Création du layout enfant à ajouter et définition des attributs
            ConstraintLayout child = new ConstraintLayout(context);
            child.setId(View.generateViewId());
            child.setBackground(context.getDrawable(R.drawable.event_shape)); // Récupération de la forme
            child.getBackground().setTint(e.getRGBColor(false)); // Récupération de la couleur de l'évènement
            // Création du LayoutParams pour les paramètres du Layout child
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayoutParams.setMargins(0, 20, 0, 20);

            child.setLayoutParams(childLayoutParams);

            // Création des différentes vues du layout à ajouter (ie titre, image, sous-titre, heure)
            //region --- titre et sous-titre ---
            LinearLayout title_and_subtitle = new LinearLayout(context);
            title_and_subtitle.setId(View.generateViewId());
            title_and_subtitle.setOrientation(LinearLayout.VERTICAL);

            //region --- titre ---
            TextView title = new TextView(context);
            title.setId(View.generateViewId());
            title.setText(e.getTitle(true));
            title.setTextSize(30);
            title.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.BLACK);
            title.setPadding(20, 5, 20, 5);
            //endregion

            //region --- sous-titre ---
            TextView subtitle = new TextView(context);
            subtitle.setId(View.generateViewId());
            subtitle.setText(e.getSubtitle(true));
            subtitle.setTextSize(20);
            subtitle.setTextColor(Color.BLACK);
            subtitle.setPadding(20, 5, 20, 5);
            //endregion

            title_and_subtitle.addView(title);
            title_and_subtitle.addView(subtitle);
            //endregion

            //region --- Heure ---
            TextView hourEvent = new TextView(context);
            hourEvent.setId(View.generateViewId());
            hourEvent.setText(e.getDate().getHourFormat());
            hourEvent.setTextSize(30);
            hourEvent.setTypeface(null, Typeface.BOLD);
            hourEvent.setPadding(20, 5, 20, 5);
            hourEvent.setTextColor(Color.BLACK);
            //endregion

            //region --- Image ---
            ImageView icon = new ImageView(context);
            icon.setId(View.generateViewId());
            int iconId = getResources().getIdentifier("icone_" + e.getIconId(), "drawable", context.getPackageName());
            icon.setImageDrawable(context.getDrawable(iconId));
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            ConstraintLayout.LayoutParams iconParams = new ConstraintLayout.LayoutParams(64, 64);
            iconParams.setMargins(20, 5, 5, 5);
            icon.setLayoutParams(iconParams);

            //endregion


            //region Gestion de la couleur en fonction de la luminance
            // Si la couleur de la note choisie est trop sombre, on applique un thème blanc à l'icone et au texte,
            // Si l'event est confirmé, on applique un thème gris à l'icone et au texte.
            double luminance = e.getLuminance();
            int gris = 90;
            if (e.isConfirmed() || e.isForgotten()) {
                icon.setColorFilter(Color.rgb(gris, gris, gris));
            }
            if (luminance < Event.LUMINANCE_ICON_LIMIT) {
                icon.setColorFilter(Color.WHITE);
            }

            if (luminance < Event.LUMINANCE_TEXT_LIMIT) {
                hourEvent.setTextColor(Color.WHITE);
                subtitle.setTextColor(Color.WHITE);
                title.setTextColor(Color.WHITE);
            }
            //endregion

            if (e.isConfirmed() || e.isForgotten()) {
                title.setTextColor(Color.rgb(gris, gris, gris));
                subtitle.setTextColor(Color.rgb(gris, gris, gris));
                hourEvent.setTextColor(Color.rgb(gris, gris, gris));
            }

            // Ajout de ces vues au layout enfant
            child.addView(title_and_subtitle);
            child.addView(hourEvent);
            child.addView(icon);

            // Création du ConstraintSet sur le ConstraintLayout child

            //region --- Fonctionnement ConstraintSet ---
            // COMPREHENSION
            // Tous les enfants du layout sur lequel on applique le ConstraintSet doivent avoir un ID (important !)
            // en l'occurence l'ID est défini à partir de la commande setId(View.generateViewId()) qui génère un identifiant unique automatiquement

            //endregion

            ConstraintSet childConstraintSet = new ConstraintSet();
            childConstraintSet.clone(child);

            //region --- Ajout des contraintes ---
            childConstraintSet.connect(hourEvent.getId(), ConstraintSet.END, child.getId(), ConstraintSet.END);
            childConstraintSet.connect(hourEvent.getId(), ConstraintSet.TOP, child.getId(), ConstraintSet.TOP);
            childConstraintSet.connect(hourEvent.getId(), ConstraintSet.BOTTOM, child.getId(), ConstraintSet.BOTTOM);

            childConstraintSet.connect(icon.getId(), ConstraintSet.TOP, child.getId(), ConstraintSet.TOP);
            childConstraintSet.connect(icon.getId(), ConstraintSet.BOTTOM, child.getId(), ConstraintSet.BOTTOM);
            childConstraintSet.connect(icon.getId(), ConstraintSet.START, child.getId(), ConstraintSet.START);

            childConstraintSet.connect(title_and_subtitle.getId(), ConstraintSet.TOP, child.getId(), ConstraintSet.TOP);
            childConstraintSet.connect(title_and_subtitle.getId(), ConstraintSet.BOTTOM, child.getId(), ConstraintSet.BOTTOM);
            childConstraintSet.connect(title_and_subtitle.getId(), ConstraintSet.START, icon.getId(), ConstraintSet.END);
            //endregion

            childConstraintSet.applyTo(child);

            // Gestion du ClickListener
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //gestion du fragment à ajouter
                    openHelpedViewEventFragment(e);
                }
            });

            //  Ajouter le layout une fois créé en tant qu'enfant de notre layout prévu à cet effet
            day_events_layout.addView(child);
        }

    }

    private void openHelpedViewEventFragment(Event event) {
        // Créer une instance du nouveau fragment
        HelpedViewEventFragment newFragment = new HelpedViewEventFragment(context, event);

        // Commencer une transaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Remplacer le contenu actuel par le nouveau fragment
        transaction.replace(R.id.frameLayout, newFragment);

        // Ajouter la transaction à la pile arrière
        transaction.addToBackStack(null);

        // Valider la transaction
        transaction.commit();
    }

    public void displayDayBanner(View view_root){
        banner_layout.removeAllViews();
        // TODO ajouter les banners
    }

    /** récupère l'event correspondant à l'ID dans la database, l'ajoute à la liste {@link #day_events} si le jour correspond
     * et actualise l'affichage
     * @param id Id de l'event à aller chercher
     * @param view_root Vue racine dans laquelle afficher les events*/
    public void eventInDatabase(String id, View view_root) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference helpedRef = database.child("aidés").child(selected_helped).child("events").child(id);

        helpedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Récupère tous les attributs d'un event dans la base de données et en crée un puis l'ajoute à la liste des events
                String jour = snapshot.child("jour").getValue(String.class);
                String annee = snapshot.child("annee").getValue(String.class);
                String mois = snapshot.child("mois").getValue(String.class);
                String heure = snapshot.child("heure").getValue(String.class);
                String minute = snapshot.child("minute").getValue(String.class);
                if(minute != null && heure != null && mois != null && annee != null && jour != null) {
                    Date date = DateAuBonFormat(annee, mois, jour, heure, minute);
                    if(date.sameDate(display_date)){
                        String title = snapshot.child("title").getValue(String.class);
                        String subtitle = snapshot.child("subtitle").getValue(String.class);
                        String informations = snapshot.child("informations").getValue(String.class);
                        String couleur = snapshot.child("color").getValue(String.class);
                        String type = snapshot.child("type").getValue(String.class);
                        Integer id = snapshot.child("id_event").getValue(Integer.class);
                        Integer icone = snapshot.child("iconId").getValue(Integer.class);
                        Boolean confirmed = snapshot.child("confirmed").getValue(Boolean.class);
                        Boolean forgotten = snapshot.child("forgotten").getValue(Boolean.class);

                        Event e = new Event(id, title, subtitle, type, couleur, informations, date, 10, icone);
                        e.setConfirmed(confirmed);
                        e.setForgotten(forgotten);
                        if (!day_events.contains(e)){
                            System.out.println(e.getId_event());
                            day_events.add(e);
                        }

                        displayDayEvents(view_root);
                    /*System.out.println(snapshot.toString());
                    System.out.println("Children" + snapshot.getChildren().toString());
                    System.out.println(title);
                    System.out.println("Info " + informations);
                    System.out.println("Jour" + jour);
                    System.out.println(icone);
                    System.out.println(date);
                    System.out.println(e.toString());*/
                        System.out.println(day_events.toString());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /** @return  la date correspondante aux entrées passées en paramètres
     * @param annee année de la date voulue
     * @param mois mois de la date voulue
     * @param jour jour de la date voulue
     * @param heure heure de la date voulue
     * @param minute minutes de la date voulue*/
    private Date DateAuBonFormat(String annee, String mois, String jour, String heure, String minute) {

        String formattedDateTime = String.format(Locale.getDefault(), "%s-%s-%sT%s:%s:00", annee, mois, jour, heure, minute);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

        // Retourner la date et l'heure parsées
        return new Date(parsedDateTime);
    }

}
