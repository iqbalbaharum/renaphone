package co.devcon.renaphone;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;

import org.restcomm.android.sdk.RCConnection;
import org.restcomm.android.sdk.RCConnectionListener;
import org.restcomm.android.sdk.RCDevice;
import org.restcomm.android.sdk.RCDeviceListener;
import org.restcomm.android.sdk.RCPresenceEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

import co.devcon.renaphone.data.adapter.MessageAdapter;
import co.devcon.renaphone.data.model.TranslatedMessage;

public class MainActivity extends AppCompatActivity implements RCDeviceListener, RCConnectionListener, ServiceConnection{

    private final static String TAG = "MAINACTIVITY";
    private final static int PERMISSION_REQUEST_DANGEROUS = 1;

    private FloatingActionButton mFABSpeak;
    private FABProgressCircle mFABProgressDuration;
    private MessageAdapter mMessageAdapter;

    private RCDevice mDevice;
    private RCConnection mConnection;
    private HashMap<String, Object> mConnectParams;
    private boolean bServiceBound = false;

    private boolean bSpeakToggle = false;

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, RCDevice.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(bServiceBound) {
            unbindService(this);
            bServiceBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFABSpeak = (FloatingActionButton) findViewById(R.id.fab_speak);
        mFABSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSpeakButton();
            }
        });

        // duration complete
        mFABProgressDuration = (FABProgressCircle) findViewById(R.id.fab_progressCircle);
        mFABProgressDuration.attachListener(new FABProgressListener() {
            @Override
            public void onFABProgressAnimationEnd() {
                toggleSpeakButton();
            }
        });

        // recyclerview
        mMessageAdapter = new MessageAdapter(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mMessageAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mConnection != null) {
            if(mConnection.getState() == RCConnection.ConnectionState.CONNECTED) {
                mConnection.disconnect();
            } else {
                mConnection = null;
            }
        }
    }

    /***
     * Change the UI of speak button
     */
    private void toggleSpeakButton() {

        bSpeakToggle = !bSpeakToggle;

        if(bSpeakToggle) {
            buttonUIActive();
        } else {
            buttonUINormal();
        }
    }


    private void buttonUINormal() {

        hangUp();

        mFABSpeak.setImageResource(R.drawable.ic_mic_24dp);
        mFABSpeak.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
        mFABProgressDuration.hide();
    }

    private void buttonUIActive() {

        callSpeechCentre();

        mFABSpeak.setImageResource(R.drawable.ic_stop_black_24dp);
        mFABSpeak.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorActive)));
        mFABProgressDuration.show();
    }

    /***
     * Call centre equipped with Speech recognition
     */
    private void callSpeechCentre() {

        if(mConnection != null)
            return;

        mConnectParams = new HashMap<>();
        mConnectParams.put(RCConnection.ParameterKeys.CONNECTION_PEER, "bob");
        mConnectParams.put(RCConnection.ParameterKeys.CONNECTION_VIDEO_ENABLED, false);
        mConnectParams.put(RCConnection.ParameterKeys.CONNECTION_LOCAL_VIDEO, findViewById(R.id.local_video_layout));
        mConnectParams.put(RCConnection.ParameterKeys.CONNECTION_REMOTE_VIDEO, findViewById(R.id.remote_video_layout));

        handlePermissions();
    }

    private void handleMessage(Intent intent) {

        if(intent.getAction() == RCDevice.ACTION_INCOMING_MESSAGE) {

            String message = intent.getStringExtra(RCDevice.EXTRA_MESSAGE_TEXT);
            String from = intent.getStringExtra(RCDevice.EXTRA_DID).replaceAll("^sip:", "").replaceAll("@.*$", "");

            mMessageAdapter.addMessage(new TranslatedMessage(from, message, ""));
        }
    }

    /***
     * Hang up call
     */
    private void hangUp() {

        if(mConnection == null)
            return;

        mConnection.disconnect();
        mConnection = null;
    }

    /**********************************************************************************************
     *                                  RCDeviceListener
     **********************************************************************************************/
    @Override
    public void onInitialized(RCDevice device, RCConnectivityStatus connectivityStatus, int statusCode, String statusText) {

    }

    @Override
    public void onInitializationError(int errorCode, String errorText) {

    }

    @Override
    public void onStartListening(RCDevice device, RCConnectivityStatus connectivityStatus) {

    }

    @Override
    public void onStopListening(RCDevice device) {

    }

    @Override
    public void onReleased(RCDevice device, int statusCode, String statusText) {

    }

    @Override
    public void onMessageSent(RCDevice device, int statusCode, String statusText) {

    }

    @Override
    public void onStopListening(RCDevice device, int errorCode, String errorText) {

    }

    @Override
    public void onConnectivityUpdate(RCDevice device, RCConnectivityStatus connectivityStatus) {

    }

    @Override
    public boolean receivePresenceEvents(RCDevice device) {
        return false;
    }

    @Override
    public void onPresenceChanged(RCDevice device, RCPresenceEvent presenceEvent) {

    }

    /********************************** RCDeviceListener END **************************************/

    /********************************** RCConnectionListener **************************************/

    @Override
    public void onConnecting(RCConnection connection) {

    }

    @Override
    public void onConnected(RCConnection connection, HashMap<String, String> customHeaders) {

    }

    @Override
    public void onDisconnected(RCConnection connection) {
        Log.i(TAG, "DISCONNECTED");
        mConnection = null;
    }

    @Override
    public void onDigitSent(RCConnection connection, int statusCode, String statusText) {

    }

    @Override
    public void onCancelled(RCConnection connection) {

    }

    @Override
    public void onDeclined(RCConnection connection) {
        Log.i(TAG, "DECLINED");
        mConnection = null;
    }

    @Override
    public void onDisconnected(RCConnection connection, int errorCode, String errorText) {
        Log.i(TAG, "DISCONNECTED - " + errorText);
        mConnection = null;
    }

    @Override
    public void onError(RCConnection connection, int errorCode, String errorText) {
        Log.e(TAG, errorText + " (" + errorCode + ")");
    }

    @Override
    public void onLocalVideo(RCConnection connection) {

    }

    @Override
    public void onRemoteVideo(RCConnection connection) {

    }

    /********************************** RCConnectionListener END **********************************/

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "Service Connected");

        RCDevice.RCDeviceBinder binder = (RCDevice.RCDeviceBinder) iBinder;
        mDevice = binder.getService();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        HashMap<String, Object> params = new HashMap<>();
        params.put(RCDevice.ParameterKeys.INTENT_INCOMING_CALL, intent);
        params.put(RCDevice.ParameterKeys.INTENT_INCOMING_MESSAGE, intent);
        params.put(RCDevice.ParameterKeys.SIGNALING_DOMAIN, "rena.devcon.co");
        params.put(RCDevice.ParameterKeys.SIGNALING_USERNAME, "#");
        params.put(RCDevice.ParameterKeys.SIGNALING_PASSWORD, "#");
        params.put(RCDevice.ParameterKeys.MEDIA_ICE_URL, "https://service.xirsys.com/ice");
        params.put(RCDevice.ParameterKeys.MEDIA_ICE_USERNAME, "atsakiridis");
        params.put(RCDevice.ParameterKeys.MEDIA_ICE_PASSWORD, "4e89a09e-bf6f-11e5-a15c-69ffdcc2b8a7");
        params.put(RCDevice.ParameterKeys.MEDIA_TURN_ENABLED, true);
        //params.put(RCDevice.ParameterKeys.SIGNALING_SECURE_ENABLED, true);

        if(!mDevice.isInitialized()) {
            mDevice.initialize(getApplicationContext(), params, this);
            mDevice.setLogLevel(Log.VERBOSE);
        }

        bServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        if(bServiceBound) {
            Log.i(TAG, "Service Disconnected");
            bServiceBound = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        // check for message
        handleMessage(intent);
    }

    // Handle android permissions needed for Marshmallow (API 23) devices or later
    private boolean handlePermissions()
    {
        ArrayList<String> permissions = new ArrayList<>(Arrays.asList(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.USE_SIP}));
        if (!havePermissions(permissions)) {
            // Dynamic permissions where introduced in M
            // PERMISSION_REQUEST_DANGEROUS is an app-defined int constant. The callback method (i.e. onRequestPermissionsResult) gets the result of the request.
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_DANGEROUS);

            return false;
        }

        resumeCall();

        return true;
    }

    // Checks if user has given 'permissions'. If it has them all, it returns true. If not it returns false and modifies 'permissions' to keep only
    // the permission that got rejected, so that they can be passed later into requestPermissions()
    private boolean havePermissions(ArrayList<String> permissions)
    {
        boolean allGranted = true;
        ListIterator<String> it = permissions.listIterator();
        while (it.hasNext()) {
            if (ActivityCompat.checkSelfPermission(this, it.next()) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
            else {
                // permission granted, remove it from permissions
                it.remove();
            }
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_DANGEROUS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    resumeCall();

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.e(TAG, "Error: Permission(s) denied; aborting call");
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request
        }
    }

    // Resume call after permissions are checked
    private void resumeCall()
    {
        if (mConnectParams != null) {
            // outgoing call
            mConnection = mDevice.connect(mConnectParams, this);
            if (mConnection == null) {
                Log.e(TAG, "Error: error connecting");
            }
        }
    }
}
