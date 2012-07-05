package com.rushfusion.remoteshow;

import com.android.rushfusion.http.HttpServer;

import android.app.Activity;
import android.os.Bundle;

public class RemoteShowActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initHttp();
    }
    
    
    private void initHttp(){    	
    	HttpServer.getInstance().init(this);
		HttpServer.getInstance().startServer();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	HttpServer.getInstance().stopServer();
    }

}