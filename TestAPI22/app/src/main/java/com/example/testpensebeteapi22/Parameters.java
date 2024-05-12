package com.example.testpensebeteapi22;

import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.analytics.FirebaseAnalytics;

// Classe permettant de sauvegarder les paramètres dans la base de données FireBase
public class Parameters {
    private boolean notifications;
    private boolean standBy;
    private boolean isPastAccessible;
    private boolean isHourVisible;
    private boolean sounds;
    private String police_size;

    public Parameters(){
        // Constructeur vide pour les exceptions de l'ajout à la base de données
    }
    public Parameters(boolean notifications, boolean standBy, boolean isPastAccessible, boolean isHourVisible, boolean sounds, String police_size) {
        this.notifications = notifications;
        this.standBy = standBy;
        this.isPastAccessible = isPastAccessible;
        this.isHourVisible = isHourVisible;
        this.sounds = sounds;
        this.police_size = police_size;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public boolean isStandBy() {
        return standBy;
    }

    public boolean isPastAccessible() {
        return isPastAccessible;
    }

    public boolean isHourVisible() {
        return isHourVisible;
    }

    public boolean isSounds() {
        return sounds;
    }

    public String getPolice_size() {
        return police_size;
    }

    public String toString(){
        return isHourVisible + " " + isPastAccessible + " " + notifications + " " + standBy + " " + sounds + " "  + police_size;
    }
}
