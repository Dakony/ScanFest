package com.example.android.scanfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.scanfest.Models.Api;
import com.example.android.scanfest.Models.Child;
import com.example.android.scanfest.Models.ChildInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class InfoActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private Child childSelect;
    private TextView textView;
    String id;
    Child finalChildSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        textView = findViewById(R.id.test);

        mRef = FirebaseDatabase.getInstance().getReference().child("StudentInfo");

        Intent intent = getIntent();
        Child childSelect = (Child)intent.getSerializableExtra("Key");
        if(childSelect == null){
            childSelect = new Child();
        }
        this.childSelect = childSelect;

        finalChildSelect = childSelect;

        loadUsers();

       // sendSms();
    }

    private void loadUsers() {

        id = finalChildSelect.getId();
        Query query = mRef.orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.child("01-May-2020").exists()){
                            ChildInfo child = ds.getValue(ChildInfo.class);
                            String name = child.getNameOfStudent();
                            textView.setText(name);

                        }
                    }
                }else {
                    Toast.makeText(InfoActivity.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendSms() {
        String phoneNumber = "+2347067721573";
        String message = "Dakony trying this text Api";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ancient-spire-19737.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);
        Call<ResponseBody> call =  api.sendSms(phoneNumber,message);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               try {
                   String s = response.body().string();
                    Toast.makeText(InfoActivity.this,s , Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


}
