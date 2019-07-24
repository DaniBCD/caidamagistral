package com.galan.app.caidamagistral.adapters;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.galan.app.caidamagistral.R;
import com.galan.app.caidamagistral.model.Challenge;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Challenge>> expandableListDetail;
    private Typeface TF;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<Challenge>> expandableListDetail) {
        TF = Typeface.createFromAsset(context.getAssets(), "font/BurbankBigCondensed-Bold.ttf");
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Challenge desafio = (Challenge) getChild(listPosition, expandedListPosition);
        final String expandedListText = desafio.getNombre();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        TextView textDificultad = convertView.findViewById(R.id.textDificultad);
        TextView textProgreso = convertView.findViewById(R.id.textProgreso);
        TextView textEstrellas = convertView.findViewById(R.id.textEstrellas);

        expandedListTextView.setTypeface(TF);
        textDificultad.setTypeface(TF);
        textProgreso.setTypeface(TF);
        textEstrellas.setTypeface(TF);

        textDificultad.setText(desafio.getDificultad().toUpperCase());
        textProgreso.setText("0/"+desafio.getTotal());
        textEstrellas.setText(""+desafio.getEstrellas());
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);

        listTitleTextView.setTypeface(TF);
        listTitleTextView.setTextSize(20);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
