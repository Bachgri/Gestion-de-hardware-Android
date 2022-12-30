package com.example.androidfinalproject;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    ArrayList<String> uris;
    Context context;
    public ImageAdapter(Context context, ArrayList<String> images) {
        uris = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public String getItem(int i) {
        return uris.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView image = new ImageView(context);
        Uri uri = Uri.fromFile(new File(getItem(i)));
        Glide.with(context).load(uri).thumbnail(0.1f).into(image);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setLayoutParams(new GridView.LayoutParams(177,220));
        image.setVerticalScrollBarEnabled(true);
        image.setVerticalFadingEdgeEnabled(true);
        return image;
    }
}
