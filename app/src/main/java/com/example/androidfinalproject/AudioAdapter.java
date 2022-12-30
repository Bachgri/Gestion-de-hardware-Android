package com.example.androidfinalproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class AudioAdapter extends BaseAdapter {
    ArrayList<String> audios;
    Context context;
    public AudioAdapter(Context context, ArrayList<String> audios) {

        this.audios = audios;
        this.context = context;

    }

    @Override
    public int getCount() {
        return audios.size();
    }

    @Override
    public String getItem(int i) {
        return new File(audios.get(i)).getName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        textView.setText("Audio_"+getItem(i));
        textView.setTextSize(21);
        textView.setPadding(20, 20, 20, 10);
        return  textView;
    }
}
