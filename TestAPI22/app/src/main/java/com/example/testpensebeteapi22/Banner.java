package com.example.testpensebeteapi22;

import android.graphics.Color;

/** <p>La classe Banner permet de stocker les informations qui devront être affichées dans le bandeau d'information de la journée. </p>
 *  <p> Elle est similaire à la classe {@link Event}, cependant à la différence de celle-ci, les informations ne sont pas horodatées, et aucune
 *      confirmation n'est demandée à l'aidé pour savoir si cette information a été prise en compte. Elle doit donc servir à stocker des
 *      informations moins importantes que la classe Event.</p>
 *  <p> Le but de la classe Banner est de :
 *  <ul>                                    <li> Stocker les caractéristique d'un bandeau d'information </li>
 *                                          <li> Permettre des accès simples et au bon format à ces caractéristiques </li>
 *  </ul></p>
 *
 * @author Méline, Pauline, Jérémy et Quentin
 * @version 1.1
 * */
public class Banner extends InformationElement{

    //region Attributs
    /** ID unique du bandeau */
    private int id_banner;
    /**  Description du bandeau */
    private String description;
    /** Information à propos du bandeau */
    private String informations;
    /** Limite en dessous de laquelle on considère qu'il faut crop la description (et ajouter '...')*/
    private static final int CROP_LIMIT = 50;
    //endregion

    /** Constructeur permettant une instanciation complète de tous les attributs de l'évènement
     * @param id ID de l'event
     * @param description Description du bandeau
     * @param type type de l'évènement, à associer à l'attibut {@link #type}
     * @param color couleur associée à l'évènement, à associer  l'attribut {@link #color}. Attention au format !
     * @param info information de l'évènement, à associer à l'attribut
     * @param d Date de l'évènement
     * @param iconId Id de l'icone de l'évènement */
    public Banner(int id, String description, String type, String color, String info, Date d, int iconId){
        this.id_banner = id;
        this.description = description;
        this.type = type;
        this.color = color;
        informations = info;
        date = d;
        this.iconId = iconId;
    }

    //region --- GETTERS AND SETTERS ---
    public int getId_banner() {return id_banner;}
    public void setId_banner(int id_banner) {this.id_banner = id_banner;}

    /** @return la description de l'objet Banner, tronquée ou non
     *
     * @param crop Si vrai tronquer la description,<p>sinon laisser la description par défaut</p> */
    public String getDescription(boolean crop) {
        int maxLength = CROP_LIMIT; // Longueur maximum d'un titre dans l'affichage du programme de la journée
        String cropDescription = crop && description.length()>maxLength ? description.substring(0, maxLength) + "..." : description;

        return cropDescription;
    }
    public void setDescription(String description) {this.description = description;}

    public String getInformations() {return informations;}
    public void setInformations(String informations) {this.informations = informations;}

    //endregion

    /** Teste si 2 bandeaux sont identiques. On considère que 2 bandeaux sont identiques si leur ID est égal
     * @param other l'autre Banner à comparer
     * @return vrai si les 2 bandeaux sont identiques, <p> faux sinon </p>*/
    @Override
    public boolean equals(InformationElement other){
        if(other instanceof Banner) {
            return this.id_banner == ((Banner) other).id_banner;
        }else{
            return false;
        }

    }

    /** Cette méthode permet d'obtenir une couleur écrite au format d'un entier grâce à la fonction {@link Color#rgb(int, int, int)}
     * @return la couleur au format ci-dessus */
    public int getRGBColor(){
        String[] rgb = this.color.split(";");
        int r, g, b;
        r = Integer.parseInt(rgb[0]);
        g = Integer.parseInt(rgb[1]);
        b = Integer.parseInt(rgb[2]);
        return Color.rgb(r,g,b);
    }

}
