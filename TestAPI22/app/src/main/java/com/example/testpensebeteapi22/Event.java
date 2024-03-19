package com.example.testpensebeteapi22;

import android.graphics.Color;

/** <p> La classe Event permet de stocker les information d'un évènement envoyé par l'aidant </p>
 *  <p> Quand un évènement arrive à terme, une confirmation est demandée à l'aidé pour savoir si l'évènement à été effectué ou non </p>
 *
 * <p> Le but de la classe Event est de : </p>
 * <ul>                                  <li> Stocker toutes les informations d'un évènement </li>
 *                                       <li> Permettre des accès à ces information avec le bon format </li>
 * </ul>
 *
 * @author Méline, Pauline, Jérémy et Quentin
 * @version 1.4
 * */
public class Event extends InformationElement implements Comparable<Event>{

    //region Attributs
    /** ID unique de l'évènement */
    private int id_event;
    /**  Titre de l'évènement */
    private String title;
    /** Description rapide de l'évènement */
    private String subtitle;
    /** Information à propos de l'évènement (plus complètes que {@link Event#subtitle}) */
    private String informations;
    /** temps en minutes à partir duquel on peut afficher le pense-bête
     * @exemple: <pre>range = 60</pre> signifie qu'on affichera le pense-bête en grand 60 minutes avant la date de l'évènement */
    private int range;

    /** Attribut permettant de savoir si l'évènement a été confirmé par l'utilisateur */
    private boolean confirmed;
    /** Attribut permettant de savoir si l'évènement a été oublié par l'utilisateur */
    private boolean forgotten;
    /** Attribut permettant de savoir si un évènement est récurrent où non*/
    private boolean recurrent;


    /** Limite en nombre de caractère au dessus de laquelle on considère qu'il faut crop le titre (et ajouter '...') */
    private static final int TITLE_CROP_LIMIT = 30;
    /** Limite en nombre de caractère au dessus de laquelle on considère qu'il faut crop le sous-titre (et ajouter '...')*/
    private static final int SUBTITLE_CROP_LIMIT = 50;

    /** Valeur de luminance entre 0 et 255 en dessous de laquel on considère qu'il faut mettre l'image en thème clair pour une meilleure visibilité */
    public static final int LUMINANCE_ICON_LIMIT = 150;
    /** Valeur de luminance entre 0 et 255 en dessous de laquel on considère qu'il faut mettre le texte en thème clair pour une meilleure visibilité */
    public static final int LUMINANCE_TEXT_LIMIT = 100;
    //endregion

    /** Constructeur permettant une instanciation complète de tous les attributs de l'évènement
     * @param id ID de l'event
     * @param title titre de l'event
     * @param subtitle sous-titre de l'évènement, à associer à l'attribut {@link Event#subtitle}
     * @param type type de l'évènement, à associer à l'attibut {@link Event#type}
     * @param color couleur associée à l'évènement, à associer  l'attribut {@link Event#color}. Attention au format !
     * @param info information de l'évènement, à associer à l'attribut
     * @param d Date de l'évènement
     * @param r Temps en minutes à partir duquel on peut afficher le pense-bête, cf {@link #range}
     * @param iconId Id de l'icone de l'évènement */
    public Event(int id, String title, String subtitle, String type, String color, String info, Date d, int r, int iconId){
        this.id_event = id;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.color = color;
        informations = info;
        date = d;
        range = r;
        confirmed = false;
        this.iconId = iconId;
    }

    /** Teste si 2 évènements sont identiques. On considère que 2 évènements sont identiques si leur ID est égal
     * @param other l'autre Event à comparer
     * @return vrai si les 2 évènements sont identiques, <p> faux sinon </p>
     * @overrides: {@link InformationElement#equals(InformationElement)}*/
    @Override
    public boolean equals(InformationElement other){
        if(other instanceof Event){
            return this.id_event==((Event) other).id_event;
        }else{
            return false;
        }
    }

    //region --- GETTERS AND SETTERS ---
    public int getId_event() {return id_event;}
    public void setId_event(int id_event) {this.id_event = id_event;}

    /** @return le titre de l'objet Event, tronqué ou non
     *
     * @param crop Si vrai tronquer le titre,<p>sinon laisser le titre par défaut</p> */
    public String getTitle(boolean crop) {

        int maxLength = TITLE_CROP_LIMIT; // Longueur maximum d'un titre dans l'affichage du programme de la journée
        String cropTitle = crop && title.length()>maxLength ? title.substring(0, maxLength) + "..." : title;

        return cropTitle;
    }
    public void setTitle(String title) {this.title = title;}


