package com.example.testpensebeteapi22;

/** <p>La classe abstraite InformationElement sert à stocker les informations des éléments informatifs rentrés par l'utilisateur </p>
 *
 * pour plus de précisions, aller à {@link Event} ou {@link Banner}, les éléments informatifs qui héritent de cette classe
 *
 * @author Méline, Pauline, Jérémy et Quentin
 * @version 1.1*/
public abstract class InformationElement {

    //region Attributs
    /** Couleur du post-it sur lequel afficher (souvent de paire avec le {@link #type})
     * <p> le format doit être : <pre>"R;G;B"</pre> avec R, G et B des entiers de 0 à 255 </p> */
    protected String color;
    /** Date et Heure de l'élément
     * @precision: Heure inutile pour les éléments {@link Banner}*/
    protected Date date;
    /** Id de l'icone associée à l'élément */
    protected int iconId;
    /** Type du bandeau (rendez-vous, médicaments, message, visite, autre...)
     * @exemple: <pre>"APT" pour "appointment"</pre>, <pre>"MDC" pour "medication"</pre> */
    protected String type;
    //endregion

    //region --- GETTERS AND SETTERS ---

    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}

    public Date getDate() {return date;}
    public void setDate(Date date) {this.date = date;}

    public int getIconId() {return iconId;}
    public void setIconId(int iconId) {this.iconId = iconId;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    //endregion

    /** Permet d'obtenir la luminance perçue par l'oeil humain sous forme d'un double entre 0 et 255. Cette luminance est associée à la couleur {@link #color}.
     * <p> <i>Cette luminance est calulée à partir d'une formule adoptée par la CIE XYZ de 1931</i> </p>
     * @return La valeur de la luminance <pre>(luminance in [0, 255])</pre>*/
    public double getLuminance(){
        String[] color = getColor().split(";"); // color[0] = "R", color[1] = "G", color[2] = "B", avec R, G et B des entiers entre 0 et 255. on récupère ces entiers avec la méthode Integer.parseInt()
        return 0.299 * Integer.parseInt(color[0]) + 0.587 * Integer.parseInt(color[1])+ 0.114 * Integer.parseInt(color[2]); // calcul de la luminance à partir d'une formule appuyée par la CIE XYZ de 1931
    }


    /**
     * @return <code> true </code> si other egal à this, <br> <code> false </code> sinon
     * @param other l'autre InformationElement à comparer */
    public abstract boolean equals(InformationElement other);

}
