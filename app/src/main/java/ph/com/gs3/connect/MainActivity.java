package ph.com.gs3.connect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.connect.presenter.fragments.MainViewFragment;

public class MainActivity extends Activity implements MainViewFragment.MainViewFragmentEventListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private MainViewFragment mainViewFragment;

    private WifiP2pManager wifiP2PManager;
    private WifiP2pManager.Channel channel;

    private IntentFilter wifiP2PIntentFilter;

    //<editor-fold desc="Life Cycle Implementation">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mainViewFragment = new MainViewFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mainViewFragment, MainViewFragment.TAG)
                    .commit();
        } else {
            mainViewFragment = (MainViewFragment) getFragmentManager().findFragmentByTag(MainViewFragment.TAG);
        }

        wifiP2PManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2PManager.initialize(this, getMainLooper(), null);

        wifiP2PIntentFilter = new IntentFilter();
        wifiP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiDirectBroadcastReceiver, wifiP2PIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiDirectBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="View Fragment Implementation">

    @Override
    public void onViewReady() {
        searchPeers();
    }

    @Override
    public void onScanDevicesCommand() {
        searchPeers();
    }
    //</editor-fold>

    private void searchPeers() {
        wifiP2PManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Successfully discovered peers");
            }

            @Override
            public void onFailure(int reason) {

                String message;

                switch (reason) {
                    case WifiP2pManager.BUSY:
                        message = "Failed to discover peers, manager is busy";
                        break;
                    case WifiP2pManager.ERROR:
                        message = "There was an error trying to search for peers";
                        break;
                    case WifiP2pManager.NO_SERVICE_REQUESTS:
                        message = "Failed to discover peers, no service requests";
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        message = "Failed to discover peers, peer to peer is not supported on this device";
                        break;
                    default:
                        message = "Failed to discover peers, unable to determine why.";
                }

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                Log.v(TAG, message);

            }
        });
    }

    //<editor-fold desc="Broadcast Receivers">
    private BroadcastReceiver wifiDirectBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    Log.v(TAG, "P2P enabled");
                } else {
                    // Wi-Fi P2P is not enabled
                    Log.v(TAG, "P2P not enabled");
                }


            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers

                if (wifiP2PManager != null) {
                    wifiP2PManager.requestPeers(channel, peerListListener);
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                Log.v(TAG, "Connection Changed");
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
                Log.v(TAG, "Device Changed");
            }
        }
    };

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {

            List<WifiP2pDevice> deviceList = new ArrayList();
            deviceList.addAll(peers.getDeviceList());

            String message = deviceList.size() + " Peer(s) available";

            Log.v(TAG, message);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

            mainViewFragment.setAvailableDevices(deviceList);

        }
    };
    //</editor-fold>

}
