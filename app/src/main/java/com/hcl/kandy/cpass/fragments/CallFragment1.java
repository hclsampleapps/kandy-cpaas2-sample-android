package com.hcl.kandy.cpass.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hcl.kandy.cpass.App;
import com.hcl.kandy.cpass.R;
import com.rbbn.cpaas.mobile.CPaaS;
import com.rbbn.cpaas.mobile.call.api.CallApplicationListener;
import com.rbbn.cpaas.mobile.call.api.CallInterface;
import com.rbbn.cpaas.mobile.call.api.CallService;
import com.rbbn.cpaas.mobile.call.api.CallState;
import com.rbbn.cpaas.mobile.call.api.IncomingCallInterface;
import com.rbbn.cpaas.mobile.call.api.MediaAttributes;
import com.rbbn.cpaas.mobile.call.api.OutgoingCallInterface;
import com.rbbn.cpaas.mobile.core.webrtc.view.VideoView;
import com.rbbn.cpaas.mobile.utilities.exception.MobileError;
import com.rbbn.cpaas.mobile.utilities.services.ServiceInfo;
import com.rbbn.cpaas.mobile.utilities.services.ServiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aman on 2/4/2019.
 */
public class CallFragment1 extends BaseFragment implements View.OnClickListener {
    String TAG = "Call Functionality";
    VideoView remoteVideoView, localVideoView;
    private EditText mEtDestination;

    public CallFragment1() {
    }

    public static CallFragment1 newInstance() {
        return new CallFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartSMS:
//                startCall(mEtDestination.getText().toString());
                break;
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_call1, container, false);
        View startcall = inflate.findViewById(R.id.startcall);
        mEtDestination = inflate.findViewById(R.id.etDestainationAddress);
        startcall.setOnClickListener(this);

        remoteVideoView = inflate.findViewById(R.id.remoteVideoView);
        localVideoView = inflate.findViewById(R.id.localVideoView);
        Context context = getContext();
        if (context != null)
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
        services.add(new ServiceInfo(ServiceType.CALL, true));
        CPaaS cpass = applicationContext.getCpass();
        CallService  callService = cpass.getCallService();

