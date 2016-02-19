package com.natarajan.movies.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.natarajan.movies.DO.MovieDetailDO;
import com.natarajan.movies.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by Natarajan on 02/12/16.
 */

public class MovieAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MovieDetailDO> movieList;

    public MovieAdapter(Context context, ArrayList<MovieDetailDO> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    //declare an inner class for ViewHolder

    static class ViewHolder {
        ImageView imageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Changed to ViewHolder and Picasso error load as per feedback received
        ViewHolder holder = new ViewHolder();

        //Inflate view if it is null
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.movie_poster);
            MovieDetailDO movie = movieList.get(position);
            convertView.setTag(holder);

            Picasso.with(context)
                    .load(movie.getPoster_path())
                    //.placeholder(R.drawable.def_pos)
                    .error(R.drawable.def_pos)
                    .into(holder.imageView);

        } else {
            holder.imageView = (ImageView) convertView.findViewById(R.id.movie_poster);
            MovieDetailDO movie = movieList.get(position);
            convertView.setTag(holder);

            Picasso.with(context)
                    .load(movie.getPoster_path())
                    //.placeholder(R.drawable.def_pos)
                    .error(R.drawable.def_pos)
                    .into(holder.imageView);
        }

        return convertView;
    }

}
