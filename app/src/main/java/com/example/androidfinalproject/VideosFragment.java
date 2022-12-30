package com.example.androidfinalproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int VIDEO_CAPTURE = 101;
    private String ARGPV = "PAUSE";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    VideoView videoView;
    Dialog dialog;
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videos";
    private ArrayList<String> videos = new ArrayList<>();
    GridView gridView;
    public VideosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideosFragment newInstance(String param1, String param2) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_videos, container, false);
        gridView = (GridView) view.findViewById(R.id.listvideo);
        getMyVideos();
        gridView.setAdapter(new VideoAdapter(getContext(), videos));
        gridView.setOnItemClickListener((adapterView, view1, i, l) -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.video_latoyt);
            dialog.show();
            ((FloatingActionButton)dialog.findViewById(R.id.controleVideo)).setOnClickListener(v -> {
                if(ARGPV.compareTo("PAUSE")==0){
                    //Toast.makeText(getContext(), "Video Paused", Toast.LENGTH_LONG).show();
                    ARGPV = "PLAY";
                    videoView.start();
                    ((FloatingActionButton)dialog.findViewById(R.id.controleVideo)).setImageDrawable(getResources().getDrawable(R.drawable.play));
                }else{
                    //Toast.makeText(getContext(), "Video Played", Toast.LENGTH_LONG).show();
                    ARGPV = "PAUSE";
                    if(videoView.isPlaying())
                        videoView.pause();
                    videoView.pause();
                    ((FloatingActionButton)dialog.findViewById(R.id.controleVideo)).setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }
            });
            videoView = dialog.findViewById(R.id.video);
            videoView.setVideoURI(Uri.parse(videos.get(i)));
            //videoView.setRotation(90);
            videoView.setMediaController(new MediaController(getContext()));
        });
        Button btn = view.findViewById(R.id.recordNew);
        btn.setOnClickListener(view1 -> {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, VIDEO_CAPTURE);
        });
        //videoView = (VideoView) view.findViewById(R.id.LastVideo);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE ) {
            Uri videoUri = intent.getData();
            // Set up the file path for the new video file
            File dir  = new File(Environment.getExternalStorageDirectory().toString(), "Videos");
            if(!dir.exists()){
                dir.mkdir();
            }
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videos/"+ getLastIndex() + ".mp4";
            // Copy the video file to the new location
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(videoUri);
                OutputStream outputStream = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                getMyVideos();
                gridView.setAdapter(new VideoAdapter(getContext(), videos));
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            Uri videoUri = Uri.parse(intent.getData().toString());
            File file = new File(String.valueOf(videoUri));
            file.renameTo(new File(filePath + "/"+file.getName()));
            MediaStore.Video.Media.getContentUri(String.valueOf(videoUri));
            Log.d("videoUri0",""+ file.getAbsolutePath());
*/
        }
    }

    private String getLastIndex() {
        getMyVideos();
        if(videos.size()==0)
            return String.format("%0" + 10 + "d", 1);
        return String.format("%0" + 10 + "d", (Integer.valueOf((new File(videos.get(0)).getName().replace(".mp4", "")))+1))  + "";
    }


    private ArrayList<String> getMyVideos() {
        videos.removeAll(videos);
        videos = new ArrayList<>();
        Uri uri ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }else{
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        Log.d("URI0", ""+uri);
        File dir = new File(String.valueOf(MediaStore.Images.Media.getContentUri(String.valueOf(uri))));
        try {
            Log.d("fils", dir.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory().toString()+"/Videos";
        Log.d("Audios", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        int n=0;
        if(files==null) {
            files = new File[0];
            n=0;
        }else{
            n=files.length;;
        }
        //Log.d("Audios", "Size: "+ files.length);
        if(n!=0){
            for (int i = 0; i < n; i++) {
                if (files[i].isFile() && (files[i].getName().endsWith(".mp4") )) {
                    videos.add(path + "/"+files[i].getName());
                    Log.d("renamed "+videos.size(), videos.get(i)+"");
                }
            }
            Collections.sort(videos, Collections.reverseOrder());
        }
        return videos;
    }
}