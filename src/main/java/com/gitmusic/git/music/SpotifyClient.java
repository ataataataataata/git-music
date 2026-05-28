
package com.gitmusic.git.music;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpotifyClient {

    public String getCurrentSong() throws IOException {
        String[] tokens;
        try {
            tokens = this.loadToken();
        } catch (FileNotFoundException e) {
            return null;
        }

        String token = tokens[0];

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/currently-playing")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if (response.code() == 401) {
            refreshToken();
            return getCurrentSong();
        }

        String responseBody = response.body().string();

        //response body handle 
        if (responseBody.isEmpty() || responseBody.isBlank()) {
            return null;
        } else {

            JsonObject json = JsonParser
                    .parseString(responseBody)
                    .getAsJsonObject();

            boolean isPlaying = json.get("is_playing").getAsBoolean();
            if (isPlaying == false) {
                return null;
            }
            String songName = json.getAsJsonObject("item").get("name").getAsString();
            String artistName = json.getAsJsonObject("item")
                    .getAsJsonArray("artists")
                    .get(0).getAsJsonObject()
                    .get("name").getAsString();
            return artistName + " - " + songName;
        }

    }

    public void getAccessToken(String code) throws IOException {
        String[] cfg = loadConfig();

        String credentials = cfg[0] + ":" + cfg[1];
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", Config.REDIRECT_URL)
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("Authorization", "Basic " + encoded)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        String accessToken = json.get("access_token").getAsString();
        String refreshToken = json.get("refresh_token").getAsString();
        saveToken(accessToken, refreshToken);

    }

    public void login() throws IOException {
        String[] cfg = loadConfig();
        final String url = "https://accounts.spotify.com/authorize"
                + "?client_id=" + cfg[0]
                + "&response_type=code"
                + "&redirect_uri=" + Config.REDIRECT_URL
                + "&scope=user-read-currently-playing";

        System.out.println("Open this link in your browser: " + url);
    }

    public String[] loadToken() throws FileNotFoundException, IOException {
       String userHome = System.getProperty("user.home");
    BufferedReader br = new BufferedReader(new FileReader(userHome + "/git-music-token.txt"));
    String accessToken = br.readLine();
    String refreshToken = br.readLine();
    br.close();
    return new String[]{accessToken, refreshToken};
    }

    public void refreshToken() throws IOException {
        String[] cfg = loadConfig();
        String credentials = cfg[0] + ":" + cfg[1];
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        String[] tokens = loadToken();

        String refreshToken = tokens[1];

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .add("client_id", cfg[0])
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + encoded)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        String newAccessToken = json.get("access_token").getAsString();
        String newRefreshToken;
        if (json.has("refresh_token")) {
            newRefreshToken = json.get("refresh_token").getAsString();
        } else {
            newRefreshToken = tokens[1];
        }
        saveToken(newAccessToken, newRefreshToken);

    }

    public void saveToken(String accessToken, String refreshToken) throws IOException {
   String userHome = System.getProperty("user.home");
    FileWriter fw = new FileWriter(userHome + "/git-music-token.txt");
    fw.write(accessToken);
    fw.write("\n");
    fw.write(refreshToken);
    fw.close();
    }

    private String[] loadConfig() throws IOException {
        String userHome = System.getProperty("user.home");
        File configFile = new File(userHome + "/git-music-config.txt");
        if (configFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String clientId = br.readLine();
            String clientSecret = br.readLine();
            br.close();
            return new String[]{clientId, clientSecret};
        } else {
            return new String[]{Config.CLIENT_ID, Config.CLIENT_SECRET};
        }
    }

}
