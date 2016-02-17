package com.natarajan.movies.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.natarajan.movies.DO.MovieDetailDO;
import com.natarajan.movies.DO.ReviewDetailsDO;
import com.natarajan.movies.DO.TrailerDetailsDO;
import com.natarajan.movies.Util.MovieAdapter;
import com.natarajan.movies.Util.FetchMoviesAsyncTask;
import com.natarajan.movies.Util.FetchMoviesAsyncTask.CompletedTask;
import com.natarajan.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Type;


// This fragment displays the list of movies and updates the views based on what is selected in movie
// list.

public class MovieListFragment extends Fragment {

    final String LOG_TAG = MovieListFragment.class.getSimpleName();
    GridView gridView;
    MovieAdapter myAdapter = new MovieAdapter(getActivity(), null);
    ArrayList<MovieDetailDO> movieDetails = new ArrayList<MovieDetailDO>();
    ArrayList<MovieDetailDO> favoritesList = new ArrayList<MovieDetailDO>();
    ArrayList<ReviewDetailsDO> reviewDetails = new ArrayList<ReviewDetailsDO>();
    ArrayList<TrailerDetailsDO> trailerDetails = new ArrayList<TrailerDetailsDO>();
    String posterPath;
    String requestURL;
    ListFragmentCallbackInterface callbackListener;
    boolean twoPane;
    MovieDetailDO movieSelected;
    boolean isFavoritesView;
    ArrayList<MovieDetailDO> PopmovieDetails = new ArrayList<MovieDetailDO>();// Stores the results from Pop Query
    ArrayList<MovieDetailDO> RatmovieDetails = new ArrayList<MovieDetailDO>(); // Stores the results from Rat Query
    ArrayList<MovieDetailDO> PRmovieDetails = new ArrayList<MovieDetailDO>(); // Sum of both results
    Set<MovieDetailDO> FinalSet = new HashSet<>();

    Map<MovieDetailDO, String> newMap = new HashMap<>();

    Type type = new TypeToken<ArrayList<MovieDetailDO>>() {
    }.getType();


    Gson gson = new Gson();