    /** @return le sous-titre de l'objet Event tronqué ou non, ou bien la mention "Oublié !" ou "Terminé !" lorsque l'event est oublié ou terminé
    *
    * @param crop Si vrai tronquer le sous-titre, <p>sinon laisser le sous-titre par défaut</p> */
    public String getSubtitle(boolean crop){

        if(crop){
            if (this.forgotten) {
                return "Oublié !";
            }
            if (this.confirmed){
                return "Terminé !";
            }
        }

        int maxLength = SUBTITLE_CROP_LIMIT; // longueur maximale d'un sous-titre dans l'affichage de la journée.
        String cropSubtitle = crop && subtitle.length()>maxLength ? subtitle.substring(0, maxLength) + "..." : subtitle;

        return cropSubtitle;
    }
    public void setSubtitle(String subtitle){this.subtitle = subtitle;}

    public String getInformations() {return informations;}
    public void setInformations(String informations) {this.informations = informations;}

    public boolean isConfirmed(){return this.confirmed;}
    public void setConfirmed(boolean confirmed){this.confirmed = confirmed;}

    public boolean isForgotten(){return this.forgotten;}
    public void setForgotten(boolean forgotten){this.forgotten = forgotten;}

    //endregion

    /** Cette méthode permet d'obtenir une couleur écrite au format d'un entier grâce à la fonction {@link Color#rgb(int, int, int)}.
     * <p> La couleur est basée sur l'attribut {@link #color} si l'évènement n'a toujours pas été confirmé. Une fois qu'il a été confirmé, la couleur est grise dans
     * le menu principal (la couleur reste inchangée si on se trouve dans le fragment d'affichage en grand de la note)</p>
     * @param inFragment à mettre à vrai si on veut obtenir la couleur de la note dans le fragment
     * @return la couleur au format ci-dessus */
    public int getRGBColor(boolean inFragment){

        if(!inFragment && (isConfirmed() || isForgotten())){
//            return Color.rgb(158,158,158);
            return getLightRGBColor();
        }

        String[] rgb = this.color.split(";");
        int r, g, b;
        r = Integer.parseInt(rgb[0]);
        g = Integer.parseInt(rgb[1]);
        b = Integer.parseInt(rgb[2]);
        return Color.rgb(r,g,b);
    }
    /** Cette méthode permet d'obtenir une couleur écrite au format d'un entier grâce à la fonction {@link Color#rgb(int, int, int)}.
     * <p> À la différence de {@link Event#getRGBColor}, la couleur obtnue sera plus claire </p>
     * @return la couleur au format ci-dessus */
    public int getLightRGBColor(){
        String[] rgb = this.color.split(";");
        int r, g, b;
        r = (Integer.parseInt(rgb[0])+255)/2;
        g = (Integer.parseInt(rgb[1])+255)/2;
        b = (Integer.parseInt(rgb[2])+255)/2;
        return Color.rgb(r,g,b);
    }


    /** Permet d'obtenir la luminance perçue par l'oeil humain sous forme d'un double entre 0 et 255
     * <p> À la différence de {@link #getLuminance()}, qui renvoie la luminance associée à la couleur {@link #color},
     * cette méthode renvoie la luminance associée à la couleur renvoyée par {@link #getLightRGBColor()}</p>
     * <p> <i>Cette luminance est calulée à partir d'une formule adoptée par la CIE XYZ de 1931</i> </p>
     * @return La valeur de la luminance <pre>(luminance in [0, 255])</pre>*/
    public double getLightLuminance(){
        String[] color = getColor().split(";"); // color[0] = "R", color[1] = "G", color[2] = "B", avec R, G et B des entiers entre 0 et 255. on récupère ces entiers avec la méthode Integer.parseInt()
        return 0.299 * (Integer.parseInt(color[0])+255)/2 + 0.587 * (Integer.parseInt(color[1])+255)/2 + 0.114 * (Integer.parseInt(color[2])+255)/2;
    }

    /** Les évènements doivent être confirmés si nous approchons de leur date.
     * <p> Cette méthode utilise l'attribut {@link #range} qui doit être défini pour chaque event, et la méthode {@link Date#isInRange(int)}</p>
     * @return Vrai si l'évènement n'est pas confirmé et que sa date approche */
    public boolean mustBeConfirmed(){return !this.confirmed && !this.forgotten && this.date.isInRange(range);}

    @Override
    public int compareTo(Event o) {
        return this.date.compareTo(o.date);
    }
}