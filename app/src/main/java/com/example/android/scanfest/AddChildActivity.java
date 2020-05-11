package com.example.android.scanfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.scanfest.Models.Child;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddChildActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;

    EditText studentName,student_Class, StudentRelationship;
    Button mSubmit;
    ArrayAdapter<String> adapter;
    ArrayList<String> schoolAdapterList;
    DatabaseReference mDatabaseference,mChildRef;
    Spinner schoolData;
    String schoolName,id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        loadingBar = new ProgressDialog(this);

        studentName = findViewById(R.id.student_name);
        student_Class = findViewById(R.id.student_class);
        StudentRelationship = findViewById(R.id.relationship);
        mSubmit = findViewById(R.id.btn_submit);
        mDatabaseference = FirebaseDatabase.getInstance().getReference("Schools");
        mChildRef = FirebaseDatabase.getInstance().getReference();

        schoolData = findViewById(R.id.schools);
        schoolAdapterList = new ArrayList<>();
        adapter = new ArrayAdapter<>(AddChildActivity.this,
                android.R.layout.simple_spinner_dropdown_item,schoolAdapterList);

        schoolData.setAdapter(adapter);

        mDatabaseference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item: dataSnapshot.getChildren()){
                    schoolAdapterList.add(item.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
                id = mChildRef.child("StudentRecords").push().getKey();
                String nameOfStudent = studentName.getText().toString().trim();
                String studentClass = student_Class.getText().toString().trim();
                String relationship = StudentRelationship.getText().toString().trim();
                schoolName = schoolData.getSelectedItem().toString();


                if(TextUtils.isEmpty(nameOfStudent)){
                    studentName.setError("Please Provide the name of the student");
                    studentName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(studentClass)){
                    student_Class.setError("Please Provide the name of the student");
                    student_Class.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(relationship)){
                    StudentRelationship.setError("Please Provide the name of the student");
                    StudentRelationship.requestFocus();
                    return;
                }

                Child child = new Child(id,schoolName,nameOfStudent,studentClass,relationship);
                mChildRef.child("StudentRecords").child(id).setValue(child).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            sendUserToMainActivity();

                        }else{
                            Toast.makeText(AddChildActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void displayDialog() {
        loadingBar.setTitle("Saving Details");
        loadingBar.setMessage("Please wait, while we are saving Records");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }
}
