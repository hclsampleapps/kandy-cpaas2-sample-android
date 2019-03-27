package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.adapters.SMSAdapter;
import com.hcl.kandy.cpass.models.SMSModel;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.messaging.api.InboundMessage;
import com.rbbn.cpaas.mobile.messaging.api.MessagingCallback;
import com.rbbn.cpaas.mobile.messaging.api.OutboundMessage;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSConversation;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSListener;
import com.rbbn.cpaas.mobile.messaging.sms.api.SMSService;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;
import com.rbbn.cpaas.mobile.utilities.services.ServiceInfo;
import com.rbbn.cpaas.mobile.utilities.services.ServiceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aman on 2/5/2019.
 */
public class SMSFragment extends BaseFragment implements View.OnClickListener {
    public SMSFragment() {
    }

    public static SMSFragment newInstance() {
        return new SMSFragment();
    }

    private SMSService smsService;
    private EditText mEtDestination,mEtSender;
    private EditText mEtMessage;
    private RecyclerView mRecyclerView;
    private SMSAdapter mSMSAdapter;
    private ArrayList<SMSModel> mSMSModels;

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
        Context context = getContext(); if (context != null)
            initSMSService(context);
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void initSMSService(@NonNull Context context) {
        App applicationContext = (App) context.getApplicationContext();

        List<ServiceInfo> services = new ArrayList<>();
        services.add(new ServiceInfo(ServiceType.SMS, true));

        CPaaS cpass = applicationContext.getCpass();
        smsService = cpass.getSMSService();
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
            public void SMSDeliveryStatusChanged(String s, String s1, String s2) {
                Log.d("CPaaS.SMSService", "Message delivery status changed to " + s1);
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
                mEtMessage.setText("");
            }
        });

    }

    private void sendMessage(String sender, String participant, String txt) {
        SMSConversation smsConversation = (SMSConversation) smsService.createConversation(participant,sender);

        OutboundMessage message = smsService.createMessage(txt);

        smsConversation.send(message, new MessagingCallback() {
            @Override
            public void onSuccess() {
                Log.d("CPaaS.SMSService", "Message is sent");
                showMessage(mEtMessage,"Message is sent");
            }

            @Override
            public void onFail(MobileError error) {
                Log.d("CPaaS.SMSService", "Message is failed");
                showMessage(mEtMessage,"Try again later");
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
                sendMessage(mEtSender.getText().toString(), mEtDestination.getText().toString(), mEtMessage.getText().toString());
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