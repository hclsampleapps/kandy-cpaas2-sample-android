package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.messaging.api.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GroupChatStatusAdapter extends RecyclerView.Adapter<GroupChatStatusAdapter.MyViewHolder> {
    protected Context context;
    protected Message messageStatusReport;

    public GroupChatStatusAdapter(Context context, Message statusReports) {
        this.context = context;
        this.messageStatusReport = statusReports;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_status_detail, parent, false);
        return new GroupChatMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ((GroupChatMessageHolder) holder).bind(messageStatusReport, position);

    }

    @Override
    public int getItemCount() {
        return messageStatusReport.getGroupDeliveryStatus().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }
    }

    private class GroupChatMessageHolder extends MyViewHolder {
        ImageView itemImageView;
        TextView statusAdressTextView;
        TextView statusText;
        TextView timeText;

        GroupChatMessageHolder(View itemView) {
            super(itemView);
            statusAdressTextView = itemView.findViewById(R.id.status_participant_text_view);
            statusText = itemView.findViewById(R.id.status_message_sent_view);
            timeText = itemView.findViewById(R.id.timestamp_text_view);
            itemImageView = itemView.findViewById(R.id.group_chat_conversation_item_image_view);
        }

        void bind(Message messageToReport, int position) {
            Map<String, String> status = messageToReport.getGroupDeliveryStatus();
            List<String> addressList = new ArrayList<>(status.keySet());
            List<String> statusList = new ArrayList<>(status.values());
            statusAdressTextView.setText(addressList.get(position));
            statusText.setText(statusList.get(position));
            if ("Displayed".equals(statusList.get(position))) {
                itemImageView.setImageResource(R.drawable.ic_done_all_green_24dp);
            }

        }

    }
}