        for (CallInterface call : callService.getActiveCalls()) {
//            if (call.getId().equals(callId))
//                return call;
        }
       try{
           CallApplicationListener listnerCall=new CallApplicationListener() {
               @Override
               public void incomingCall(IncomingCallInterface call) {
                   Log.i(TAG, "incomingCall: id is " + call.getId());
                   call.setRemoteVideoView(remoteVideoView);
                   call.setLocalVideoView(localVideoView);
                   call.acceptCall(true);

                   // For audio call
//                   call.acceptCall(false);
               }

               @Override
               public void callStatusChanged(CallInterface callInterface, CallState callState) {

               }

               @Override
               public void mediaAttributesChanged(CallInterface callInterface, MediaAttributes mediaAttributes) {

               }

               @Override
               public void callAdditionalInfoChanged(CallInterface callInterface, Map<String, String> map) {

               }

               @Override
               public void errorReceived(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void errorReceived(MobileError mobileError) {

               }

               @Override
               public void establishCallSucceeded(OutgoingCallInterface outgoingCallInterface) {

               }

               @Override
               public void establishCallFailed(OutgoingCallInterface outgoingCallInterface, MobileError mobileError) {

               }

               @Override
               public void acceptCallSucceed(IncomingCallInterface incomingCallInterface) {

               }

               @Override
               public void acceptCallFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {

               }

               @Override
               public void rejectCallSucceeded(IncomingCallInterface incomingCallInterface) {

               }

               @Override
               public void rejectCallFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {

               }

               @Override
               public void ignoreSucceed(IncomingCallInterface incomingCallInterface) {

               }

               @Override
               public void ignoreFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {

               }

               @Override
               public void videoStopSucceed(CallInterface callInterface) {

               }

               @Override
               public void videoStopFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void videoStartSucceed(CallInterface callInterface) {

               }

               @Override
               public void videoStartFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void muteCallSucceed(CallInterface callInterface) {

               }

               @Override
               public void muteCallFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void unMuteCallSucceed(CallInterface callInterface) {

               }

               @Override
               public void unMuteCallFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void holdCallSucceed(CallInterface callInterface) {

               }

               @Override
               public void holdCallFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void unHoldCallSucceed(CallInterface callInterface) {

               }

               @Override
               public void unHoldCallFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void endCallSucceeded(CallInterface callInterface) {

               }

               @Override
               public void endCallFailed(CallInterface callInterface, MobileError mobileError) {

               }

               @Override
               public void ringingFeedbackSucceeded(IncomingCallInterface incomingCallInterface) {

               }

               @Override
               public void ringingFeedbackFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {

               }

               @Override
               public void notifyCallProgressChange(CallInterface callInterface) {

               }
           };

           callService.setCallApplicationListener(listnerCall);
       }catch (Exception e){
           e.printStackTrace();
       }

    }
    private String parseCalleeAddress(String callee) {
        if (callee.contains("@")) {
            return callee;
        } else {
//            String domain = getLoggedInUsername().split("@")[1];
            return "";//TextUtils.isEmpty(domain) ? callee : callee + "@" + domain;
        }
    }

//    private void startCall(String participant) {
//        String callee = parseCalleeAddress(participant);
//
//        callService.createOutgoingCall(callee, new CallApplicationListener() {
//            @Override
//            public void incomingCall(IncomingCallInterface incomingCallInterface) {
//                System.out.println("call started=================");
//            }
//
//            @Override
//            public void callStatusChanged(CallInterface callInterface, CallState callState) {
//                System.out.println("call started================="+callState.getStatusCode());
//            }
//
//            @Override
//            public void mediaAttributesChanged(CallInterface callInterface, MediaAttributes mediaAttributes) {
//
//            }
//
//            @Override
//            public void callAdditionalInfoChanged(CallInterface callInterface, Map<String, String> map) {
//
//            }
//
//            @Override
//            public void errorReceived(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void errorReceived(MobileError mobileError) {
//
//            }
//
//            @Override
//            public void establishCallSucceeded(OutgoingCallInterface outgoingCallInterface) {
//
//            }
//
//            @Override
//            public void establishCallFailed(OutgoingCallInterface outgoingCallInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void acceptCallSucceed(IncomingCallInterface incomingCallInterface) {
//
//            }
//
//            @Override
//            public void acceptCallFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void rejectCallSucceeded(IncomingCallInterface incomingCallInterface) {
//
//            }
//
//            @Override
//            public void rejectCallFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void ignoreSucceed(IncomingCallInterface incomingCallInterface) {
//
//            }
//
//            @Override
//            public void ignoreFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void videoStopSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void videoStopFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void videoStartSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void videoStartFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void muteCallSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void muteCallFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void unMuteCallSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void unMuteCallFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void holdCallSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void holdCallFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void unHoldCallSucceed(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void unHoldCallFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void endCallSucceeded(CallInterface callInterface) {
//
//            }
//
//            @Override
//            public void endCallFailed(CallInterface callInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void ringingFeedbackSucceeded(IncomingCallInterface incomingCallInterface) {
//
//            }
//
//            @Override
//            public void ringingFeedbackFailed(IncomingCallInterface incomingCallInterface, MobileError mobileError) {
//
//            }
//
//            @Override
//            public void notifyCallProgressChange(CallInterface callInterface) {
//
//            }
//        }, new OutgoingCallCreationCallback() {
//            @Override
//            public void callCreated(OutgoingCallInterface callInterface) {
//                // Call successfully created, use the CallInterface object to process the call.
//                callInterface.setRemoteVideoView(remoteVideoView); // Provide a VideoView widget object to show the local video on the UI
//                callInterface.setLocalVideoView(localVideoView); // Provide a VideoView widget object to show the local video on the UI
//
//                // For Video call
//                callInterface.establishCall(true);
//
//                // For Double M-Line Audio Call
//                callInterface.establishCall(false);
//
//                // For Single M-Line Audio Call
//                callInterface.establishAudioCall();
//            }
//
//            @Override
//            public void callCreationFailed(MobileError error) {
//                // Call creation failed, handle the exception
//                Log.e(TAG, "callCreation" +
//                        "Failed: " + error.getErrorMessage());
//            }
//        });
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}