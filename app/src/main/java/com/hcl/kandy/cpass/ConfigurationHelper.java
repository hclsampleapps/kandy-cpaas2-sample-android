package com.hcl.kandy.cpass;

import com.rbbn.cpaas.mobile.utilities.Configuration;
import com.rbbn.cpaas.mobile.utilities.webrtc.CodecSet;
import com.rbbn.cpaas.mobile.utilities.webrtc.ICEOptions;

public class ConfigurationHelper {

    public static void setConfigurations(String baseUrl) {
        Configuration configuration = Configuration.getInstance();
        configuration.setDTLS(true);
        configuration.setIceOption(ICEOptions.ICE_VANILLA);
        configuration.setICECollectionTimeout(1);
        setPreferedCodecs(baseUrl);
    }

    private static void setPreferedCodecs(String baseUrl) {

        Configuration configuration = Configuration.getInstance();
//        ICEServers iceServers = new ICEServers();
//        iceServers.addICEServer("turns:turn-"+baseUrl+":443?transport=tcp");
//        iceServers.addICEServer("turn:turn-"+baseUrl+":3535?transport=udp");
//        configuration.setICEServers(iceServers);

        CodecSet codecSet = new CodecSet();
//        codecSet.audioCodecs = new CodecSet.AudioCodecType[]{CodecSet.AudioCodecType.AC_G722, CodecSet.AudioCodecType.AC_OPUS};
//        codecSet.videoCodecs = new CodecSet.VideoCodecType[]{CodecSet.VideoCodecType.VC_H264, CodecSet.VideoCodecType.VC_VP9};
        configuration.setPreferredCodecSet(codecSet);
    }
}
