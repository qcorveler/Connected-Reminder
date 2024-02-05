package com.example.testpensebeteapi22;

import org.threeten.bp.LocalDateTime;

/** <p> La classe Date permet d'utiliser une date au format LocalDateTime de façon plus facile que cette dernière classe. </p>
 *
 * <p> Le but de la classe Date est de : </p>
 * <ul>                                  <li> afficher directement à partir d'un objet date la date, l'heure et le contexte de la date </li>
 *                                       <li> comparer 2 dates pour les classer dans l'ordre chronologique </li>
 *                                       <li> comparer 2 dates pour obtenir le nombre de jours de différence </li>
 *                                       <li> comparer 2 dates pour savoir si elles sont situées le même jour </li>
 * </ul>
 *
 * @author Méline, Pauline, Jérémy et Quentin
 * @version 1.4
 * */
public class Date {

    //region Attributs
    private LocalDateTime date;

    /** tableau contenant les jours de la semaine dans l'ordre en français.
     * @exemple  <pre> jours[0] = "Lundi", jours[6] = "Dimanche" </pre>
     * @warning Tableau de taille 7 : l'indice maximal est 6 ! */
    private static final String[] jours = {"Lundi" , "Mardi" , "Mercredi" , "Jeudi" , "Vendredi" , "Samedi" , "Dimanche"};
    /** tableau contenant les mois de l'année dans l'ordre en français.
     * @exemple  <pre> jours[0] = "Janvier", jours[11] = "Décembre" </pre>
     * @warning Tableau de taille 12 : l'indice maximal est 11 ! */
    private static final String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
    //endregion

    /** La date par défaut est la date actuelle */
    public Date(){date = LocalDateTime.now();}

    /** Instanciation de la classe date à partir d'une date de la classe {@link LocalDateTime}
     * @param date date à laquelle va être instancié l'objet {@link Date} */
    public Date(LocalDateTime date){this.date = date;}

    public LocalDateTime getDate(){return this.date;}

    /** @return l'heure au format "hh:mm" ou "h:mm"
     * @exemple "12:12", "9:09" */
    public String getHourFormat(){
        String minutes = "" + this.date.getMinute();
        if(this.date.getMinute()<10){
            minutes = "0"+this.date.getMinute();
        }
        return this.date.getHour() + ":"  + minutes;
    }

    /** @return la date au format "dd/mm/yyyy"
     * @exemple  "01/01/2000"*/
    public String getDateFormat1(){
        String res = "";
        res += date.getDayOfMonth();
        if(date.getDayOfMonth()<10){
            res = "0" + res;
        }
        if(date.getMonthValue()<10){
            res += "/0" + date.getMonthValue();
        }else {
            res += "/" + date.getMonthValue();
        }
        res += "/" + date.getYear();
        return res;
    }

    /** @return la date au format "Jour n° Mois Année"
     * @exemple  "Samedi 1er Janvier 2000" */
    public String getDateFormat2(){
        return jours[date.getDayOfWeek().getValue() - 1] +
                " " +
                date.getDayOfMonth() +
                " " +
                mois[date.getMonthValue() - 1] +
                " " +
                date.getYear();
    }

    /** réinitialise la date en la passant à la date actuelle sans instancier de nouvelle date */
    public void refreshDate(){
        date = LocalDateTime.now();
    }

    /** rénitialise l'heure en la passant sur l'heure acutelle sans modifier la date */
    public void refreshHour(){
        this.date = this.date.withHour(LocalDateTime.now().getHour());
        this.date = this.date.withMinute(LocalDateTime.now().getMinute());
    }

    /** Teste si la date passée en paramètre est identique à la date qui appelle cette méthode (sans prendre en compte l'heure)
     * @return <p> <code> true </code> si les 2 dates sont identiques </p>
     *         <p> <code> false </code> sinon </p>
     * @param date  date à tester avec la date d'appel
     * @exemple : <code> date_01_01_2000.sameDate(date_01_01_2000) = true </code>,<p></p> <code> date_01_01_2000.sameDate(date_02_01_2000) = false </code> */
    public boolean sameDate(Date date){
        return  (date.date.getYear()==this.date.getYear()) &&
                (date.date.getDayOfYear()==this.date.getDayOfYear());
    }

    /** <p> Compare la date passée en paramètre avec la date qui appelle la fonction </p>
     * <p> La comparaison est basée sur <b>l'ordre chronologique</b>. Elle utilise la méthode {@link LocalDateTime#compareTo}</p>
     * @param other l'autre date à comparer (non null)
     * @return <p> la valeur du comparateur : </p>
     *          <p> positive si this > other </p>
     *          <p> nulle si this = other </p>
     *          <p> négative sinon </p>
     * @exemple: <code> date_01_01_2000.compareTo(date_now) </code> < 0 </code>*/
    public int compareTo(Date other){return this.date.compareTo(other.date);}

    private int minuteDifference(Date other){return 24*60*dayDifference(other) + 60*(other.date.getHour()-this.date.getHour()) + other.date.getMinute() - this.date.getMinute();}
    private int hourDifference(Date other){return minuteDifference(other)/60;}

