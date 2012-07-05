package com.rushfusion.remoteshow.bean;

public class STB {
	String ip;
	String mcid;
	String password;
	PlayStatus playStatus;
	String taskno;
	String username;

	public STB() {
	}

	public STB(String ip, String taskno, String username,
			String password, String mcid) {
		this.ip = ip;
		this.taskno = taskno;
		this.username = username;
		this.password = password;
		this.mcid = mcid;
	}

	public STB(String ip, String taskno, String username,
			String password, String mcid, PlayStatus playStatus) {
		this.ip = ip;
		this.taskno = taskno;
		this.username = username;
		this.password = password;
		this.mcid = mcid;
		this.playStatus = playStatus;
	}

	public String getIp() {
		return this.ip;
	}

	public String getMcid() {
		return this.mcid;
	}

	public String getPassword() {
		return this.password;
	}

	public PlayStatus getPlayStatus() {
		return this.playStatus;
	}

	public String getTaskno() {
		return this.taskno;
	}

	public String getUsername() {
		return this.username;
	}

	public void setIp(String paramString) {
		this.ip = paramString;
	}

	public void setMcid(String paramString) {
		this.mcid = paramString;
	}

	public void setPassword(String paramString) {
		this.password = paramString;
	}

	public void setPlayStatus(PlayStatus paramPlayStatus) {
		this.playStatus = paramPlayStatus;
	}

	public void setTaskno(String paramString) {
		this.taskno = paramString;
	}

	public void setUsername(String paramString) {
		this.username = paramString;
	}

	@Override
	public String toString() {
		return "STB [ip=" + ip + ", mcid=" + mcid + ", password=" + password
				+ ", playStatus=" + playStatus + ", taskno=" + taskno
				+ ", username=" + username + "]";
	}

}