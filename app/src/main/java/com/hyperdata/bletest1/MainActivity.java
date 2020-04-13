package com.hyperdata.bletest1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final int REQUEST_ENABLE_BT = 1;
    private TextView mText;
    private EditText mEditText;
    private Button mAdvertiseButton;
    private Button mStopAdvertiseButton;
    private Button mDiscoverButton;

    BluetoothLeAdvertiser advertiser;
    AdvertiseCallback advertisingCallback;


    private BluetoothAdapter bluetoothAdapter;
    private AdvertisingSet currentAdvertisingSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE", "BLUETOOTH not granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE", "BLUETOOTH_ADMIN not granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE", "ACCESS_COARSE_LOCATION not granted");
        }

        mText = (TextView) findViewById( R.id.text );
        mEditText = (EditText) findViewById( R.id.edit_text );
        mDiscoverButton = (Button) findViewById( R.id.discover_btn );
        mAdvertiseButton = (Button) findViewById( R.id.advertise_btn );
        mStopAdvertiseButton = (Button) findViewById( R.id.stop_btn );

        mDiscoverButton.setOnClickListener( this );
        mAdvertiseButton.setOnClickListener( this );
        mStopAdvertiseButton.setOnClickListener(this);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if( !bluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Log.e( "BLE", "Multiple Advertisement not supported" );
            Toast.makeText( this, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
            mAdvertiseButton.setEnabled( false );
            mDiscoverButton.setEnabled( false );
        }

        advertiser = bluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();


        advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }

        };


    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.discover_btn){
             discover();

        } else if (v.getId() == R.id.stop_btn){
            stopAdvertise();

        }else if (v.getId() == R.id.advertise_btn){
            advertise();
        }
    }

    private void discover(){
        Log.e("BLE","Discover clicked");
    }

    private void stopAdvertise(){
        Log.e("BLE",advertisingCallback.toString());
        advertiser.stopAdvertising(advertisingCallback);
    }

    private void advertise(){

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        String localName = mEditText.getText().toString();

        if (localName == null || localName.isEmpty()){
            localName = "01234567";
        }



        bluetoothAdapter.getDefaultAdapter().setName(localName);

        mText.setText(bluetoothAdapter.getName());
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString("1852BC70-3848-4F5C-B0A2-F45B4746F3D4"));
        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid( pUuid )
                .setIncludeDeviceName( true )
                .build();



        advertiser.startAdvertising( settings, data, advertisingCallback );
        advertiser.stopAdvertising(advertisingCallback);
        mText.setText(bluetoothAdapter.getName());
        advertiser.startAdvertising( settings, data, advertisingCallback );



    }

}
