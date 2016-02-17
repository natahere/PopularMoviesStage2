package com.natarajan.movies.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.natarajan.movies.DO.MovieDetailDO;

import com.natarajan.movies.R;


import org.parceler.Parcels;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used on handset devices.
 */

public class MovieDetailActivity extends AppCompatActivity {

    MovieDetailFragment detailFragment;
    MovieDetailDO movieSelected = new MovieDetailDO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        movieSelected = Parcels.unwrap(intent.getParcelableExtra("movieSelected"));

        setContentView(R.layout.activity_movie_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Movie Details");

        if (detailFragment == null) {
            detailFragment = MovieDetailFragment.newInstance(movieSelected);
        } else {
            detailFragment.updateMovie(movieSelected, false);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_container, detailFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
