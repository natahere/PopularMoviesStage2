package com.natarajan.movies.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.natarajan.movies.DO.MovieDetailDO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Natarajan on 02/13/16.
 * fetch JSON from URL and convert it to a string for further use
 */
public class FetchMoviesAsyncTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMoviesAsyncTask.class.getSimpleName();
    private String requestURL;
    public CompletedTask delegate = null;
    Context mContext;
    ProgressDialog dialog;




    protected void onPreExecute() {

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading...");
        dialog.show();
        Log.i("AsyncTast","Came in preexec");

    }

    //Set the delegate
    public FetchMoviesAsyncTask(CompletedTask delegate,Context context) {
        this.delegate = delegate;
        this.mContext = context;

    }


    // Do start fetch in BackGround
    @Override
    protected String doInBackground(String... params) {
        requestURL = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        try {
            Uri builtUri = Uri.parse(requestURL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.v(LOG_TAG, "InputStream is null");
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                   buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                Log.v(LOG_TAG, "Buffer length is 0");
                // Stream is empty
                return null;
            }
            movieJsonStr = buffer.toString();
            Log. v(LOG_TAG, "Movie string: " + movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return movieJsonStr;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.i("AsyncTast","Came in PostExec");
        if (dialog.isShowing()) {
            dialog.dismiss();}
        delegate.completedTask(result);
    }

    public interface CompletedTask {
        void completedTask(String result);
    }


}
