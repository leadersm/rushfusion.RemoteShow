package com.rushfusion.remoteshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.rushfusion.http.HttpServer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RemoteShowActivity extends ListActivity {

	
	private Cursor c;
	private List<HashMap<String, String>> data;
	private static final int DIALOG_EXIT = 0;
	private static final int refresh = 2;
	private static final int MENU = 1;
	private MyAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHttp();
		adapter = new MyAdapter();
		data = obtainVideos();
		if(data==null)return;
		ListView lv = getListView();
		TextView textView = new TextView(this) ;
		textView.setText("我的视频") ;
		textView.setFocusable(false);
		textView.setPadding(5, 5, 5, 5);
		textView.setTextSize(35);
		textView.setTextColor(Color.rgb(183, 255, 0));
		lv.addHeaderView(textView, null, false);
//		lv.addHeaderView(textView) ;//放在setAdapter之前，否则报错
		setListAdapter(adapter);
		lv.setBackgroundResource(R.drawable.bg);
		lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
//		lv.setDividerHeight(2);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, refresh, 0, "刷新列表").setIcon(R.drawable.icon_refresh);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case 2:
			refreshData();
			Toast.makeText(this, "列表刷新成功！", 10);
			break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initHttp() {
		HttpServer.getInstance().init(this);
		HttpServer.getInstance().startServer();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		HttpServer.getInstance().stopServer();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
//		l.setDivider(new ColorDrawable(Color.RED));
//		l.setDividerHeight(6);
		Intent i = new Intent(this,ScreenControlActivity.class);
		i.putExtra("path", data.get(position-1).get("path"));
		i.putExtra("name", data.get(position-1).get("name"));
		startActivity(i);
	}

	public List<HashMap<String, String>> obtainVideos() {
		List<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
		ContentResolver contentResolver = getContentResolver();
		String[] video = new String[] { MediaStore.Video.Media.DATA,
				                        MediaStore.Video.Media.TITLE };
		c = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
								  video, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		if(c!=null)c.moveToFirst();
		else return null;
		for (int i = 0; i < c.getCount(); i++) {
			HashMap<String, String> file = new HashMap<String, String>();
			String path = c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
			String name = c.getString(c.getColumnIndex(MediaStore.Video.Media.TITLE));
			System.out.println("--name-->" + name + "path" + path);
			file.put("name", name);
			file.put("path", path);
			files.add(file);
			c.moveToNext();
		}
		c.close();
		return files;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
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
//			parent.setPadding(200, 0, 200, 0);
			if (convertView == null) {
				convertView = LinearLayout.inflate(RemoteShowActivity.this,R.layout.listitem, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.position = position;
			holder.mTextView.setTextSize(30);
			holder.mTextView.setText(data.get(position).get("name"));
			return convertView;
		}

		class ViewHolder {
			int position;
			TextView mTextView;

			public ViewHolder(View v) {
				super();
				mTextView = (TextView) v.findViewById(R.id.videoName);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			showDialog(DIALOG_EXIT);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void refreshData() {
		// TODO Auto-generated method stub
		data = obtainVideos();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id == DIALOG_EXIT){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("确定要退出吗？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(-1) ;
					//finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(0);
				}
			});
			return builder.create();
		}                                                                                                                                           
		return null;
	}

}
