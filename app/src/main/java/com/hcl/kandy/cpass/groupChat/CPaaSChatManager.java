package com.hcl.kandy.cpass.groupChat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
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

   /* private void showInboundChatNotification(InboundMessage message) {
        String participant = message.getSenderAddress();

        // Build the Intent that will start the specified activity when the notification is touched
        Intent intent = new Intent(Globals.getApplicationContext(), ChatDetailActivity.class);
        intent.putExtra("participant", participant);

        intent.putExtra("sender", message.getSenderAddress());
        intent.putExtra("destination", message.getDestinationAddress());
        intent.putExtra("messageId", message.getMessageId());
        intent.putExtra("message", message.getMessage());
        intent.putExtra("timestamp", message.getTimestamp());

        // Show a notification
        NotificationsHelper.showNotifications("New message from " + participant, message.getMessage(), intent);
    }
*/
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
     /*   Log.d(TAG, "InboundMessage - id: " + message.getMessageId() + ", sender: " + message.getSenderAddress());
        String className = getContextClassName();

        if (className.equals("GroupChatDetailActivity")) {
            // add the message to the group chat detail view
            GroupChatDetailActivity groupChatDetailActivity = (GroupChatDetailActivity) context;
            groupChatDetailActivity.handleInboundChat(message);
        } else if (className.equals("ChatDetailActivity")) {
            // add the message to the 1:1 chat detail view
            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) context;
            chatDetailActivity.handleInboundChat(message);
        } else {
            if (message.isGroupChat()) {
                // don't do anything with a group chat message notification if you aren't on the specific group chat screen
                // TODO: is there something better to do in this case? It could get cluttered.
                showInboundGroupChatNotification(message);
                return;
            }

            if (className.equals("MainActivity")) {
                MainActivity mainActivity = (MainActivity) context;
                ChatFragment chatFragment = mainActivity.getChatFragment();
                View fragmentRootView = chatFragment.getView();
                if (fragmentRootView != null && fragmentRootView.getGlobalVisibleRect(new Rect())) {
                    // ChatFragment is visible
                    chatFragment.handleInboundChat(message);
                } else {
                    // ChatFragment is not visible
                    showInboundChatNotification(message);
                }
            } else {
                showInboundChatNotification(message);
            }
        }*/
    }

    @Override
    public void chatDeliveryStatusChanged(String participant, String deliveryStatus, String messageID) {
        String className = getContextClassName();

        if (className.equals("ChatDetailActivity")) {
//            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) context;
//            chatDetailActivity.handleChatDeliveryStatusChanged(participant, deliveryStatus, messageID);
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
/*
        String className = getContextClassName();

        if (className.equals("ChatDetailActivity")) {
            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) context;
            chatDetailActivity.handleOutboundChat(message);
        } else if (className.equals("GroupChatDetailActivity")) {
            GroupChatDetailActivity groupChatDetailActivity = (GroupChatDetailActivity) context;
            groupChatDetailActivity.handleOutboundChat(message);
        }
*/
    }

    @Override
    public void isComposingReceived(String participant, String state, long lastActive) {
/*
        String className = getContextClassName();

        if (className.equals("ChatDetailActivity")) {
            ChatDetailActivity chatDetailActivity = (ChatDetailActivity) context;
            chatDetailActivity.handleIsComposingReceived(participant, state, lastActive);
        }
*/

        // TODO: I don't think GroupChatDetailActivity should do anything with isComposing
        //        the screen may get too cluttered when there are a lot of members
    }

    @Override
    public void groupChatSessionInvitation(List<ChatGroupParticipant> chatGroupParticipants, String groupId, String groupName) {
        // show a toast of the invite
        // on the group chat list, you'll see indicators of which groups a user is connected to versus invited to
        // TODO: any other demo app custom handling for invitations...start from here
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
