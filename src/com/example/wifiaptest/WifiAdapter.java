package com.example.wifiaptest;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter {

    private static final String TAG = "WifiAdapter";

    private List<ScanResult> mResult;
    private Context mContext;
    
    static class ViewHolder{
        public TextView ssid;
    }
    
    
    public WifiAdapter(Context context, List<ScanResult> result) {
        this.mResult = result;
        this.mContext = context;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.mResult.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.mResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ScanResult resultItem = this.mResult.get(position);
        
        ViewHolder viewHolder = new ViewHolder();;
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        
        if(convertView == null){
            convertView = inflater.inflate(R.layout.wifi_list_item, null);
            viewHolder.ssid = (TextView)convertView.findViewById(R.id.wifi_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        
        if(resultItem != null){
            viewHolder.ssid.setText(resultItem.SSID);
        }
        
        return convertView;
    }
    
    public void refreshList(List<ScanResult> newList) {
        this.mResult = newList;
        this.notifyDataSetChanged();
        if(DebugUtil.isDebug) {
            Log.d(TAG, "refresh data");
        }
    }
    
    public void clearList() {
        
        if (this.mResult != null && this.mResult.size() > 0) {
            this.mResult.clear();
            this.mResult = null;
        }
    }

}
