package ph.com.gs3.connect.presenter.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ph.com.gs3.connect.R;
import ph.com.gs3.connect.presenter.adapters.DeviceListViewAdapter;

/**
 * Created by Ervinne Sodusta on 7/15/2015.
 */
public class MainViewFragment extends Fragment {

    public static final String TAG = MainViewFragment.class.getSimpleName();

    private ListView lvAvailableDevices;
    private Button bScanDevices;

    private DeviceListViewAdapter deviceListViewAdapter;

    private MainViewFragmentEventListener mainViewFragmentEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainViewFragmentEventListener = (MainViewFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(
                    String.format("%s must implement %s",
                            activity.getClass().getSimpleName(),
                            MainViewFragmentEventListener.class.getSimpleName()
                    ));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        deviceListViewAdapter = new DeviceListViewAdapter(getActivity());

        lvAvailableDevices = (ListView) rootView.findViewById(R.id.Main_lvAvailableDevices);
        lvAvailableDevices.setAdapter(deviceListViewAdapter);

        bScanDevices = (Button) rootView.findViewById(R.id.Main_bScan);
        bScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewFragmentEventListener.onScanDevicesCommand();
            }
        });

        mainViewFragmentEventListener.onViewReady();

        return rootView;
    }

    public void setAvailableDevices(List<WifiP2pDevice> devices) {
        deviceListViewAdapter.setDeviceList(devices);
    }

    public interface MainViewFragmentEventListener {

        void onScanDevicesCommand();

        void onViewReady();

    }

}
