package com.wordpress.juniadev.booklisting;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.wordpress.juniadev.booklisting.BookListingActivity.LOG_TAG;

/**
 * Methods to retrieve book information from Google Books API.
 */
public final class QueryUtils {

    private QueryUtils() {
        // Private constructor
    }

    public static List<Book> getBooksFromAPI(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return extractBooks(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Book> extractBooks(String jsonResponse) {

        final List<Book> books = new ArrayList<>();

        try {
            final JSONObject jsonObject = new JSONObject(jsonResponse);
            final JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = (JSONObject) items.get(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                String thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                String title = volumeInfo.getString("title");
                String author = volumeInfo.has("authors") ? volumeInfo.getJSONArray("authors").getString(0) : "";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.getString("publisher") : "";
                String year =  volumeInfo.has("publishedDate") ? volumeInfo.getString("publishedDate") : "";

                books.add(new Book(thumbnail, title, author, publisher, year));
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        return books;
    }
}
