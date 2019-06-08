package com.galan.app.caidamagistral.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.astritveliu.boom.Boom;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galan.app.caidamagistral.R;
import com.galan.app.caidamagistral.fragments.DuoFragment;
import com.galan.app.caidamagistral.fragments.SoloFragment;
import com.galan.app.caidamagistral.fragments.SquadFragment;
import com.galan.app.caidamagistral.model.PageAdapter;
import com.galan.app.caidamagistral.model.Stats;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StatsActivity extends AppCompatActivity {

    public TextView textStats, responseView;
    public EditText usuario;
    public ProgressBar progressBar;
    public ImageButton buscar;
    public TabLayout tabLayout;
    public TabItem tabSolo, tabDuo, tabSquad;
    public ViewPager viewPager;

    String idUsuario, user;
    Stats soloStats = new Stats();
    Stats duoStats = new Stats();
    Stats squadStats = new Stats();
    Typeface TF;
    PageAdapter pageAdapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        textStats = findViewById(R.id.textStats);
        progressBar = findViewById(R.id.progressBar);
        responseView = findViewById(R.id.responseView);
        usuario = findViewById(R.id.editText);
        buscar = findViewById(R.id.searchButton);
        tabLayout = findViewById(R.id.tabs);
        tabSolo = findViewById(R.id.tabsolo);
        tabDuo = findViewById(R.id.tabduo);
        tabSquad = findViewById(R.id.tabsquad);
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdView = findViewById(R.id.adView);

        new Boom(buscar);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), 3);

        MobileAds.initialize(this, "ca-app-pub-6138983841028001~6606303317");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        TF = Typeface.createFromAsset(getAssets(), "font/BurbankBigCondensed-Bold.ttf");

        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setVisibility(View.INVISIBLE);
        viewPager.setOffscreenPageLimit(3);
        setCustomFont();

        final LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        /*for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    viewPager.setCurrentItem();
                    return true;
                }
            });
        }*/

        tabStrip.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.setCurrentItem(0);
                return true;
            }
        });

        tabStrip.getChildAt(1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.setCurrentItem(1);
                return true;
            }
        });

        tabStrip.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.setCurrentItem(2);
                return true;
            }
        });

        tabLayout.setVisibility(View.INVISIBLE);

        responseView.setTextSize(20);
        responseView.setText(R.string.stats);

        textStats.setTypeface(TF);
        responseView.setTypeface(TF);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(StatsActivity.this);
                user = usuario.getText().toString().trim();
                new getPlayerId().execute();
            }
        });

    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(TF);
                }
            }
        }
    }

    class getStats extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            // Do some validation here

            try {
                URL url = new URL("https://fortnite-public-api.theapinetwork.com/prod09/users/public/br_stats_v2?user_id="+idUsuario);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
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
        }

        protected void onPostExecute(String response) {

            progressBar.setVisibility(View.GONE);

            if (response == null) {
                viewPager.setVisibility(View.INVISIBLE);
                responseView.setVisibility(View.VISIBLE);
                responseView.setText(R.string.error_solicitud);
            } else {
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject data = object.getJSONObject("data");

                    if (data.has("keyboardmouse") && data.has("gamepad") && data.has("touch")) {
                        //ha jugado con teclado, mando y tactil
                        JSONObject keyboard = data.getJSONObject("keyboardmouse");
                        JSONObject gamepad = data.getJSONObject("gamepad");
                        JSONObject touch = data.getJSONObject("touch");

                        //COMPROBAMOS DUO
                        if (keyboard.has("defaultduo") && gamepad.has("defaultduo") && touch.has("defaultduo")) {
                            //hay duo en teclado, mando y tactil
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuo.has("placetop1") && defaultDuoM.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuo.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1") && defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5") && defaultDuoM.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuo.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5") && defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12") && defaultDuoM.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuo.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12") && defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills") && defaultDuoM.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuo.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuo.has("kills") && defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuo.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuo.getInt("kills"));
                            } else if (defaultDuoM.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived") && defaultDuoM.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuo.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived") && defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed") && defaultDuoM.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuo.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed") && defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed") && defaultDuoM.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuo.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed") && defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score") && defaultDuoM.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuo.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuo.has("score") && defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuo.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuo.getInt("score"));
                            } else if (defaultDuoM.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultduo") && gamepad.has("defaultduo")) {
                            //hay duo en teclado y mando
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuo.has("placetop1") && defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5") && defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12") && defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills") && defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived") && defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed") && defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed") && defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score") && defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultduo") && touch.has("defaultduo")) {
                            //hay duo en teclado y tactil
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuo.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuo.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuo.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuo.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuo.getInt("kills"));
                            } else if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuo.getInt("score"));
                            } else if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultduo") && touch.has("defaultduo")) {
                            //hay duo en mando y tactil
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoM.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultduo")) {
                            //hay duo en teclado
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultduo")) {
                            //hay duo en mando
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultduo")) {
                            //hay duo en tactil
                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (keyboard.has("defaultsquad") && gamepad.has("defaultsquad") && touch.has("defaultsquad")) {
                            //hay squad en teclado, mando y tactil
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquad.has("placetop1") && defaultSquadM.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquad.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1") && defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3") && defaultSquadM.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquad.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3") && defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6") && defaultSquadM.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquad.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6") && defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills") && defaultSquadM.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquad.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquad.has("kills") && defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquad.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquad.getInt("kills"));
                            } else if (defaultSquadM.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived") && defaultSquadM.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquad.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived") && defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed") && defaultSquadM.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquad.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed") && defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed") && defaultSquadM.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquad.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed") && defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score") && defaultSquadM.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquad.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquad.has("score") && defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquad.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquad.getInt("score"));
                            } else if (defaultSquadM.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsquad") && gamepad.has("defaultsquad")) {
                            //hay squad en teclado y mando
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquad.has("placetop1") && defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3") && defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6") && defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills") && defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived") && defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed") && defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed") && defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score") && defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsquad") && touch.has("defaultsquad")) {
                            //hay squad en teclado y tactil
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquad.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquad.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquad.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquad.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquad.getInt("kills"));
                            } else if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquad.getInt("score"));
                            } else if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsquad") && touch.has("defaultsquad")) {
                            //hay squad en mando y tactil
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadM.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsquad")) {
                            //hay squad en teclado
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsquad")) {
                            //hay squad en mando
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsquad")) {
                            //hay squad en tactil
                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (keyboard.has("defaultsolo") && gamepad.has("defaultsolo") && touch.has("defaultsolo")) {
                            //hay solo en teclado, mando y tactil
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSolo.has("placetop1") && defaultSoloM.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSolo.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1") && defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10") && defaultSoloM.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSolo.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10") && defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25") && defaultSoloM.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSolo.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25") && defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills") && defaultSoloM.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSolo.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSolo.has("kills") && defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSolo.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSolo.getInt("kills"));
                            } else if (defaultSoloM.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived") && defaultSoloM.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSolo.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived") && defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed") && defaultSoloM.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSolo.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed") && defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed") && defaultSoloM.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSolo.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed") && defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score") && defaultSoloM.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSolo.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSolo.has("score") && defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSolo.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSolo.getInt("score"));
                            } else if (defaultSoloM.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsolo") && gamepad.has("defaultsolo")) {
                            //hay solo en teclado y mando
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSolo.has("placetop1") && defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10") && defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25") && defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills") && defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived") && defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed") && defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed") && defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score") && defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsolo") && touch.has("defaultsolo")) {
                            //hay solo en teclado y tactil
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSolo.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSolo.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSolo.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSolo.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSolo.getInt("kills"));
                            } else if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSolo.getInt("score"));
                            } else if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsolo") && touch.has("defaultsolo")) {
                            //hay solo en mando y tactil
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloM.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsolo")) {
                            //hay solo en teclado
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsolo")) {
                            //hay solo en mando
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsolo")) {
                            //hay solo en tactil
                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("keyboardmouse") && data.has("gamepad")) {
                        //ha jugado con teclado y mando
                        JSONObject keyboard = data.getJSONObject("keyboardmouse");
                        JSONObject gamepad = data.getJSONObject("gamepad");

                        //COMPROBAMOS DUO
                        if (keyboard.has("defaultduo") && gamepad.has("defaultduo")) {
                            //hay duo en teclado y mando
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuo.has("placetop1") && defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5") && defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12") && defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills") && defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived") && defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed") && defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed") && defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score") && defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultduo")) {
                            //hay duo en teclado
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultduo")) {
                            //hay duo en mando
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (keyboard.has("defaultsquad") && gamepad.has("defaultsquad")) {
                            //hay squad en teclado y mando
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquad.has("placetop1") && defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3") && defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6") && defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills") && defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived") && defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed") && defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed") && defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score") && defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsquad")) {
                            //hay squad en teclado
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsquad")) {
                            //hay squad en mando
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (keyboard.has("defaultsolo") && gamepad.has("defaultsolo")) {
                            //hay solo en teclado y mando
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSolo.has("placetop1") && defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10") && defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25") && defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills") && defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived") && defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed") && defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed") && defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score") && defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsolo")) {
                            //hay solo en teclado
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsolo")) {
                            //hay solo en mando
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("keyboardmouse") && data.has("touch")) {
                        //ha jugado con teclado y tactil
                        JSONObject keyboard = data.getJSONObject("keyboardmouse");
                        JSONObject touch = data.getJSONObject("touch");

                        //COMPROBAMOS DUO
                        if (keyboard.has("defaultduo") && touch.has("defaultduo")) {
                            //hay duo en teclado y tactil
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuo.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuo.getInt("placetop1"));
                            } else if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuo.getInt("placetop5"));
                            } else if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuo.getInt("placetop12"));
                            } else if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuo.getInt("kills"));
                            } else if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuo.getInt("score"));
                            } else if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultduo")) {
                            //hay duo en teclado
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultduo")) {
                            //hay duo en tactil
                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (keyboard.has("defaultsquad") && touch.has("defaultsquad")) {
                            //hay squad en teclado y tactil
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquad.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquad.getInt("placetop1"));
                            } else if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquad.getInt("placetop3"));
                            } else if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquad.getInt("placetop6"));
                            } else if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquad.getInt("kills"));
                            } else if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquad.getInt("score"));
                            } else if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsquad")) {
                            //hay squad en teclado
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsquad")) {
                            //hay squad en tactil
                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (keyboard.has("defaultsolo") && touch.has("defaultsolo")) {
                            //hay solo en teclado y tactil
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSolo.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSolo.getInt("placetop1"));
                            } else if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSolo.getInt("placetop10"));
                            } else if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSolo.getInt("placetop25"));
                            } else if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSolo.getInt("kills"));
                            } else if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSolo.getInt("score"));
                            } else if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (keyboard.has("defaultsolo")) {
                            //hay solo en teclado
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsolo")) {
                            //hay solo en tactil
                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("gamepad") && data.has("touch")) {
                        //ha jugado con mando y tactil
                        JSONObject gamepad = data.getJSONObject("gamepad");
                        JSONObject touch = data.getJSONObject("touch");

                        //COMPROBAMOS DUO
                        if (gamepad.has("defaultduo") && touch.has("defaultduo")) {
                            //hay duo en mando y tactil
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoM.has("placetop1") && defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1") + defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5") && defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5") + defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12") && defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12") + defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills") && defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills") + defaultDuoM.getInt("kills"));
                            } else if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived") && defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived") + defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed") && defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed") + defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed") && defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed") + defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score") && defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score") + defaultDuoM.getInt("score"));
                            } else if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultduo")) {
                            //hay duo en mando
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultduo")) {
                            //hay duo en tactil
                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (gamepad.has("defaultsquad") && touch.has("defaultsquad")) {
                            //hay squad en mando y tactil
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadM.has("placetop1") && defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1") + defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3") && defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3") + defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6") && defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6") + defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills") && defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills") + defaultSquadM.getInt("kills"));
                            } else if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived") && defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived") + defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed") && defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed") + defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed") && defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed") + defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score") && defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score") + defaultSquadM.getInt("score"));
                            } else if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsquad")) {
                            //hay squad en mando
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsquad")) {
                            //hay squad en tactil
                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (gamepad.has("defaultsolo") && touch.has("defaultsolo")) {
                            //hay solo en mando y tactil
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloM.has("placetop1") && defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1") + defaultSoloM.getInt("placetop1"));
                            } else if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10") && defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10") + defaultSoloM.getInt("placetop10"));
                            } else if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25") && defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25") + defaultSoloM.getInt("placetop25"));
                            } else if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills") && defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills") + defaultSoloM.getInt("kills"));
                            } else if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived") && defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived") + defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed") && defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed") + defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed") && defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed") + defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score") && defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score") + defaultSoloM.getInt("score"));
                            } else if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (gamepad.has("defaultsolo")) {
                            //hay solo en mando
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else if (touch.has("defaultsolo")) {
                            //hay solo en tactil
                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("keyboardmouse")) {
                        //ha jugado con teclado
                        JSONObject keyboard = data.getJSONObject("keyboardmouse");

                        //COMPROBAMOS DUO
                        if (keyboard.has("defaultduo")) {
                            //hay duo en teclado
                            JSONObject duo = keyboard.getJSONObject("defaultduo");
                            JSONObject defaultDuo = duo.getJSONObject("default");

                            if (defaultDuo.has("placetop1")) {
                                duoStats.setTop1(defaultDuo.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuo.has("placetop5")) {
                                duoStats.setTop2(defaultDuo.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuo.has("placetop12")) {
                                duoStats.setTop3(defaultDuo.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuo.has("kills")) {
                                duoStats.setKills(defaultDuo.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuo.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuo.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuo.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuo.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuo.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuo.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuo.has("score")) {
                                duoStats.setPuntos(defaultDuo.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (keyboard.has("defaultsquad")) {
                            //hay squad en teclado
                            JSONObject squad = keyboard.getJSONObject("defaultsquad");
                            JSONObject defaultSquad = squad.getJSONObject("default");

                            if (defaultSquad.has("placetop1")) {
                                squadStats.setTop1(defaultSquad.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquad.has("placetop3")) {
                                squadStats.setTop2(defaultSquad.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquad.has("placetop6")) {
                                squadStats.setTop3(defaultSquad.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquad.has("kills")) {
                                squadStats.setKills(defaultSquad.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquad.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquad.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquad.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquad.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquad.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquad.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquad.has("score")) {
                                squadStats.setPuntos(defaultSquad.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (keyboard.has("defaultsolo")) {
                            //hay solo en teclado
                            JSONObject solo = keyboard.getJSONObject("defaultsolo");
                            JSONObject defaultSolo = solo.getJSONObject("default");

                            if (defaultSolo.has("placetop1")) {
                                soloStats.setTop1(defaultSolo.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSolo.has("placetop10")) {
                                soloStats.setTop2(defaultSolo.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSolo.has("placetop25")) {
                                soloStats.setTop3(defaultSolo.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSolo.has("kills")) {
                                soloStats.setKills(defaultSolo.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSolo.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSolo.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSolo.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSolo.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSolo.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSolo.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSolo.has("score")) {
                                soloStats.setPuntos(defaultSolo.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("gamepad")) {
                        //ha jugado con mando
                        JSONObject gamepad = data.getJSONObject("gamepad");

                        //COMPROBAMOS DUO
                        if (gamepad.has("defaultduo")) {
                            //hay duo en mando
                            JSONObject duoM = gamepad.getJSONObject("defaultduo");
                            JSONObject defaultDuoM = duoM.getJSONObject("default");

                            if (defaultDuoM.has("placetop1")) {
                                duoStats.setTop1(defaultDuoM.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoM.has("placetop5")) {
                                duoStats.setTop2(defaultDuoM.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoM.has("placetop12")) {
                                duoStats.setTop3(defaultDuoM.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoM.has("kills")) {
                                duoStats.setKills(defaultDuoM.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoM.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoM.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoM.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoM.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoM.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoM.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoM.has("score")) {
                                duoStats.setPuntos(defaultDuoM.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (gamepad.has("defaultsquad")) {
                            //hay squad en mando
                            JSONObject squadM = gamepad.getJSONObject("defaultsquad");
                            JSONObject defaultSquadM = squadM.getJSONObject("default");

                            if (defaultSquadM.has("placetop1")) {
                                squadStats.setTop1(defaultSquadM.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadM.has("placetop3")) {
                                squadStats.setTop2(defaultSquadM.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadM.has("placetop6")) {
                                squadStats.setTop3(defaultSquadM.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadM.has("kills")) {
                                squadStats.setKills(defaultSquadM.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadM.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadM.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadM.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadM.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadM.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadM.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadM.has("score")) {
                                squadStats.setPuntos(defaultSquadM.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (gamepad.has("defaultsolo")) {
                            //hay solo en mando
                            JSONObject soloM = gamepad.getJSONObject("defaultsolo");
                            JSONObject defaultSoloM = soloM.getJSONObject("default");

                            if (defaultSoloM.has("placetop1")) {
                                soloStats.setTop1(defaultSoloM.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloM.has("placetop10")) {
                                soloStats.setTop2(defaultSoloM.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloM.has("placetop25")) {
                                soloStats.setTop3(defaultSoloM.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloM.has("kills")) {
                                soloStats.setKills(defaultSoloM.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloM.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloM.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloM.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloM.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloM.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloM.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloM.has("score")) {
                                soloStats.setPuntos(defaultSoloM.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    } else if (data.has("touch")) {
                        //ha jugado con tactil
                        JSONObject touch = data.getJSONObject("touch");

                        //COMPROBAMOS DUO
                        if (touch.has("defaultduo")) {
                            //hay duo en tactil
                            JSONObject duoT = touch.getJSONObject("defaultduo");
                            JSONObject defaultDuoT = duoT.getJSONObject("default");

                            if (defaultDuoT.has("placetop1")) {
                                duoStats.setTop1(defaultDuoT.getInt("placetop1"));
                            } else {
                                duoStats.setTop1(0);
                            }

                            if (defaultDuoT.has("placetop5")) {
                                duoStats.setTop2(defaultDuoT.getInt("placetop5"));
                            } else {
                                duoStats.setTop2(0);
                            }

                            if (defaultDuoT.has("placetop12")) {
                                duoStats.setTop3(defaultDuoT.getInt("placetop12"));
                            } else {
                                duoStats.setTop3(0);
                            }

                            if (defaultDuoT.has("kills")) {
                                duoStats.setKills(defaultDuoT.getInt("kills"));
                            } else {
                                duoStats.setKills(0);
                            }

                            if (defaultDuoT.has("playersoutlived")) {
                                duoStats.setJugadores(defaultDuoT.getInt("playersoutlived"));
                            } else {
                                duoStats.setJugadores(0);
                            }

                            if (defaultDuoT.has("matchesplayed")) {
                                duoStats.setPartidas(defaultDuoT.getInt("matchesplayed"));
                            } else {
                                duoStats.setPartidas(0);
                            }

                            if (defaultDuoT.has("minutesplayed")) {
                                duoStats.setMinutos(defaultDuoT.getInt("minutesplayed"));
                            } else {
                                duoStats.setMinutos(0);
                            }

                            if (defaultDuoT.has("score")) {
                                duoStats.setPuntos(defaultDuoT.getInt("score"));
                            } else {
                                duoStats.setPuntos(0);
                            }

                        } else {
                            //no hay duo
                            duoStats.setTop1(0);
                            duoStats.setTop2(0);
                            duoStats.setTop3(0);
                            duoStats.setJugadores(0);
                            duoStats.setMinutos(0);
                            duoStats.setPuntos(0);
                            duoStats.setPartidas(0);
                            duoStats.setKills(0);
                        }

                        //COMPROBAMOS SQUAD

                        if (touch.has("defaultsquad")) {
                            //hay squad en tactil
                            JSONObject squadT = touch.getJSONObject("defaultsquad");
                            JSONObject defaultSquadT = squadT.getJSONObject("default");

                            if (defaultSquadT.has("placetop1")) {
                                squadStats.setTop1(defaultSquadT.getInt("placetop1"));
                            } else {
                                squadStats.setTop1(0);
                            }

                            if (defaultSquadT.has("placetop3")) {
                                squadStats.setTop2(defaultSquadT.getInt("placetop3"));
                            } else {
                                squadStats.setTop2(0);
                            }

                            if (defaultSquadT.has("placetop6")) {
                                squadStats.setTop3(defaultSquadT.getInt("placetop6"));
                            } else {
                                squadStats.setTop3(0);
                            }

                            if (defaultSquadT.has("kills")) {
                                squadStats.setKills(defaultSquadT.getInt("kills"));
                            } else {
                                squadStats.setKills(0);
                            }

                            if (defaultSquadT.has("playersoutlived")) {
                                squadStats.setJugadores(defaultSquadT.getInt("playersoutlived"));
                            } else {
                                squadStats.setJugadores(0);
                            }

                            if (defaultSquadT.has("matchesplayed")) {
                                squadStats.setPartidas(defaultSquadT.getInt("matchesplayed"));
                            } else {
                                squadStats.setPartidas(0);
                            }

                            if (defaultSquadT.has("minutesplayed")) {
                                squadStats.setMinutos(defaultSquadT.getInt("minutesplayed"));
                            } else {
                                squadStats.setMinutos(0);
                            }

                            if (defaultSquadT.has("score")) {
                                squadStats.setPuntos(defaultSquadT.getInt("score"));
                            } else {
                                squadStats.setPuntos(0);
                            }

                        } else {
                            //no hay squad
                            squadStats.setTop1(0);
                            squadStats.setTop2(0);
                            squadStats.setTop3(0);
                            squadStats.setJugadores(0);
                            squadStats.setMinutos(0);
                            squadStats.setPuntos(0);
                            squadStats.setPartidas(0);
                            squadStats.setKills(0);
                        }

                        //COMPROBAMOS SOLO

                        if (touch.has("defaultsolo")) {
                            //hay solo en tactil
                            JSONObject soloT = touch.getJSONObject("defaultsolo");
                            JSONObject defaultSoloT = soloT.getJSONObject("default");

                            if (defaultSoloT.has("placetop1")) {
                                soloStats.setTop1(defaultSoloT.getInt("placetop1"));
                            } else {
                                soloStats.setTop1(0);
                            }

                            if (defaultSoloT.has("placetop10")) {
                                soloStats.setTop2(defaultSoloT.getInt("placetop10"));
                            } else {
                                soloStats.setTop2(0);
                            }

                            if (defaultSoloT.has("placetop25")) {
                                soloStats.setTop3(defaultSoloT.getInt("placetop25"));
                            } else {
                                soloStats.setTop3(0);
                            }

                            if (defaultSoloT.has("kills")) {
                                soloStats.setKills(defaultSoloT.getInt("kills"));
                            } else {
                                soloStats.setKills(0);
                            }

                            if (defaultSoloT.has("playersoutlived")) {
                                soloStats.setJugadores(defaultSoloT.getInt("playersoutlived"));
                            } else {
                                soloStats.setJugadores(0);
                            }

                            if (defaultSoloT.has("matchesplayed")) {
                                soloStats.setPartidas(defaultSoloT.getInt("matchesplayed"));
                            } else {
                                soloStats.setPartidas(0);
                            }

                            if (defaultSoloT.has("minutesplayed")) {
                                soloStats.setMinutos(defaultSoloT.getInt("minutesplayed"));
                            } else {
                                soloStats.setMinutos(0);
                            }

                            if (defaultSoloT.has("score")) {
                                soloStats.setPuntos(defaultSoloT.getInt("score"));
                            } else {
                                soloStats.setPuntos(0);
                            }

                        } else {
                            //no hay solo
                            soloStats.setTop1(0);
                            soloStats.setTop2(0);
                            soloStats.setTop3(0);
                            soloStats.setJugadores(0);
                            soloStats.setMinutos(0);
                            soloStats.setPuntos(0);
                            soloStats.setPartidas(0);
                            soloStats.setKills(0);
                        }

                    }

                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);

                    viewPager.setCurrentItem(0);
                    SoloFragment solo = (SoloFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    solo.setStats(soloStats);
                    viewPager.setCurrentItem(1);

                    DuoFragment duo = (DuoFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    duo.setStats(duoStats);
                    viewPager.setCurrentItem(2);

                    SquadFragment squad = (SquadFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    squad.setStats(squadStats);
                    viewPager.setCurrentItem(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class getPlayerId extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            user = user.trim();

            if(hasInternet()){
                try {
                    URL url = new URL("https://fortnite-api.theapinetwork.com/users/id?username="+user);
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
                viewPager.setVisibility(View.INVISIBLE);
                responseView.setVisibility(View.VISIBLE);
                responseView.setText(R.string.error_solicitud);
                response = "THERE WAS AN ERROR";
            }else if(response.equals("error")){
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.INVISIBLE);
                responseView.setVisibility(View.VISIBLE);
                responseView.setText(R.string.error_internet);
            }else{
                try {
                    JSONObject data = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject object = data.getJSONObject("data");

                    if(object.has("uid")){
                        idUsuario = object.getString("uid");
                    }else{
                        idUsuario = null;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(idUsuario==null){
                    responseView.setText(R.string.error_user);
                    progressBar.setVisibility(View.GONE);
                }else{
                    StatsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new getStats().execute();
                        }
                    });
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
