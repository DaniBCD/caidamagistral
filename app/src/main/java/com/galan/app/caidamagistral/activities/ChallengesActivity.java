package com.galan.app.caidamagistral.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galan.app.caidamagistral.R;
import com.galan.app.caidamagistral.adapters.CustomExpandableListAdapter;
import com.galan.app.caidamagistral.model.Challenge;
import com.galan.app.caidamagistral.model.ShopItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChallengesActivity extends AppCompatActivity {

    Typeface TF;
    TextView textName, responseView;
    ProgressBar progressBar;
    ExpandableListView challengesList;
    private AdView mAdView;
    HashMap<String, List<Challenge>> hashDesafios = new HashMap<>();
    ExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        textName = findViewById(R.id.textName);
        responseView = findViewById(R.id.responseView);
        progressBar = findViewById(R.id.progressBar);
        challengesList = findViewById(R.id.challengesList);
        mAdView = findViewById(R.id.adView);

        MobileAds.initialize(this, "ca-app-pub-6138983841028001~6606303317");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        TF = Typeface.createFromAsset(getAssets(), "font/BurbankBigCondensed-Bold.ttf");
        textName.setTypeface(TF);
        responseView.setTypeface(TF);
        responseView.setText("");

        new getChallenges().execute();
    }

    class getChallenges extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
            challengesList.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(Void... urls) {

            if(hasInternet()){
                try {
                    URL url = new URL("https://fortnite-api.theapinetwork.com/challenges/get?season=current");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "ccd3273db53f7f064f8fde9d61941a11");
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    finally{
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }else{
                String e = "error";
                return e;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                progressBar.setVisibility(View.GONE);
                responseView.setVisibility(View.VISIBLE);
                responseView.setText(R.string.error_solicitud);
            }else if(response.equals("error")){
                progressBar.setVisibility(View.GONE);
                responseView.setVisibility(View.VISIBLE);
                responseView.setText(R.string.error_internet);
            }else{
                progressBar.setVisibility(View.GONE);
                responseView.setText("");
                challengesList.setVisibility(View.VISIBLE);
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray arraySemanas = object.optJSONArray("challenges");
                    List<String> expandableListTitle = new ArrayList<String>();

                    int semana = 1;
                    for (int i = 0; i < arraySemanas.length(); i++) {

                        List<Challenge> challenges = new ArrayList<>();
                        JSONObject item = arraySemanas.getJSONObject(i);
                        JSONArray arrayChallenges = item.getJSONArray("entries");

                        for(int j = 0; j < arrayChallenges.length(); j++){

                            Challenge challenge = new Challenge();
                            JSONObject desafio = arrayChallenges.getJSONObject(j);
                            challenge.setNombre(desafio.getString("challenge"));
                            challenge.setTotal(desafio.getInt("total"));
                            challenge.setEstrellas(desafio.getInt("stars"));
                            challenge.setDificultad(desafio.getString("difficulty"));

                            System.out.println(challenge);
                            challenges.add(challenge);
                        }

                        if(!challenges.isEmpty()){
                            hashDesafios.put(getString(R.string.semana)+semana, challenges);
                            expandableListTitle.add(getString(R.string.semana)+semana);
                        }
                        semana++;
                    }

                    expandableListAdapter = new CustomExpandableListAdapter(ChallengesActivity.this, expandableListTitle, hashDesafios);
                    challengesList.setAdapter(expandableListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.i("INFO", response);
        }
    }

    public static boolean hasInternet() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
            //urlc.setRequestProperty("User-Agent", "Test");
            //urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
        } catch (IOException e) {
            System.out.println("Error comprobando internet");
        }
        return false;
    }

}
