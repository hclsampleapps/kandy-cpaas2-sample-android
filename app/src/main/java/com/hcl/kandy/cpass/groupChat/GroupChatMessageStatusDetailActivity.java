package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.FetchMessageCallback;
import com.rbbn.cpaas.mobile.messaging.api.Message;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatConversation;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;


public class GroupChatMessageStatusDetailActivity extends AppCompatActivity implements View.OnClickListener {

    protected static ChatService chatService;
    protected static Message participantStatusReports;
    protected ChatConversation chatConversation;
    protected GroupChatStatusAdapter adapter;
    protected ImageView statusItemImageView;
    protected TextView statusTextView;
    private RecyclerView messageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_message_status_detail);

        messageRecycler = findViewById(R.id.statusLists);
        LinearLayout updateStatusLayout = findViewById(R.id.group_chat_status_layout);
        updateStatusLayout.setOnClickListener(this);
        statusTextView = findViewById(R.id.status_text_view);
        statusItemImageView = findViewById(R.id.status_item_image_view);
        statusItemImageView.setImageResource(R.drawable.ic_done_all_grey_24dp);

        // set reverseLayout to true so the list is built from the bottom up
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        messageRecycler.setLayoutManager(manager);
        Intent intent = getIntent();
        String messageText = intent.getStringExtra("messageText");
        String messageId = intent.getStringExtra("messageId");
        String groupId = intent.getStringExtra("groupId");
        statusTextView.setText(messageText);

        participantMessageStatusReports(messageText, messageId, groupId);

    }

    public void participantMessageStatusReports(String messageText, String messageId, String groupId) {
        chatService = initChatService(this);
        chatConversation = (ChatConversation) chatService.createConversation(groupId);
        chatConversation.fetchGroupChatMessage(messageId, new FetchMessageCallback() {
            @Override
            public void onSuccess(Message message) {
                adapter = new GroupChatStatusAdapter(getApplicationContext(), message);
                messageRecycler.setAdapter(adapter);
            }

            @Override
            public void onFail(MobileError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private ChatService initChatService(@NonNull Context context) {
        App applicationContext = (App) context.getApplicationContext();
        CPaaS cpass = applicationContext.getCpass();
        return chatService = cpass.getChatService();
    }
}
