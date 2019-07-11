package com.galan.app.caidamagistral.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.galan.app.caidamagistral.BuildConfig;
import com.galan.app.caidamagistral.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ResultActivity extends Activity {

    TextView resultado;
    ImageView mapa;
    ImageButton retry, share;
    String[] localizaciones;
    String localizacion, text;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    int numero;
    Bitmap bm;
    OutputStream os;
    Resources res;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        resultado = findViewById(R.id.textView);
        mapa = findViewById(R.id.mapa);
        retry = findViewById(R.id.retrybutton);
        share = findViewById(R.id.sharebutton);

        MobileAds.initialize(this, "ca-app-pub-6138983841028001~6606303317");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6138983841028001/4766484322");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        Intent i = getIntent();
        numero = i.getIntExtra("numero", 1);

        res = getResources();
        localizaciones = res.getStringArray(R.array.localizaciones);

        localizacion = localizaciones[numero];
        resultado.setText(localizacion);

        text = res.getString(R.string.compartir) + localizacion + "!";

        File filesDir = getApplicationContext().getFilesDir();
        final File imageFile = new File(filesDir, "localizacion.png");

        switch (numero){
            case 0:
                mapa.setImageResource(R.drawable.aldehuela);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.aldehuela);
                break;
            case 1:
                mapa.setImageResource(R.drawable.aerodromo);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.aerodromo);
                break;
            case 2:
                mapa.setImageResource(R.drawable.pico);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.pico);
                break;
            case 3:
                mapa.setImageResource(R.drawable.aterrizaje);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.aterrizaje);
                break;
            case 4:
                mapa.setImageResource(R.drawable.oasis);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.oasis);
                break;
            case 5:
                mapa.setImageResource(R.drawable.latifundio);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.latifundio);
                break;
            case 6:
                mapa.setImageResource(R.drawable.tuneles);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.tuneles);
                break;
            case 7:
                mapa.setImageResource(R.drawable.senorio);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.senorio);
                break;
            case 8:
                mapa.setImageResource(R.drawable.ribera);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.ribera);
                break;
            case 9:
                mapa.setImageResource(R.drawable.pisos);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.pisos);
                break;
            case 10:
                mapa.setImageResource(R.drawable.comercio);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.comercio);
                break;
            case 11:
                mapa.setImageResource(R.drawable.soto);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.soto);
                break;
            case 12:
                mapa.setImageResource(R.drawable.socavon);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.socavon);
                break;
            case 13:
                mapa.setImageResource(R.drawable.balsa);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.balsa);
                break;
            case 14:
                mapa.setImageResource(R.drawable.parque);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.parque);
                break;
            case 15:
                mapa.setImageResource(R.drawable.lomas);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.lomas);
                break;
            case 16:
                mapa.setImageResource(R.drawable.escalones);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.escalones);
                break;
            case 17:
                mapa.setImageResource(R.drawable.albufera);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.albufera);
                break;
            case 18:
                mapa.setImageResource(R.drawable.bloque);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.bloque);
                break;
            case 19:
                mapa.setImageResource(R.drawable.chiringuito);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.chiringuito);
                break;
            case 20:
                mapa.setImageResource(R.drawable.planta);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.planta);
                break;
            default:
                mapa.setImageResource(R.drawable.mapa);
                bm = BitmapFactory.decodeResource(ResultActivity.this.getResources(), R.drawable.mapa);
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // DO your work here
                try {
                    os = new FileOutputStream(imageFile);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, os); // 100% quality
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
            }
        }).start();

        Typeface TF = Typeface.createFromAsset(getAssets(), "font/BurbankBigCondensed-Bold.ttf");
        resultado.setTypeface(TF);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Intent intent=new Intent(ResultActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(ResultActivity.this, BuildConfig.APPLICATION_ID, imageFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, res.getString(R.string.textShare)));
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent=new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent intent=new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
