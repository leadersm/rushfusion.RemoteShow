package com.rushfusion.remoteshow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rushfusion.remoteshow.bean.STB;
import com.rushfusion.remoteshow.util.MscpDataParser;
import com.rushfusion.remoteshow.util.XmlUtil;

public class ScreenControlActivity extends Activity {
	/** Called when the activity is first created. */
	
	private static final int PORT = 6806;
	private static final int DIALOG_NETWORK = 0;
	private static final int DIALOG_PROGRESS = 1;
	
	private static final int PRIORITY = 7;
	
	
	private TextView mIpTV;
	private Button searchBtn, clearBtn;
	private LayoutInflater inflater;
	private String localIp = "";

	private List<STB> stbs;
	private LinearLayout stblist;
	private Handler handler;
	private DatagramSocket s = null;
	String fileName,path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent intent = getIntent();
		fileName = intent.getStringExtra("name");
		path = intent.getStringExtra("path");
		init();
	}

	private void init() {
		if (checkNetworking(this)) {
			try {
				if(stbs==null)stbs = new ArrayList<STB>();
				s = new DatagramSocket(PORT);
				findByIds();
				Thread mReceiveThread = new Thread(updateThread);
				mReceiveThread.setPriority(PRIORITY);
				mReceiveThread.start();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		} else
			showDialog(DIALOG_NETWORK);
	}

	/**
	 * 检查网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetworking(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if (nwi != null) {
			return nwi.isAvailable();
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NETWORK:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("网络连接提示");
			builder.setMessage("当前没有可用网络，是否设置?")
					.setCancelable(false)
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
									startActivity(intent);
									init();
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									finish();
								}
							});
			return builder.create();
		case DIALOG_PROGRESS:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("提示!");
			dialog.setMessage("正在搜索,请稍候...");
			return dialog;
		default:
			break;
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	private void findByIds() {
		inflater = LayoutInflater.from(this);
		mIpTV = (TextView) findViewById(R.id.mIp);
		searchBtn = (Button) findViewById(R.id.search);
		searchBtn.setEnabled(false);
		clearBtn = (Button) findViewById(R.id.clear);
		stblist = (LinearLayout) findViewById(R.id.list);
		localIp = getLocalIpAddress();
		mIpTV.setText("本机ip-->" + localIp+"  名称："+fileName+"  路径:"+path);
		
		SharedPreferences sp = getSharedPreferences("RemoteShow", Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = sp.edit();
		Map<String,String> data = (Map<String, String>) sp.getAll();
		if(data.keySet().size()<=0){
			searchBtn.setEnabled(true);
		}
		for(String key:data.keySet()){
			STB stb = new STB();
			String name = data.get(key);
			stb.setIp(key);
			stb.setUsername(name);
			stblist.addView(getView(stb));
		}
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				STB stb = (STB) msg.obj;
				editor.putString(stb.getIp(),stb.getUsername());
				stblist.addView(getView(stb));
				editor.commit();
			}
		};
		
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBtn.setEnabled(false);
				showDialog(DIALOG_PROGRESS);
//				localIp = getLocalIpAddress();// "192.168.2.xxx";
				final String destIp = localIp.substring(0,localIp.lastIndexOf(".") + 1);
				System.out.println("destIp---->" + destIp);
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 2; i < 255; i++) {
							if (!localIp.equals(destIp + i))
								search(destIp + i);
						}
						try {
							dismissDialog(DIALOG_PROGRESS);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}).start();
			}
		});
		
		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchBtn.setEnabled(true);
				stblist.removeAllViews();
				editor.clear();
				editor.commit();
				stbs.clear();
			}
		});

	}
	public View getView(STB stb) {
		ViewHolder holder = new ViewHolder();
		View view = inflater.inflate(R.layout.stbitem, null);
		holder.name = (TextView) view.findViewById(R.id.stbName);
		holder.ip = (TextView) view.findViewById(R.id.stbIp);
		holder.play = (Button) view.findViewById(R.id.play);
		holder.pause = (Button) view.findViewById(R.id.pause);
		holder.stop = (Button) view.findViewById(R.id.stop);
		holder.seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
		holder.init(stb);
		return view;
	}

	public void search(String destip) {
		try {
			InetAddress stbIp = InetAddress.getByName(destip);
			byte[] data = XmlUtil.SearchReq("123456", localIp);
			DatagramPacket p = new DatagramPacket(data, data.length, stbIp,XmlUtil.STB_PORT);
			s.send(p);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress() {
		
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
						enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					String s = inetAddress.getHostAddress().toString();
//					if (!inetAddress.isLoopbackAddress()) {
//						return s;
//					}
					
					if(mActiveNetInfo.getType()==ConnectivityManager.TYPE_WIFI){
						if(s.indexOf(":")==-1 && !(s.equals("127.0.0.1"))){
							System.out.println(inetAddress.toString()+"hostname->"+inetAddress.getHostName()+"---s->"+s);
							return s;
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	Runnable updateThread = new Runnable() {
		public void run() {
			System.out.println("the receive-thread is running");
			startReceive();
		}
	};
	protected void startReceive() {
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (true) {
				s.receive(packet);
				if (packet.getLength() > 0) {
					String str = new String(buffer, 0, packet.getLength());
					System.out.println("receive-->" + str);
					MscpDataParser.getInstance().init(this);
					MscpDataParser.getInstance().parse(packet,new MscpDataParser.CallBack() {
								@Override
								public void onParseCompleted(HashMap<String, String> map) {
									if (map != null) {
										String cmd = map.get("cmd");
										if(cmd.equals("searchresp")){
											System.out.println("IP===>"+ map.get("IP"));
											if (!map.get("IP").equals("null")&& !map.get("IP").equals(localIp)) {
												STB stb = new STB(map.get("IP"),"test", map.get("username"), "test","test");
												if (!checkStbIsExist(stb)){ //如果在列表中没有该stb，则添加上
													stbs.add(stb);
													Message msg = new Message();
													msg.what = 1;
													msg.obj = stb;
													handler.sendMessageDelayed(msg, 200);
												}
											}
										}else if(cmd.equals("completeresp")){//'completeresp' 
											Looper.prepare();
											Toast.makeText(ScreenControlActivity.this, "播放完毕！！", 1000).show();
											finish();
											Looper.loop();
										}else if(cmd.equals("errorresp")){
											String errorcode = map.get("errorCode");
											Looper.prepare();
											Toast.makeText(ScreenControlActivity.this, "出错了，请检查视频格式和路径！！error code->"+errorcode, 1).show();
											finish();
											Looper.loop();
										}
									}
								}
								private boolean checkStbIsExist(STB stb) {
									for (STB temp : stbs) {
										if (temp.getIp().equals(stb.getIp()))
											return true;
									}
									return false;
								}

								@Override
								public void onError(int code, String desc) {

								}
							});
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (s != null)
			s.close();
	}

	
	private class ViewHolder {

		TextView name;
		TextView ip;
		Button play,pause,stop;
		SeekBar seekBar;
		
		public void init(final STB stb) {
			name.setText(stb.getUsername());
			ip.setText(stb.getIp());
			play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.PlayReq(1, localIp, fileName, 1000,getUrl(path));
					
					sendDataTo(stb, data);
					play.setEnabled(false);
				}
				
			});
			pause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.PauseReq(1, localIp);
					sendDataTo(stb, data);
					play.setEnabled(true);
				}
			});
			stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.StopReq(1, localIp);
					sendDataTo(stb, data);
					play.setEnabled(true);
				}
			});
			seekBar.setMax(100) ;
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				private int seekPosition;

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					try {
						Log.d("remote-show", "=======onStopTrackingTouch======") ;
						Log.d("remote-show", "seekPosition:"+seekPosition) ;
						byte[] data = XmlUtil.SeekReq(1, localIp, seekPosition) ;
						InetAddress stbIp = InetAddress.getByName(stb.getIp());
						DatagramPacket p = new DatagramPacket(data, data.length, stbIp,XmlUtil.STB_PORT);
						s.send(p);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if(fromUser) {
						seekPosition = seekBar.getProgress();
					}
				}
			}) ;
		}

		protected String getUrl(String path) {
			//http://192.168.1.104:9905/download/sdcard/video/video.mp4
			StringBuffer url = new StringBuffer("http://"+localIp.toString()+":9905/download");
			String [] temp = path.split("/");
			try {
				for(int i = 1;i<temp.length ;i++){
					System.out.println(temp[i]);
					temp[i] = URLEncoder.encode(temp[i] , "UTF-8");
					url.append('/'+temp[i]);
				}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
			}
			Log.d("RemoteShow", "url--->"+url.toString().replaceAll("&", "&amp;"));
			return url.toString().replaceAll("&", "&amp;");
		}
		
	}
	
	private void sendDataTo(final STB stb, byte[] data) {
		try {
			InetAddress stbIp = InetAddress.getByName(stb.getIp());
			DatagramPacket p = new DatagramPacket(data, data.length, stbIp,XmlUtil.STB_PORT);
			s.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(s!=null)s.close();
	}
	
}
