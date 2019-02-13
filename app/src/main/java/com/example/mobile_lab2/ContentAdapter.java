package com.example.mobile_lab2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private ArrayList<News> mNewsList;                                           // ArrayList of news objects.

    public class ContentViewHolder extends RecyclerView.ViewHolder {             // Inner class -> Holds a single 'card' inside the RecycleView.
        public static final String NEWS_ARTICLE = "NEWS_ARTICLE ";               // Name of the news link to be used in intent.
        public ImageView mImageViewNewsImage;                                    // The image in the layout.
        public TextView mTextViewNewsDate;                                       // The "date" text in the layout.
        public TextView mTextViewNewsHeader;                                     // The "header" text in the layout.
        public TextView mTextViewNewsSummary;                                    // The "summary" text in the layout.


        public ContentViewHolder(@NonNull View itemView) {                       // Holds the content in the RecycleView.
            super(itemView);
            this.mImageViewNewsImage = itemView.findViewById(R.id.newsImage);    // Sets the image field.
            this.mTextViewNewsDate = itemView.findViewById(R.id.newsDate);       // Sets tge date field.
            this.mTextViewNewsHeader = itemView.findViewById(R.id.newsHeader);   // Sets the header field
            this.mTextViewNewsSummary = itemView.findViewById(R.id.newsSummary); // sets the summary field.


            itemView.setOnClickListener(v -> {                                   // Listener for when item is clicked.
                News clickedItem = ContentAdapter.this.mNewsList.get(getAdapterPosition());
                // Starts the WebView for the item clicked.
                Intent intent = new Intent(itemView.getContext(), articleViewActivity.class);
                intent.putExtra(NEWS_ARTICLE, clickedItem.getNewsLink());
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public ContentAdapter(ArrayList<News> newsList) {                            // Constructor, stores the ArrayList.
        this.mNewsList = newsList;
    }

    @NonNull
    @Override                                                                    // Adapter to connect to the xml layout.
    public ContentAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_content, viewGroup, false);

        ContentViewHolder newsContent = new ContentViewHolder(v);
        return newsContent;

    }

    // ToDo: Get data from database and read image from internal storage.
    @Override                                                                   // Sets the content of the card.
    public void onBindViewHolder(@NonNull ContentViewHolder contentViewHolder, int i) {
        News currentNewItem = mNewsList.get(i);

        Glide.with(contentViewHolder.itemView.getContext()).                    // Loads the image into the view.
                load(mNewsList.get(i).getImageLink()).
                into(contentViewHolder.mImageViewNewsImage);
        // Loads the rest of the text into the views.
        contentViewHolder.mTextViewNewsDate.setText(currentNewItem.getNewsDate());
        contentViewHolder.mTextViewNewsHeader.setText(currentNewItem.getNewsHeader());
        contentViewHolder.mTextViewNewsSummary.setText(currentNewItem.getNewsSummary());
    }

    @Override                                                                   // The size of array.
    public int getItemCount() {
        return mNewsList.size();
    }
}
