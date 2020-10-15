package com.hcl.kandy.cpass.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.models.ChatModel;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int CHAT_TYPE_ME = 1;
    private final static int CHAT_TYPE_OTHER = 2;

    private ArrayList<ChatModel> chatModels;

    public ChatAdapter(ArrayList<ChatModel> chatModels) {
        this.chatModels = chatModels;
    }


    @Override
    public int getItemViewType(int position) {
        ChatModel chatModel = chatModels.get(position);
        if (chatModel.isMessageIn())
            return CHAT_TYPE_OTHER;
        else
            return CHAT_TYPE_ME;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == CHAT_TYPE_ME) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_chat_me, viewGroup, false);

            return new MyViewHolder(itemView);
        } else if (viewType == CHAT_TYPE_OTHER) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_chat, viewGroup, false);

            return new OtherViewHolder(itemView);
        } else return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        ChatModel chatModel = chatModels.get(i);
        String destination = chatModel.getDestination();

        switch (holder.getItemViewType()) {
            case CHAT_TYPE_ME:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.title.setText(destination);
                myViewHolder.message.setText(chatModel.getMessageTxt());

                break;
            case CHAT_TYPE_OTHER:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                String[] split = destination.split("@");
                otherViewHolder.title.setText(split[0]);
                otherViewHolder.message.setText(chatModel.getMessageTxt());
                break;
        }

    }


    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtDestination);
            message = itemView.findViewById(R.id.txtMessage);
        }
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;

        OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtDestination);
            message = itemView.findViewById(R.id.txtMessage);
        }
    }
}
