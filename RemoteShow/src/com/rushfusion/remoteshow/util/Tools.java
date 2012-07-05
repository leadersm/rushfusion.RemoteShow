package com.rushfusion.remoteshow.util;

import java.util.Arrays;

public class Tools {
	public static byte[] intToByteArray(int a)
	{
	    byte[] ret = new byte[4];
	    ret[3] = (byte) (a & 0xFF);   
	    ret[2] = (byte) ((a >> 8) & 0xFF);   
	    ret[1] = (byte) ((a >> 16) & 0xFF);   
	    ret[0] = (byte) ((a >> 24) & 0xFF);
	    return ret;
	}

	public static int byteArrayToInt(byte[] b)
	{
	    return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
	}
	
	public static byte[] subByteArray(byte[] original, int from, int to)
	{
	    if( from > to )
	        throw new IllegalArgumentException("The initial index is after " + "the final index.");
	    byte[] newArray = new byte[to - from];
	    if( to > original.length )
	    {
	        System.arraycopy(original, from, newArray, 0, original.length - from);
	        Arrays.fill(newArray, original.length, newArray.length, (byte)0);
	    }
	    else
	        System.arraycopy(original, from, newArray, 0, to - from);
	    return newArray;
	  }

}
