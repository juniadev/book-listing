package com.wordpress.juniadev.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to retrieve the list of books from the API.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String url;
    private boolean query;

    public BookLoader(Context context, String url, boolean query) {
        super(context);
        this.url = url;
        this.query = query;
    }

    @Override
    public List<Book> loadInBackground() {
        if (query) {
            return QueryUtils.getBooksFromAPI(url);
        }
        return new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