    /**@param other l'autre date à comparer (non null)
     * @return le nombre de jours de différence entre 2 dates : <p> Positif si <code> this.{@link Date#compareTo}(other) </code> > 0,</p> <p>négatif sinon</p> */
    public int dayDifference(Date other){return 365*(other.date.getYear()-this.date.getYear()) + (other.date.getDayOfYear() - this.date.getDayOfYear());}

    /** Cette méthode utilise la méthode {@link Date#dayDifference(Date)} et considère qu'un mois est constitué de 30 jours
     * @param other l'autre date à comparer (non null)
     * @return le nombre de mois de différence entre 2 dates : <p> Positif si <code> this.{@link Date#compareTo}(other) </code> > 0,</p> <p>négatif sinon</p> */
    private int monthDifference(Date other){return this.dayDifference(other)/30;}

    /** Cette méthode utilise la méthode {@link Date#dayDifference(Date)} et considère qu'un an est constitué de 365 jours
     * @param other l'autre date à comparer (non null)
     * @return le nombre d'années de différence entre 2 dates : <p> Positif si <code> this.{@link Date#compareTo}(other) </code> > 0,</p> <p>négatif sinon</p> */
    private int yearDifference(Date other){return this.dayDifference(other)/365;}

    /** Le contexte de la date est utilisé pour situer la date par rapport à aujourd'hui. Le format est le suivant :
     * <pre>"Dans/Il y a x jour(s)/mois/an(s) (jour_de_la_semaine)"</pre> <p> ou <pre> "Hier/Aujourd'hui/Demain (jour_de_la_semaine)</pre></p>
     *
     * Pour plus de précision regarder la méthode {@link Date#dayDifference}
     * 
     * @return le contexte de la date en français sous la forme précisée ci-dessus
     * @exemple: <pre> <code> tomorrow.dateContext() </code> = "Demain (Mercredi)" </pre> si on est mardi aujourd'hui*/
    public String dateContext(){
        Date today = new Date();
        String context_adverb;

        int day_difference = today.dayDifference(this);

        if(day_difference > 0) {
            context_adverb = "Dans ";
        }else{
            context_adverb = "Il y a ";
        }


        if(Math.abs(day_difference) > 365){
            switch (Math.abs(today.yearDifference(this))){
                case 0 : break;
                case 1 : return context_adverb + "1 an (" + jours[date.getDayOfWeek().getValue() - 1] + ")";
                default : return context_adverb + Math.abs(today.yearDifference(this)) + " ans (" + jours[date.getDayOfWeek().getValue()-1] + ")";
            }
        }

        if(Math.abs(day_difference)> 30){
            switch (Math.abs(today.monthDifference(this))){
                case 0 : break;
                default : return context_adverb + Math.abs(today.monthDifference(this)) + " mois ("+ jours[date.getDayOfWeek().getValue() - 1] + ")";
            }
        }

        switch (day_difference){
            case -1 : return "Hier (" + jours[date.getDayOfWeek().getValue() - 1] + ")";
            case 0 : return "Aujourd'hui (" + jours[date.getDayOfWeek().getValue() - 1] + ")";
            case 1 : return "Demain (" + jours[date.getDayOfWeek().getValue() - 1] + ")";
            default : return context_adverb + Math.abs(day_difference) + " jours (" + jours[date.getDayOfWeek().getValue()-1] + ")";
        }

    }

    /** Le contexte de l'heure est utilisé pour situer l'heure par rapport à maintenant. Le format est le suivant :
     * <pre>"Dans/Il y a x minute(s)/heure(s)"</pre>
     *
     * Pour plus de précision regarder la méthode {@link Date#minuteDifference}
     *
     * @return le contexte de l'heure en français sous la forme précisée ci-dessus
     * @exemple: <pre> <code> nowPlus0neHour.hourContext() </code> = "Dans 1 heure" </pre> */
    public String hourContext(){
        Date today = new Date();
        String context_adverb;

        int minute_difference = today.minuteDifference(this);

        if(Math.abs(minute_difference)> 60*24){
            return "";
        }

        if (minute_difference > 0){
            context_adverb = "Dans ";
        }else{
            context_adverb = "Il y a ";
        }

        if(Math.abs(minute_difference) > 60){
            switch (Math.abs(today.hourDifference(this))){
                case 1 : return context_adverb + "1 heure";
                default : return context_adverb + Math.abs(today.hourDifference(this)) +" heures";
            }
        }else{
            switch (Math.abs(minute_difference)){
                case 0 : return "C'est l'heure !";
                case 1 : return context_adverb + "1 minute";
                default : return context_adverb + Math.abs(minute_difference) + " minutes";
            }
        }
    }

    /** ajoute 1 jour à la date this 
     * @utilise {@link LocalDateTime#plusDays}*/
    public void nextDay() { this.date = this.date.plusDays(1); }

    /** retire 1 jour à la date this 
     * @utilise {@link LocalDateTime#minusDays}*/
    public void previousDay() { this.date = this.date.minusDays(1); }

    /** permet de savoir si la date d'appel est dans moins de minutes que la valeur passée en paramètre
     * @param minutes temps en minutes à tester
     * @return vrai si la date d'appel est dans moins de minutes que le paramètre <code>minutes</code>.
     *          <p> faux sinon</p>*/
    public boolean isInRange(int minutes){
        Date today = new Date();
        return today.date.plusMinutes(minutes).compareTo(this.date) > 0;
    }
}
