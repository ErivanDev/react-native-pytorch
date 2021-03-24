package com.reactlibrary;

import android.net.Uri;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays; 

import java.lang.Math;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class PytorchModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private org.pytorch.Module module;

    // VÊ ESSA PARTE
    static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};

    public static String[] IMAGENET_CLASSES = new String[]{
        "Aedes albopictus",
        "Aedes vexans",
        "Anopheles sinensis",
        "Culex pipiens",
        "Culex tritaeniorhynchus",
        "Non vectors"
    };

    public PytorchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.module = null;
    }

    @Override
    public String getName() {
        return "Pytorch";
    }

    @ReactMethod
    public void loadModel(String stringArgument, int numberArgument, Callback callback) {
        Bitmap bitmap = null;
        String pathModel = null;

        try {
            // bitmap = BitmapFactory.decodeStream(this.reactContext.getAssets().open("image.jpg"));
            pathModel = assetFilePath(this.reactContext, "dense.pt");
            this.module = Module.load( pathModel );
        } catch (Exception e) {
            //TODO: handle exception
        }

        callback.invoke("Model navire Loaded: " + this.module.toString());
    }

    @ReactMethod
    public void predict(String imagePath, int numberArgument, Callback callback) {
        Bitmap bitmap = null;
        String pathModel = null;
        //org.pytorch.Module module = null;

        try {
            //bitmap = BitmapFactory.decodeStream(this.reactContext.getAssets().open("image.jpg"));
            //pathModel = assetFilePath(this.reactContext, "model.pt");
            //module = Module.load( pathModel );
            Uri uri = Uri.parse( imagePath );
            ContentResolver contentResolver = this.reactContext.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream( uri );
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            //TODO: handle exception
        }

        // preparing input tensor
        // final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
        // TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
        NO_MEAN_RGB, NO_STD_RGB);

        // running the model
        final Tensor outputTensor = this.module.forward(IValue.from(inputTensor)).toTensor();

        // getting tensor content as java array of floats
        final float[] scores = softMax( outputTensor.getDataAsFloatArray() );

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }

        String className = this.IMAGENET_CLASSES[maxScoreIdx];

        // TODO: Implement some actually useful functionality
        callback.invoke("Ei Erivan, Mosquito: " + className + "\n" + " Probabilidade: " + maxScore);
    }

    public float[] softMax(float[] arr) {
        int length = arr.length;
        float[] copy = arr.clone();
        Arrays.sort(copy);
        float max = copy[length - 1];

        float[] exp_a = new float[arr.length];
        for(int i = 0; i < length; i++) {
            exp_a[i] = (float) Math.pow(Math.E, arr[i] - max);
        }

        float sum = 0;
        for(int i = 0; i < length; i++) {
            sum += exp_a[i];
        }

        float[] result = new float[length];
        for(int i = 0; i < length; i++) {
            result[i] = exp_a[i] / sum;
        }
        
        return result;
    }

    /**
   * Copies specified asset to the file in /files app directory and returns this
   * file absolute path.
   *
   * @return absolute file path
   */

    public static String assetFilePath(ReactApplicationContext context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }      
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            // Log.e(TAG, "Error process asset " + assetName + " to file path");
        }

        return null;
    }
}
