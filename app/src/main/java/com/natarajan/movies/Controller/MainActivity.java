package com.natarajan.movies.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.natarajan.movies.DO.MovieDetailDO;
import com.natarajan.movies.Controller.MovieListFragment.ListFragmentCallbackInterface;

import com.natarajan.movies.R;


import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity implements ListFragmentCallbackInterface {
    private boolean mTwoPane; // Boolean to be set for tablet or mobile
    MovieListFragment listFragment; // containts Movie List - Shows Posters alone
    MovieDetailDO movieSelected;
    MovieDetailFragment detailFragment;
    String requestURL;
    boolean isFavoritesView = false;
    int queryValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (listFragment == null) {
            listFragment = MovieListFragment.newInstance(mTwoPane);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.movie_list_container, listFragment);
        fragmentTransaction.commit();


        if (mTwoPane) {
            //show the detail view in this activity by adding or replacing the detail fragment
            detailFragment = new MovieDetailFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.movie_detail_container, detailFragment);
            ft.commit();

        }
    }

    //Update the movie selected and clear the detail fragment to show new selection
    public void updateMovie(MovieDetailDO movieSelected, boolean favView) {
        this.movieSelected = movieSelected;
        detailFragment.clearView();
        detailFragment.updateMovie(movieSelected, favView);
    }

    public void startDetailActivity(MovieDetailDO movieSelected) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movieSelected", Parcels.wrap(movieSelected));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {


            case R.id.action_sortpopular:
                sortOnPopSelected();
                return true;
            case R.id.action_sortrated:
                sortOnRatSelected();
                return true;
            case R.id.action_favorites:
                sortOnFavSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void sortOnPopSelected()
    {
        requestURL = getString(R.string.popular_url) + "&" + getString(R.string.api_key);
        queryValue = 1;
        CheckView(queryValue);
        getSupportActionBar().setTitle("Movies by Popularity");
    }


    public void sortOnRatSelected()
    {
        requestURL = getString(R.string.top_rate_url) + getString(R.string.api_key);
        queryValue = 2;
        CheckView(queryValue);
        getSupportActionBar().setTitle("Movies by Rating");
    }

    public void sortOnFavSelected()
    {
        isFavoritesView = true;
        queryValue = 3;
        if (detailFragment != null) {
            detailFragment.clearView();
        }
        listFragment.getFavorites(isFavoritesView);
        getSupportActionBar().setTitle("Favorite Movies");
    }

    public void CheckView(int q)
    {
        isFavoritesView = false;
        queryValue = q;
        if (detailFragment != null) {
            detailFragment.clearView();
        }
        listFragment.updateList(requestURL, isFavoritesView, queryValue);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
