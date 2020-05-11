package com.example.android.scanfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.android.scanfest.Models.Child;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    private DatabaseReference mDatabaseReference,mChildRef;

    TextView displayData;
    private Child childSelect;
    private OkHttpClient mClient = new OkHttpClient();
    private Context mContext;
    private String nameOfStudent, todayDate , todayTime, studentPushKey;
    String linkUrl="http://6b99f307.ngrok.io -> http://localhost:4567";
    Child finalChildSelect;

    public ScanActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


       // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
       // StrictMode.setThreadPolicy(policy);

        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this,scannerView);

        displayData = findViewById(R.id.result_view);
        mContext = getApplicationContext();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("StudentRecords");
        mChildRef = FirebaseDatabase.getInstance().getReference().child("StudentInfo");


        Intent intent = getIntent();
        Child childSelect = (Child)intent.getSerializableExtra("ChildKey");
        if(childSelect == null){
            childSelect = new Child();
        }
        this.childSelect = childSelect;

         finalChildSelect = childSelect;
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayData.setText(result.getText());
                        //displayData.setText(finalChildSelect.getNameOfStudent());

                        if(displayData != null){
                           // scanBarcode();
                            saveChildRecord();

                            //try {
                               // post(getApplicationContext().getString(Integer.parseInt(linkUrl)), new  Callback(){

                                  //  @Override
                                   // public void onFailure(Call call, IOException e) {
                                    //    Toast.makeText(ScanActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                     //   e.printStackTrace();
                                   // }

                                  //  @Override
                                  //  public void onResponse(Call call, Response response) throws IOException {
                                   //     runOnUiThread(new Runnable() {
                                   //         @Override
                                    //        public void run() {

                                      //          Toast.makeText(getApplicationContext(),"SMS Sent!",Toast.LENGTH_SHORT).show();
                                       //     }
                                       // });
                                   // }
                               // });
                            //} catch (IOException e) {
                            //    e.printStackTrace();
                           // }

                        }
                    }
                });
            }
        });

       // scannerView.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View view) {
         //       codeScanner.startPreview();
        //    }
       // });
    }

    private void saveChildRecord() {



        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        todayDate = currentDate.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        todayTime = currentTime.format(calForTime.getTime());

        nameOfStudent = finalChildSelect.getNameOfStudent();
        studentPushKey = finalChildSelect.getId();

        Calendar c = Calendar.getInstance();
        final int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        Query query = mChildRef.equalTo(studentPushKey);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.child(todayDate).exists()){
                            if(timeOfDay >= 0 && timeOfDay < 12){
                                HashMap postMap = new HashMap();
                                postMap.put("id", studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeIn", todayTime);
                                postMap.put("timeOut", "");
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Morning", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else if(timeOfDay >= 12 && timeOfDay < 16){
                                HashMap postMap = new HashMap();
                                postMap.put("id", studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Afternoon", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }else if(timeOfDay >= 16 && timeOfDay < 21){
                                HashMap postMap = new HashMap();
                                postMap.put("id",studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Evening", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else if(timeOfDay >= 21 && timeOfDay < 24){
                                HashMap postMap = new HashMap();
                                postMap.put("id",studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Night", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        }else {
                            if(timeOfDay >= 0 && timeOfDay < 12){
                                HashMap postMap = new HashMap();
                                postMap.put("id", studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeIn", todayTime);
                                postMap.put("timeOut", "");
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Morning", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else if(timeOfDay >= 12 && timeOfDay < 16){
                                HashMap postMap = new HashMap();
                                postMap.put("id", studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Afternoon", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }else if(timeOfDay >= 16 && timeOfDay < 21){
                                HashMap postMap = new HashMap();
                                postMap.put("id",studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Evening", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else if(timeOfDay >= 21 && timeOfDay < 24){
                                HashMap postMap = new HashMap();
                                postMap.put("id",studentPushKey);
                                postMap.put("nameOfStudent", nameOfStudent);
                                postMap.put("timeOut", todayTime);
                                postMap.put("date", todayDate);
                                mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScanActivity.this, "Good Night", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        }
                    }
                }else {
                    if(timeOfDay >= 0 && timeOfDay < 12){
                        HashMap postMap = new HashMap();
                        postMap.put("id", studentPushKey);
                        postMap.put("nameOfStudent", nameOfStudent);
                        postMap.put("timeIn", todayTime);
                        postMap.put("timeOut", "");
                        postMap.put("date", todayDate);
                        mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ScanActivity.this, "Good Morning", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else if(timeOfDay >= 12 && timeOfDay < 16){
                        HashMap postMap = new HashMap();
                        postMap.put("id", studentPushKey);
                        postMap.put("nameOfStudent", nameOfStudent);
                        postMap.put("timeOut", todayTime);
                        postMap.put("date", todayDate);
                        mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ScanActivity.this, "Good Afternoon", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else if(timeOfDay >= 16 && timeOfDay < 21){
                        HashMap postMap = new HashMap();
                        postMap.put("id",studentPushKey);
                        postMap.put("nameOfStudent", nameOfStudent);
                        postMap.put("timeOut", todayTime);
                        postMap.put("date", todayDate);
                        mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ScanActivity.this, "Good Evening", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else if(timeOfDay >= 21 && timeOfDay < 24){
                        HashMap postMap = new HashMap();
                        postMap.put("id",studentPushKey);
                        postMap.put("nameOfStudent", nameOfStudent);
                        postMap.put("timeOut", todayTime);
                        postMap.put("date", todayDate);
                        mChildRef.child(studentPushKey).child(todayDate).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ScanActivity.this, "Good Night", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    Call post(String url, Callback callback) throws IOException{
        RequestBody formBody = new FormBody.Builder()
                .add("To", "+2349061768923")
                .add("Body","Am testing" )
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call response = mClient.newCall(request);
        response.enqueue(callback);
        return response;
    }

    private void scanBarcode() {
        try {
            // Construct data
            String apiKey = "apikey=" + "3dRo1S39lok-NuZDCLIxEgW0S1ANKdoZaYAwVtn5K7";
            String message = "&message=" + "Your Child was in School";
            String sender = "&sender=" + "ScanFest";
            String numbers = "&numbers=" + "+2348065117781";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            Toast.makeText(ScanActivity.this, "Sms sent Successfully", Toast.LENGTH_SHORT).show();

            //return stringBuffer.toString();
        } catch (Exception e) {
            //System.out.println("Error SMS "+e);
            //return "Error "+e;
            Toast.makeText(ScanActivity.this, "Error Sms" + e, Toast.LENGTH_SHORT).show();
            Toast.makeText(ScanActivity.this, "Error" +e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestPermission();
    }

    private void requestPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(ScanActivity.this, "Camera Permission is required", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.cancelPermissionRequest();

            }
        }).check();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}
