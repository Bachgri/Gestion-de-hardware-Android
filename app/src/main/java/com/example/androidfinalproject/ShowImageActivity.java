package com.example.androidfinalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ShowImageActivity extends AppCompatActivity {

    private int i;
    ArrayList<String> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        getIntent().getExtras().getInt("i");
        images = getIntent().getExtras().getStringArrayList("images");
        for (String s: images) {
            Log.d("images ", s);
        }
        i = getIntent().getExtras().getInt("i");
        ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(getIntent().getExtras().getInt("i"))));
        ((Button)findViewById(R.id.next)).setOnClickListener(view -> {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
            i = ((++i)%images.size());
            ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(i)));
        });
        ((Button)findViewById(R.id.prev)).setOnClickListener(view -> {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
            i -=1;
            if(i<0){
                i=images.size()-1;
            }
            ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(i)));
        });
        ((FloatingActionButton) findViewById(R.id.delete)).setOnClickListener(view -> {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            new AlertDialog.Builder(this)
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete the file '" + new File(images.get(images.size()>1? --i : 0)).getName() + "' ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(new File(images.get(i)).delete()){
                                images.remove(i);
                                if(images.size()==0){
                                    finish();
                                }else if(images.size()==1){
                                    ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(0)));
                                }else{
                                    ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(((i++)%images.size()))));
                                }
                                Toast.makeText(getApplicationContext(), "Image deleted succefully", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Unable to delete this image", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
        ((FloatingActionButton) findViewById(R.id.edit)).setOnClickListener(view -> {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("edit");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            input.setText((new File(images.get(i)).getName().replace(".png", "").replace(".jpg", "")));

            builder.setPositiveButton("ok", (dialogInterface, i1) -> {
                File current = new File(images.get(i));
                if(current.renameTo(new File(current.getParent()+"/"+ input.getText()+".jpg"))){
                    getMyImages();
                    ((ImageView) findViewById(R.id.current_image)).setImageURI(Uri.parse(images.get(i)));
                }

            });
            builder.setNegativeButton("cancel", (dialogInterface, i1) -> {
                //dialog.dismiss();
            });
            AlertDialog dialog = builder.create();

            dialog.show();
        });
        ((FloatingActionButton) findViewById(R.id.share)).setOnClickListener(view -> {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file = new File(getApplicationContext().getExternalCacheDir() + "/"+ new File(images.get(i)).getName());
            ImageView curr = (ImageView) findViewById(R.id.current_image);
            curr.setDrawingCacheEnabled(true);
            Bitmap image = curr.getDrawingCache();
            curr.setDrawingCacheEnabled(false);
            Intent shareIntent;
                shareIntent = new Intent(Intent.ACTION_SEND);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                //image.recycle();
                image = BitmapFactory.decodeFile(new File(images.get(i))+"");
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                Log.d("uriToShare", Uri.fromFile(file)+"");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Toast.makeText(getApplicationContext(), "Toasted hh ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Catche hh ", Toast.LENGTH_SHORT).show();
            }
            startActivity(Intent.createChooser(shareIntent, "Share file : " + new File(images.get(i)).getName()));
            // Create the share intent and set the type to image


        });
    }
    private ArrayList<String> getMyImages() {
        ArrayList<String> uris = new ArrayList<>();
        Uri uri ;
        images.removeAll(images);
        images = new ArrayList<>();
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
}