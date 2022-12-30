package com.example.androidfinalproject;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

public class VideoAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> videos ;
    public VideoAdapter(Context context, ArrayList<String> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public String getItem(int i) {
        return videos.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // get screen with
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        ImageView video = new ImageView(context);
        Uri uri = Uri.fromFile(new File(getItem(i)));
        Glide.with(context).load(uri).thumbnail(0.1f).into(video);
        //video.setForegroundGravity(Gravity.FILL);
        video.setLayoutParams(new GridView.LayoutParams(width/2-10,350));
        video.setVerticalScrollBarEnabled(true);
        video.setVerticalFadingEdgeEnabled(true);
        video.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return video;
    }
}
