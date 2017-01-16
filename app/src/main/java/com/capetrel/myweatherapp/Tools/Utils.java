package com.capetrel.myweatherapp.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.capetrel.myweatherapp.Models.FavoriteItem;
import com.capetrel.myweatherapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by capetrel on 28/07/2016.
 */
public class Utils {

    public static final String PREF_FILE_NAME = "pref_file_name";
    public static final String KEY_PREFS_FAVORITE = "key_prefs_favourite";

    // regex unique pour les entiers
    private static final String nonIntPattern = "^[^0-9]+$";

    /**
     * Méthode qui initialise l'icon blanc présent dans la MainActivity
     */
    public static int setWeatherIcon(int actualId, long sunrise, long sunset) {

        int id = actualId / 100;
        int icon = R.drawable.weather_sunny_white;

        if (actualId == 800) {
            long currentTime = new Date().getTime() / 1000;

            if (currentTime >= sunrise && currentTime < sunset) {
                icon = R.drawable.weather_sunny_white;
            } else {
                icon = R.drawable.weather_clear_night_white;
            }
        } else {
            switch (id) {
                case 2:
                    icon = R.drawable.weather_thunder_white;
                    break;
                case 3:
                    icon = R.drawable.weather_drizzle_white;
                    break;
                case 7:
                    icon = R.drawable.weather_foggy_white;
                    break;
                case 8:
                    icon = R.drawable.weather_cloudy_white;
                    break;
                case 6:
                    icon = R.drawable.weather_snowy_white;
                    break;
                case 5:
                    icon = R.drawable.weather_rainy_white;
                    break;
            }
        }

        return icon;
    }

    /**
     * Méthode qui initialise l'icon gris présent dans le forecast et dans la liste des favoris.
     */
    public static int setWeatherIcon(int actualId) {

        int id = actualId / 100;
        int icon = R.drawable.weather_sunny_grey;

        if (actualId != 800) {
            switch (id) {
                case 2:
                    icon = R.drawable.weather_thunder_grey;
                    break;
                case 3:
                    icon = R.drawable.weather_drizzle_grey;
                    break;
                case 7:
                    icon = R.drawable.weather_foggy_grey;
                    break;
                case 8:
                    icon = R.drawable.weather_cloudy_grey;
                    break;
                case 6:
                    icon = R.drawable.weather_snowy_grey;
                    break;
                case 5:
                    icon = R.drawable.weather_rainy_grey;
                    break;
            }
        }

        return icon;
    }

    public static void writeFavouritePrefs(Context context, ArrayList<FavoriteItem> dataForFavs) {

        // Cette méthode permet de sauvegarder la liste des favoris dans les préférences.

        SharedPreferences preferencesList = context.getSharedPreferences(Utils.PREF_FILE_NAME, 0);
        SharedPreferences.Editor editor = preferencesList.edit();

        // Initialisation d'un tableau JSON
        JSONArray jsonArray = new JSONArray();

        // Ajout dans le tableau de chaque favoris au format JSON
        for (int i = 0; i < dataForFavs.size(); i++) {
            jsonArray.put(dataForFavs.get(i).strJson);
        }

        // Ecriture dans les préférences en écrasant l'existant.
        editor.putString(Utils.KEY_PREFS_FAVORITE, jsonArray.toString());
        editor.commit();

        // map pour recupérer en log le contenu du shared preferences
        //Map<String, ?> keys =preferencesList.getAll();
        //for (Map.Entry<String, ?> entry : keys.entrySet()) {
        //    Log.d("lol map values", entry.getKey() + ": " + entry.getValue().toString());
        //}
    }

    public static ArrayList<FavoriteItem> getFavoriteFromPrefs(Context context) {

        // 1° - Initialisation de la liste vide des favoris
        ArrayList<FavoriteItem> arrayListFavorites = new ArrayList<>();

        // 2° - Récupération des favoris (s'ils existent) via les preferences.
        SharedPreferences preferences = context.getSharedPreferences(Utils.PREF_FILE_NAME, 0);
        String strFavourite = preferences.getString(Utils.KEY_PREFS_FAVORITE, null);

        Log.d("lol", "-----> " + strFavourite);

        // 3° - Ajout de chaque favoris si la liste est non vide
        if (strFavourite != null) {
            try {
                JSONArray jsonArray = new JSONArray(strFavourite);
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayListFavorites.add(new FavoriteItem(jsonArray.getString(i)));
                }
            } catch (Exception e) {
            }
        }

        return arrayListFavorites;
    }

    // Pour tester la chaine de caractère
    public static boolean cityIsString(String isString) {
        // Pour initialiser l'API regex java.
        Pattern pattern = Pattern.compile(nonIntPattern, Pattern.CASE_INSENSITIVE);
        Matcher m1 = pattern.matcher(isString);
        return m1.matches();
    }

    // Pour formater la date (uniquement le jour de la semaine dans ce cas).
    public static String setDay(long ts) {
        return (new SimpleDateFormat("EE", Locale.FRANCE)).format(new Date(ts * 1000));
    }

}
