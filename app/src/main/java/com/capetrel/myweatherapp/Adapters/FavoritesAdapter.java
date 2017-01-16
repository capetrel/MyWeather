package com.capetrel.myweatherapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capetrel.myweatherapp.Models.FavoriteItem;
import com.capetrel.myweatherapp.R;
import com.capetrel.myweatherapp.Tools.Utils;

import java.util.ArrayList;

/**
 * Created by capetrel on 22/08/2016.
 */
public class FavoritesAdapter extends BaseAdapter {

    public ArrayList<FavoriteItem> mFavorites;
    private LayoutInflater inflater;

    public FavoritesAdapter(Context context, ArrayList<FavoriteItem> favorites) {
        inflater = LayoutInflater.from(context);
        mFavorites = favorites;
    }

    private class ViewHolder {
        private TextView mtextViewFavCityName;
        private TextView mTextViewFavWeather;
        private ImageView mImageViewFavIcon;
        private TextView mTextViewFavTemp;
    }

    @Override
    public int getCount() {
        //Taille de la "bibliothèque"
        return mFavorites.size();
    }

    @Override
    public long getItemId(int position) {
        // La position
        return position;
    }

    @Override
    public Object getItem(int position) {
        // L'item correspondant à cette position
        return mFavorites.get(position);
    }

    // La méthode getView est appelé par le système à chaque fois qu’il faut afficher un item du ListView.
    // C’est dans cette méthode que vous allez instancier et modifier les éléments de l’item qui va être affiché.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Toujours prévoir un log pour voir le fonctionnement de la méthode.
        Log.d(" lol", "position------------>" + position);

        ViewHolder holder;

        //si holder est vide, première fois qu'on l'appel
        if (convertView == null) {
            // Nouvel objet avec les composants de l’item
            holder = new ViewHolder();

            // La nouvelle vue est initialisé avec le layout d’un item
            convertView = inflater.inflate(R.layout.item_favoris, null);

            // Initialisation des attributs de notre objet ViewHolder
            holder.mtextViewFavCityName = (TextView) convertView.findViewById(R.id.text_view_fav_city_name);
            holder.mTextViewFavWeather = (TextView) convertView.findViewById(R.id.text_view_fav_weather_txt);
            holder.mImageViewFavIcon = (ImageView) convertView.findViewById(R.id.image_view_fav_weather_icon);
            holder.mTextViewFavTemp = (TextView) convertView.findViewById(R.id.text_view_fav_temperature);

            // On lie l’objet ViewHolder à la vue pour le sauvegarder grace à setTag
            convertView.setTag(holder);

        } else { // sinon holder à déjà été appellé.

            // On renvoie les valeurs stocké dans le setTag
            holder = (ViewHolder) convertView.getTag();
        }

        FavoriteItem favorites = mFavorites.get(position);

        holder.mtextViewFavCityName.setText(favorites.mCity);
        holder.mTextViewFavWeather.setText(favorites.mWeather);
        holder.mImageViewFavIcon.setImageResource(Utils.setWeatherIcon(favorites.mIntIcon));
        holder.mTextViewFavTemp.setText(""+favorites.mTemperature);

        return convertView;
    }
}
