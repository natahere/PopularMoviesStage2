package com.natarajan.movies.Controller;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.natarajan.movies.DO.MovieDetailDO;
import com.natarajan.movies.DO.ReviewDetailsDO;
import com.natarajan.movies.DO.TrailerDetailsDO;
import com.natarajan.movies.R;
import com.squareup.picasso.Picasso;


import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This fragment represents the Movie detail screen.
 */

public class MovieDetailFragment extends Fragment {

    final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    MovieDetailDO movieSelected;
    View rootView;

    @Bind(R.id.title) TextView title;
    @Bind(R.id.poster) ImageView poster;
    @Bind(R.id.release) TextView release;
    @Bind(R.id.rating)TextView rating;
    @Bind(R.id.overview)TextView overView;
    @Bind(R.id.favorite_button)ImageButton favoriteButton;
    @Bind(R.id.trailerLayout) LinearLayout trailerLayout;
    @Bind(R.id.trailerSection)TextView trailerSection;
    @Bind(R.id.reviewLayout)LinearLayout reviewLayout;
    @Bind(R.id.reviewSection)TextView reviewSection;



    public static MovieDetailFragment newInstance(MovieDetailDO movieSelected) {
        MovieDetailFragment detailFragment = new MovieDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable("movieSelected", Parcels.wrap(movieSelected));
        detailFragment.setArguments(args);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {

            movieSelected = (MovieDetailDO) Parcels.unwrap(getArguments().getParcelable("movieSelected"));
        }
        if (savedInstanceState != null) {

            movieSelected = (MovieDetailDO) Parcels.unwrap(savedInstanceState.getParcelable("movieSelected"));

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ButterKnife.bind(this,rootView);

        rootView.setVisibility(View.INVISIBLE);

        if (movieSelected != null) {
            //Show view and update UI
            updateMovie(movieSelected, false);
        }

        return rootView;
    }

    //Sets the UI
    public void updateMovie(final MovieDetailDO movieSelected, final boolean isFavoritesView) {

        this.movieSelected = movieSelected;
        final String movieId = movieSelected.getId();

        rootView.setVisibility(View.VISIBLE);

        title.setText(movieSelected.getOriginal_title());
        //Picasso.with(getActivity()).load(movieSelected.getPoster_path()).into(poster);
        Picasso.with(getActivity())
                .load(movieSelected.getPoster_path())
                //.placeholder(R.drawable.def_pos)
                .error(R.drawable.def_pos)
                .into(poster);
        release.setText(movieSelected.getRelease_date());
        rating.setText(movieSelected.getVote_average());
        overView.setText(movieSelected.getOverview());
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);

        // This is to check during load time if the movie is already is fav
        // if so, set the background accordingly
        if (!preferences.getString("movie:" + movieSelected.getOriginal_title(), "").isEmpty()) {
            favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            favoriteButton.setBackgroundColor(getResources().getColor(R.color.white));
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(getResources().getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                //if id does not exist, add to preferences (As a favorite), if it does remove it (undo favorite)
                if (preferences.getString("movie:" + movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.putString("movie:" + movieSelected.getOriginal_title(), movieId);
                    editor.commit();

                    favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                } else if (!preferences.getString("movie:" + movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.remove("movie:" + movieSelected.getOriginal_title());
                    editor.commit();

                    favoriteButton.setBackgroundColor(getResources().getColor(R.color.white));
                    Toast.makeText(getActivity(), "Removed From Favorites", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //Check if there are trailers and display them
        if (movieSelected.getTrailers().isEmpty()) {
            trailerSection.setText(getString(R.string.no_trailers_title));

        } else {
            trailerSection.setText(getString(R.string.trailers_title));
            //add trailers
            for (int i = 0; i < movieSelected.getTrailers().size(); i++) {
                TrailerDetailsDO trailer = new TrailerDetailsDO();
                trailer = movieSelected.getTrailers().get(i);
                final String url = trailer.getSource();
                TextView movieTrailer = new TextView(getActivity());
                movieTrailer.setText(trailer.getName());
                movieTrailer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_play, 0, 0, 0);
                movieTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTrailer(url);
                    }
                });
                trailerLayout.addView(movieTrailer);
            }
        }

        //Check if there are reviews and display them
        if (movieSelected.getReviews().isEmpty()) {
            reviewSection.setText(getString(R.string.no_reviews_title));
        } else {
            reviewSection.setText(getString(R.string.reviews_title));
            // add reviews
            for (int i = 0; i < movieSelected.getReviews().size(); i++) {
                ReviewDetailsDO review;// = new ReviewDetailsDO();
                review = movieSelected.getReviews().get(i);
                TextView movieReview = new TextView(getActivity());
                movieReview.setText(Html.fromHtml(review.getContent() + "<b>" + " -- " + review.getAuthor() + "</b>" + "<br />"));
                movieReview.setPadding(0, 15, 0, 15);

                reviewLayout.addView(movieReview);
            }
        }
    }



    public void clearView() {
        //Clear the views and layouts sort preference is selected
        rootView.setVisibility(View.GONE);
        trailerLayout.removeAllViews();
        reviewLayout.removeAllViews();
    }

    public void openTrailer(String trailerURL) {
        try {
            //Open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.youtube_base_url) + trailerURL));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("movieSelected", Parcels.wrap(movieSelected));
    }
}
