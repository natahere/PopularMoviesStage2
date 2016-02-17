package com.natarajan.movies.DO;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Natarajan on 02/12/16
 */

@org.parceler.Parcel
public class MovieDetailDO {

    public int page;

    public List<MovieDO> results;

    public int total_pages;

    public int total_results;

    public String vote_average;

    public String backdrop_path;

    public String adult;

    public String id;

    public String title;

    public String original_language;

    public String overview;

    public String[] genre_ids;

    public String original_title;

    public String release_date;

    public String vote_count;

    public String poster_path;

    public String video;

    public String popularity;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieDO> getResults() {
        return results;
    }

    public void setResults(List<MovieDO> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String[] getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(String[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public ArrayList<ReviewDetailsDO> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewDetailsDO> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<TrailerDetailsDO> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<TrailerDetailsDO> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<ReviewDetailsDO> reviews = new ArrayList<ReviewDetailsDO>();
    public ArrayList<TrailerDetailsDO> trailers = new ArrayList<TrailerDetailsDO>();
}
