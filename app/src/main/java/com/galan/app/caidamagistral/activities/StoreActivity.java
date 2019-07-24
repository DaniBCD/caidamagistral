package com.galan.app.caidamagistral.activities;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.galan.app.caidamagistral.R;
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
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    Typeface TF;
    LinearLayout galleryFeatured, galleryDaily;
    TextView textDestacado, textDiario, textStore, responseView;
    ProgressBar progressBar;
    HorizontalScrollView scroll1, scroll2;
    List<ShopItem> shopDestacados = new ArrayList<>();
    List<ShopItem> shopDiario = new ArrayList<>();
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        galleryFeatured = findViewById(R.id.gallery1);
        galleryDaily = findViewById(R.id.gallery2);
        textDestacado = findViewById(R.id.textDestacados);
        textDiario = findViewById(R.id.textDiario);
        textStore = findViewById(R.id.textStore);
        responseView = findViewById(R.id.responseView);
        progressBar = findViewById(R.id.progressBar);
        scroll1 = findViewById(R.id.scroll1);
        scroll2 = findViewById(R.id.scroll2);
        mAdView = findViewById(R.id.adView);

        MobileAds.initialize(this, "ca-app-pub-6138983841028001~6606303317");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        TF = Typeface.createFromAsset(getAssets(), "font/BurbankBigCondensed-Bold.ttf");
        textDestacado.setTypeface(TF);
        textDiario.setTypeface(TF);
        textStore.setTypeface(TF);
        responseView.setTypeface(TF);
        responseView.setText("");

        new getStore().execute();
    }

    class getStore extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
            textDestacado.setVisibility(View.INVISIBLE);
            textDiario.setVisibility(View.INVISIBLE);
            scroll1.setVisibility(View.INVISIBLE);
            scroll2.setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(Void... urls) {

            if(hasInternet()){
                try {
                    URL url = new URL("https://fortnite-api.theapinetwork.com/store/get");
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
                textDestacado.setVisibility(View.VISIBLE);
                textDiario.setVisibility(View.VISIBLE);
                scroll1.setVisibility(View.VISIBLE);
                scroll2.setVisibility(View.VISIBLE);
                try {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray arrayShop = object.optJSONArray("data");

                    for (int i = 0; i < arrayShop.length(); i++) {

                        JSONObject item = arrayShop.getJSONObject(i);
                        JSONObject store = item.getJSONObject("store");
                        if(store.getBoolean("isFeatured")){
                            ShopItem destacado = new ShopItem();


                            JSONObject images = item.getJSONObject("item");
                            JSONObject image = images.getJSONObject("images");

                            destacado.setNombre(images.getString("name"));
                            destacado.setPrecio(store.getString("cost"));
                            destacado.setImagen(image.getString("background"));
                            destacado.setNuevo(store.getBoolean("isNew"));
                            destacado.setNumeroVeces(store.getInt("occurrences"));

                            shopDestacados.add(destacado);
                        }else{
                            ShopItem diario = new ShopItem();
                            JSONObject images = item.getJSONObject("item");
                            JSONObject image = images.getJSONObject("images");

                            diario.setNombre(images.getString("name"));
                            diario.setPrecio(store.getString("cost"));
                            diario.setImagen(image.getString("background"));
                            diario.setNuevo(store.getBoolean("isNew"));
                            diario.setNumeroVeces(store.getInt("occurrences"));

                            shopDiario.add(diario);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LayoutInflater inflater = LayoutInflater.from(StoreActivity.this);

                for (ShopItem item : shopDestacados){

                    View view = inflater.inflate(R.layout.item, galleryFeatured, false);

                    TextView text = view.findViewById(R.id.textItem);
                    TextView precio = view.findViewById(R.id.textPrecio);
                    ImageView image = view.findViewById(R.id.imageItem);
                    ImageView imageLabel = view.findViewById(R.id.imageLabel);

                    text.setTypeface(TF);
                    text.setText(item.getNombre());
                    precio.setTypeface(TF);
                    precio.setText(item.getPrecio());

                    if(!item.isNuevo()){
                        imageLabel.setVisibility(View.INVISIBLE);
                        //image.setLabelText(getResources().getString(R.string.nuevoTienda));
                        //image.setLabelBackgroundColor(Color.parseColor("#ffffff"));
                        //image.setLabelBackgroundAlpha(80);
                    }

                    Picasso.get()
                            .load(item.getImagen())
                            .resize(150, 150)
                            .centerCrop()
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(image);

                    galleryFeatured.addView(view);
                }

                for (ShopItem item : shopDiario){

                    View view = inflater.inflate(R.layout.item, galleryDaily, false);

                    TextView text = view.findViewById(R.id.textItem);
                    TextView precio = view.findViewById(R.id.textPrecio);
                    ImageView image = view.findViewById(R.id.imageItem);
                    ImageView imageLabel = view.findViewById(R.id.imageLabel);

                    text.setTypeface(TF);
                    text.setText(item.getNombre());
                    precio.setTypeface(TF);
                    precio.setText(item.getPrecio());

                    if(!item.isNuevo()){
                        imageLabel.setVisibility(View.INVISIBLE);
                        //image.setLabelText(getResources().getString(R.string.nuevoTienda));
                        //image.setLabelBackgroundColor(Color.parseColor("#ffffff"));
                        //image.setLabelBackgroundAlpha(80);
                    }

                    Picasso.get()
                            .load(item.getImagen())
                            .resize(150, 150)
                            .centerCrop()
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(image);

                    galleryDaily.addView(view);
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
