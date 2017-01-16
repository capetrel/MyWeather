package com.capetrel.myweatherapp.Models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by capetrel on 22/08/2016.
 */
public class FavoriteItem {

    private static final String CITYNAME = "name";
    private static final String WEATHER = "weather";
    private static final String IDCITY = "id";
    private static final String TIMESTAMP = "dt";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String INTICONE = "id";
    private static final String SUNRISE = "sunrise";
    private static final String SUNSET = "sunset";
    private static final String TEMPERATURE = "temp";
    private static final String TEMP_MIN = "temp_min";
    private static final String TEMP_MAX = "temp_max";

    public String mCity;
    public String mWeather;
    public int mIdCity;
    public int mTimeStamp;
    public double mLatitude;
    public double mLongitude;
    public int mIntIcon;
    public int mSunrise;
    public int mSunset;
    public double mTemperature;
    public double mTempMin;
    public double mTempMax;

    public String strJson;

    public FavoriteItem(String strJson) throws JSONException {

        this.strJson = strJson;

        JSONObject json = new JSONObject(strJson);

        if (json.getInt("cod") != 200) {
            throw new JSONException("status fail");
        }

        // On récupère le nom de la ville
        mCity = json.getString("name");

        // Il faut l'ID
        mIdCity = json.getInt("id");

        // Il faut le timeStamp
        mTimeStamp = json.getInt("dt");

        // On récupère le temps : couvert, ensolleillé, etc ...
        JSONArray weatherArray = json.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        mWeather = weatherObject.getString("description");

        // Récupérer la latitude
        JSONObject keyLat = json.getJSONObject("coord");
        mLatitude = keyLat.getDouble("lat");

        // Pareil pour la longitude
        JSONObject keyLon = json.getJSONObject("coord");
        mLongitude = keyLon.getDouble("lon");

        // récupèrer l'icone
        JSONArray iconArray = json.getJSONArray("weather");
        JSONObject iconObject = iconArray.getJSONObject(0);
        mIntIcon = iconObject.getInt("id");

        // récupèrer le lever de soleil
        JSONObject keySunrise = json.getJSONObject("sys");
        mSunrise = keySunrise.getInt("sunrise");

        // récupèrer le coucher de soleil
        JSONObject keySunset = json.getJSONObject("sys");
        mSunset = keySunrise.getInt("sunset");

        // On récupère la température actuelle
        JSONObject keyMain = json.getJSONObject("main");
        mTemperature = keyMain.getDouble("temp");

        // On récupère la température minimum
        JSONObject keyMainTmin = json.getJSONObject("main");
        mTempMin = keyMainTmin.getDouble("temp_min");

        // On récupère la température maximum
        JSONObject keyMainTmax = json.getJSONObject("main");
        mTempMax = keyMainTmax.getDouble("temp_max");

    }
}
