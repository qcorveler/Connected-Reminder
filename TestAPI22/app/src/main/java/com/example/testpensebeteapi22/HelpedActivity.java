package com.example.testpensebeteapi22;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

/** <p> <b> Cette version est un prototype destiné à être montré à la Soutenance Finale de notre projet.<br>
 * Le but de ce prototype est de fonctionner sur la version 5.1 d'Android (API 22) (la tablette dont nous disposions pour la soutenance
 * intermédiaire ne supportait que cette version).<br>
 * Pour une version d'android plus récente disposant de plus de fonctionnalité regarder l'application Pense-Bête</b></p>
 * <p> La classe Home décrit la page d'accueil de notre application </p>
 *
 * <p> Le but de la page d'accueil est :</p>
 * <ul>                                  <li> d'afficher l'heure et la date du jour </li>
 *                                       <li> d'afficher la liste des évènements prévu pour le jour </li>
 *                                       <li> d'afficher un bandeau d'information contenant des infos sur le jour (qui ne sont pas horodatées) </li>
 *                                       <li> faire défiler les jours </li>
 * </ul>
 * <p> À la création, on récupère dans notre base de données les activités correspondantes à l'utilisateur
 * On affiche les activités du jour. </p>
 *
 * <p> Au bout d'un certain temps sans activité sur la page (pas de bouton cliqué par exemple),
 * si on est pas positionné sur aujourd'hui, on revient automatiquement sur aujourd'hui (le plus urgent) </p>
 *
 * <p> Si des évènements arrivent à terme (c'est à dire si ils sont dans moins de temps qu'une certaine limite prévue par l'utilisateur),
 * alors on affiche ces évènements en grand en attendant confirmation </p>
 *
 * @author Méline, Pauline, Jérémy et Quentin
 * @version 1.7.1 <i>prototype</i>
 * */
public class HelpedActivity extends AppCompatActivity {

    //region Attributs

    //region Settings

    /**
     * @parametre : <p>si vrai : afficher l'heure</p> <p>sinon : ne pas afficher </p>
     */
    private boolean setting_hourIsVisible;

    /**
     * @parametre : <p>si vrai : permettre la navigation dans le passé </p>
     */
    private boolean setting_pastIsAccessible;

    /**
     * @parametre : Limite des jours consultable
     * @exemple : 7 signifie qu'on peut aller jusqu'à une semaine en avance
     */
    private int setting_accessibleDaysLimit;

    /**
     * @parametre : <p> permet d'adapter la taille des textes </p>
     * @bounds : <p> de 0.5 à 2 (1 par défaut pour une tablette, 0.66 pour un téléphone)</p>
     */
    private float setting_Textsize;
    //endregion
    /**
     * Date actuelle
     */
    private Date today;
    /**
     * Date à afficher
     */
    private Date display_date;

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
     * Liste de tous les evenements prévus pour l'utilisateur
     */
    private ArrayList<Event> events_list = new ArrayList<>();
    /**
     * liste d'évenements du jour à afficher
     */
    private ArrayList<Event> day_events = new ArrayList<>();
    /**
     * liste des évènements du jour qui n'ont pas été confirmés par l'utilisateur
     */
    private ArrayList<Event> day_events_to_confirm = new ArrayList<>();
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

    private Thread backgroundHour;
    private Thread backgroundStandby;
    private Thread backgroundPastEvents;

    /**
     * Bouton de navigation des pense-bêtes vers la droite (le futur)
     */
    private ImageButton navigation_right_button;
    /**
     * Bouton de navigation des pense-bêtes vers la gauche (le passé)
     */
    private ImageButton navigation_left_button;

    /**
     * Bouton d'affichage des paramètres
     */
    private ImageButton parameters_button;

