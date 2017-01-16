package com.capetrel.myweatherapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capetrel.myweatherapp.Models.FavoriteItem;
import com.capetrel.myweatherapp.Tools.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntroActivity extends AppCompatActivity {

    private Context mContext;
    private Handler mHandler;

    private String cityFromIp;

    private TextView mTextViewMyCity;
    private TextView mTextViewWeather;
    private ImageView mImageViewIcone;
    private TextView mTextViewTemperature;

    private TextView mTextViewDay;
    private ImageView mImageViewMiniIcone;
    private TextView mTextViewTempMax;
    private TextView mTextViewTempMin;

    private Double latIntro;
    private Double longIntro;

    // methode pour les actions de l'action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    // Click sur item de l'ActionBar envoie sur mapsActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                Intent intentMap = new Intent(IntroActivity.this, MapsActivity.class);
                intentMap.putExtra("latitude", latIntro);
                intentMap.putExtra("longitude", longIntro);
                intentMap.putExtra("city", cityFromIp);
                startActivity(intentMap);
                Log.d("lol", "---------> coordonnées vers carte google : " + latIntro + ", " + longIntro);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mContext = this;
        mHandler = new Handler();

        // Cliquer sur le floating button envoie sur favorite activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_favorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, FavoritesActivity.class);
                startActivity(intent);
                Log.d("lol", "---------> onclick vers page favoris");
            }
        });

        //TODO
        // Faire un loader

        // Au lancement de l'application on vérifie la connection et le réseau
        if (checkInternetConnection()) {

            // Si la connection est ok  actualise la ville -> V1 avec IP-API
            threadIpForCity();
        }

        // 2
        // TODO
        // Ensuite L'application actualise la ville -> V2 avec geoloc telephone

        // 3
        // TODO
        // Lappli affiche les prévision pour les 4 jours suivants
        // Gérer un date format "EE" avec le timestamp du Json (Ecart de 1000 à ne pas oublier)
    }

    private boolean checkInternetConnection() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            Toast.makeText(IntroActivity.this, "L'appli est connecté à internet", Toast.LENGTH_SHORT).show();

            Log.d("lol", "---------> accès internet ok");

            return true;
        } else {
            // display error
            Toast.makeText(IntroActivity.this, "Il n'y a pas de réseau", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void threadIpForCity() {
        Log.d("lol", "---------> threadIpForCity START");
        new Thread() {
            public void run() {
                // Do stuff

                try {
                    URL url = new URL("http://ip-api.com/json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String response = "";
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }
                    reader.close();

                    final JSONObject data = new JSONObject(response);

                    Log.d("lol", "---------> data IP: " + data.toString());

                    // ce bout de code est executé dans le thread principal
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateCity(data);
                        }
                    });

                } catch (Exception e) {
                    Log.d("lol", "---------> problème reseau");
                }
            }
        }.start();
    }

    public void updateCity(JSONObject dataFromThread) {
        // 4
        try {

            String city = dataFromThread.getString("city");

            latIntro = dataFromThread.getDouble("lat");
            longIntro = dataFromThread.getDouble("lon");

            Log.d("lol", "---------> info ville utilisé : " + city);

            // On mémorise la ville dans une variable global pour que toutes les méthode y est accès
            cityFromIp = city;

            mTextViewMyCity = (TextView) findViewById(R.id.text_view_city);
            mTextViewMyCity.setText(city);


            // Avec le nom de la ville l'appli met à jours les info météos
            getMeteoInformation(cityFromIp);

            // Avec le nom de la ville l'appli met à jours les prévisions météos
            getMeteoInformationPrev(cityFromIp);


        } catch (Exception e) {
            Toast.makeText(this, "Pour la ville, le fichier json ou ce dernier est mal formaté", Toast.LENGTH_SHORT).show();
        }

    }


    // méthode à laquelle on donne la ville et renvoie les info de l'API météo en objet json et met a jours les infos
    private void getMeteoInformation(final String city) {

        Log.d("lol", "---------> getMeteoInformation START");

        new Thread() {
            public void run() {
                // Récupération des données via le web service

                try {

                    String s = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "=&lang=fr&units=metric&APPID=7cdad1875c5955549d7df72ef2bd6179";

                    Log.d("lol", "---------> Mon url est  : " + s);
                    URL url = new URL(s);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String response = "";
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }
                    reader.close();

                    final String data = response;

                    Log.d("lol", "---------> data meteo : " + response);

                    // ce bout de code est executé dans le thread principal
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateMeteo(data);
                        }
                    });

                } catch (Exception e) {
                    Log.d("lol", "---------> problème reseau");
                }
            }
        }.start();
    }

    // Méthode qui met à jours les textfield du layout du haut avec le temps, la temperature, l'icone en fonction du jours ou de la nuit
    private void updateMeteo(String dataMeteo) {
        try {

            FavoriteItem currentCity = new FavoriteItem(dataMeteo);

            mTextViewWeather = (TextView) findViewById(R.id.text_view_weather);
            mTextViewWeather.setText(currentCity.mWeather);

            mTextViewTemperature = (TextView) findViewById(R.id.text_view_temperature);
            mTextViewTemperature.setText("" + currentCity.mTemperature);

            mImageViewIcone = (ImageView) findViewById(R.id.image_view_icone_meteo);
            mImageViewIcone.setImageResource(Utils.setWeatherIcon(currentCity.mIntIcon, currentCity.mSunrise, currentCity.mSunset));

        } catch (Exception e) {
            Toast.makeText(this, "Pour la météo, le fichier json est mal formaté", Toast.LENGTH_SHORT).show();
        }
    }

    // méthode à laquelle on donne la ville et renvoie les info de l'API météo en objet json et met a jours les infos pour les prévision
    private void getMeteoInformationPrev(final String city) {

        new Thread() {
            public void run() {
                // Récupération des données via le web service

                try {
                    // url prévision :
                    // http://api.openweathermap.org/data/2.5/forecast/daily?q=" + city + "=&lang=fr&units=metric&APPID=7cdad1875c5955549d7df72ef2bd6179&cnt=5

                    // url meteo du jours
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "=&lang=fr&units=metric&APPID=7cdad1875c5955549d7df72ef2bd6179");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String response = "";
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }
                    reader.close();

                    final JSONObject dataPrev = new JSONObject(response);

                    Log.d("lol", "---------> fichier json meteo prevision: " + dataPrev.toString());

                    // ce bout de code est executé dans le thread principal
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateMeteoPrev(dataPrev);
                        }
                    });

                } catch (Exception e) {
                    Log.d("lol", "---------> problème reseau");
                }
            }
        }.start();
    }

    // Méthode qui met à jours les textfields de la liste des prévision pour 6 jours avec le jours, l'icone, temp max, temps min
    private void updateMeteoPrev(JSONObject dataMeteoPrev) {
        try {

            // Ici on gère le jour de la semaine V1
            int day = dataMeteoPrev.getInt("dt");

            // On récupère l'icone
            JSONArray iconArray = dataMeteoPrev.getJSONArray("weather");
            JSONObject iconObject = iconArray.getJSONObject(0);
            int iconId = iconObject.getInt("id");

            // On récupère la temp max
            JSONObject keyMainMax = dataMeteoPrev.getJSONObject("main");
            Double tempMax = keyMainMax.getDouble("temp_max");

            // On récupère la temp min
            JSONObject keyMainMin = dataMeteoPrev.getJSONObject("main");
            Double tempMin = keyMainMin.getDouble("temp_min");

            Log.d("lol", "---------> infos prevision utilisé : " + tempMax + " ," + tempMin + " ," + iconId);

            // Jours numéros un
            mTextViewDay = (TextView) findViewById(R.id.text_view_day_one);
            mTextViewDay.setText(Utils.setDay(day));

            mImageViewMiniIcone = (ImageView) findViewById(R.id.img_view_day_one);
            mImageViewMiniIcone.setImageResource(Utils.setWeatherIcon(iconId));

            mTextViewTempMax = (TextView) findViewById(R.id.text_view_temp_max_day_one);
            mTextViewTempMax.setText("" + tempMax);

            mTextViewTempMin = (TextView) findViewById(R.id.text_view_temp_min_day_one);
            mTextViewTempMin.setText("" + tempMin);

            /*
            // Jours numéro deux
            mTextViewDay = (TextView) findViewById(R.id.text_view_day_two);
            mTextViewDay.setText(Utils.setDay(day));

            mImageViewMiniIcone = (ImageView) findViewById(R.id.img_view_day_two);
            mImageViewMiniIcone.setImageResource(Utils.setWeatherIcon(iconId));

            mTextViewTempMax = (TextView) findViewById(R.id.text_view_temp_max_day_two);
            mTextViewTempMax.setText("" + tempMax);

            mTextViewTempMin = (TextView) findViewById(R.id.text_view_temp_min_day_two);
            mTextViewTempMin.setText("" + tempMin);
            */

        } catch (Exception e) {
            Toast.makeText(this, "Pour les prévisions, le fichier json est mal formaté", Toast.LENGTH_SHORT).show();
        }
    }

}
