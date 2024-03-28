package com.example.iseellphone_object_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iseellphone_object_detection.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    // Used to load the 'iseellphone_object_detection' library on application startup.
    static {
        System.loadLibrary("iseellphone_object_detection");
    }

    private ActivityMainBinding binding;
    public static final int REQUEST_CAMERA = 100;
    private SurfaceView cameraView;
    private Yolov8Ncnn yolov8ncnn = new Yolov8Ncnn();
    private int facing = 1;
    private Spinner spinnerModel;
    private Spinner spinnerCPUGPU;
    private int current_model = 0;
    private int current_cpugpu = 0;
    private static TextView tv;
    private static Button btn;
    private static Button switchcam;
    public static Context context;
    private TextToSpeech textToSpeech;
    private Handler handler;
    private Runnable runnable;

    public static native String[] getArrayFromCpp();
    public static native int[] getloc();
    public static native int[] getdis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isScreenOn) {
                    speak();
                    // Post the runnable again after 30 seconds

                }
//                speak();
                handler.postDelayed(this, 8000); // 30,000 milliseconds = 30 seconds
            }
        };

        // Start the initial runnable with a delay
        handler.postDelayed(runnable, 8000);

        cameraView = (SurfaceView) findViewById(R.id.cameraview);
        cameraView.getHolder().setFormat(PixelFormat.RGBA_8888);
        cameraView.getHolder().addCallback(this);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set language (optional)
                textToSpeech.setLanguage(Locale.US);

                // Add UtteranceProgressListener for callback (optional)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            // Called when TTS starts
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            // Called when TTS finishes
                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Called on TTS error
                        }
                    });
                }
            } else {

            }
        });
        switchcam = (Button) findViewById(R.id.Camera_btn);
        switchcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int new_facing = 1 - facing;

                yolov8ncnn.closeCamera();

                yolov8ncnn.openCamera(new_facing);

                facing = new_facing;
            }
        });
//        btn = (Button) findViewById(R.id.btn);
//        btn.setOnClickListener(view -> {
//            String[] resultArray = getArrayFromCpp();
//            // Toast the values
//            speak();
//        });

        reload();

        // Example of a call to a native method
       // TextView tv = binding.sampleText;
       // tv.setText(stringFromJNI());

    }


    public void speak(){
        String[] resultArray = getArrayFromCpp();
        int[] resultloc = getloc();
        int[] resultdis=getdis();
        int j = resultArray.length;

        // Toast the values
       // StringBuilder message = new StringBuilder("I See ");
       // for (String value : resultArray) {
      //      message.append(value).append(" ");
      //  }
        //tv.setText( message.toString());
        //textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        // String[] result = {"chair","stair","table"};

        if (resultArray.length==0){
            textToSpeech.speak("No Object Detected ", TextToSpeech.QUEUE_FLUSH, null, null);
        }
        String loc="";
        int dis;

        for (int i = 0; i<j;i++){

            if (resultloc[i]<140){
                loc="going left";
            }
           else if (resultloc[i]>280){
                loc="going right";
            }else {
                loc="going straight";
            }
           int ol;
            switch (resultArray[i]) {
                case "downward handrail":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you Going down handrail detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "downstairs":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you downstairs detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "upstairs":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you upstairs detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "upward handrail":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you Going up handrail detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;

                case "pillar":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you pillar detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;

                case "chair":
                    ol = resultdis[i]+100;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you chair detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "trash can":
                    ol = resultdis[i]+100;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you trash can detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "water fountain":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you water fountain detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "elevator":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you elevator detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "handrail":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you handrail detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "table":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you table detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "person":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you person detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
                case "door":
                    ol = resultdis[i]+150;
                    textToSpeech.speak(loc+" "+ol+"centimeter from you door detected", TextToSpeech.QUEUE_ADD, null, null);
                    break;
               
                // Add more cases for other days as needed
                default:
                    textToSpeech.speak(loc+" "+resultloc[i]+"centimeter from you I detect a "+resultArray[i]+"!", TextToSpeech.QUEUE_ADD, null, null);
                    break;
            }

        }
       // TextView tv = binding.tv;
        //tv.setText(String.valueOf(dis));
    }
    public String[] processInJava() {
        // Your actual implementation here
        String[] resultArray = new String[]{};
        return resultArray;
    }
   // public native String[] process();

    private void reload()
    {
        boolean ret_init = yolov8ncnn.loadModel(getAssets(), current_model, current_cpugpu);
        if (!ret_init)
        {
            Log.e("MainActivity", "yolov8ncnn loadModel failed");
        }
    }

    /**
     * A native method that is implemented by the 'iseellphone_object_detection' native library,
     * which is packaged with this application.
     */
   // public native String stringFromJNI();

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        yolov8ncnn.setOutputWindow(surfaceHolder.getSurface());
        speak();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        yolov8ncnn.openCamera(facing);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        yolov8ncnn.closeCamera();

    }

}