package com.rushfusion.remoteshow.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;

public class MscpDataParser {
    
    private static MscpDataParser mInstance = null;
    private Context mContext;
    
    public interface CallBack {
		public void onParseCompleted(HashMap<String,String> map);
		public void onError(int code,String desc);
	}
    
    private MscpDataParser(){
    }
    
    public static MscpDataParser getInstance(){
    	if( mInstance == null )
    		mInstance = new MscpDataParser();
    	return mInstance;
    }
    
    public void init(Context context){
    	mContext = context;
    }
    
    public void parse(DatagramPacket dPackage,CallBack callback){
    	if( dPackage == null ){
    		if( callback != null )
    			System.out.println("ErrorCode.RET_PARSE_XML_EXCEPTION,PARSE_XML_EXCEPTION");
    	}
    	byte[] bytes = dPackage.getData();
    	if( bytes != null && bytes.length > 12 ){ //The header of protocol is 12 bytes
    		int headlen = Tools.byteArrayToInt( bytes );
    		int bodylen = Tools.byteArrayToInt( Tools.subByteArray(bytes, 4, 8) );
    		int version = Tools.byteArrayToInt( Tools.subByteArray(bytes, 8, 12) );
    		if( headlen == 12 && bodylen > 0 && bodylen <= (bytes.length-12) ){
    			byte[] data = Tools.subByteArray(bytes, 12, 12+bodylen );//bytes.length);
    			if( data != null && data.length > 0 && data.length == bodylen ){
    				parse( data,bodylen, callback);
    				return;
    			}
    			
    		}
    	}
    	
    	if( callback != null )
    		System.out.println("ErrorCode.ERR_INVALIDE_ARGS, Data Empty!");
    }
    
    public void parse(byte[] data, int length,CallBack callback){
    	if( data == null || length <= 0 ){
    		if( callback != null )
    			System.out.println("ErrorCode.ERR_INVALIDE_ARGS, ERR_INVALIDE_ARGS");
        }
    	InputStream inputStream = null;
    	if( data != null && data.length > 0 ){
    		inputStream = new ByteArrayInputStream( data );
    		 /*try{
 		    	URL url = new URL( "http://192.168.1.10/mscp/search.xml" );
 		    	inputStream = url.openStream();
 		    }catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}*/
    	}
		if( inputStream != null ){
		    Document doc = null;
		    try {
			    doc = DocumentBuilderFactory.newInstance()
					  .newDocumentBuilder().parse(inputStream);
			    inputStream.close();

			    Element Package = (Element)doc.getDocumentElement();
			    
			    HashMap<String,String> map = new HashMap<String,String>();
			    
			    NodeList nodeList = Package.getElementsByTagName("Property");
			    for( int i = 0; i < nodeList.getLength(); i++ ){
			    	Element node = (Element)nodeList.item(i);
			    	if( node.hasAttributes() ){
			    		Attr nameAttr = node.getAttributeNode("name");
			    		String name = nameAttr.getValue();
			    		Attr valueAttr = node.getAttributeNode("vaule");
			    		String value = valueAttr.getValue();
			    		if( name != null && !name.equals("") && value != null)
			    		    map.put(name, value);
			    	}
			    }
			    
			    
			    if( callback != null ){
			    	callback.onParseCompleted( map );
			    }
		    }catch( Exception e ) {
			    e.printStackTrace();
			    if( callback != null ){
			    	System.out.println("ErrorCode.RET_PARSE_XML_EXCEPTION,PARSE_XML_EXCEPTION");
		        }
		    }
        }
    }
}
