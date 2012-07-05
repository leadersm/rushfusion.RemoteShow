package com.rushfusion.remoteshow;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RemoteShowActivity extends Activity {
	
	private Button openBtn;
	private ListView showLV;
	private Cursor cTitle;
	private Cursor cPath;
	private int fileNum;
	private String[] videoNames ;
	private String[] videoPaths;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        showLV = (ListView)findViewById(R.id.showLocalVideoFiles);
        
        
//        =======================获取视频文件名称=======================
        ContentResolver contentResolver = getContentResolver();
        String[] titles = new String[]{MediaStore.Video.Media.TITLE};
        cTitle = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,titles,null,null,MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cTitle.moveToFirst();
        fileNum = cTitle.getCount();
        System.out.println("文件数："+fileNum);
        videoNames = new String[fileNum];
        for(int i=0;i<fileNum;i++){
        	videoNames[i] = cTitle.getString(cTitle.getColumnIndex(MediaStore.Video.Media.TITLE));
        	System.out.println("文件名称为："+cTitle.getString(cTitle.getColumnIndex(MediaStore.Video.Media.TITLE)));
        	cTitle.moveToNext();
        }
        cTitle.close();
        
        
//        =======================获取视频文件路径=======================
        String[] paths = new String[]{MediaStore.Video.Media.DATA};
        cPath = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, paths, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cPath.moveToFirst();
        videoPaths = new String[fileNum];
        for(int j=0;j<fileNum;j++){
        	videoPaths[j] = cPath.getString(cPath.getColumnIndex(MediaStore.Video.Media.DATA));
        	System.out.println("路径为："+cPath.getString(cPath.getColumnIndex(MediaStore.Video.Media.DATA)));
        	cPath.moveToNext();
        }
        cPath.close();
        
        
        
        openBtn = (Button)findViewById(R.id.openLocalVideoFiles);
        openBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLV.setAdapter(new MyAdapter());
			}
		});
        
        showLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//发送路径给service，service通知电视开始播放
				
			}
		});
    }
    
    class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fileNum;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView==null){
				convertView=LinearLayout.inflate(RemoteShowActivity.this, R.layout.listitem, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.position = position;
			holder.mTextView.setText(videoNames[position]);
//			holder.mEditText.setText(map.get(position));
			return convertView;
		}
		class ViewHolder {
			int position;
			TextView mTextView;
			public ViewHolder(View v) {
				super();
				mTextView = (TextView)v.findViewById(R.id.videoName);
			}
			
			
		}
    	
    }
}

//Map<String,String> file = new HashMap<String,String>();
//file.put("name", "");
//file.put("path", "");
//
//List<HashMap<String,String>> list ;
//list.add(file);