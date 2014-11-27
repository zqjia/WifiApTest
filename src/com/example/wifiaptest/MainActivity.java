package com.example.wifiaptest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    
    private Button setupApButton;
    private Button closeApButton;
    private Button openWifi;
    private Button closeWifi;
    private Button scanWifi;
    private ListView mListView;
    
    private WiFiAdmin mWifiAdmin;
    
    private ProgressDialog progressDialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.setupApButton = (Button)this.findViewById(R.id.setup_ap);
        this.closeApButton = (Button)this.findViewById(R.id.close_ap);
        this.openWifi = (Button)this.findViewById(R.id.open_wifi);
        this.closeWifi = (Button)this.findViewById(R.id.close_wifi);
        this.scanWifi = (Button)this.findViewById(R.id.scan_wifi);
        this.mListView = (ListView)this.findViewById(R.id.wifi_list);
        
        this.mWifiAdmin = new WiFiAdmin(this);
        
        this.setupApButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        this.openWifi.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity.this.mWifiAdmin.openWifi();
                
               while(MainActivity.this.mWifiAdmin.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
                   if(DebugUtil.isDebug){
                       Log.d(TAG, "wait for wifi enbaled");
                   }
                   
                   try {
                       Thread.sleep(300);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
               }
               
               Toast.makeText(MainActivity.this, "打开wifi成功", Toast.LENGTH_SHORT).show();
            }
            
        });
        
        this.closeWifi.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               
                MainActivity.this.mWifiAdmin.closeWifi();
                
                while(MainActivity.this.mWifiAdmin.getWifiState() != WifiManager.WIFI_STATE_DISABLED){
                    if(DebugUtil.isDebug){
                        Log.d(TAG, "wait for wifi disbaled");
                    }
                    
                    try {
                        Thread.sleep(300);
                     } catch (InterruptedException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                }
                
               
                Toast.makeText(MainActivity.this, "关闭wifi成功", Toast.LENGTH_SHORT).show();
            }
            
        });
        
        this.scanWifi.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity.this.mWifiAdmin.startScan();
                
                WifiAdapter adapter = new WifiAdapter(MainActivity.this, MainActivity.this.mWifiAdmin.getWifiForFileShared());
                MainActivity.this.mListView.setAdapter(adapter);
                MainActivity.this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        ScanResult item = (ScanResult) parent.getItemAtPosition(position);
                        MainActivity.this.mWifiAdmin.saveAndConnect(item.SSID);
                        
                       
                    }

                  
                });
            }
            
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
