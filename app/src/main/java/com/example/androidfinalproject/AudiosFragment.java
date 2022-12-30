package com.example.androidfinalproject;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudiosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudiosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String ARGPL = "PLAY";
    private boolean recorde = true;
    TextView textTim;
    MediaRecorder recorder;
    ArrayList<String> audios = new ArrayList<>();
    Thread thread;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isInterrupted = false;
    private boolean isFirstTime = false;

    public AudiosFragment() {
        // Required empty public constructor
        recorder = new MediaRecorder();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     *
     * 
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudiosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudiosFragment newInstance(String param1, String param2) {
        AudiosFragment fragment = new AudiosFragment();
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
    private String formatTime(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, seconds);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(calendar.getTime());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_audios, container, false);

        getMyAudios();
        AtomicInteger seconds = new AtomicInteger();
        AtomicReference<Thread> thread0 = new AtomicReference<>(new Thread(() -> {
            seconds.set(0);
            while (true) {

                textTim.setText(formatTime(seconds.getAndIncrement()) + "" );
                Log.d("time", String.valueOf(seconds.get()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }));
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_dialo_play_audio);
        ProgressBar bar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        bar.setProgress(0);
        MediaPlayer mediaPlayer = new MediaPlayer();

        dialog.setOwnerActivity(getActivity());
        Handler handler = new Handler();
        
        textTim = (TextView) view.findViewById(R.id.timeRecord);
        ((ListView) view.findViewById(R.id.allaudios)).setAdapter(new AudioAdapter(getContext(), audios));
        dialog.setOnDismissListener(dialogInterface -> {
            isFirstTime = false;
            mediaPlayer.stop();
        });

        AtomicReference<Thread> thread = new AtomicReference<>();
        ((ListView) view.findViewById(R.id.allaudios)).setOnItemClickListener((adapterView, view1, j, l) -> {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            ((TextView) dialog.findViewById(R.id.mname)).setText(new File(audios.get(j)).getName());
            try {
                mediaPlayer.setDataSource(audios.get(j));
                mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(!dialog.isShowing())
                dialog.show();
            thread.set(new Thread(new Runnable() {
                @Override
                public void run() {
                    bar.setMax(mediaPlayer.getDuration());
                    bar.setProgress(0);
                    while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
                        bar.setProgress(mediaPlayer.getCurrentPosition());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                            break;
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ARGPL = "PLAY";
                            dialog.dismiss();
                            ((MaterialButton) dialog.findViewById(R.id.start)).setIcon(getResources().getDrawable(R.drawable.play));
                        }
                    });
                }
            }));
        });
        dialog.findViewById(R.id.start).setOnClickListener(view2 -> {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            if(ARGPL.equals("PLAY")){
                ARGPL = "PAUSE";
                Log.d("State", ARGPL);
                mediaPlayer.start();
                thread.set(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bar.setMax(mediaPlayer.getDuration());
                        bar.setProgress(mediaPlayer.getCurrentPosition());
                        while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration()) {
                            bar.setProgress(mediaPlayer.getCurrentPosition());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                                break;
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ARGPL = "PLAY";
                                dialog.dismiss();
                                ((MaterialButton) dialog.findViewById(R.id.start)).setIcon(getResources().getDrawable(R.drawable.play));
                            }
                        });
                    }
                }));
                thread.get().start();
                ((MaterialButton)dialog.findViewById(R.id.start)).setIcon(getResources().getDrawable(R.drawable.pause));
            }else{
                ARGPL= "PLAY";
                Log.d("State", ARGPL);
                //mediaPlayer.stop();
                mediaPlayer.pause();
                thread.get().interrupt();
                isInterrupted = true;
                ((MaterialButton)dialog.findViewById(R.id.start)).setIcon(getResources().getDrawable(R.drawable.play));
            }
        });
        ((ListView) view.findViewById(R.id.allaudios)).setOnItemLongClickListener((adapterView, view1, i, l) -> {
            //Toast.makeText(getContext(), "Long presse", Toast.LENGTH_LONG).show();
            return true;
        });
        ((FloatingActionButton) view.findViewById(R.id.recorde)).setOnClickListener(view1 -> {
            Toast.makeText(getContext(), "Recording ... ", Toast.LENGTH_LONG).show();
            String path = Environment.getExternalStorageDirectory().toString()+"/Audio";
            File dir  = new File(Environment.getExternalStorageDirectory().toString(), "Audio");
            if(!dir.exists()){
                dir.mkdir();
            }

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            int last = getLast();
            ((FloatingActionButton) view.findViewById(R.id.recorde)).setEnabled(false);
            ((FloatingActionButton) view.findViewById(R.id.stoprecorde)).setEnabled(true);

            Log.d("last", last+"");
                recorder.setOutputFile(new File(path + "/" + (String.format("%0" + 10 + "d", (last+1))) +".mp3").getAbsolutePath());
                Log.d("pathAudio", path+"aud.mp3");
                getMyAudios();
                recorde = true;
            try {
                Thread thread00 = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        int seconds = 0;
                        while (recorde==true) {
                            textTim.setText(formatTime(seconds++) + "" );
                            Log.d("time", String.valueOf(seconds));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                recorder.prepare();
                recorder.start();
                thread00.start();

                //thread.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ((FloatingActionButton) view.findViewById(R.id.stoprecorde)).setOnClickListener(view1 -> {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            recorder.stop();
            recorder.release();
            getMyAudios();
            recorde = false;
            recorde = false;
            textTim.setText("Tap to start recording ");
            //thread00.interrupt();
            //.get().interrupt();
            ((ListView) view.findViewById(R.id.allaudios)).setAdapter(new AudioAdapter(getContext(), audios));
            ((FloatingActionButton) view.findViewById(R.id.stoprecorde)).setEnabled(false);
            ((FloatingActionButton) view.findViewById(R.id.recorde)).setEnabled(true);
        });
        return  view;
    }


    private int getLast() {
        getMyAudios();
        if(audios.size()==0)
            return 0;
        return Integer.valueOf((new File(audios.get(0)).getName().replace(".mp3", "")));
    }

    private ArrayList<String> getMyAudios() {
        audios.removeAll(audios);
        audios = new ArrayList<>();
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
        String path = Environment.getExternalStorageDirectory().toString()+"/Audio";
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
                if (files[i].isFile() && (files[i].getName().endsWith(".mp3") )) {
                    audios.add(path + "/"+files[i].getName());
                    //Log.d("renamed "+audios.size(), audios.get(audios.size()-1)+"");
                }
            }
            Collections.sort(audios, Collections.reverseOrder());

        }
        return audios;
    }
}