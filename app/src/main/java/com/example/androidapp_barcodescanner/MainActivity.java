package com.example.androidapp_barcodescanner;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    File file;

    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;

    Button scan;
    ImageView imageView;
    TextView textView;

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    FirebaseVisionImage image;
    Task<List<FirebaseVisionBarcode>> result;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.barcode);
        imageView.setImageBitmap(bitmap);
        textView = findViewById(R.id.textView);

        file = new File(this.getFilesDir(),"Barcodes");

        if(!file.exists()){
            try {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{}");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();

        detector = FirebaseVision.getInstance().getVisionBarcodeDetector();

        image = FirebaseVisionImage.fromBitmap(bitmap);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                                if(firebaseVisionBarcodes.size()!=0) {
                                    JSONObject newBarcode = new JSONObject();
                                    JSONArray newBarcodeDetails = new JSONArray();
                                    newBarcodeDetails.put(Calendar.getInstance().getTime());
                                    newBarcodeDetails.put(firebaseVisionBarcodes.get(0).getValueType());

                                    try {
                                        newBarcode.put(firebaseVisionBarcodes.get(0).getRawValue(), newBarcodeDetails);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    textView.setText("Rawvalue " + firebaseVisionBarcodes.get(0).getRawValue() + "\n Calendar " + Calendar.getInstance().getTime() + "\n ValueType " + firebaseVisionBarcodes.get(0).getValueType());
                                    Log.i("MainActivity", "Rawvalue " + firebaseVisionBarcodes.get(0).getRawValue() + "\n Calendar " + Calendar.getInstance().getTime() + "\n ValueType " + firebaseVisionBarcodes.get(0).getValueType());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });


    }

}
