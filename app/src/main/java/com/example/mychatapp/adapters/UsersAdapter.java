package com.example.mychatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapp.R;
import com.example.mychatapp.activities.UsersActivity;
import com.example.mychatapp.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public interface OnUserClickListener{
        void onUserClick(int position);
    }

    private List<ModelUser> modelUserList=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnUserClickListener clickListener;
    private Context context;

    public UsersAdapter(List<ModelUser> modelUserList, Context context, OnUserClickListener clickListener) {
        this.modelUserList = modelUserList;
        this.layoutInflater = LayoutInflater.from(context);
        Collections.sort((ArrayList)this.modelUserList);
        this.clickListener = clickListener;
        this.context=context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=layoutInflater.inflate(R.layout.user_item_view,parent,false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ModelUser user=modelUserList.get(position);
        int newPosition=position;

        holder.name.setText(user.getName());
        if(user.getUserAvatar().trim().length()>0)
            Glide.with(context).load(user.getUserAvatar()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onUserClick(newPosition);
            }
        });
    }
    private Bitmap drawableFromUrl(String url) throws java.net.MalformedURLException, java.io.IOException {

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        return BitmapFactory.decodeStream(input);
    }

    @Override
    public int getItemCount() {
        return modelUserList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        TextView name,online;
        ImageView image;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.user_list_name);
            image=itemView.findViewById(R.id.user_image);
            online=itemView.findViewById(R.id.user_online);
        }
    }
}
