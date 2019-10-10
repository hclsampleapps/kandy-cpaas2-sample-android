package com.hcl.kandy.cpass.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.models.SMSModel;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class SMSAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int CHAT_TYPE_ME = 1;
    private final static int CHAT_TYPE_OTHER = 2;

    private ArrayList<SMSModel> smsModels;

    public SMSAdapter(ArrayList<SMSModel> smsModels) {
        this.smsModels = smsModels;
    }


    @Override
    public int getItemViewType(int position) {
        SMSModel smsModel = smsModels.get(position);
        if (smsModel.isMessageIn())
            return CHAT_TYPE_OTHER;
        else
            return CHAT_TYPE_ME;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == CHAT_TYPE_ME) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_sms_me, viewGroup, false);

            return new MyViewHolder(itemView);
        } else if (viewType == CHAT_TYPE_OTHER) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_sms, viewGroup, false);

            return new OtherViewHolder(itemView);
        } else return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        SMSModel smsModel = smsModels.get(i);
        String destination = smsModel.getDestination();

        switch (holder.getItemViewType()) {
            case CHAT_TYPE_ME:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.title.setText(destination);
                myViewHolder.message.setText(smsModel.getMessageTxt());

                break;
            case CHAT_TYPE_OTHER:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                String[] split = destination.split("@");
                otherViewHolder.title.setText(split[0]);
                otherViewHolder.message.setText(smsModel.getMessageTxt());
                break;
        }

    }


    @Override
    public int getItemCount() {
        return smsModels.size();
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
