package com.example.android.scanfest;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class GenerateActivity extends AppCompatActivity {
    EditText qrValue;
    Button generateBtn, scanBtn;
    ImageView qrImage;

    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

    private AppCompatActivity activity;

    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        qrValue = findViewById(R.id.qrInput);
        generateBtn = findViewById(R.id.qrGenerate);
        scanBtn = findViewById(R.id.qrScan);
        qrImage = findViewById(R.id.qrPlace);

        activity = this;


        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = qrValue.getText().toString();
                if(data.length() > 0){

                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;


                    qrgEncoder = new QRGEncoder(
                            data, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    //qrgEncoder.setColorBlack(Color.RED);
                    //qrgEncoder.setColorWhite(Color.BLUE);
                    try {
                        bitmap = qrgEncoder.getBitmap();
                        qrImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    if(TextUtils.isEmpty(data))
                        qrValue.setError("Please Provide the name");
                    qrValue.requestFocus();
                }

            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),ScanActivity.class));
            }
        });

       // findViewById(R.id.save_barcode).setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
          //      if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
           //         try {
            //            boolean save = new QRGSaver().save(savePath, qrValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
           //             String result = save ? "Image Saved" : "Image Not Saved";
            //            Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
             //           qrValue.setText(null);
              //      } catch (Exception e) {
               //         e.printStackTrace();
               //     }
              //  } else {
              //      ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
              //  }
           // }
       // });

    }

}
