package com.rushfusion.remoteshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.rushfusion.http.HttpServer;

public class RemoteShowActivity extends ListActivity {

	
	private Cursor c;
	private List<HashMap<String, String>> data;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = obtainVideos();
		setListAdapter(new MyAdapter());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this,ScreenControlActivity.class);
		i.putExtra("path", data.get(position).get("path"));
		i.putExtra("name", data.get(position).get("name"));
		startActivity(i);
	}

	public List<HashMap<String, String>> obtainVideos() {
		List<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
		ContentResolver contentResolver = getContentResolver();
		String[] paths = new String[] { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.TITLE };
		c = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				paths, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			HashMap<String, String> file = new HashMap<String, String>();
			String path = c.getString(c
					.getColumnIndex(MediaStore.Video.Media.DATA));
			String name = c.getString(c
					.getColumnIndex(MediaStore.Video.Media.TITLE));
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
			if (convertView == null) {
				convertView = LinearLayout.inflate(RemoteShowActivity.this,
						R.layout.listitem, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.position = position;
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

}
