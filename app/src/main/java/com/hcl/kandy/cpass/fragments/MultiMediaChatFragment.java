package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.adapters.MultiMediaChatAdapter;
import com.hcl.kandy.cpass.models.MultimediaChatModelChatModel;
import com.hcl.kandy.cpass.utils.MarshMallowPermission;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.Attachment;
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.MessagingCallback;
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatConversation;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatGroupParticipant;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferProgressListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferRequestHandle;
import com.rbbn.cpaas.mobile.messaging.chat.api.UploadCompleteListener;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

import java.util.ArrayList;
import java.util.List;

public class MultiMediaChatFragment extends BaseFragment implements View.OnClickListener {

    public int ACTIVITY_CHOOSE_FILE = 1;
    private ImageView imagePreview;
    private int PermissionDenied = 0;
    private List<Attachment> attachments = new ArrayList<>();
    private ImageButton btnFetchChat;
    private LinearLayout showChatLayout;
    private Uri uri;
    private ChatService chatService;
    private EditText mEtDestination;
    private EditText mEtMessage;
    private RecyclerView mRecyclerView;
    private MultiMediaChatAdapter mMultimediaChatAdapter;
    private ArrayList<MultimediaChatModelChatModel> mMultimediaChatModels;

    public MultiMediaChatFragment() {

    }

