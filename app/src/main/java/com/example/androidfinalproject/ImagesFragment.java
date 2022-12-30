package com.example.androidfinalproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView imageView;
    private final  int CAMERA_REQUEST_CODE = 1001;
    GridView gridView;
    private ArrayList<String> images = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImagesFragment newInstance(String param1, String param2) {
        ImagesFragment fragment = new ImagesFragment();
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
        //images  = getMyImages();
    }

    private ArrayList<String> getMyImages() {
        ArrayList<String> uris = new ArrayList<>();
        images.removeAll(images);
        images = new ArrayList<>();
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
        String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && (files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpeg") || files[i].getName().endsWith(".jpg"))) {
                images.add(path + "/"+files[i].getName());
                Log.d("renamed "+images.size(), images.get(images.size()-1)+"");
            }
        }
        Collections.sort(images, Collections.reverseOrder());
        return  images;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_images, container, false);
        Button newPec = (Button) view.findViewById(R.id.newPec);
        //imageView = (ImageView) view.findViewById(R.id.Lastimage);
        newPec.setOnClickListener(view1 -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        });
        getMyImages();
        Log.d("Size hh ", images.size()+"");
        gridView = (GridView) view.findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(getContext(), images));
        gridView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Log.d("clicked ", images.get(i)+"");
            Intent intent = new Intent(getContext(), ShowImageActivity.class);
            intent.putExtra("i", i);
            intent.putExtra("uri", images.get(i));
            intent.putExtra("images", images);
            startActivity(intent);
        });
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyImages();
        gridView.setAdapter(new ImageAdapter(getContext(), images));

    }

    private void saveImage(Bitmap finalBitmap) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            OutputStream out = contentResolver.openOutputStream(Uri.parse(finalBitmap.toString()));
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Objects.requireNonNull(out);
            Toast.makeText(getContext(),  "*Saved*", Toast.LENGTH_LONG).show();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE ) {
            if(resultCode == -1){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //imageView.setImageBitmap(bitmap);
                ContentResolver contentResolver = getActivity().getContentResolver();
                OutputStream out = null;
                Uri images;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                }else{
                    images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "oualidAndroid_"+".jpeg");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
                Uri uri = contentResolver.insert(images, contentValues);
                try {
                    out = contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    Objects.requireNonNull(out);
                    Toast.makeText(getContext(),  "*Saved* to " , Toast.LENGTH_LONG).show();
                    out.flush();
                    out.close();
                    Log.d("URI", uri+"");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),  "*Not Saved*", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*try (FileOutputStream out = new FileOutputStream( Environment.getExternalStorageDirectory().toString()+"/filename")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                    Toast.makeText(getContext(), Environment.getExternalStorageDirectory().toString()+"/filename" +" **", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), Environment.getExternalStorageDirectory().toString()+"/filename.png" +" **", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }*/
            }
        }
    }
}