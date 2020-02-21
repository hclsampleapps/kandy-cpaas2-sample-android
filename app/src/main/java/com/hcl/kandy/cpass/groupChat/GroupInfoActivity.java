package com.hcl.kandy.cpass.groupChat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.MessagingCallback;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatConversation;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatGroup;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatGroupParticipant;
import com.rbbn.cpaas.mobile.messaging.chat.api.ChatService;
import com.rbbn.cpaas.mobile.messaging.chat.api.FetchAllParticipantsForGroupCallback;
import com.rbbn.cpaas.mobile.messaging.chat.api.FetchGroupCallback;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {
    private static final String TAG = "GroupInfoActivity";
    protected GroupMembersAdapter groupMembersAdapter;
    protected List<ChatGroupParticipant> memberList = new ArrayList<>();
    String groupId, groupName, groupSubject, participant;
    boolean newGroup = true;
    boolean extendingOneToOne = false;
    boolean readOnly = false;
    EditText nameEditText, subjectEditText;
    ListView membersListView;
    FloatingActionButton saveGroupInfoButton;
    private ChatConversation conversation = null;
    private ChatGroupParticipant selectedChatGroupParticipant;
    protected ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        nameEditText = findViewById(R.id.groupInfo_name_editText);
        subjectEditText = findViewById(R.id.groupInfo_subject_editText);
        membersListView = findViewById(R.id.group_member_list);

        saveGroupInfoButton = findViewById(R.id.save_group_fab);
        saveGroupInfoButton.setOnClickListener(view -> {
            saveGroupInformation();
        });

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        groupSubject = intent.getStringExtra("groupSubject");
        participant = intent.getStringExtra("participant");
        extendingOneToOne = participant != null;

        readOnly = intent.getBooleanExtra("readOnly", false);

        if (chatService == null)
            initChatService(this);

        setTitle("Options");

        if (groupId.length() > 0) {
            // this is an existing group
            newGroup = false;

            nameEditText.setText(groupName);
            nameEditText.setEnabled(false);

            subjectEditText.setText(groupSubject);
            subjectEditText.setEnabled(false);

            saveGroupInfoButton.setVisibility(View.GONE);

            chatService.fetchAllParticipantsForGroup(groupId, new FetchAllParticipantsForGroupCallback() {
                @Override
                public void onSuccess(List<ChatGroupParticipant> participants) {
                    if (participants != null) {
                        memberList = participants;
                        groupMembersAdapter.setGroupMembersList(memberList);
                    }
                }

                @Override
                public void onFail(MobileError error) {
                }
            });
        } else {
            // this is a new group
            nameEditText.setEnabled(true);
            subjectEditText.setEnabled(true);
            saveGroupInfoButton.setVisibility(View.VISIBLE);

            setTitle("Create Chat Group");

            if (extendingOneToOne) {
                setTitle("Extend to Chat Group");
                ChatGroupParticipant chatGroupParticipant = chatService.createChatGroupParticipant(participant);
                memberList.add(chatGroupParticipant);
            }
        }

        groupMembersAdapter = new GroupMembersAdapter(this, memberList);
        membersListView.setAdapter(groupMembersAdapter);
        registerForContextMenu(membersListView);

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

    private void saveGroupInformation() {
        String groupName = nameEditText.getText().toString();
        String subject = subjectEditText.getText().toString();
        String image = "https://imagelink";
        boolean isOpen = false;

        if (extendingOneToOne) {
            conversation.extend(subject, image, groupName, isOpen, memberList, new FetchGroupCallback() {
                @Override
                public void onSuccess(ChatGroup group) {
                    Toast.makeText(getApplicationContext(), "Conversation is extended to Group", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                @Override
                public void onFail(MobileError error) {
                    Toast.makeText(getApplicationContext(), "Group not created", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            chatService.createGroup(subject, image, groupName, isOpen, memberList, new FetchGroupCallback() {
                @Override
                public void onSuccess(ChatGroup group) {
                    onBackPressed();
                }

                @Override
                public void onFail(MobileError error) {
                    Toast.makeText(getApplicationContext(), "Group not created", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addGroupMember(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = params.leftMargin;
        input.setLayoutParams(params);
        input.setHint("Enter the group name");
        container.addView(input);

        alert.setTitle("Add Group Member");
        alert.setView(container);

        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            String member = input.getText().toString();
            Log.d("", "New group member: " + member);
            ChatGroupParticipant chatGroupParticipant = chatService.createChatGroupParticipant(member);

            if (newGroup) {
                memberList.add(chatGroupParticipant);
                runOnUiThread(() -> groupMembersAdapter.notifyDataSetChanged());
            } else {
                chatService.addParticipantToGroup(groupId, member, new MessagingCallback() {
                    @Override
                    public void onSuccess() {
                        memberList.add(chatGroupParticipant);
                        runOnUiThread(() -> groupMembersAdapter.notifyDataSetChanged());
                    }

                    @Override
                    public void onFail(MobileError error) {
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel",
                (dialog, which) -> {
                });

        alert.show();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.group_chat_participant_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu item was pressed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ChatGroupParticipant chatGroupParticipant = groupMembersAdapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.delete_participant_from_group_option:
                removeParticipantFromGroup(chatGroupParticipant);
                return true;
            default:
                return false;
        }
    }

    private void removeParticipantFromGroup(ChatGroupParticipant chatGroupParticipant) {
        String participant = chatGroupParticipant.getAddress();

        if (groupId.length() > 0) {
            // existing group, remove the participant from the group on the server
            chatService.removeParticipantFromGroup(groupId, participant, new MessagingCallback() {
                @Override
                public void onSuccess() {
                    memberList.remove(chatGroupParticipant);
                    runOnUiThread(() -> groupMembersAdapter.notifyDataSetChanged());
                }

                @Override
                public void onFail(MobileError error) {
                }
            });
        } else {
            // new group, just remove the participant from the list
            memberList.remove(chatGroupParticipant);
            runOnUiThread(() -> groupMembersAdapter.notifyDataSetChanged());
        }
    }

    public void handleChatParticipantStatusChanged(ChatGroupParticipant chatGroupParticipant, String groupId) {
        if (!groupId.equals(this.groupId)) {
            // ignore this isn't for the current group
            return;
        }

        String address = chatGroupParticipant.getAddress();

        for (ChatGroupParticipant c : memberList) {
            String status = c.getStatus();
            if (c.getAddress().equals(address)) {
                // update the status of the participant
                c.setStatus(chatGroupParticipant.getStatus());
                runOnUiThread(() -> groupMembersAdapter.notifyDataSetChanged());
            }
        }
    }
}
