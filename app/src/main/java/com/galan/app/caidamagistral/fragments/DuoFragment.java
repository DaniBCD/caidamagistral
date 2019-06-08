package com.galan.app.caidamagistral.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galan.app.caidamagistral.R;
import com.galan.app.caidamagistral.model.Stats;


/**
 * A simple {@link Fragment} subclass.
 */
public class DuoFragment extends Fragment {


    TextView textVictorias, textTop2, textTop3, textPartidas, textKills, textPuntos, textMinutos, textJugadores;
    TextView textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9;
    Typeface TF;

    public DuoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_duo, container, false);

        textVictorias = v.findViewById(R.id.textVictorias);
        textTop2 = v.findViewById(R.id.textTop2);
        textTop3 = v.findViewById(R.id.textTop3);
        textPartidas = v.findViewById(R.id.textPartidas);
        textKills = v.findViewById(R.id.textKills);
        textPuntos = v.findViewById(R.id.textPuntos);
        textMinutos = v.findViewById(R.id.textMinutos);
        textJugadores = v.findViewById(R.id.textJugadores);

        textView2 = v.findViewById(R.id.textView2);
        textView3 = v.findViewById(R.id.textView3);
        textView4 = v.findViewById(R.id.textView4);
        textView5 = v.findViewById(R.id.textView5);
        textView6 = v.findViewById(R.id.textView6);
        textView7 = v.findViewById(R.id.textView7);
        textView8 = v.findViewById(R.id.textView8);
        textView9 = v.findViewById(R.id.textView9);

        System.out.println("HOLAAAAA TODO CREADO");

        changeFont();

        return v;
    }

    public void changeFont(){
        TF = Typeface.createFromAsset(getActivity().getAssets(), "font/BurbankBigCondensed-Bold.ttf");
        textVictorias.setTypeface(TF);
        textTop2.setTypeface(TF);
        textTop3.setTypeface(TF);
        textPartidas.setTypeface(TF);
        textKills.setTypeface(TF);
        textPuntos.setTypeface(TF);
        textMinutos.setTypeface(TF);
        textJugadores.setTypeface(TF);
        textView2.setTypeface(TF);
        textView3.setTypeface(TF);
        textView4.setTypeface(TF);
        textView5.setTypeface(TF);
        textView6.setTypeface(TF);
        textView7.setTypeface(TF);
        textView8.setTypeface(TF);
        textView9.setTypeface(TF);
    }

    public void setStats(Stats stats){

        textVictorias.setText(""+stats.getTop1());
        textTop2.setText(""+stats.getTop2());
        textTop3.setText(""+stats.getTop3());
        textPartidas.setText(""+stats.getPartidas());
        textKills.setText(""+stats.getKills());
        textPuntos.setText(""+stats.getPuntos());
        textMinutos.setText(""+stats.getMinutos());
        textJugadores.setText(""+stats.getJugadores());

    }

}
