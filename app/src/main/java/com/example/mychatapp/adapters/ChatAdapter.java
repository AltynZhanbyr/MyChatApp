package com.example.mychatapp.adapters;

import static com.example.mychatapp.R.color.dark_blue;
import static com.example.mychatapp.R.color.dark_gray;
import static com.example.mychatapp.R.color.gray;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapp.R;
import com.example.mychatapp.model.ModelMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<ModelMessage> modelMessages;
    private LayoutInflater layoutInflater;
    private int messageType;
    private Context context;
    private OnMessageClickListener messageClickListener;

    public interface OnMessageClickListener{
        void onMessageClick();
    }

    public ChatAdapter(Context context,List<ModelMessage>messages){
        this.layoutInflater=LayoutInflater.from(context);
        this.modelMessages=messages;
        this.context=context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.message_item,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ModelMessage message=modelMessages.get(position);
        RelativeLayout layout=holder.itemView.findViewById(R.id.message_item_view);
        CardView cardView=holder.itemView.findViewById(R.id.message_card_view);

        LinearLayout.LayoutParams layoutParams3=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams4=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(message.isMine()) {
            layout.setGravity(Gravity.END);
            layoutParams3.gravity=Gravity.END;
            layoutParams4.gravity=Gravity.START;
            layoutParams4.setMarginStart(15);
            layoutParams4.setMarginEnd(0);
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, dark_blue));
            layout.setPadding(150,0,0,0);
        }
        else {
            layout.setGravity(Gravity.START);
            layoutParams3.gravity=Gravity.START;
            layoutParams4.gravity=Gravity.END;
            layoutParams4.setMarginStart(0);
            layoutParams4.setMarginEnd(15);
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, dark_gray));
            layout.setPadding(0,0,150,0);
        }


        holder.name.setLayoutParams(layoutParams3);

        LinearLayout.LayoutParams layoutParams;
        LinearLayout.LayoutParams layoutParams2;

        holder.name.setText(message.getName());
        boolean isText=message.getImageURL()==null;
        if(isText){
            layoutParams=new LinearLayout.LayoutParams(0, 0);
            layoutParams2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.gravity=layoutParams4.gravity;
            layoutParams2.setMarginEnd(layoutParams4.getMarginEnd());
            layoutParams2.setMarginStart(layoutParams4.getMarginStart());
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setGravity(Gravity.CENTER);
            holder.image.setVisibility(View.INVISIBLE);
            holder.text.setText(message.getText());
            holder.text.setLayoutParams(layoutParams2);
            holder.image.setLayoutParams(layoutParams);
        }
        else{
            layoutParams=new LinearLayout.LayoutParams(450, 500);
            layoutParams2=new LinearLayout.LayoutParams(0, 0);
            holder.text.setVisibility(View.INVISIBLE);
            holder.text.setLayoutParams(layoutParams2);
//            layoutParams.gravity=Gravity.CENTER;
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setLayoutParams(layoutParams);
            Glide.with(context).load(message.getImageURL()).into(holder.image);
        }
        holder.name.setText(message.getName());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                messageClickListener.onMessageClick();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return modelMessages.size() ;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name,text;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            text=itemView.findViewById(R.id.message_text);
            name=itemView.findViewById(R.id.user_name);
            image=itemView.findViewById(R.id.message_image_view);
        }
    }
}
