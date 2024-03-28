package com.example.iseellphone_object_detection;

import static com.example.iseellphone_object_detection.MainActivity.context;

import android.content.res.AssetManager;
import android.view.Surface;
import android.widget.Toast;

public class Yolov8Ncnn
{
    public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
    public native boolean openCamera(int facing);
    public native boolean closeCamera();
    public native boolean setOutputWindow(Surface surface);

    static {
        System.loadLibrary("iseellphone_object_detection");
    }
    public static void checkk(String text){
        Toast.makeText(context, text.toString(), Toast.LENGTH_SHORT).show();
    }
}