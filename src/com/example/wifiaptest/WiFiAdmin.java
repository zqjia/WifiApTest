package com.example.wifiaptest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WiFiAdmin {

    private static final String TAG = "WiFiManager";
    
    private Context mContext;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private WifiLock mWifiLock;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
       
    private static final int TYPE_NOPASSWORD = 1;
    private static final int TYPE_WEP = 2;
    private static final int TYPE_WPA = 3;
    
    
    public WiFiAdmin(Context context) {
        this.mContext = context;
        this.mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
    }
    
    public void openWifi() {
        if(!this.mWifiManager.isWifiEnabled()){
            this.mWifiManager.setWifiEnabled(true);
        }
    }
    
    public void closeWifi() {
        if(this.mWifiManager.isWifiEnabled()){
            this.mWifiManager.setWifiEnabled(false);
        }
    }
    
    public int getWifiState() {
        return this.mWifiManager.getWifiState();
    }
    
/*    public void acquireWifiLock(){
       this.mWifiLock.acquire();
    }
    
    public void releaseWifiLock(){
        if(this.mWifiLock.isHeld()){
            this.mWifiLock.release();
        }
    }
    
    public void createWifiLock(){
        this.mWifiLock = this.mWifiManager.createWifiLock("test");
    }*/
    
    public void startScan(){
        //这里得到的是配置好的网络连接，不过应用在我们的应用中需要配置一下，
        //符合我们的应用特点的网络才是我们需要的,所以我们需要对扫面的结果进行一些过滤操作
        //暂时先获取到所有的wifi
        if(this.mWifiManager.startScan()){
            this.mWifiList = this.mWifiManager.getScanResults();
        }
              
        this.mWifiConfiguration = this.mWifiManager.getConfiguredNetworks();
        
        
    }
    
    public List<ScanResult> getWifiForFileShared(){
        
        List<ScanResult> wifiForFileSharedList = new ArrayList<ScanResult>();
        
        for(ScanResult item : this.mWifiList){
            Log.d(TAG, "ssid is " + item.SSID);
            
            if(item.SSID.startsWith("zqjia_")){
                wifiForFileSharedList.add(item);
            }
        }
        
        return wifiForFileSharedList;
    }
    
    public boolean saveAndConnect(String ssid){
        
        return addNetwork(ssid, "123456789", TYPE_WPA);
        
    }
    
    
    public List<WifiConfiguration> getWifiConfigurationList(){
        
        this.mWifiConfiguration = this.mWifiManager.getConfiguredNetworks();
        return this.mWifiConfiguration;
    }
    
    public List<ScanResult> getWifiList(){
        return this.mWifiList;
    }
    
    
    public boolean addNetwork(WifiConfiguration wcg){
        //这里需要打开wifi，则需要关闭热点
        //WiFiApAdmin。closeWifiAP(mContext);
        
        int wcgID = this.mWifiManager.addNetwork(wcg);
        boolean result = this.mWifiManager.enableNetwork(wcgID, true);
        return result;
    }
    
    public boolean addNetwork(String ssid, String password, int type) {
        if(ssid == null || password == null || ssid.equals("")){
            if(DebugUtil.isDebug){
                Log.e(TAG, "addNetwork :null point error");
            }
            return false;
        }
        
        if(!(type == TYPE_NOPASSWORD || type == TYPE_WEP || type == TYPE_WPA)){
            if(DebugUtil.isDebug){
                Log.e(TAG, "addNetwork: type is unknow " + type);
            }
            return false;
           
        }
        
        return  addNetwork(createWifiInfo(ssid, password, type));
    }
    
    public WifiConfiguration createWifiInfo(String SSID, String password, int type){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        
        config.SSID = "\"" + SSID + "\"";
        
        WifiConfiguration tempConfig = null;
        if((tempConfig = isExist(SSID)) != null){
            this.mWifiManager.removeNetwork(tempConfig.networkId);
        }
        
        if(type == TYPE_NOPASSWORD){
            //have no password
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }else if(type == TYPE_WEP){
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\""; 
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }else if(type == TYPE_WPA){
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        
        return config;
    }
    
    private WifiConfiguration isExist(String SSID){
        List<WifiConfiguration> existConfigs = this.mWifiManager.getConfiguredNetworks();
        for(WifiConfiguration existConfig : existConfigs){
            if(existConfig.SSID.equals("\"" + SSID + "\"")){
                return existConfig;
            }
        }
        return null;
    }
    
    
    
    
}
