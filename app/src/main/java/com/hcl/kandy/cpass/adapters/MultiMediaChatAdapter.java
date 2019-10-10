package com.hcl.kandy.cpass.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.models.MultimediaChatModelChatModel;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService;
import com.rbbn.cpaas.mobile.messaging.chat.api.DownloadCompleteListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferProgressListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferRequestHandle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class MultiMediaChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String DOWNLOAD_FOLDER = "/storage/emulated/0/Download";
    private final static int CHAT_TYPE_ME = 1;
    private final static int CHAT_TYPE_OTHER = 2;
    Activity activity;
    ChatService chatService;
    private ArrayList<MultimediaChatModelChatModel> chatModels;

    public MultiMediaChatAdapter(ArrayList<MultimediaChatModelChatModel> chatModels, Activity activity, ChatService chatService) {
        this.chatModels = chatModels;
        this.activity = activity;
        this.chatService = chatService;
    }

    @Override
    public int getItemViewType(int position) {
        MultimediaChatModelChatModel chatModel = chatModels.get(position);
        if (chatModel.isMessageIn())
            return CHAT_TYPE_OTHER;
        else
            return CHAT_TYPE_ME;
    }

    void downloadfile(String url, ImageView attachment_sent, String filename) {
        TransferProgressListener progressCallback = new TransferProgressListener() {
            @Override
            public void reportProgress(long bytes, long totalBytes) {
                Log.d("===========", "Downloaded " + bytes + " of " + totalBytes + " bytes");
            }
        };

        DownloadCompleteListener downloadCompleteListener = new DownloadCompleteListener() {
            @Override
            public void downloadSuccess(String path) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Glide.with(activity).asBitmap()
                        .load(stream.toByteArray())
                        .into(attachment_sent);
            }

            @Override
            public void downloadFail(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        };
        String folder = DOWNLOAD_FOLDER + File.separator;
        TransferRequestHandle handle = chatService.downloadAttachment(url, folder, filename, progressCallback, downloadCompleteListener);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == CHAT_TYPE_ME) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_multimediachat_me, viewGroup, false);

            return new MyViewHolder(itemView);
        } else if (viewType == CHAT_TYPE_OTHER) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_multimedia_chat, viewGroup, false);

            return new OtherViewHolder(itemView);
        } else return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        MultimediaChatModelChatModel chatModel = chatModels.get(i);
        String destination = chatModel.getDestination();

        switch (holder.getItemViewType()) {
            case CHAT_TYPE_ME:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
//                myViewHolder.title.setText(destination);
                myViewHolder.message.setText(chatModel.getMessageTxt());
                if (chatModel.getParts() != null && chatModel.getParts().size() > 1) {
                    myViewHolder.attachment_sent.setVisibility(View.VISIBLE);
                    downloadfile(chatModel.getParts().get(1).getFile().getLink(),
                            myViewHolder.attachment_sent,
                            chatModel.getParts().get(1).getFile().getName());
                } else {
                    myViewHolder.attachment_sent.setVisibility(View.GONE);
                }
                if (chatModel.getMessageTxt().trim().length() == 0) {
                    myViewHolder.message.setVisibility(View.GONE);
                } else {
                    myViewHolder.message.setVisibility(View.VISIBLE);
                }

                break;
            case CHAT_TYPE_OTHER:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                if (chatModel.getParts() != null && chatModel.getParts().size() > 1) {
                    otherViewHolder.attachment_sent.setVisibility(View.VISIBLE);
                    downloadfile(chatModel.getParts().get(1).getFile().getLink(), otherViewHolder.attachment_sent,
                            chatModel.getParts().get(1).getFile().getName());
                } else {
                    otherViewHolder.attachment_sent.setVisibility(View.GONE);
                }
                if (chatModel.getMessageTxt().trim().length() == 0) {
                    otherViewHolder.linearMessageLayout.setVisibility(View.GONE);
                } else {
                    otherViewHolder.linearMessageLayout.setVisibility(View.VISIBLE);
                }
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
        public TextView message;
        ImageView attachment_sent;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            title = itemView.findViewById(R.id.txtDestination);
            message = itemView.findViewById(R.id.txtMessage);
            attachment_sent = itemView.findViewById(R.id.attachment_sent);
        }
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message;
        LinearLayout linearMessageLayout;
        ImageView attachment_sent;


        OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            linearMessageLayout = itemView.findViewById(R.id.linearMessageLayout);
            attachment_sent = itemView.findViewById(R.id.attachment_sent);
            title = itemView.findViewById(R.id.txtDestination);
            message = itemView.findViewById(R.id.txtMessage);
        }
    }
}
