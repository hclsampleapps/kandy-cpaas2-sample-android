package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hcl.kandy.cpass.utils.NotificationsHelper;
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatGroupParticipant;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatListener;
import com.rbbn.cpaas.mobile.utilities.Globals;

import java.util.List;

public class CPaaSChatManager implements ChatListener {

    private final String TAG = "CPaaSCallManager";
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private String getContextClassName() {
        return context.getClass().getSimpleName();
    }

    private void showInboundGroupChatNotification(InboundMessage message) {
        String participant = message.getSenderAddress();
        String messageGroupId = message.getDestinationAddress();

        // Build the Intent that will start the specified activity when the notification is touched
        Intent intent = new Intent(Globals.getApplicationContext(), GroupChatDetailActivity.class);
        intent.putExtra("participant", participant);
        intent.putExtra("groupId", messageGroupId);
        intent.putExtra("destination", message.getDestinationAddress());
        intent.putExtra("messageId", message.getMessageId());
        intent.putExtra("message", message.getMessage());
        intent.putExtra("timestamp", message.getTimestamp());

        // Show a notification
        NotificationsHelper.showNotifications("New message from " + participant, message.getMessage(), intent);
    }

    @Override
    public void inboundChatMessageReceived(InboundMessage message) {

    }

    @Override
    public void chatDeliveryStatusChanged(String participant, String deliveryStatus, String messageID) {
        String className = getContextClassName();

        if (className.equals("ChatDetailActivity")) {
        } else if (className.equals("GroupChatDetailActivity")) {
            GroupChatDetailActivity groupChatDetailActivity = (GroupChatDetailActivity) context;
            groupChatDetailActivity.handleChatDeliveryStatusChanged(participant, deliveryStatus, messageID);
        }
    }

    @Override
    public void chatParticipantStatusChanged(ChatGroupParticipant chatGroupParticipant, String groupId) {
        String className = getContextClassName();

        if (className.equals("GroupInfoActivity")) {
            GroupInfoActivity groupInfoActivity = (GroupInfoActivity) context;
            groupInfoActivity.handleChatParticipantStatusChanged(chatGroupParticipant, groupId);
        }
    }

    @Override
    public void outboundChatMessageSent(OutboundMessage message) {

    }

    @Override
    public void isComposingReceived(String participant, String state, long lastActive) {

    }

    @Override
    public void groupChatSessionInvitation(List<ChatGroupParticipant> chatGroupParticipants, String groupId, String groupName) {
        Toast.makeText(getContext(), "You have been invited to " + groupName, Toast.LENGTH_LONG).show();
    }

    @Override
    public void groupChatEventNotification(String type, String description, String groupId) {
        String className = getContextClassName();

        if (className.equals("GroupChatDetailActivity")) {
            GroupChatDetailActivity groupChatDetailActivity = (GroupChatDetailActivity) context;
            groupChatDetailActivity.handleChatEventNotification(groupId, type, description);
        }
    }
}
