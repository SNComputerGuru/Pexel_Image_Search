package com.example.pexelimageviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = "F9Z3nszKpx1FkZuB5RXahHv5u3BPpr9SAspz9j85AJ9EZIFhO4pggCsontaHt";
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button displayButton = findViewById(R.id.btnDisplay);

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndDisplayImage();
            }
        });
    }

    private void fetchAndDisplayImage() {

        String searchQuery = getFromInput();

        String apiUrl = "https://api.pexels.com/v1/search?query=" + searchQuery + "&per_page=10";

        if(searchQuery.equals("")) {
            Toast.makeText(this, "Seach Input Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            currentIndex=(currentIndex + 1) % 10;

        }
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                showToast();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    String imageUrl = parseImageUrl(jsonData, currentIndex);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView imageView = findViewById(R.id.imageView);
                            displayImage(imageView, imageUrl);
                        }
                    });
                } else {
                    showToast();
                }
            }
        });
    }

    private String getFromInput() {
        EditText editText = findViewById(R.id.searchIn);
        String input = String.valueOf(editText.getText());
        if(input.equals("")){
            return "";
        }else{
            return input;
        }
    }

    private String parseImageUrl(String jsonData, int index) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray photosArray = jsonObject.getAsJsonArray("photos");
        JsonObject photoObject = photosArray.get(index).getAsJsonObject();
        return photoObject.getAsJsonObject("src").get("large").getAsString();
    }

    private void displayImage(ImageView imageView, String url) {
        Picasso.get()
                .load(url)
                .into(imageView);
    }

    private void showToast() {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show());
    }
}
