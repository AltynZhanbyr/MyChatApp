package com.example.mychatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mychatapp.R;
import com.example.mychatapp.adapters.UsersAdapter;
import com.example.mychatapp.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener userChildEventListener;

    private List<ModelUser> userList=new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UsersAdapter usersAdapter;
    private ModelUser myUser=null;
    private FirebaseAuth auth;
    private DatabaseReference connectedRef;
    private UsersAdapter.OnUserClickListener onUserClickListener;

    private String userKey;
    private String userName="";

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userName=bundle.getString("UserName");
        }
        auth=FirebaseAuth.getInstance();
        initValues();

    }
    private void initValues(){
        onUserClickListener=new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        };

        database=FirebaseDatabase.getInstance();
        connectedRef = database.getReference(".info/connected");
        usersDatabaseReference=database.getReferenceFromUrl("https://simplechatapp-ec01e-default-rtdb.firebaseio.com/");
        usersAdapter=new UsersAdapter(userList,this,onUserClickListener);
//        if(userChildEventListener==null){
//            userChildEventListener=new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    ModelUser user=snapshot.getValue(ModelUser.class);
//                    if(!user.getId().equals(auth.getCurrentUser().getUid())) {
//                        //user.setUserAvatar(R.drawable.ic_baseline_person_24);
//                        userList.add(user);
//                        usersAdapter.notifyDataSetChanged();
//                    }
//                    else {
//                        myUser = user;
//                    }
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    ModelUser user=snapshot.getValue(ModelUser.class);
//                    if(!user.getId().equals(auth.getCurrentUser().getUid())) {
//                        //user.setUserAvatar(R.drawable.ic_baseline_person_24);
//                        for (ModelUser newUser: userList){
//                            if(newUser.getId()==user.getId())
//                                userList.remove(newUser);
//                        }
//                        userList.add(user);
//                        usersAdapter.notifyDataSetChanged();
//                    }
//                    else{
//                        myUser = user;
//                    }
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };
//        }

       // usersDatabaseReference.addChildEventListener(userChildEventListener);
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(userList.size()>0)
                    userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.child("users").getChildren()){
                    String key=dataSnapshot.getKey();
                    if(!key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        userList.add(user);
                    }
                    else
                        myUser=dataSnapshot.getValue(ModelUser.class);

                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView=findViewById(R.id.users_list);
        recyclerView.setAdapter(usersAdapter);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        usersAdapter.notifyDataSetChanged();

        recyclerView.setHasFixedSize(true);

        progressBar=findViewById(R.id.load_progress_user_bar);

    }

    private void goToChat(int position) {
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("RecipientID",userList.get(position).getId());
        intent.putExtra("UserName",userList.get(position).getName());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersAdapter.notifyDataSetChanged();
//        usersDatabaseReference.orderByChild("id")
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
//                            myUser = childSnapshot.getValue(ModelUser.class);
//                            userKey=childSnapshot.getKey();
//                        }
//                        if(myUser!=null) {
//                            if (!isNetworkConnected()) {
//                                myUser.setOnline(false);
//                                usersDatabaseReference.child(userKey).child("isOnline").setValue(true);
//                                progressBar.setVisibility(View.VISIBLE);
//                            } else {
//                                myUser.setOnline(true);
//                                usersDatabaseReference.child(userKey).child("isOnline").setValue(false);
//                                progressBar.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);

        menuInflater.inflate(R.menu.user_activity_menu,menu);
        return true;
    }

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_item:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,SignInActivity.class));
                finish();
                break;
            case R.id.settings_item:
                Intent intent=new Intent(this,SettingsActivity.class);
                if(!myUser.equals(null))
                    intent.putExtra(ModelUser.class.getSimpleName(),myUser);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}