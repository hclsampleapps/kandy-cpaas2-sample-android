package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
//import com.rbbn.cpaas.mobile.demo_java.CPaaSManager;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.Attachment;
import com.rbbn.cpaas.mobile.messaging.api.Conversation;
import com.rbbn.cpaas.mobile.messaging.api.FetchConversationCallback;
import com.rbbn.cpaas.mobile.messaging.api.FetchMessagesCallback;
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.Message;
import com.rbbn.cpaas.mobile.messaging.api.MessagingCallback;
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatConversation;
import com.rbbn.cpaas.mobile.messaging.chat.api.DownloadCompleteListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferProgressListener;
import com.rbbn.cpaas.mobile.messaging.chat.api.TransferRequestHandle;
import com.rbbn.cpaas.mobile.messaging.chat.api.UploadCompleteListener;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.hcl.kandy.cpass.groupChat.GroupChatFragment.chatService;

//import static com.rbbn.cpaas.mobile.demo_java.ui.messaging.groupchat.GroupChatFragment.chatService;

public class GroupChatDetailActivity extends AppCompatActivity implements RecyclerGroupChatItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String DOWNLOAD_FOLDER = "/storage/emulated/0/Download";

    private static final String TAG = "ChatDetailActivity";

    protected ChatConversation chatConversation;

    String groupId;
    String name;
    String subject;
    int max;

    LinearLayout groupLayout;
    TextView groupSubjectTextView;
    EditText messageEditText;
    LinearLayout imagePreviewLayout;
    ImageView imagePreview;

    List<Message> messageList = new ArrayList<>();

    List<Attachment> attachments;

    private RecyclerView messageRecycler;
    private GroupChatMessageListAdapter messageAdapter;

    private boolean isComposing = false;
    private Timer mComposingTimer;

    final int ACTIVITY_CHOOSE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_detail);

        groupLayout = findViewById(R.id.layout_group);
        groupSubjectTextView = findViewById(R.id.group_subject_textview);
        messageEditText = findViewById(R.id.messageEditText);

        imagePreviewLayout = findViewById(R.id.image_preview_layout);
        imagePreviewLayout.setVisibility(View.GONE);
        imagePreview = findViewById(R.id.image_preview);

        messageRecycler = findViewById(R.id.group_message_reyclerview);
        // set reverseLayout to true so the list is built from the bottom up
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageRecycler.setLayoutManager(manager);
        messageAdapter = new GroupChatMessageListAdapter(this, new ArrayList<>());
        messageRecycler.setAdapter(messageAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerGroupChatItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(messageRecycler);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        name = intent.getStringExtra("name");
        subject = intent.getStringExtra("subject");

        if (chatService == null)
            initChatService(this);

       // CPaaSManager.getInstance().getcPaaSChatManager().setContext(this);

        attachments = new ArrayList<>();

        chatService.fetchGroupChatSession(groupId, new FetchConversationCallback() {
            @Override
            public void onSuccess(Conversation conversation) {
                chatConversation = (ChatConversation) conversation;

                max = intent.getIntExtra("max", 50);
                if (max > 50)
                    max = 50;

                // TODO: currently, the backend returns "max" oldest messages, because the backend returns them ordered oldest to newest

                chatConversation.fetchGroupChatMessages(max, new FetchMessagesCallback() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        messageList = messages;

                        // TODO: currently, the backend returns messages ordered from oldest to newest
                        //       reverse the list so the messages are ordered from newest to oldest
                        Collections.reverse(messageList);

                        messageAdapter.setMessageList(messageList);
                        runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
                    }

                    @Override
                    public void onFail(MobileError error) {

                    }
                });
            }

            @Override
            public void onFail(MobileError error) {
                // create a new conversation with the given participant
                chatConversation = (ChatConversation) chatService.createConversation(groupId);
                String sender = intent.getStringExtra("sender");
                String destination = intent.getStringExtra("destination");
                String messageId = intent.getStringExtra("messageId");
                String message = intent.getStringExtra("message");
                long timestamp = intent.getLongExtra("timestamp", 0L);

                if (sender.length() > 0 && messageId.length() > 0) {
                    // if a message was specified in the intent, then add it to the message list
                    InboundMessage inboundMessage = new com.rbbn.cpaas.mobile.messaging.InboundMessage(sender, destination, messageId, message, timestamp);
                    messageList.add(0, inboundMessage);
                }
                messageAdapter.setMessageList(messageList);
                runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
            }
        });

        setTitle(name);
        groupSubjectTextView.setText(subject);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    private void initChatService(@NonNull Context context) {
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        chatService = cpass.getChatService();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void isComposingActive() {
        mComposingTimer = new Timer();
        mComposingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    isComposing = false;
                    chatConversation.sendGroupChatComposing(false, new MessagingCallback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onFail(MobileError error) {}
                    });
                    if (mComposingTimer != null)
                        mComposingTimer.cancel();
                });
            }
        }, 5000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!isComposing) {
            isComposing = true;
            chatConversation.sendGroupChatComposing(true, new MessagingCallback() {
                @Override
                public void onSuccess() { isComposingActive(); }

                @Override
                public void onFail(MobileError error) {}
            });
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof GroupChatMessageListAdapter.MyViewHolder) {
            String messageID = messageList.get(viewHolder.getAdapterPosition()).getMessageId();

            // tell the backend to remove the message
            // the message will be removed from the conversation object's message list
            chatConversation.deleteGroupChatMessage(messageID, new MessagingCallback() {
                @Override
                public void onSuccess() {
                    // loop over the message list and delete the ID if it is there
                    for (Iterator iter = messageList.iterator(); iter.hasNext(); ) {
                        Message message = (Message) iter.next();
                        if (message.getMessageId().equals(messageID)) {
                            iter.remove();
                        }
                    }

                    // remove the message from recycler view
                    runOnUiThread(() -> messageAdapter.notifyItemRemoved(position));
                }

                @Override
                public void onFail(MobileError error) {

                }
            });
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void attachFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Choose a file"), ACTIVITY_CHOOSE_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (data == null)
                return;

            Uri uri = data.getData();

            TransferProgressListener transferProgressListener = new TransferProgressListener() {
                @Override
                public void reportProgress(long bytes, long totalBytes) {
                    Log.d(TAG, "Uploaded " + bytes + " of " + totalBytes + " bytes");
                }
            };

            UploadCompleteListener uploadCompleteListener = new UploadCompleteListener() {
                @Override
                public void uploadSuccess(Attachment attachment) {
                    Log.i(TAG, "Attachment uploaded");
                    imagePreviewLayout.setVisibility(View.VISIBLE);
                    imagePreview.setImageURI(uri);
                    attachments.add(attachment);
                }

                @Override
                public void uploadFail(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            };

            TransferRequestHandle handle = chatService.uploadAttachment(uri, transferProgressListener, uploadCompleteListener);

            // You can cancel the upload request by doing the following
            //handle.cancel();
        }
    }

    public void sendMessage(View view) {
        // do nothing if no message text is present
        String txt = messageEditText.getText().toString();
        if (txt.length() == 0) {
            if (attachments.size() > 0) {
                txt = " ";
            } else {
                Log.w(TAG, "Message text not specified");
                return;
            }
        }

        isComposing = false;
        if (mComposingTimer != null)
            mComposingTimer.cancel();

        // create a new message and send it to the adapter for display
        OutboundMessage message = chatService.createMessage(txt);

        // if there are any attachments, add them to the message
        for (Attachment attachment : attachments) {
            message.attachFile(attachment);
        }

        // clear out the message EditText
        messageEditText.setText("");
        hideKeyboard();

        // Add the new message to the front of the list
        messageList.add(0, message);

        // send the message to the backend
        chatConversation.sendGroupChatMessage(message, new MessagingCallback() {
            @Override
            public void onSuccess() {
                // update the UI when confirmation has been received
                runOnUiThread(() -> {
                    imagePreview.setImageURI(null);
                    imagePreviewLayout.setVisibility(View.GONE);
                    messageAdapter.notifyDataSetChanged();
                });

                // clear the attachments list
                attachments = new ArrayList<>();
            }

            @Override
            public void onFail(MobileError error) {
                // update the UI when confirmation has been received
                runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
            }
        });

        // update the RecyclerView with the newly-sent message
        runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
    }

    public void handleInboundChat(InboundMessage message) {
        String messageGroupId = message.getDestinationAddress();
        Log.d(TAG, "InboundMessage is GroupChat - id: " + message.getMessageId() + ", sender: " + message.getSenderAddress() + ", groupId: " + messageGroupId);

        if (messageGroupId.equals(this.groupId)) {
            // add the inbound message to the message list if it is for this group
            Log.d(TAG, "GroupChat is for this Group. Will refresh screen - id: " + message.getMessageId());
            messageList.add(0, message);
            runOnUiThread(() -> messageAdapter.notifyDataSetChanged());


            // notify the backend that the message was displayed
            chatConversation.sendGroupChatDisplayed(message.getMessageId(), new MessagingCallback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFail(MobileError error) {}
            });
        }else {
            Log.d(TAG, "GroupChat is not for this Group. - screen groupId: " + groupId);
        }
    }

    public void handleChatDeliveryStatusChanged(String participant, String deliveryStatus, String messageID) {
//        if (!participant.equals(this.participant)) {
            // ignore this if the participant isn't for the current conversation
//            return;
//        }

        for (Message msg : messageList) {
            String id = msg.getMessageId();
            if (messageID != null && messageID.equals(id)) {
                // update the status of the message
                msg.setStatus(deliveryStatus);

                // show a Toast, or something
            }
        }
    }

    public void handleOutboundChat(OutboundMessage message) {
//        String participant = message.getDestinationAddress();

//        if (!participant.equals(this.participant)) {
            // ignore this if the participant isn't for the current conversation
//            return;
//        }

        String messageID = message.getMessageId();
        for (Message msg : messageList) {
            String id = msg.getMessageId();
            if (messageID != null && messageID.equals(id)) {
                // If this message is already in the local message list, then disregard the notification
                return;
            }
        }

        messageList.add(0, message);
        runOnUiThread(() -> messageAdapter.notifyDataSetChanged());
    }


    public void handleIsComposingReceived(String participant, String state, long lastActive) {
        String str = participant + (state.equals("active") ? " is typing" : " has stopped typing");
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void handleChatEventNotification(String groupId, String type, String description) {
        if (groupId.equals(this.groupId)) {
            Toast.makeText(this, description, Toast.LENGTH_LONG).show();
        }
    }

    public void downloadAttachment(Attachment attachment, ImageView imageView) {
        String url = attachment.getLink();
        String folder = DOWNLOAD_FOLDER + File.separator;
        String filename = attachment.getName();
        String absolutePath = folder + filename;

        TransferProgressListener progressCallback = new TransferProgressListener() {
            @Override
            public void reportProgress(long bytes, long totalBytes) {
                Log.d(TAG, "Downloaded " + bytes + " of " + totalBytes + " bytes");
            }
        };

        DownloadCompleteListener downloadCompleteListener = new DownloadCompleteListener() {
            @Override
            public void downloadSuccess(String path) {
                Context context = GroupChatDetailActivity.this;
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
                        intent.setDataAndType(uri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void downloadFail(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        };

        TransferRequestHandle handle = chatService.downloadAttachment(url, folder, filename, progressCallback, downloadCompleteListener);
        // You can cancel the download request by doing the following
        //handle.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_chat_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group_options:
                Intent optionsIntent = new Intent(this, GroupInfoActivity.class);
                optionsIntent.putExtra("groupId", groupId);
                optionsIntent.putExtra("groupName", name);
                optionsIntent.putExtra("groupSubject", subject);
                startActivity(optionsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
