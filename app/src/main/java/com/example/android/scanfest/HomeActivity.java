package com.example.android.scanfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android.scanfest.Models.Users;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private static final String TAG = "HomeActivity";
    TextView txtReset,txtSignUp;
    EditText user_fullname, user_emmail, user_password, user_phone, user_address,input_email,input_password;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtSignUp = findViewById(R.id.input_register);
        txtReset = findViewById(R.id.input_forgetpassword);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);

        btnLogin = findViewById(R.id.btnLogin);

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                final String email = input_email.getText().toString().trim();
                String password = input_password.getText().toString().trim();


                if(email.isEmpty()){
                    input_email.setError("Please Type a valid Email");
                    input_email.requestFocus();
                    return;
                }

                if (password.isEmpty()){
                    input_password.setError("Password cannot be Empty");
                    input_password.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful()){
                            sendUserToMainActivity();

                        }else{
                            Toast.makeText(getApplicationContext(),"Sorry incorrect Password or email",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View register_user = LayoutInflater.from(HomeActivity.this)
                        .inflate(R.layout.register_user, null);
                new MaterialStyledDialog.Builder(HomeActivity.this)
                        .setIcon(R.drawable.ic_person)
                        .setTitle("Create Account")
                        .setDescription("Please Fill all the Fields")
                        .setCustomView(register_user)
                        .setNegativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("CREATE")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                user_fullname = (EditText)register_user.findViewById(R.id.user_fullname);
                                user_emmail = (EditText)register_user.findViewById(R.id.user_email);
                                user_password = (EditText)register_user.findViewById(R.id.user_password);
                                user_phone = (EditText)register_user.findViewById(R.id.user_phone);
                                user_address = (EditText)register_user.findViewById(R.id.user_address);

                                if(TextUtils.isEmpty(user_fullname.getText().toString())){
                                    user_fullname.setError("Please Provide your fullname");
                                    user_fullname.requestFocus();
                                    return;
                                }
                                if(TextUtils.isEmpty(user_emmail.getText().toString())){
                                    user_emmail.setError("Please Provide your Valid Email ");
                                    user_emmail.requestFocus();
                                    return;
                                }
                                if(TextUtils.isEmpty(user_password.getText().toString())){
                                    user_password.setError("Please Password cannot be empty");
                                    user_password.requestFocus();
                                    return;
                                }
                                if(TextUtils.isEmpty(user_phone.getText().toString())){
                                    user_phone.setError("Please Provide your phone number");
                                    user_phone.requestFocus();
                                    return;
                                }
                                if(TextUtils.isEmpty(user_address.getText().toString())){
                                    user_address.setError("Please Provide your address");
                                    user_address.requestFocus();
                                    return;
                                }

                                registerUser(user_fullname.getText().toString().trim(),
                                        user_emmail.getText().toString().trim(),
                                        user_password.getText().toString().trim(),
                                        user_phone.getText().toString().trim(),
                                        user_address.getText().toString().trim());
                            }
                        }).show();
            }
        });


    }

    private void showDialog() {
        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait, while we are Authenticating your Account...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }

    private void registerUser(final String name, final String email, String password, final String phone, final String address) {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please Wait while we are Creating Your Account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Users users = new Users(
                                name,
                                email,
                                phone,
                                address
                        );
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    sendUserToMainActivity();
                                }else {
                                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
