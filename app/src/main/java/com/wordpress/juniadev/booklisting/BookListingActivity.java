package com.wordpress.juniadev.booklisting;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Book Listing's main activity.
 */
public class BookListingActivity extends AppCompatActivity {

    public static final String LOG_TAG = BookListingActivity.class.getName();

    private BookAdapter adapter;

    private String query = "";
    private boolean informedQuery = false;

    private static final String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_listing_activity);

        EditText queryEditText = getQueryEditText();
        hideKeyboardIfFocusChanged(queryEditText);
        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                  // Perform query when user taps next button in the EditText.
                    performQuery();
                }
                return false;   // hide keyboard
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);

        TextView emptyTextView = getEmptyTextView();
        emptyTextView.setText(R.string.first_time);
        listView.setEmptyView(emptyTextView);

        adapter = new BookAdapter(this, new ArrayList<Book>());

        listView.setAdapter(adapter);

        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                performQuery();
            }
        });

        // This inits the loader but don't display anything, because the user hasn't entered a query yet.
        getLoaderManager().initLoader(0, null, callbacks);
    }

    private void performQuery() {
        query = getQueryEditText().getText().toString();
        informedQuery = query != null && !query.isEmpty();
        TextView emptyTextView = getEmptyTextView();

        if (!isConnected()) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet_connection);
            getSpinner().setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            getSpinner().setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(0, null, callbacks);
        }
    }

    private EditText getQueryEditText() {
        return (EditText) findViewById(R.id.book_search);
    }

    private TextView getEmptyTextView() {
        return (TextView) findViewById(R.id.empty);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Only show the keyboard if the field has been focused.
     * http://stackoverflow.com/a/19828165
     * @param editText
     */
    private void hideKeyboardIfFocusChanged(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private LoaderManager.LoaderCallbacks<List<Book>> callbacks = new LoaderManager.LoaderCallbacks<List<Book>>() {
        @Override
        public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
            Uri baseUri = Uri.parse(REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", query);
            uriBuilder.appendQueryParameter("maxResults", "10");
            return new BookLoader(BookListingActivity.this, uriBuilder.toString(), informedQuery);
        }

        @Override
        public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
            adapter.clear();

            getSpinner().setVisibility(View.GONE);

            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            } else if (informedQuery) {
                // Only display this message if user has informed something to query.
                TextView emptyView = getEmptyTextView();
                emptyView.setText(R.string.no_books_found);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Book>> loader) {
            adapter.clear();
        }
    };

    private ProgressBar getSpinner() {
        return (ProgressBar) findViewById(R.id.spinner);
    }
}
