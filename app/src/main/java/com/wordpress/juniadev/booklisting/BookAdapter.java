package com.wordpress.juniadev.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter to display the list of books in the app.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    private static final String SEPARATOR = ", ";

    public BookAdapter(@NonNull Context context, @NonNull List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        Book book = getItem(position);

        setThumbnail(listItemView, book);

        setTitle(listItemView, book);

        setAuthor(listItemView, book);

        setPublisherYear(listItemView, book);

        return listItemView;
    }

    private void setThumbnail(View listItemView, Book book) {
        ImageView thumbnailView = (ImageView) listItemView.findViewById(R.id.thumbnail);

        Picasso.with(listItemView.getContext())
                .load(book.getThumbnail())
                .resize(66, 95)
                .centerCrop()
                .into(thumbnailView);
    }

    private void setTitle(View listItemView, Book book) {
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(book.getTitle());
    }

    private void setAuthor(View listItemView, Book book) {
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(book.getAuthor());
    }

    private void setPublisherYear(View listItemView, Book book) {
        TextView pubYearView = (TextView) listItemView.findViewById(R.id.publisherYear);
        String publishedYear = getPublishedYear(book);
        if (publishedYear.isEmpty()) {
            pubYearView.setText(book.getPublisher());
        } else {
            pubYearView.setText(book.getPublisher().concat(SEPARATOR).concat(publishedYear));
        }
    }

    private String getPublishedYear(Book book) {
        String publishedDate = book.getPublishedDate();
        if (publishedDate.isEmpty()) {
            return "";
        }
        if (publishedDate.contains("-")) {
            // Date returned on the format "YYYY-MM-DD"
            return publishedDate.split("-")[0];
        }
        // Date contains only the year
        return publishedDate;
    }
}