    /**
     * Compteur permettant de savoir depuis combien de temps aucune action n'a été effectuée
     */
    private int standby_counter;
    /**
     * limite de temps avant la mise en veille (en secondes)
     */
    private static final int STANDBY_LIMIT = 120;
    private boolean isInStandby;
    private Parameters parameters;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) { // COMPREHENSION : La méthode onCreate se lance à la création de l'activité (semblable à un constructeur)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // region ---------------- Paramètres par défaut -----------------
        setParametersFromDatabase();
        //setting_pastIsAccessible = false;
        setting_accessibleDaysLimit = 7;
        //setting_hourIsVisible = true;

        double screenInches = DimensionsUtil.getScreenInches(this);
        setting_Textsize = screenInches > 0 ? (float) screenInches / 7 : 1;
        //endregion

        //region ------------ Gestion du compteur d'activité ------------
        this.standby_counter = 0;
        isInStandby = false;
        //endregion ---------- FIN Gestion du compteur d'activité ----------

        //region ---------- Gestion de l'affichage de la date -----------

        // L'heure est constamment actualisée dans le Thread prévu à cet effet, donc pas d'affichage dans cette région

        today = new Date();
        display_date = new Date();

        text_view_display_day = findViewById(R.id.date); // récupération des TextView qui contiennent la date et l'heure pour l'affichage
        text_view_display_hour = findViewById(R.id.hour);
        text_view_display_temporality = findViewById(R.id.temporality);

        if (!setting_hourIsVisible) {
            text_view_display_hour.setVisibility(View.GONE);
        }
        text_view_display_temporality.setText(display_date.dateContext()); // Affichage de la date et de la temporalité au bon endroit
        text_view_display_day.setText(display_date.getDateFormat1());

        //endregion ---------------- FIN affichage de l'heure ---------------

        //region -------------- Récupération des évènements -------------
        //region --- simulation de la récupération des évènements ---
        /**  Date date4 = new Date(LocalDateTime.parse("2024-01-01T00:00:00"));

         Date date1 = new Date(); // format UTC pour la date et l'heure : LocalDateTime.parse("2023-12-20T15:00:00")
         date1 = new Date(date1.getDate().withHour(15).withMinute(0));
         Date date6 = new Date(date1.getDate().withHour(20).withMinute(0));
         Date date2 = new Date(date1.getDate().withHour(9).withMinute(0));

         date1.nextDay();
         Date date5 = new Date(date1.getDate().withHour(13).withMinute(0));
         Date date7 = new Date(date1.getDate().withHour(15).withMinute(0));
         Date date8 = new Date(date1.getDate().withHour(20).withMinute(30));
         Date date9 = new Date(date1.getDate().withHour(20).withMinute(0));
         Date date10 = new Date(date1.getDate().withHour(9).withMinute(0));

         date1.nextDay();
         Date date3 = new Date(date1.getDate().withHour(12).withMinute(0));

         this.events_list.add(new Event(1, "RDV médical", "Ophtalmologie", "APT", "111;136;255", "Docteur : Dr. Ferrezuelo,                                                                              Adresse : 21 rue du Pléssis 35700, Rennes.                                                 Note : N'oublie pas d'apporter tes lunettes !", date1, 60,10));
         this.events_list.add(new Event(8, "Médicaments", "Desloratadine DesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadineDesloratadine", "MDC", "253;82;82", "N'oublie pas de prendre tes médicaments c'est hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra hyper mega super giga tera maxi supra  hyper mega super giga tera maxi supra  important :)", date2, 15, 5));
         this.events_list.add(new Event(2, "Médicaments", "Desloratadine", "MDC", "253;82;82", "N'oublie pas de prendre tes médicaments c'est important :)", date6, 15, 5));

         this.events_list.add(new Event(3, "Visite médicale", "Vaccin", "APT", "111;136;255", "L'infirmière vient chez toi aujourd'hui. Prépare ton carnet de santé", date5, 30, 12));
         this.events_list.add(new Event(6, "Chauffage", "Il fait froid", "ORDRE", "0;193;255", "Il va faire particulièrement froid cette nuit, tu peux monter le chauffage dans le garage ", date7,10, 20));
         this.events_list.add(new Event(7, "Film", "Le Père Noël est une ordure", "ORDRE", "231;8;8", "Le film passe sur la 2 ce soir, si jamais ça t'intéresse", date8,10, 62));
         this.events_list.add(new Event(8, "Médicaments", "Desloratadine", "MDC", "253;82;82", "N'oublie pas de prendre tes médicaments c'est important :)", date9, 15, 5));
         this.events_list.add(new Event(2, "Médicaments", "Desloratadine", "MDC", "253;82;82", "N'oublie pas de prendre tes médicaments c'est important :)", date10, 15, 5));


         this.events_list.add(new Event(5, "BONNE ANNÉE !!! <3", "Meilleurs Voeux", "POS", "255;235;59", "Gros Bisous de la part de toute la famille :3, on pense à toi", date4, 1,70));

         this.banners_list.add(new Banner(1, "C'est l'anniversaire d'Annick !", "BTD", "255;235;59", "N'oublie pas de fêter l'anniversaire d'Annick aujourd'hui, voici son numéro si tu veux l'appeler : 07 83 73 84 50", date3, 72));
         **/
        //endregion

        String id = GlobalData.id; // id utilisateur
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference helpedRef = database.child("aidés").child(id).child("events");
        helpedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String idEvent = childSnapshot.getKey();
                    if (idEvent != null) {
                        eventInDatabase(idEvent);
                        System.out.println("Event ajouté");
                        events_list.toString();
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
        System.out.println("Events list :" + events_list.toString());



//            this.events_list.clear();

        sortEventList();
        //endregion ------------ FIN récupération des évènements ------------

        //region ---------------- Affichage des évènements --------------

        banner_layout = findViewById(R.id.day_banner_layout);
        banner_text = findViewById(R.id.day_banner_text);
        banner_image = findViewById(R.id.day_banner_imageview);

        day_events_layout = findViewById(R.id.event_list_layout);
        day_events_scrollview = findViewById(R.id.event_list_scrollview);


        displayDayEvents();
        displayDayBanner();

        //endregion -------------- FIN affichage des évènements -------------

        //region ---------- Gestion des boutons de navigation -----------

        navigation_right_button = findViewById(R.id.navigation_right_button);
        navigation_right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on reset le compteur de veille
                standby_counter = 0;
                isInStandby = false;

                display_date.nextDay();

                // si on atteint la limite de jours consultables, on cache le bouton
                if (Math.abs(today.dayDifference(display_date)) >= setting_accessibleDaysLimit) {
                    navigation_right_button.setClickable(false);
                    navigation_right_button.setVisibility(View.GONE);
                }

                // On affiche le bouton de retour (ici le bouton gauche)
                navigation_left_button.setClickable(true);
                navigation_left_button.setVisibility(View.VISIBLE);

                // on réaffiche la date
                text_view_display_temporality.setText(display_date.dateContext());
                text_view_display_day.setText(display_date.getDateFormat1());

                displayDayEvents();
                displayDayBanner();
            }
        });


        navigation_left_button = findViewById(R.id.navigation_left_button);
        navigation_left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on reset le compteur de veille
                standby_counter = 0;
                isInStandby = false;

                display_date.previousDay();

                // si on atteint la limite de jours consultables, ou si le passé n'est pas consultable on cache le bouton
                if ((Math.abs(today.dayDifference(display_date)) >= setting_accessibleDaysLimit) || (!setting_pastIsAccessible && onToday())) {
                    navigation_left_button.setClickable(false);
                    navigation_left_button.setVisibility(View.GONE);
                }

                // On affiche le bouton de retour (ici le bouton droit)
                navigation_right_button.setClickable(true);
                navigation_right_button.setVisibility(View.VISIBLE);

                // on réaffiche la date
                text_view_display_temporality.setText(display_date.dateContext());
                text_view_display_day.setText(display_date.getDateFormat1());

                displayDayEvents();
                displayDayBanner();
            }
        });

        this.parameters_button = findViewById(R.id.parameters_button);
        this.parameters_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO afficher un pop_up de retour vers le menu principal
            }
        });
        displayButtons();

        //gestion de l'appui sur le bouton retour :
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Si il y a un fragment ouvert : on le ferme
                EventFragment fragment_to_supp = (EventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (fragment_to_supp != null) {

                    isInStandby = false;
                    standby_counter = 0;

                    if (backgroundPastEvents != null) {
                        try {
                            backgroundPastEvents.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace(); // TODO → GERER L'EXCEPTION ?
                        }
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(fragment_to_supp);
                    transaction.commit();

                    displayDayEvents();

                } else
                    // Si on est pas en veille : on se place en veille
                    if (!isInStandby) {
                        standby();
                    } else {
                        // Si on est déjà en veille, on ferme l'application
                        finish();
                    }

            }
        });

        //endregion --------- FIN Gestion des boutons de navigation ---------

        //region -------- Gestion du Thread qui refresh l'heure ---------

        // Thread permettant la mise à jour de l'heure sur l'application
        backgroundHour = new Thread(new Runnable() {
            private static final int PAUSE = 500;

            @Override
            public void run() { // TODO → GESTION DES EXCEPTIONS DANS LES CATCHS
                while (true) {
                    while (setting_hourIsVisible) {
                        try {
                            Date newDay = new Date(today.getDate());
                            today.refreshDate();

                            String heureFormat = today.getHourFormat(); // Récupération de l'heure sous forme de String au bon format

                            //region          /!\ ------------- FONCTIONNEMENT THREADS ET UI THREAD --------------- /!\
                            //* COMPREHENSION
                            //* Sur un appareil Android, seul le UI Thread (User Interface Thread, qui gère l'affichage et les interractions avec l'utilisateur)
                            //* peut modifier l'affichage !!! Les méthodes setText() et généralement les méthodes d'affichage ne sont pas Thread safe.
                            //* Cela signifie qu'elles ne peuvent PAS être exécutées depuis n'importe quel Thread. Le résultat est incertain, cela peut aussi
                            //* bien marcher ou faire crasher l'application, et l'IDE ne prévient pas de ces erreurs, ce qui les rend difficiles à détecter.
                            //*
                            //* La méthode runOnUiThread(), qui prend un Runnable en paramètre, permet de mettre des instructions dans la file d'attente du UI Thread
                            //* depuis un autre Thread. On met donc les instructions d'affichage dans la méthode run() du paramètre de cette méthode runOnUiThread().
                            //*
                            //* Exemple ci-dessous ↓
                            //endregion

                            runOnUiThread(() -> text_view_display_hour.setText(heureFormat));

                            // si la date est passée au jour suivant alors que la tablette était en veille, on affiche les évènements du jour suivant
                            if (!today.sameDate(newDay) && isInStandby) {
                                display_date.refreshDate();
                                runOnUiThread(() -> displayDayEvents());
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(PAUSE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (!setting_hourIsVisible) {
                        try {
                            today.refreshDate();
                            Thread.sleep(2 * PAUSE); // ralentissement du Thread
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "backgroundHour");
        backgroundHour.start();

        //endregion

    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO → AJOUTER UN THREAD POUR LA MAJ DE LA BASE DE DONNEE SUR L'APPLICATION

        // Thread gérant le compteur de veille
        backgroundStandby = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (!isInStandby) {

                        standby_counter++;

                        if (standby_counter > STANDBY_LIMIT) {
                            runOnUiThread(() -> standby()); // COMPREHENSION : Runnable anonyme remplacé par une lambda expression
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (isInStandby) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "backgroundStandby");
        backgroundStandby.start();
    }

    /**
     * passage en veille de l'activité.
     *
     * @warning: <b>NOT ThreadSafe, utiliser runOnUIThread()</b>
     * <p></p>
     * Permet un ralentissement des threads TODO → RALENTISSEMENT DES THREADS ? OPTIMISATION DE LA BATTERIE ?
     */
    private void standby() { // Le passage en standby permet de repasser sur aujourd'hui quand ça fait trop longtemps qu'aucune action n'a été effectuée

        display_date.refreshDate(); // on remet la date d'affichage à aujourd'hui

        //suppression du fragment sur lequel se trouvait l'utilisateur au moment du passage en veille
        Fragment fragment_to_supp = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment_to_supp != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment_to_supp);
            getSupportFragmentManager().popBackStack();
            transaction.commit();
        }

        // on réaffiche la date
        text_view_display_temporality.setText(display_date.dateContext());
        text_view_display_day.setText(display_date.getDateFormat1());

        findViewById(R.id.fragment_container).setVisibility(View.GONE);

        updateEvent_to_confirm();

        displayDayEvents();
        displayDayBanner();
        displayButtons();

        isInStandby = true; // passage en mode standby

        //Thread gérant les events dont la date arrive à terme (avec demande de confirmation)
        backgroundPastEvents = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isInStandby) {
                    try {

                        // actualisation de la listes d'event non confirmées
                        updateEvent_to_confirm();

                        // affichage des events à confirmer en grand si on est en veille.
                        if (!day_events_to_confirm.isEmpty()) {

                            EventFragment informations_fragment = new EventFragment(getApplicationContext(), day_events_to_confirm.get(0), setting_Textsize);

                            if (isInStandby) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, informations_fragment);

                                        transaction.commit();
                                    }
                                });
                            }

                        } else {
                            EventFragment fragment_to_supp = (EventFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (fragment_to_supp != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.remove(fragment_to_supp);
                                        transaction.commit();

                                        displayDayEvents();
                                    }
                                });
                            }
                        }


                    } catch (Exception e) {
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }

            }
        }, "backgroundPastEvents");
        backgroundPastEvents.start();

    }

    /**
     * tri de la liste d'évènements dans l'ordre chronologique
     */
    private void sortEventList() {
        if (!events_list.isEmpty()) {
            // COMPREHENSION : définition d'un comparateur anonyme pour la liste d'évènement
            Collections.sort(events_list);
        }
    }

    /**
     * Affiche les boutons de navigation en fonction des choix de l'utilisateur
     *
     * @utilisation : cette méthode est destinée à être utilisée au moment où on affiche aujourd'hui sans passer par les boutons
     * (à la création ou lorsqu'on passe en veille)
     * @warning: <b>NOT ThreadSafe, utiliser runOnUIThread()</b>
     */
    private void displayButtons() {
        // si il n'est pas autorisé à consulter les évènements des dates passées, on enlève le bouton gauche par défaut
        if (!setting_pastIsAccessible) {
            navigation_left_button.setVisibility(View.GONE);
            navigation_left_button.setClickable(false);
        }

        // si la limite de jours est de 0, alors on ne peut pas regarder ni dans le passé, ni dans le futur
        if (setting_accessibleDaysLimit == 0) {
            navigation_right_button.setVisibility(View.GONE);
            navigation_right_button.setClickable(false);

            navigation_left_button.setVisibility(View.GONE);
            navigation_left_button.setClickable(false);
        }
    }

    /**
     * Affiche les evenements du jour selectionné par la variable {@link #display_date}.
     * Cette méthode gère aussi les clickListener des pense-bêtes qu'elle affiche
     *
     * @warning: <b>NOT ThreadSafe, utiliser runOnUIThread()</b>
     */
    public void displayDayEvents() {

        // Récupération des events du jour dans la liste day_events
        day_events.clear();
        day_events_layout.removeAllViews();
        System.out.println("Event list in displayDayEvents : " + events_list.toString());
        for (Event e : events_list) {
            if (e.getDate().sameDate(display_date)) {
                System.out.println("Day trouvé");
                day_events.add(e);
            }
        }

        // Si rien n'est prévu pour aujourd'hui, afficher le message correspondant
        if (day_events.isEmpty()) {
            findViewById(R.id.no_pensebete_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_pensebete_layout).setVisibility(View.GONE);
        }

        // Affichage des events de la liste de jour
        for (Event e : day_events) {
            // Création du layout enfant à ajouter et définition des attributs
            ConstraintLayout child = new ConstraintLayout(this);
            child.setId(View.generateViewId());
            child.setBackground(getDrawable(R.drawable.event_shape)); // Récupération de la forme
            child.getBackground().setTint(e.getRGBColor(false)); // Récupération de la couleur de l'évènement
            // Création du LayoutParams pour les paramètres du Layout child
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayoutParams.setMargins(0, 20, 0, 20);

            child.setLayoutParams(childLayoutParams);

            // Création des différentes vues du layout à ajouter (ie titre, image, sous-titre, heure)
            //region --- titre et sous-titre ---
            LinearLayout title_and_subtitle = new LinearLayout(this);
            title_and_subtitle.setId(View.generateViewId());
            title_and_subtitle.setOrientation(LinearLayout.VERTICAL);

            //region --- titre ---
            TextView title = new TextView(this);
            title.setId(View.generateViewId());
            title.setText(e.getTitle(true));
            title.setTextSize(30 * setting_Textsize);
            title.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.BLACK);
            title.setPadding(20, 5, 20, 5);
            //endregion

            //region --- sous-titre ---
            TextView subtitle = new TextView(this);
            subtitle.setId(View.generateViewId());
            subtitle.setText(e.getSubtitle(true));
            subtitle.setTextSize(20 * setting_Textsize);
            subtitle.setTextColor(Color.BLACK);
            subtitle.setPadding(20, 5, 20, 5);
            //endregion

            title_and_subtitle.addView(title);
            title_and_subtitle.addView(subtitle);
            //endregion

            //region --- Heure ---
            TextView hourEvent = new TextView(this);
            hourEvent.setId(View.generateViewId());
            hourEvent.setText(e.getDate().getHourFormat());
            hourEvent.setTextSize(30 * setting_Textsize);
            hourEvent.setTypeface(null, Typeface.BOLD);
            hourEvent.setPadding(20, 5, 20, 5);
            hourEvent.setTextColor(Color.BLACK);
            //endregion

            //region --- Image ---
            ImageView icon = new ImageView(this);
            icon.setId(View.generateViewId());
            int iconId = getResources().getIdentifier("icone_" + e.getIconId(), "drawable", getPackageName());
            icon.setImageDrawable(getDrawable(iconId));
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

                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                        //gestion de la veille
                        standby_counter = 0;
                        isInStandby = false;
                        parameters_button.setVisibility(View.GONE);

                        //gestion du fragment à ajouter
                        EventFragment informations_fragment = new EventFragment(getApplicationContext(), e, setting_Textsize);

                        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, informations_fragment);

                        transaction.commit();
                    }

                }
            });

            //  Ajouter le layout une fois créé en tant qu'enfant de notre layout prévu à cet effet
            day_events_layout.addView(child);
        }
    }

    /**
     * Affiche le bandeau d'information du jour sélectionné par la variable {@link #display_date} si il existe.
     *
     * @warning: <b>NOT ThreadSafe, utiliser runOnUIThread()</b>
     */
    private void displayDayBanner() {
        day_banner = null;
        for (Banner banner : banners_list) {
            if (banner.getDate().sameDate(display_date)) {
                day_banner = banner;
            }
        }

        if (day_banner == null) {
            banner_layout.setVisibility(View.GONE);
        } else {
            banner_layout.setVisibility(View.VISIBLE);
            banner_layout.getBackground().setTint(day_banner.getRGBColor());

            banner_text.setText(day_banner.getDescription(true));
            banner_text.setTextSize(30 * setting_Textsize);
            int iconId = getResources().getIdentifier("icone_" + day_banner.getIconId(), "drawable", getPackageName());
            banner_image.setImageDrawable(getDrawable(iconId));

            // Si la couleur de la note choisie est trop sombre, on applique un thème blanc à l'icone
            double luminance = day_banner.getLuminance();
            if (luminance < Event.LUMINANCE_ICON_LIMIT) {
                banner_image.setColorFilter(Color.WHITE);
            }
            if (luminance < Event.LUMINANCE_TEXT_LIMIT) {
                banner_text.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * @return vrai si la date d'affichage concorde avec aujourd'hui
     */
    public boolean onToday() {
        return this.today.sameDate(this.display_date);
    }

    /**
     * Actualise la liste {@link #day_events_to_confirm} avec tous les évènements qui doivent être confirmés.
     * <p> Supprime les évènements confirmés et trie la liste dans l'ordre chronologique </p>
     */
    private void updateEvent_to_confirm() {
        for (Event e : day_events) {
            if ((e.isConfirmed() || e.isForgotten()) && day_events_to_confirm.contains(e)) {
                day_events_to_confirm.remove(e);
            }
            if (e.mustBeConfirmed() && !day_events_to_confirm.contains(e)) {
                day_events_to_confirm.add(e);
            }
        }

        Collections.sort(day_events_to_confirm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        standby_counter = 0;
        return super.onTouchEvent(event);
    }

    public void setInStandby(boolean stanby) {
        this.isInStandby = false;
        if (!stanby) {
            this.standby_counter = 0;
        }
    }

    public void setVisible_ParametersButton() {
        parameters_button.setVisibility(View.VISIBLE);
    }

    public Thread getBackgroundPastEvents() {
        return this.backgroundPastEvents;
    }

    private String readConfig() {
        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        // Créer un objet Properties
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
            // Charger les propriétés à partir du fichier
            prop.load(input);

            // Récupérer l'identifiant à partir des propriétés
            return prop.getProperty("id");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public void eventInDatabase(String id) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference helpedRef = database.child("aidés").child(GlobalData.id).child("events").child(id);
        helpedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Récupère tous les attributs d'un event dans la base de données et en crée un puis l'ajoute à la liste des events
                String title = snapshot.child("title").getValue(String.class);
                String subtitle = snapshot.child("subtitle").getValue(String.class);
                String informations = snapshot.child("informations").getValue(String.class);
                String jour = snapshot.child("jour").getValue(String.class);
                String annee = snapshot.child("annee").getValue(String.class);
                String mois = snapshot.child("mois").getValue(String.class);
                String heure = snapshot.child("heure").getValue(String.class);
                String minute = snapshot.child("minute").getValue(String.class);
                String couleur = snapshot.child("color").getValue(String.class);
                String type = snapshot.child("type").getValue(String.class);
                Integer id = snapshot.child("id_event").getValue(Integer.class);
                Integer icone = snapshot.child("iconId").getValue(Integer.class);
                Date date = DateAuBonFormat(annee, mois, jour, heure, minute);
                Event e = new Event(id, title, subtitle, type, couleur, informations, date, 10, icone);
                events_list.add(e);

                System.out.println(snapshot.toString());
                System.out.println("Children" + snapshot.getChildren().toString());
                System.out.println(title);
                System.out.println("Info " + informations);
                System.out.println("Jour" + jour);
                System.out.println(icone);
                System.out.println(date);
                System.out.println(e.toString());
                System.out.println(events_list.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Date DateAuBonFormat(String annee, String mois, String jour, String heure, String minute) {

        String formattedDateTime = String.format(Locale.getDefault(), "%s-%s-%sT%s:%s:00", annee, mois, jour, heure, minute);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

        // Retourner la date et l'heure parsées
        return new Date(parsedDateTime);
    }

    public void setParametersFromDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference helpedRef = database.child("aidés").child(GlobalData.id).child("parameters");
        helpedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Parameters parameters1 = snapshot.getValue(Parameters.class);
                if(parameters1 != null) {
                    System.out.println("Paramètres : " + parameters1.toString());
                }
                else{
                    // s'il n'y a pas de paramètres enregistrés dans la base, on utilise ceux par défaut
                    parameters1 = new Parameters(true,true,false,true,true,"Moyenne");
                    }
                // Je sais pas si ça fonctionne bien, faudra tester
                setting_hourIsVisible = parameters1.isHourVisible();
                setting_pastIsAccessible = parameters1.isPastAccessible();
                // On a pas encore implémenté les notifications et les sons. Le standby c'est mieux de le laisser à false
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* TODO → FAIRE EN SORTE QUE L'APPLICATION SOIT UTILISABLE SUR TELEPHONE (MÊME SI C'EST MOCHE)*/
        /* TODO → SOUNDS */
        /* TODO → POP-UP AU MOMENT DE CLIQUER SUR BACK QUAND ON A PAS CONFIRMÉ L'EVENT */
    }
}

