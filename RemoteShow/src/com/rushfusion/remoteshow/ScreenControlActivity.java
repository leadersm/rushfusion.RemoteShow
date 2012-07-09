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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rushfusion.http.HttpServer;
import com.rushfusion.remoteshow.bean.STB;
import com.rushfusion.remoteshow.util.MscpDataParser;
import com.rushfusion.remoteshow.util.XmlUtil;

public class ScreenControlActivity extends Activity {
	/** Called when the activity is first created. */
	
	private static final int PORT = 6806;
	private static final int DIALOG_NETWORK = 0;
	private static final int DIALOG_PROGRESS = 1;
	
	private TextView mIp;
	private Button searchBtn, clearBtn;
	private LayoutInflater inflater;
	private String localIp = "";

	private static List<STB> stbs;
	private LinearLayout stblist;
	private Handler handler;
	private DatagramSocket s = null;
	String fileName,path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initHttp();
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
			dialog.setMessage("正在搜索,请稍后...");
			return dialog;
		default:
			break;
		}
		return null;
	}

	private void initHttp() {
		HttpServer.getInstance().init(this);
		HttpServer.getInstance().startServer();
	}

	@Override
	public void onPause() {
		super.onPause();
		HttpServer.getInstance().stopServer();
	}
	private void findByIds() {
		inflater = LayoutInflater.from(this);
		mIp = (TextView) findViewById(R.id.mIp);
		searchBtn = (Button) findViewById(R.id.search);
		clearBtn = (Button) findViewById(R.id.clear);
		stblist = (LinearLayout) findViewById(R.id.list);
		mIp.setText("本机ip-->" + getLocalIpAddress()+"  名称："+fileName+"  路径:"+path);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				STB stb = (STB) msg.obj;
				stblist.addView(getView(stb));
			}

			public View getView(STB stb) {
				ViewHolder holder = new ViewHolder();
				View view = inflater.inflate(R.layout.stbitem, null);
				holder.name = (TextView) view.findViewById(R.id.stbName);
				holder.ip = (TextView) view.findViewById(R.id.stbIp);
				holder.play = (Button) view.findViewById(R.id.play);
				holder.pause = (Button) view.findViewById(R.id.pause);
				holder.stop = (Button) view.findViewById(R.id.stop);
				holder.ff = (Button) view.findViewById(R.id.ff);
				holder.fb = (Button) view.findViewById(R.id.fb);
				holder.init(stb);
				return view;
			}
			
		};
		
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBtn.setEnabled(false);
				showDialog(DIALOG_PROGRESS);
				localIp = getLocalIpAddress();// "192.168.2.xxx";
				final String destIp = localIp.substring(0,localIp.lastIndexOf(".") + 1);
				System.out.println("destIp---->" + destIp);
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 2; i < 255; i++) {
							if (!localIp.equals(destIp + i))
								search(destIp + i);
						}
						dismissDialog(DIALOG_PROGRESS);
					}
				}).start();
			}
		});
		
		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchBtn.setEnabled(true);
				stblist.removeAllViews();
				stbs.clear();
			}
		});

	}

	public void search(String destip) {
		try {
			InetAddress stbIp = InetAddress.getByName(destip);
			byte[] data = XmlUtil.SearchReq("123456", getLocalIpAddress());
			DatagramPacket p = new DatagramPacket(data, data.length, stbIp,XmlUtil.STB_PORT);
			s.send(p);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
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
												STB stb = new STB(map.get("IP"),"test", "test", "test","test");
												if (!checkStbIsExist(stb)){ //如果在列表中没有该stb，则添加上
													stbs.add(stb);
													Message msg = new Message();
													msg.what = 1;
													msg.obj = stb;
													handler.sendMessageDelayed(msg, 200);
												}
											}
										}else if(cmd.equals("")){
											
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
		Button play,pause,stop,ff,fb;
		
		public void init(final STB stb) {
			name.setText(stb.getUsername());
			ip.setText(stb.getIp());
			play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.PlayReq(1, stb.getIp(), fileName, 1000,getUrl(path));
					
					sendDataTo(stb, data);
					play.setEnabled(false);
				}
				
			});
			pause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.PauseReq(1, stb.getIp());
					sendDataTo(stb, data);
					play.setEnabled(true);
				}
			});
			stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					byte[] data = XmlUtil.StopReq(1, stb.getIp());
					sendDataTo(stb, data);
					play.setEnabled(true);
				}
			});
			ff.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					sendDataTo(stb, data);
				}
			});
			fb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					sendDataTo(stb, data);
				}
			});
			
		}

		protected String getUrl(String path) {
			//http://192.168.1.104:9905/download/sdcard/video/video.mp4
			StringBuffer url = new StringBuffer("http://"+localIp+":9905/download");
			String [] temp = path.split("/");
			try {
				for(int i = 0;i<temp.length ;i++){
					System.out.println(temp[i]);
					temp[i] = URLEncoder.encode(temp[i] , "utf-8");
					if(i>0)
					url.append('/'+temp[i]);
				}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
			}
			Log.d("RemoteShow", "url--->"+url);
			return url.toString();
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

}
