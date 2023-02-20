package com.example.mychatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychatapp.R;
import com.example.mychatapp.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {
    
    private FirebaseDatabase firebaseDatabase;;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference userDatabaseReference;
    private StorageReference userImageStorageReference;
    private FirebaseAuth auth;

    private final static int REQUEST_IMAGE_CODE=101;

    private String userKey;
    private String userImageURL ="";

    private ImageView imageView;
    private EditText userName;

    private ModelUser user=new ModelUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initValues();

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            user=(ModelUser)bundle.getSerializable(ModelUser.class.getSimpleName());
            userName.setText(user.getName());
            if(user.getUserAvatar().trim().length()>0) {
                Glide.with(this).load(user.getUserAvatar()).into(imageView);
                userImageURL=user.getUserAvatar();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initValues(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        userDatabaseReference=firebaseDatabase.getReference().child("users");
        userImageStorageReference=firebaseStorage.getReference().child("account_images");
        auth= FirebaseAuth.getInstance();

        imageView=findViewById(R.id.user_settings_image);
        userName=findViewById(R.id.user_enter_name);
    }
    public void setImage(View view) {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Choose an image"),REQUEST_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE && resultCode==RESULT_OK){
            Uri imageUri=data.getData();

            StorageReference imageReference=userImageStorageReference.child(imageUri.getLastPathSegment());

            UploadTask uploadTask=imageReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        userImageURL =downloadUri.toString();
                        Glide.with(getApplicationContext()).load(downloadUri).into(imageView);
                        //message.setText(null);
                    } else {
                        // Handle failures
                        // ...
                    }

                }
            });
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);

        menuInflater.inflate(R.menu.settings_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_settings_item:
                save();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        userDatabaseReference.orderByChild("id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                            userKey = childSnapshot.getKey();
                        }
                        userDatabaseReference.child(userKey).child("userAvatar").setValue(userImageURL);
                        userDatabaseReference.child(userKey).child("name").setValue(userName.getText().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}