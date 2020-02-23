package com.eso.bluetoothfinder;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.logging.Logger;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    ListView mListView;
    TextView mStatusText;
    Button mSearchBtn;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> bluetoothDevice = new ArrayList<>();
    ArrayList<String> addresss = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String deviceString = "";
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mStatusText.setText("Finished");
                mSearchBtn.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (!addresss.contains(address)) {
                    addresss.add(address);
                    if (name == null || name.equals("")) {
                        deviceString = address + " -RSSI " + rssi + "dBm";
                    } else {
                        deviceString = name + " -RSSI " + rssi + "dBm";
                    }
                    bluetoothDevice.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.listView);
        mStatusText = findViewById(R.id.statusText);
        mSearchBtn = findViewById(R.id.search_btn);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bluetoothDevice);
        mListView.setAdapter(arrayAdapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void searchClicked(View view) {
        mStatusText.setText("Searching...");
        mSearchBtn.setEnabled(false);
        bluetoothAdapter.startDiscovery();
    }

}
