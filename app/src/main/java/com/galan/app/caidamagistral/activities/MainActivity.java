package com.galan.app.caidamagistral.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.astritveliu.boom.Boom;
import com.galan.app.caidamagistral.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;
import java.util.Random;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    GifImageView mapa;
    //TextView privacy;
    ImageButton boton, botonStats, botonStore;
    Handler handler;
    private AdView mAdView;
    final int numero = new Random().nextInt(21);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapa = findViewById(R.id.mapa);
        boton = findViewById(R.id.button);
        botonStats = findViewById(R.id.buttonStats);
        botonStore = findViewById(R.id.buttonStore);
        mAdView = findViewById(R.id.adView);

        new Boom(boton);
        new Boom(botonStats);
        new Boom(botonStore);
        /*privacy = findViewById(R.id.textView3);

        privacy.setText(
                //Html.fromHtml("<a href=\"https://sites.google.com/view/caida-magistral/privacy-policy\">Política de Privacidad</a>"));
        privacy.setMovementMethod(LinkMovementMethod.getInstance());*/


        MobileAds.initialize(this, "ca-app-pub-6138983841028001~6606303317");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        botonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        botonStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mapa.setImageResource(R.drawable.anim_min);

                handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        switch (numero){
                            case 0:
                                mapa.setImageResource(R.drawable.aldehuela);
                                break;
                            case 1:
                                mapa.setImageResource(R.drawable.aerodromo);
                                break;
                            case 2:
                                mapa.setImageResource(R.drawable.pico);
                                break;
                            case 3:
                                mapa.setImageResource(R.drawable.aterrizaje);
                                break;
                            case 4:
                                mapa.setImageResource(R.drawable.oasis);
                                break;
                            case 5:
                                mapa.setImageResource(R.drawable.latifundio);
                                break;
                            case 6:
                                mapa.setImageResource(R.drawable.tuneles);
                                break;
                            case 7:
                                mapa.setImageResource(R.drawable.senorio);
                                break;
                            case 8:
                                mapa.setImageResource(R.drawable.ribera);
                                break;
                            case 9:
                                mapa.setImageResource(R.drawable.pisos);
                                break;
                            case 10:
                                mapa.setImageResource(R.drawable.comercio);
                                break;
                            case 11:
                                mapa.setImageResource(R.drawable.soto);
                                break;
                            case 12:
                                mapa.setImageResource(R.drawable.socavon);
                                break;
                            case 13:
                                mapa.setImageResource(R.drawable.balsa);
                                break;
                            case 14:
                                mapa.setImageResource(R.drawable.parque);
                                break;
                            case 15:
                                mapa.setImageResource(R.drawable.lomas);
                                break;
                            case 16:
                                mapa.setImageResource(R.drawable.escalones);
                                break;
                            case 17:
                                mapa.setImageResource(R.drawable.albufera);
                                break;
                            case 18:
                                mapa.setImageResource(R.drawable.bloque);
                                break;
                            case 19:
                                mapa.setImageResource(R.drawable.chiringuito);
                                break;
                            case 20:
                                mapa.setImageResource(R.drawable.planta);
                                break;
                            default:
                                mapa.setImageResource(R.drawable.mapa);
                                break;
                        }

                        Intent intent=new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("numero", numero);
                        startActivity(intent);

                        finish();
                    }
                },1500+(new Random().nextInt(3000)));
            }
        });
    }

}
