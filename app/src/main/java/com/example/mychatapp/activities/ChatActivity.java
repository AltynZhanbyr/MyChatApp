    package com.example.mychatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mychatapp.R;
import com.example.mychatapp.adapters.ChatAdapter;
import com.example.mychatapp.model.ModelMessage;
import com.example.mychatapp.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    public class ChatActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "messages_channel";
    private static final String CHANNEL_NAME="messages_channel_name";

    private RecyclerView recyclerView;
    private ChatAdapter.OnMessageClickListener messageClickListener;
    private EditText messageText;
    private ChatAdapter adapter;
    private ProgressBar progressBar;
    private ImageView sendImage;
    private Button sendMessage;
    private LinearLayoutManager manager;

    private List<ModelMessage> messageList=new ArrayList<>();

    private String userName;
    private String userKey;
    private String senderName;
    private String receiverName;
    private String recipientID;
    private boolean isNewUser;

    private static boolean activityState=false;

    private final static int REQUEST_IMAGE_CODE=101;
    private static int NOTIFY_ID =0;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener messagesChildEventListener;
    private ChildEventListener usersChildEventListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference chatImageStorageReference;
    private FirebaseAuth auth;

    private SharedPreferences chatSharedPreferences;
    private SharedPreferences.Editor editor;

    private Notification notification;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initValues();

        editor.putBoolean("ActivityState",true);
        editor.apply();

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            recipientID=bundle.getString("RecipientID");
            receiverName=bundle.getString("UserName");
        }
        messagesChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelMessage message=snapshot.getValue(ModelMessage.class);
                if(message.getRecipient().equals(recipientID)&&message.getSender().equals(auth.getCurrentUser().getUid())) {
                    message.setMine(true);
                    messageList.add(message);
                    adapter.notifyDataSetChanged();
                }
                if(message.getRecipient().equals(auth.getCurrentUser().getUid())&&message.getSender().equals(recipientID)) {
                    message.setMine(false);
                    messageList.add(message);
                    adapter.notifyDataSetChanged();
                    Handler handler=new Handler(Looper.getMainLooper());
                    ExecutorService executorService= Executors.newSingleThreadExecutor();

//                    if(!chatSharedPreferences.getBoolean("ActivityState",false)){
//                        newMessageNotification(message.getName(),message.getText());
//                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                if(modelUser.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    userName=modelUser.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                if(modelUser.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    userName=modelUser.getName();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        userDatabaseReference.addChildEventListener(usersChildEventListener);
        databaseReference.addChildEventListener(messagesChildEventListener);

        messageText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()>0){
                    sendMessage.setEnabled(true);
                }
                else
                    sendMessage.setEnabled(false);
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });

        messageText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
    }

        @Override
        protected void onStop() {
            super.onStop();

            editor.putBoolean("ActivityState",false);
            editor.apply();
        }

        @Override
        protected void onPause() {
            super.onPause();

            editor.putBoolean("ActivityState",false);
            editor.apply();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            editor.putBoolean("ActivityState",false);
            editor.apply();
        }

        @Override
        protected void onResume() {
            super.onResume();

            setTitle(receiverName);

            adapter.notifyDataSetChanged();
            editor.putBoolean("ActivityState",true);
            editor.apply();
    }
    private void initValues(){

        chatSharedPreferences=getSharedPreferences("ChatSettings", Context.MODE_PRIVATE);
        editor=chatSharedPreferences.edit();

        database=FirebaseDatabase.getInstance();
        databaseReference =database.getReference("messages");
        userDatabaseReference=database.getReference().child("users");
        firebaseStorage=FirebaseStorage.getInstance();
        chatImageStorageReference=firebaseStorage.getReference().child("chat_images");
        auth=FirebaseAuth.getInstance();

        recyclerView=findViewById(R.id.recycler_message_view);

        progressBar=findViewById(R.id.load_progress_bar);
        sendImage=findViewById(R.id.image_button);
        sendMessage=findViewById(R.id.send_button);
        messageText=findViewById(R.id.message_edit_text);
        messageClickListener =new ChatAdapter.OnMessageClickListener() {
            @Override
            public void onMessageClick() {

            }
        };

        adapter=new ChatAdapter(this,messageList);
        manager=new LinearLayoutManager(this);

        manager.setStackFromEnd(true);
        manager.setReverseLayout(false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);


        progressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        public void onSendMessage(View view) {
            ModelMessage message=new ModelMessage();
            message.setText(messageText.getText().toString());
            message.setName(userName);
            message.setSender(auth.getCurrentUser().getUid());
            message.setRecipient(recipientID);
            //message.setImageURL(null);

            databaseReference.push().setValue(message);
            messageText.setText("");
        }

        public void onSendImageMessage(View view) {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
            startActivityForResult(Intent.createChooser(intent,"Choose an image"),REQUEST_IMAGE_CODE);
        }

        @SuppressLint("MissingSuperCall")
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if(requestCode==REQUEST_IMAGE_CODE && resultCode==RESULT_OK){
                    Uri uri=data.getData();

                    StorageReference imageReference=chatImageStorageReference.child(uri.getLastPathSegment());

                    UploadTask uploadTask=imageReference.putFile(uri);

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

                                ModelMessage message=new ModelMessage();
                                message.setName(userName);
                                message.setImageURL(downloadUri.toString());
                                message.setSender(auth.getCurrentUser().getUid());
                                message.setRecipient(recipientID);
                                //message.setText(null);

                                databaseReference.push().setValue(message);
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });}
    }
    private void newMessageNotification(String name,String message){
        Intent notificationIntent =new Intent(this,ChatActivity.class);
        notificationIntent.putExtra("RecipientID",recipientID);
        notificationIntent.putExtra("UserName",receiverName);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.MessagingStyle messagingStyle =
                    new NotificationCompat.MessagingStyle(name).addMessage(message,System.currentTimeMillis(),name);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_chat_24);
        builder.setContentText(message);
        builder.setContentTitle(name);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setStyle(messagingStyle);
        builder.setOngoing(false);
        builder.setContentIntent(pendingIntent);

        notification=builder.build();

        notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFY_ID,notification);
    }
    }