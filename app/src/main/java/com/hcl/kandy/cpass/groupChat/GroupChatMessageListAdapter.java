package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.messaging.api.Attachment;
import com.rbbn.cpaas.mobile.messaging.api.Message;
import com.rbbn.cpaas.mobile.messaging.api.Part;

import java.io.File;
import java.util.List;

public class GroupChatMessageListAdapter extends RecyclerView.Adapter<GroupChatMessageListAdapter.MyViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    protected Context context;
    private List<Message> messageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }

        void handleAttachment(Message message, ImageView imageView) {
            List<Part> parts = message.getParts();
            if (parts.size() > 1) {
                Attachment attachment = message.getParts().get(1).getFile();
                File f = new File(GroupChatDetailActivity.DOWNLOAD_FOLDER + File.separator + attachment.getName());
                if (f.exists()) {
                    // if the file exists locally, and is an image, then show it
                    Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GroupChatDetailActivity activity = (GroupChatDetailActivity) context;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f);
                            intent.setDataAndType(uri, "image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            activity.startActivity(intent);
                        }
                    });
                } else {
                    // file does not exist
                    // set a temporary image to show that an image is there, and then add a click to download listener
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GroupChatDetailActivity activity = (GroupChatDetailActivity) context;
                            activity.downloadAttachment(attachment, imageView);
                        }
                    });
                }
                imageView.setVisibility(View.VISIBLE);
            } else {
                // no attachments in this message, so hide the temporary ImageView
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                imageView.setVisibility(View.GONE);
            }
        }
    }

    public GroupChatMessageListAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public void setMessageList(List<Message> list) {
        messageList = list;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

/*    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message instanceof OutboundMessage) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
*/
    // Inflates the appropriate layout according to the ViewType.
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_chat_message, parent, false);

        return new GroupChatMessageHolder(view);
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        ((GroupChatMessageHolder) holder).bind(message);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, GroupChatMessageStatusDetailActivity.class);
                intent.putExtra("messageText", message.getMessage());
                intent.putExtra("messageId", message.getMessageId());
                intent.putExtra("groupId", message.getDestinationAddress());
                view.getContext().startActivity(intent);
                return false;
            }
        });
    }

    private class GroupChatMessageHolder extends MyViewHolder {
        ImageView attachedImageView;
        TextView messageSender;
        TextView messageText;
        TextView timeText;

        GroupChatMessageHolder(View itemView) {
            super(itemView);

            attachedImageView = itemView.findViewById(R.id.attached_image);
            messageSender = itemView.findViewById(R.id.group_message_sender);
            messageText = itemView.findViewById(R.id.group_message_body);
            timeText = itemView.findViewById(R.id.group_message_time);
        }

        void bind(Message message) {
            String[] parts = message.getSenderAddress().split("@");
            messageSender.setText(parts[0]);

            messageText.setText(message.getMessage());

            handleAttachment(message, attachedImageView);

            String t_str;
            long timestamp = message.getTimestamp();
            if (timestamp == -1) {
                t_str = "Fail!";
            } else if (timestamp == 0) {
                t_str = "...";
            } else {
                t_str = GroupChatFragment.formatDate(timestamp);
            }
            timeText.setText(t_str);
        }
    }
}