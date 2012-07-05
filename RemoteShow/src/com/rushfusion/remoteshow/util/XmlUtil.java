package com.rushfusion.remoteshow.util;

public class XmlUtil {

	public static final int STB_PORT = 6806;

	public static byte[] SearchReq(String taskno, String mip) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<Package> "
				// <!— 命令名称 -->
				+ "<Property  name = 'cmd' vaule = 'searchreq' /> "
				// <!— 将请求任务序号回传给客户端 -->
				+ "<Property  name = 'taskno' vaule = '" + taskno + "' /> "
				// <!— 客户端内网IP，可以用于机顶盒区分多个客户端的请求 -->
				+ "<Property  name = 'IP' vaule = '" + mip + "' /> "
				// <!— 客户端侦听端口号 -->
				+ "<Property  name = 'port' vaule = '" + STB_PORT + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] PlayReq(String taskno, String mip, String title,String duration, String url) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<Package> "
				// <!— 命令名称 -->
				+ "<Property  name = 'cmd' vaule = 'playreq' /> "
				// <!— 将请求任务序号回传给客户端 -->
				+ "<Property  name = 'taskno' vaule = '" + taskno + "' /> "
				// <!— 客户端内网IP，可以用于机顶盒区分多个客户端的请求 -->
				+ "<Property  name = 'IP' vaule = '" + mip + "' /> "
				// <!— 客户端侦听端口号 -->
				+ "<Property  name = 'port' vaule = '" + STB_PORT + "' /> "
				// <!— 播放url -->
				+ "<Property  name = 'url' vaule = '" + url + "' /> "
				// <!— 节目名称 -->
				+ "<Property  name = 'title' vaule = '" + title + "' /> "
				// <!— 节目时长 -->
				+ "<Property  name = 'duration' vaule = '" + duration + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] PauseReq(int taskno, String ip) {

		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<Package> "
				+ "<Property name = 'cmd' vaule = 'pausereq' /> "
				+ "<Property name = 'taskno' vaule = '" + taskno + "' /> "
				+ "<Property name = 'IP' vaule = '" + ip + "' /> "
				+ "<Property name = 'port' vaule = '" + STB_PORT + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] PlayReq(int taskno, String ip,String title, int duration, String url) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<Package> <Property  name = 'cmd' vaule = 'playreq' />"
				+ " <Property  name = 'taskno' vaule = '" + taskno + "' /> "
				+ "<Property  name = 'IP' vaule = '" + ip + "' /> "
				+ "<Property  name = 'port' vaule = '" + STB_PORT + "' /> "
				+ "<Property  name = 'url' vaule = '" + url + "' /> "
				+ "<Property  name = 'title' vaule = '" + title+ "' /> "
				+ "<Property  name = 'duration' vaule = '"+ duration + "' /> "
				+ "<Property  name = 'CP' vaule = 'itv' /> " 
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] ResumeReq(String taskno, String ip) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
				"<Package> <Property  name = 'cmd' vaule = 'resumereq' /> " +
				"<Property  name = 'taskno' vaule = '"+ taskno+ "' /> "
				+ "<Property  name = 'IP' vaule = '"+ ip+ "' /> "
				+ "<Property  name = 'port' vaule = '"+ STB_PORT + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] SeekReq(int taskno, String ip,int pos) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<Package> <Property  name = 'cmd' vaule = 'seekreq' />"
				+ " <Property  name = 'taskno' vaule = '" + taskno + "' /> "
				+ "<Property  name = 'IP' vaule = '" + ip + "' /> "
				+ "<Property  name = 'port' vaule = '" + STB_PORT + "' /> "
				+ "<Property  name = 'pos' vaule = '" + pos + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] StopReq(int taskno, String ip) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
				"<Package> <Property  name = 'cmd' vaule = 'stopreq' />" 
				+" <Property  name = 'taskno' vaule = '"+ taskno+ "' /> "
				+ "<Property  name = 'IP' vaule = '"+ ip+ "' /> "
				+ "<Property  name = 'port' vaule = '"+ STB_PORT + "' /> "
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

	public static byte[] VolumeReq(int taskno, String IP,int volume, int maxvol) {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
				"<Package> <Property  name = 'cmd' vaule = 'volumereq' />" +
				" <Property  name = 'taskno' vaule = '"+ taskno+ "' /> "
				+ "<Property  name = 'IP' vaule = '"+ IP+ "' /> "
				+ "<Property  name = 'port' vaule = '"+ STB_PORT+ "' /> "
				+ "<Property  name = 'volume' vaule = '"+ volume+ "' /> "
				+ "<Property  name = 'maxvol' vaule = '"+ maxvol + "' /> " 
				+ "</Package>";
		byte[] xml_bytes = xml.getBytes();
		byte[] headlen_bytes = Tools.intToByteArray(12);
		byte[] bodylen_bytes = Tools.intToByteArray(xml_bytes.length);
		byte[] version_bytes = Tools.intToByteArray(1);

		byte[] data = new byte[12 + xml_bytes.length];

		System.arraycopy(headlen_bytes, 0, data, 0, 4);
		System.arraycopy(bodylen_bytes, 0, data, 4, 4);
		System.arraycopy(version_bytes, 0, data, 8, 4);
		System.arraycopy(xml_bytes, 0, data, 12, xml_bytes.length);

		return data;
	}

}
