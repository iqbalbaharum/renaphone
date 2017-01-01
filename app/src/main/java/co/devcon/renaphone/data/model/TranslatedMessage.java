package co.devcon.renaphone.data.model;

/**
 * Created by MuhammadIqbal on 1/1/2017.
 */

public class TranslatedMessage {

    private String fromSID;
    private String message;
    private String voiceURL;

    public TranslatedMessage(String fromSID, String message, String voiceURL) {
        this.fromSID = fromSID;
        this.message = message;
        this.voiceURL = voiceURL;
    }

    public String getFromSID() {
        return fromSID;
    }

    public void setFromSID(String fromSID) {
        this.fromSID = fromSID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVoiceURL() {
        return voiceURL;
    }

    public void setVoiceURL(String voiceURL) {
        this.voiceURL = voiceURL;
    }
}
