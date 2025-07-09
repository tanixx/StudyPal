package com.example.studypal;

import android.net.Uri;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class GeminiService {

    private static final String API_KEY =BuildConfig.GEMINI_API_KEY;
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    public interface GeminiCallback {
        void onResponse(String reply);
        void onError(String error);
    }
    public void sendMessage(String userInput, GeminiCallback callback) {
        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", userInput);

            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONArray().put(textPart));

            JSONArray contents = new JSONArray().put(content);

            JSONObject body = new JSONObject();
            body.put("contents", contents);

            RequestBody requestBody = RequestBody.create(
                    body.toString(), MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError("Network Error: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError("HTTP Error: " + response.code() + " " + response.message());
                        return;
                    }

                    String resBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(resBody);
                        JSONArray candidates = json.getJSONArray("candidates");
                        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                        String reply = content.getJSONArray("parts").getJSONObject(0).getString("text");

                        callback.onResponse(reply);
                    } catch (Exception e) {
                        callback.onError("Parsing Error: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Request Build Error: " + e.getMessage());
        }
    }

}
