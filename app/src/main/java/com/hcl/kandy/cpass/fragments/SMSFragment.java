package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.adapters.SMSAdapter;
import com.hcl.kandy.cpass.models.SMSModel;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.MessageDeliveryStatus;
import com.rbbn.cpaas.mobile.messaging.api.MessagingCallback;
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.SendMessageCallback;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSConversation;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSListener;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSService;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;
import com.rbbn.cpaas.mobile.utilities.services.ServiceInfo;
import com.rbbn.cpaas.mobile.utilities.services.ServiceType;

import java.util.ArrayList;
import java.util.List;

public class SMSFragment extends BaseFragment implements View.OnClickListener {
    private SMSService smsService;
    private EditText mEtDestination, mEtSender;
    private EditText mEtMessage;
    private RecyclerView mRecyclerView;
    private SMSAdapter mSMSAdapter;
    private ArrayList<SMSModel> mSMSModels;

    public SMSFragment() {
    }

    public static SMSFragment newInstance() {
        return new SMSFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSMSModels = new ArrayList<>();
        mSMSAdapter = new SMSAdapter(mSMSModels);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_sms, container, false);
        View mBtnSendMessage = inflate.findViewById(R.id.btnStartSMS);
        mEtDestination = inflate.findViewById(R.id.etDestainationAddress);
        mEtSender = inflate.findViewById(R.id.etSenderAddress);
        mEtMessage = inflate.findViewById(R.id.etMessage);
        mRecyclerView = inflate.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSMSAdapter);
        mBtnSendMessage.setOnClickListener(this);
        Context context = getContext();
        if (context != null)
            initSMSService(context);
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    int select_position = 0;
    List<String> localAddressList;

    private void initSMSService(@NonNull Context context) {
        App applicationContext = (App) context.getApplicationContext();

        List<ServiceInfo> services = new ArrayList<>();
        services.add(new ServiceInfo(ServiceType.SMS, true));

        CPaaS cpass = applicationContext.getCpass();
        smsService = cpass.getSMSService();
        localAddressList = smsService.getLocalAddressList();
        if (localAddressList != null && localAddressList.size() > 0) {

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
            builderSingle.setTitle("Select One Number:-");
            builderSingle.setCancelable(false);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_singlechoice);

            for (int i = 0; i < localAddressList.size(); i++) {
                arrayAdapter.add(localAddressList.get(i));
            }

            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    select_position = position;
                    String strName = arrayAdapter.getItem(select_position);
                    mEtSender.setText(strName);
                    mEtSender.setEnabled(false);
                }
            });
            builderSingle.show();

        }

        smsService.setSMSListener(new SMSListener() {
            @Override
            public void inboundSMSMessageReceived(InboundMessage inboundMessage) {
                Log.d("CPaaS.SMSService", "New message from " + inboundMessage.getSenderAddress() + inboundMessage.getMessage());
                SMSModel smsModel = new SMSModel(
                        inboundMessage.getMessage(),
                        inboundMessage.getSenderAddress(),
                        true,
                        inboundMessage.getMessageId()
                );
                notifyList(smsModel);
            }

            @Override
            public void SMSDeliveryStatusChanged(String s, MessageDeliveryStatus messageDeliveryStatus, String s1) {

            }

            @Override
            public void outboundSMSMessageSent(OutboundMessage outboundMessage) {
                Log.d("CPaaS.SMSService", "Message is sent to " + outboundMessage.getSenderAddress());

                SMSModel smsModel = new SMSModel(
                        outboundMessage.getMessage(),
                        outboundMessage.getSenderAddress(),
                        false,
                        outboundMessage.getMessageId()
                );
                notifyList(smsModel);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEtMessage.setText("");
                    }
                });
            }
        });

    }

    private void sendMessage(String participant, String txt) {

        SMSConversation smsConversation = (SMSConversation)
                smsService.createConversation(participant, localAddressList.get(select_position));

        OutboundMessage message = smsService.createMessage(txt);

        smsConversation.send(message, new SendMessageCallback() {

            @Override
            public void onSuccess(OutboundMessage outboundMessage) {
                Log.d("CPaaS.SMSService", "Message is sent");
                showMessage(mEtMessage, "Message is sent");
            }

            @Override
            public void onFail(MobileError error) {
                Log.d("CPaaS.SMSService", "Message is failed");
                showMessage(mEtMessage, "Try again later");
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartSMS:
                if (mEtDestination.getText().length() == 0) {
                    showMessage(mEtDestination, "Enter Destination number");
                    return;
                } else if (mEtSender.getText().length() == 0) {
                    showMessage(mEtSender, "Enter Sender number");
                    return;
                } else
                    sendMessage(mEtDestination.getText().toString(), mEtMessage.getText().toString());
                break;
        }
    }

    private void notifyList(SMSModel smsModel) {
        mSMSModels.add(smsModel);
        Context context = getContext();
        if (context != null) {
            Handler mainHandler = new Handler(context.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("cpass", "notify list");
                    mSMSAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mSMSModels.size() - 1);
                }
            };
            mainHandler.post(myRunnable);
        }
    }
}