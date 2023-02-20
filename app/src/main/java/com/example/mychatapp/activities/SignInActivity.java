package com.example.mychatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatapp.R;
import com.example.mychatapp.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private final static String TAG="SignUpOrLoginTag";

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userDatabaseReference;

    private EditText email,password,password2,name;
    private Button signUpButton;
    private TextView logInText;

    private boolean loginMode=false;
    private String userName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        userDatabaseReference=database.getReference();

        if (auth.getCurrentUser() != null) {
            auth.fetchSignInMethodsForEmail(auth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if(isNetworkConnected()) {
                        boolean isUser = task.getResult().getSignInMethods().isEmpty();
                        if (!isUser)
                            getIn();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No network connection",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        preferences=getSharedPreferences("UserSettings",MODE_PRIVATE);
        editor=preferences.edit();

        email=findViewById(R.id.email_edit_text);
        password=findViewById(R.id.password_edit_text);
        password2=findViewById(R.id.password2_edit_text);
        name=findViewById(R.id.name_edit_text);
        signUpButton=findViewById(R.id.sign_up_button);
        logInText=findViewById(R.id.sign_in_text);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!loginMode) {
                    if (password.getText().toString().equals(password2.getText().toString())) {
                        if (password.getText().toString().trim().length() > 6)
                            signUpOrLogIn(email.getText().toString().trim(), password.getText().toString().trim());
                        else
                            Toast.makeText(getApplicationContext(), "Length of password should be at least 7!!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Passwords are not equals!!!", Toast.LENGTH_SHORT).show();
                }
                else
                    signUpOrLogIn(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void logIn(View view) {
        ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        if(loginMode){
            loginMode=false;
            signUpButton.setText("Sign up");
            logInText.setText("Or, log in");
            password2.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            layoutParams.topToBottom=password2.getId();
        }
        else{
            loginMode=true;
            signUpButton.setText("Log in");
            logInText.setText("Or, sign up");
            password2.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            layoutParams.topToBottom=password.getId();
        }
        name.setLayoutParams(layoutParams);
    }

    public void signUpOrLogIn(String email,String password) {
                                if(email.trim().length()==0){
                                    Toast.makeText(getApplicationContext(),"Please input your email address",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if (loginMode) {
                                        auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    getIn();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    generateUser(user);
                                    getIn();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        }
    }
    private void getIn(){
        if(isNetworkConnected()){
            Intent intent =new Intent(this,UsersActivity.class);
            intent.putExtra("UserName",userName);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext()
                    ,"No internet connection",Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }

        return false;
    }
    private void generateUser(FirebaseUser user){
        ModelUser modelUser=new ModelUser();
        modelUser.setName(name.getText().toString());
        modelUser.setEmail(user.getEmail());
        modelUser.setId(user.getUid());
        modelUser.setUserAvatar("");
        //modelUser.setOnline(true);

        userName=modelUser.getName();

        editor.putString("Name",userName);
        editor.apply();

        userDatabaseReference.child("users").child(modelUser.getId()).setValue(modelUser);
    }
}