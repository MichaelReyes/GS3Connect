package ph.com.gs3.connect.presenter.adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.connect.R;

/**
 * Created by Ervinne Sodusta on 7/15/2015.
 */
public class DeviceListViewAdapter extends BaseAdapter {

    private Context context;
    private List<WifiP2pDevice> deviceList;

    public DeviceListViewAdapter(Context context) {
        this.context = context;
        deviceList = new ArrayList<>();
    }

    public void setDeviceList(List<WifiP2pDevice> deviceList) {
        this.deviceList.clear();
        this.deviceList.addAll(deviceList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class DeviceListItemViewHolder {

        final TextView tvHeader;
        final TextView tvBody;

        public DeviceListItemViewHolder(View view) {

            tvHeader = (TextView) view.findViewById(R.id.DeviceLI_tvHeader);
            tvBody = (TextView) view.findViewById(R.id.DeviceLI_tvBody);

        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeviceListItemViewHolder viewHolder = null;

        WifiP2pDevice device = (WifiP2pDevice) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_device_list_item, parent, false);

            viewHolder = new DeviceListItemViewHolder(row);
            row.setTag(viewHolder);

        }

        viewHolder = (DeviceListItemViewHolder) row.getTag();
        viewHolder.tvHeader.setText(device.deviceName);
        viewHolder.tvBody.setText(device.deviceAddress);

        return row;
    }
}
