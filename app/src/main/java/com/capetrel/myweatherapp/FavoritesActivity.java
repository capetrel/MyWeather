package com.capetrel.myweatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capetrel.myweatherapp.Adapters.FavoritesAdapter;
import com.capetrel.myweatherapp.Models.FavoriteItem;
import com.capetrel.myweatherapp.Tools.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private Handler mHandler;
    private Context mContext;

    private String line;
    private String response;

    private ListView mlistFavorites;
    private FavoritesAdapter mFavoritesAdapter;
    public ArrayList<FavoriteItem> mFavorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //TODO
        // Au lancement de l'activity
        // On récupère le nom des villes ou les id contenu dans chaque item
        // On intéroge l'API soit pour chaque ville soit pour un groupe (forecast)
        // Pour chaque ville on récupère les info météo
        // et on les transmet aux items respectifs

        // Test de la methode qui disallow les number dans une string
        String s1 = "paris";
        boolean b1 = Utils.cityIsString(s1);
        Log.d("lol", "---------> nom de la ville recherché: " + s1 +", "+b1);

        String s2 = "paris123";
        boolean b2 = Utils.cityIsString(s2);
        Log.d("lol", "---------> nom de la ville recherché: " + s2 +", "+b2);

        String s3 = "paris   ";
        boolean b3 = Utils.cityIsString(s3);
        Log.d("lol", "---------> nom de la ville recherché: " + s3 +", "+b3);

        mContext = this;
        mHandler = new Handler();

        mFavorites = Utils.getFavoriteFromPrefs(this);

        // Récupération du ListView présent dans notre IHM
        mlistFavorites = (ListView) findViewById(R.id.list_view_favorites);
        mFavoritesAdapter = new FavoritesAdapter(this, mFavorites);
        mlistFavorites.setAdapter(mFavoritesAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // recherche un lieu pour ajouter un favoris
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_favorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ouvre alertDialogue avec textview pour taper du texte et chercher une ville
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                builder.setTitle("Ajouter un favoris").setMessage("Saisissez le nom d'une ville");

                // Rajoute le (ou des) editText à l'alert dialog
                View content = getLayoutInflater().inflate(R.layout.alert_dialog, null);

                final EditText mSearchCity = (EditText) content.findViewById(R.id.edit_view_search_city);

                builder.setView(content);

                // On affiche les boutons de l'alert dialog
                builder.setPositiveButton("Chercher", new DialogInterface.OnClickListener() {

                    // Quand on clique sur "chercher" ...
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // On récupère l'entré dans une variable
                        String searchCity = (mSearchCity.getText().toString());

                        Log.d("lol", "---------> nom de la ville entrée " + searchCity);

                        String searchCityReg = searchCity.replaceAll("\\s","-");

                        Log.d("lol", "---------> nom de la ville regexée: " + searchCityReg);

                        // Au clic sur "chercher" il faut traiter les infos
                        final String cityNameFromAlertDialog = searchCityReg;

                        if (Utils.cityIsString(cityNameFromAlertDialog) == true) {

                            // On insère le nom de la ville dans l'URL de open weather pour récupérer les info météo.
                            new Thread() {
                                public void run() {

                                    try {

                                        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + cityNameFromAlertDialog + "=&lang=fr&units=metric&APPID=7cdad1875c5955549d7df72ef2bd6179");
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                                        response = "";
                                        line = "";
                                        while ((line = reader.readLine()) != null) {
                                            response += line;
                                        }
                                        reader.close();

                                        // ce bout de code est executé dans le thread principal
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                // On passe le Json à la méthode updateListView
                                                updateListView(response);

                                                Log.d("lol", "---------> info meteo ville recherchée :" +response);

                                            }
                                        });

                                    } catch (Exception e) {
                                        Toast.makeText(FavoritesActivity.this, "problème de réseau", Toast.LENGTH_SHORT).show();

                                        Log.d("lol", "---------> problème reseau");
                                    }
                                }
                            }.start();
                            // Sinon l'alert dialog se ferme et un pop up d'erreur apparait
                        } else {
                            Toast.makeText(FavoritesActivity.this, "La ville n'a pas été trouvée ou est invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        // ici la gestion du click sur un item de la liste permet de se rendre sur maps Activitys
        mlistFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewName, int position, long id) {
                // Récupérer les coordonnées latitude et longitude
                double latiFromFavorites = mFavorites.get(position).mLatitude;
                double longiFromFavorites = mFavorites.get(position).mLongitude;
                String cityFromFavorite = mFavorites.get(position).mCity;

                Intent intentMap = new Intent(FavoritesActivity.this, MapsActivity.class);
                intentMap.putExtra("latitude", latiFromFavorites);
                intentMap.putExtra("longitude", longiFromFavorites);
                intentMap.putExtra("city", cityFromFavorite);
                startActivity(intentMap);
                Log.d("lol", "---------> coordonnées vers Gmap depuis fav : " + latiFromFavorites + ", " + longiFromFavorites + ", " + cityFromFavorite);
            }
        });


        // Le clic long permet de supprimer un favoris
        mlistFavorites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFavorites(position);
                return true;
            }
        });


    }

    // Créer un méthode pour supprimer les données dans le shared preference.
    private void deleteFavorites(final int position) {

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Supprimer un favoris");
        builder.setMessage("Voulez vous vraiment supprimer ?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mFavorites.remove(position);
                mFavoritesAdapter.notifyDataSetChanged();
                Utils.writeFavouritePrefs(mContext, mFavorites);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.create();
        builder.show();
    }

    // Pour afficher le menu en haut
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    // Pour gérer le clic sur le ou les icones du menu du haut
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_back:
                finish();
                Log.d( "lol", "---------> ferme l'activity Favorite : ");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateListView(String dataForFav) {

        try {

            FavoriteItem favorites = new FavoriteItem(dataForFav);

            // Transmettre les données aux textview de favoris adapter
            mFavorites.add(favorites);
            mFavoritesAdapter.notifyDataSetChanged();

            // sauvegarde le favoris dans le share preference
            Utils.writeFavouritePrefs(mContext, mFavorites);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FavoritesActivity.this, "Il y a eu une erreur (updateListView), essayez à nouveau", Toast.LENGTH_SHORT).show();
        }

    }


}
