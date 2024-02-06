package com.example.testpensebeteapi22;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


/** <p> La classe DimensionsUtil est une classe utilitaire utilisée pour obtenir facilement les dimensions de l'écran sur lequel
 * tourne l'application (notamment grâce à la méthode {@link #getScreenInches})</p>
 *
 * <p> code obtenu en partie par chatGPT </p>
 * @author Méline, Jérémy, Pauline et Quentin
 * @version 1.1*/
public class DimensionsUtil {

    /** fonction utilisée pour obtenir la largeur de la fenêtre dans laquelle tourne l'application
     * @since 1.1
     * @param context Context de l'application, souvent l'activité dans laquel on se trouve
     * @return largeur de la fenêtre en pixel, -1 si il y a une erreur */
    public static int getScreenWidth(Context context) {
        // Obtenir le gestionnaire de fenêtres
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            // Obtenir l'affichage par défaut du gestionnaire de fenêtres
            Display display = windowManager.getDefaultDisplay();

            // Créer un objet DisplayMetrics pour contenir les informations sur l'écran
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // Obtenir les métriques de l'écran
            display.getMetrics(displayMetrics);

            // Obtenir la largeur et la hauteur de l'écran en pixels
            int screenWidth = displayMetrics.widthPixels;


            return screenWidth;
        }

        return -1;
    }

    /** fonction utilisée pour obtenir la hauteur de la fenêtre dans laquelle tourne l'application
     * @since 1.1
     * @param context Context de l'application, souvent l'activité dans laquel on se trouve
     * @return hauteur de la fenêtre en pixel, -1 si il y a une erreur */
    public static int getScreenHeight(Context context) {
        // Obtenir le gestionnaire de fenêtres
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            // Obtenir l'affichage par défaut du gestionnaire de fenêtres
            Display display = windowManager.getDefaultDisplay();

            // Créer un objet DisplayMetrics pour contenir les informations sur l'écran
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // Obtenir les métriques de l'écran
            display.getMetrics(displayMetrics);

            // Obtenir la largeur et la hauteur de l'écran en pixels
            int screenHeight = displayMetrics.heightPixels;


            return screenHeight;
        }

        return -1;
    }

    /** méthode pour retourner la longueur de la  diagonale de l'écran en pouces
     * @return la longueur de la diagonale de l'écran en pouces, -1 si il y a une erreur
     * @param context le contexte de l'application (souvent l'activité sur laquelle on se trouve)
     * @since 1.1*/
    public static double getScreenInches(Context context) {
        // Obtenir le gestionnaire de fenêtres
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            // Obtenir l'affichage par défaut du gestionnaire de fenêtres
            Display display = windowManager.getDefaultDisplay();

            // Créer un objet DisplayMetrics pour contenir les informations sur l'écran
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // Obtenir les métriques de l'écran
            display.getMetrics(displayMetrics);

            // Obtenir la densité d'affichage
            float xDpi = displayMetrics.xdpi;
            float yDpi = displayMetrics.ydpi;

            // Calculer la taille de l'écran en pouces
            double screenWidthInches = displayMetrics.widthPixels / xDpi;
            double screenHeightInches = displayMetrics.heightPixels / yDpi;

            return Math.sqrt(Math.pow(screenWidthInches, 2) + Math.pow(screenHeightInches, 2));
        }

        return -1;
    }
}
