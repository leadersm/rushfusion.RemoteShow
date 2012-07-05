package com.rushfusion.remoteshow.bean;

public class PlayStatus
{
  int duration;
  int maxvol;
  int pos;
  int status;
  int volume;

  public PlayStatus()
  {
  }

  public PlayStatus(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.status = paramInt1;
    this.pos = paramInt2;
    this.duration = paramInt3;
    this.volume = paramInt4;
    this.maxvol = paramInt5;
  }

  public int getDuration()
  {
    return this.duration;
  }

  public int getMaxvol()
  {
    return this.maxvol;
  }

  public int getPos()
  {
    return this.pos;
  }

  public int getStatus()
  {
    return this.status;
  }

  public int getVolume()
  {
    return this.volume;
  }

  public void setDuration(int paramInt)
  {
    this.duration = paramInt;
  }

  public void setMaxvol(int paramInt)
  {
    this.maxvol = paramInt;
  }

  public void setPos(int paramInt)
  {
    this.pos = paramInt;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }

  public void setVolume(int paramInt)
  {
    this.volume = paramInt;
  }
}