    public static MovieListFragment newInstance(boolean twoPane) {
        MovieListFragment listFragment = new MovieListFragment();

        Bundle args = new Bundle();
        args.putBoolean("twoPane", twoPane);
        listFragment.setArguments(args);

        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState == null) {
            twoPane = getArguments().getBoolean("twoPane");
        } else {
            twoPane = savedInstanceState.getBoolean("twoPane");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        requestURL = getString(R.string.movie_main_url) + getString(R.string.api_key);
        startAsyncTask(requestURL, 0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reviewDetails.clear();
                trailerDetails.clear();
                if (isFavoritesView) {
                    movieSelected = favoritesList.get(position);
                } else {
                    movieSelected = movieDetails.get(position);
                }
                String movieId = movieSelected.getId();
                String trailerReviewRequestUrl = getString(R.string.trailer_base_url) + movieId + "?" +
                        getString(R.string.api_key) + getString(R.string.trailer_review_append);
                findTrailersReviews(trailerReviewRequestUrl);
            }
        });

        return rootView;
    }

    //Request for all movies
    public void startAsyncTask(final String requestURL, final int query) {
        FetchMoviesAsyncTask task = new FetchMoviesAsyncTask(new CompletedTask() {
            @Override
            //public void completedTask(ArrayList<MovieDetailDO> result, ArrayList<MovieDetailDO> details) {
            public void completedTask(String movies) {
                try {
                    ArrayList<MovieDetailDO> result = getMovieDataFromJson(movies);
                    myAdapter = new MovieAdapter(getActivity(), result);
                    if (query == 1 || query == 0) { // if Default discover or Popular
                        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("FavPop");
                        String PopFav = gson.toJson(result);
                        editor.putString("FavPop", PopFav);
                        Log.i("FavPop", "is" + PopFav);
                        editor.commit();


                    } else if (query == 2) { // if Rate Query

                        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("RatFav");
                        String RatFav = gson.toJson(result);

                        editor.putString("RatFav", RatFav);
                        Log.i("RatFav", "is" + RatFav);
                        editor.commit();
                    }
                    gridView.setAdapter(myAdapter);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }, getActivity());
        task.execute(requestURL);
    }

    //Request for trailers and reviews of a movie
    public void findTrailersReviews(String requestURL) {
        FetchMoviesAsyncTask task = new FetchMoviesAsyncTask(new CompletedTask() {
            @Override
            public void completedTask(String movieResult) {
                try {
                    getMovieTrailerReviewsFromJson(movieResult);

                    if (twoPane) {
                        //if in tablet, update fragment
                        callbackListener.updateMovie(movieSelected, isFavoritesView);
                    } else {
                        //start activity
                        callbackListener.startDetailActivity(movieSelected);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }, getActivity());
        task.execute(requestURL);
    }

    /**
     * Take the String representing the result of movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private ArrayList<MovieDetailDO> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        try {
            // These are the names of the JSON objects that need to be extracted.
            final String results = "results";
            final String MOVIEID = "id";
            final String ORIGINAL_TITLE = "original_title";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String POSTER_PATH = "poster_path";
            final String VOTE_AVERAGE = "vote_average";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(results);

            for (int i = 0; i < movieArray.length(); i++) {
                MovieDetailDO detail = new MovieDetailDO();

                // Set details for each movie and add to arraylist
                JSONObject movie = movieArray.getJSONObject(i);
                detail.setId(movie.getString(MOVIEID));
                detail.setOriginal_title(movie.getString(ORIGINAL_TITLE));
                detail.setOverview(movie.getString(OVERVIEW));
                detail.setRelease_date(movie.getString(RELEASE_DATE));
                // Get the poster url and construct it
                String poster = movie.getString(POSTER_PATH);
                detail.setPoster_path(constructPosterURL(poster));
                detail.setVote_average(movie.getString(VOTE_AVERAGE));

                movieDetails.add(detail);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Looks like your device is not connected to Internet, Please check", Toast.LENGTH_SHORT).show();
        }
        return movieDetails;
    }

    /**
     Code to get Trailer and Review
     */
    private void getMovieTrailerReviewsFromJson(String movieJsonStr)
            throws JSONException {

        try {
            // These are the names of the JSON objects that need to be extracted.
            final String REVIEWS = "reviews";
            final String TRAILERS = "trailers";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray reviews = movieJson.getJSONObject(REVIEWS).getJSONArray("results");
            JSONArray trailers = movieJson.getJSONObject(TRAILERS).getJSONArray("youtube");

            for (int i = 0; i < reviews.length(); i++) {
                ReviewDetailsDO reviewDetail = new ReviewDetailsDO();
                // Set details for each review and add to arraylist
                JSONObject review = reviews.getJSONObject(i);
                reviewDetail.setContent(review.getString("content"));
                reviewDetail.setAuthor(review.getString("author"));
                reviewDetails.add(reviewDetail);
            }

            for (int i = 0; i < trailers.length(); i++) {
                TrailerDetailsDO trailerDetail = new TrailerDetailsDO();
                // Set details for each trailer and add to arraylist
                JSONObject trailer = trailers.getJSONObject(i);
                trailerDetail.setSource(trailer.getString("source"));
                trailerDetail.setName(trailer.getString("name"));
                trailerDetails.add(trailerDetail);
            }
            //Add reviews and trailers to movieSelected
            movieSelected.setReviews(reviewDetails);
            movieSelected.setTrailers(trailerDetails);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Looks like your device is not connected to Internet, Please check", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateList(String requestURL, boolean isFavoritesView, int query) {
        movieDetails.clear();
        reviewDetails.clear();
        trailerDetails.clear();
        this.isFavoritesView = isFavoritesView;
        myAdapter.notifyDataSetChanged();
        startAsyncTask(requestURL, query);
    }

    //Get favorites and update the view to display them
    public void getFavorites(boolean isFavoritesView) {
        favoritesList.clear();
        this.isFavoritesView = isFavoritesView;

        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
//        Log.i("value in fav", "is" + preferences.getString("FavPop", ""));
//        Log.i("value in fav", "is" + preferences.getString("RatFav", ""));

        // We need to read Fav List from POP
        // We need to read Fav List from Rat
        // We can make a FINAL LIST containing both. This will be used for Fav collection check

        PopmovieDetails = gson.fromJson(preferences.getString("FavPop", ""), type);
        RatmovieDetails = gson.fromJson(preferences.getString("RatFav", ""), type);
        PRmovieDetails.clear();
        PRmovieDetails.addAll(PopmovieDetails);
        PRmovieDetails.addAll(RatmovieDetails);
        FinalSet.clear();
        FinalSet.addAll(PRmovieDetails);
        PRmovieDetails.clear();
        PRmovieDetails.addAll(FinalSet);

//        Log.i("PRmovieDet value is", "is" + PRmovieDetails);
//        Log.i("Size of PR", "is" + PRmovieDetails.size());
//        Log.i("Final Set value is","is" + FinalSet);
//        Log.i("Size of Final","is" + FinalSet.size());
//        Log.i("value of pref", "is" + preferences.getAll().size());

        for (int i = 0; i < preferences.getAll().size(); i++) {

            for (int j = 0; j < PRmovieDetails.size(); j++) {

                MovieDetailDO possibleMovie = PRmovieDetails.get(j);
                String movieID = preferences.getString("movie:" + possibleMovie.getOriginal_title(), "");

                if ((movieID != null) && (possibleMovie.getId().equalsIgnoreCase(movieID))) {
                    if (!favoritesList.contains(possibleMovie)) {
                        favoritesList.add(possibleMovie);

                    }
                }
            }
        }

        //Get all Movie ID from favorite list collection//
        // There is a data quality issue
        // Same Movie appearing on both Pop and Rate query with different object ID
        //this results in same movie appearing twice in favorite
        // Need to remove duplicates based on Movie ID

        for (int k = 0; k < favoritesList.size(); k++) {
            MovieDetailDO favMov = favoritesList.get(k);
            String MovID = favMov.getId();
            //myMap.put(favMov.toString(),MovID);
            newMap.put(favMov, MovID);
            Log.i("Value of FavMov is", "is" + newMap);
        }


        Set<String> mySet = new HashSet<String>();

        for (Iterator itr = newMap.entrySet().iterator(); itr.hasNext(); ) {
            Map.Entry<MovieDetailDO, String> entrySet = (Map.Entry) itr.next();

            String value = entrySet.getValue();

            if (!mySet.add(value)) {
                itr.remove();
            }
        }

        Log.i("Value of itr is", "is" + newMap);
        favoritesList.clear();
        favoritesList.addAll(newMap.keySet());
        newMap.clear();
        myAdapter.notifyDataSetChanged();
        myAdapter = new MovieAdapter(getActivity(), favoritesList);
        Log.i("value of favorite list", "is" + favoritesList);
        gridView.setAdapter(myAdapter);
    }

    //This function constructs the poster url and size for requesting in Picasso
    public String constructPosterURL(String poster) {
        String baseURL = getString(R.string.base_poster_url);
        String posterSize = "w500";
        posterPath = baseURL + posterSize + poster;

        return posterPath;
    }

    public interface ListFragmentCallbackInterface {
        void updateMovie(MovieDetailDO movieSelected, boolean isFavoritesView);

        void startDetailActivity(MovieDetailDO movieSelected);
    }

    @Override

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbackListener = (ListFragmentCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement MovieListCallbackInterface");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("twoPane", twoPane);
    }
}
