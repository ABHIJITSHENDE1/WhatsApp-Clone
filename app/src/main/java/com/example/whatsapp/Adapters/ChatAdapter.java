package com.example.whatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.Messeges;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<Messeges> messeges;
    Context context;
    String recId,sendId;

    int SENDER_VIEW_TYPE =1;
    int RECEIVER_VIEW_TYPE =2;

    public ChatAdapter(ArrayList<Messeges> messeges, Context context) {
        this.messeges = messeges;
        this.context = context;
    }

    public ChatAdapter(ArrayList<Messeges> messeges, Context context, String recId) {
        this.messeges = messeges;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new senderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messeges.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else {
            return RECEIVER_VIEW_TYPE;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       Messeges messegesModel  = messeges.get(position);
       if (holder.getClass() == senderViewHolder.class){
           ((senderViewHolder)holder).senderMessage.setText(messegesModel.getMessage());
       }else {
           ((ReceiverViewHolder)holder).receiverMessage.setText(messegesModel.getMessage());
       }
       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               new AlertDialog.Builder(context)
                       .setTitle("Delete")
                       .setMessage("Are you sure you want to delete this message")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               FirebaseDatabase database = FirebaseDatabase.getInstance();
                               String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                               database.getReference().child("chats").child(senderRoom)
                                       .child(messegesModel.getMessageId())
                                       .setValue(null);
                           }
                       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               }).show();
               return false;
           }
       });


    }

    @Override
    public int getItemCount() {
        return messeges.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMessage, receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessage = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }
    public class senderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMessage, senderTime;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }
}