    public static MultiMediaChatFragment newInstance() {
        return new MultiMediaChatFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Boolean res = true;
        for (int c = 0; c <= grantResults.length - 1; c++) {
            res = res && (grantResults[c] == PackageManager.PERMISSION_GRANTED);
        }
        if (res) {
            PermissionDenied = 0;
            attachFile();
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.common_message_permission_all_required),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        if (context != null)
            initChatService(context);
        mMultimediaChatModels = new ArrayList<>();
        mMultimediaChatAdapter = new MultiMediaChatAdapter(mMultimediaChatModels, getActivity(), chatService);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_multimediachat, container, false);
        View mBtnSendMessage = inflate.findViewById(R.id.btnSendChat);
        View mBtnAttachImageMessage = inflate.findViewById(R.id.btnStartAttach);
        mEtDestination = inflate.findViewById(R.id.etDestainationAddress);
        imagePreview = inflate.findViewById(R.id.image_preview);
        mEtMessage = inflate.findViewById(R.id.etMessage);
        btnFetchChat = inflate.findViewById(R.id.btnFetchChat);
        btnFetchChat.setOnClickListener(this);
        showChatLayout = inflate.findViewById(R.id.showChatLayout);
        mRecyclerView = inflate.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mMultimediaChatAdapter);
        mBtnSendMessage.setOnClickListener(this);
        mBtnAttachImageMessage.setOnClickListener(this);
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(getActivity());
        if (Build.VERSION.SDK_INT >= 23 && !marshMallowPermission.checkAllPermissions()) {
            PermissionDenied = 1;
            marshMallowPermission.requestALLPermissions();
        }
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initChatService(@NonNull Context context) {
        attachments = new ArrayList<>();
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        chatService = cpass.getChatService();
        ChatListener cl = new ChatListener() {
            @Override
            public void inboundChatMessageReceived(InboundMessage inboundMessage) {
                Log.d("CPaaS.ChatService", "New message from " + inboundMessage.getSenderAddress() + inboundMessage.getMessage());
                MultimediaChatModelChatModel multimediaChatModel = new MultimediaChatModelChatModel(
                        inboundMessage.getMessage(),
                        inboundMessage.getSenderAddress(),
                        true,
                        inboundMessage.getMessageId(),
                        inboundMessage.getParts()
                );
                notifyList(multimediaChatModel);
            }

            @Override
            public void chatDeliveryStatusChanged(String s, String s1, String s2) {
                Log.d("CPaaS.ChatService", "Message delivery status changed to " + s1);
            }

            @Override
            public void chatParticipantStatusChanged(ChatGroupParticipant chatGroupParticipant, String s) {

            }

            @Override
            public void outboundChatMessageSent(OutboundMessage outboundMessage) {
                Log.d("CPaaS.ChatService", "Message is sent to " + outboundMessage.getSenderAddress());
                hideProgressBAr();

                MultimediaChatModelChatModel multimediaChatModelChatModel;
                if (attachments.size() > 0) {
                    multimediaChatModelChatModel = new MultimediaChatModelChatModel(
                            outboundMessage.getMessage(),
                            outboundMessage.getSenderAddress(),
                            false,
                            outboundMessage.getMessageId(),
                            outboundMessage.getParts()
                    );
                } else {
                    multimediaChatModelChatModel = new MultimediaChatModelChatModel(
                            outboundMessage.getMessage(),
                            outboundMessage.getSenderAddress(),
                            false,
                            outboundMessage.getMessageId()
                    );

                }
                Log.d("CPaaS.ChatService", "Message is sent");
                notifyList(multimediaChatModelChatModel);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagePreview.setVisibility(View.GONE);
                        attachments.clear();
                        mEtMessage.setText("");
                    }
                });
            }

            @Override
            public void isComposingReceived(String s, String s1, long l) {

            }

            @Override
            public void groupChatSessionInvitation(List<ChatGroupParticipant> list, String s, String s1) {

            }

            @Override
            public void groupChatEventNotification(String s, String s1, String s2) {

            }
        };
        chatService.setChatListener(cl);

    }

    private void sendMessage(String participant, String txt) {
        ChatConversation chatConversation = (ChatConversation) chatService.createConversation(participant);
        OutboundMessage message = chatService.createMessage(txt);

        if (uri != null) {

            TransferProgressListener transferProgressListener = new TransferProgressListener() {
                @Override
                public void reportProgress(long bytes, long totalBytes) {
                    Log.d("CPass", "Uploaded " + bytes + " of " + totalBytes + " bytes");
                }
            };

            UploadCompleteListener uploadCompleteListener = new UploadCompleteListener() {
                @Override
                public void uploadSuccess(Attachment attachment) {
                    Log.i("", "Attachment uploaded");

                    attachments.add(attachment);
                    for (Attachment attachment1 : attachments) {
                        message.attachFile(attachment1);
                    }
                    chatConversation.send(message, new MessagingCallback() {
                        @Override
                        public void onSuccess() {
                            uri = null;
                            hideProgressBAr();
                            showMessage("Success");
                            Log.d("CPaaS.ChatService", "Message is sent");
                        }

                        @Override
                        public void onFail(MobileError error) {
                            Log.d("CPaaS.ChatService", "Message is failed");
                            hideProgressBAr();
                            showMessage("Try again later");
                        }
                    });
                }

                @Override
                public void uploadFail(String error) {
                    hideProgressBAr();
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
            };

            TransferRequestHandle handle = chatService.uploadAttachment(uri, transferProgressListener, uploadCompleteListener);


        } else {
            chatConversation.send(message, new MessagingCallback() {
                @Override
                public void onSuccess() {
                    uri = null;
                    showMessage("Success");
                    hideProgressBAr();
                    Log.d("CPaaS.ChatService", "Message is sent");
                }

                @Override
                public void onFail(MobileError error) {
                    Log.d("CPaaS.ChatService", "Message is failed");
                    hideProgressBAr();
                    showMessage("Try again later");
                }
            });
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFetchChat:
                if (mEtDestination.getText().length() > 0) {
                    mEtDestination.setEnabled(false);
                    btnFetchChat.setVisibility(View.GONE);
                    showChatLayout.setVisibility(View.VISIBLE);
                } else {
                    showMessage("Please enter Destination Address.");
                }
                break;
            case R.id.btnSendChat:
                if (mEtMessage.getText().length() == 0) {
                    showMessage("Enter Message");
                    return;
                }
                showProgressBar("");
                sendMessage(mEtDestination.getText().toString(), mEtMessage.getText().toString());
                break;
            case R.id.btnStartAttach:
                MarshMallowPermission marshMallowPermission = new MarshMallowPermission(getActivity());
                if (Build.VERSION.SDK_INT >= 23 && !marshMallowPermission.checkAllPermissions()) {
                    PermissionDenied = 1;
                    marshMallowPermission.requestALLPermissions();
                } else {
                    attachFile();
                }
                break;
        }
    }

    public void attachFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), ACTIVITY_CHOOSE_FILE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (data == null)
                return;
            uri = data.getData();
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(uri);
        }
    }

    private void notifyList(MultimediaChatModelChatModel chatModel) {
        mMultimediaChatModels.add(chatModel);
        Context context = getContext();
        if (context != null) {
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("cpass", "notify list");
                    mMultimediaChatAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mMultimediaChatModels.size() - 1);
                } // This is your code
            };
            mainHandler.post(myRunnable);
        }

    }

    private void showMessage(String message) {
        Snackbar.make(mEtMessage, message, Snackbar.LENGTH_SHORT).show();
    }
}

