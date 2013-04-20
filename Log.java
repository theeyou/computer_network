import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Log {
	private int currPeer;
	private FileOutputStream fos;
	java.text.SimpleDateFormat d=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	java.util.Date d1=null;//new java.util.Date();
	Log(int peerId){
		currPeer=peerId;
		try {
			fos=new FileOutputStream("log_peer_"+currPeer+".log");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void TCP_makeconn(int remoteId) {
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] makes a connection to Peer ["+remoteId+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void TCP_getconn(int remoteId) {
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] is connected from Peer ["+remoteId+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void changeOfPreferredNeighbors(ArrayList<Integer> ids) {
		d1=new java.util.Date();
		String id="";
		for (int i=0;i<ids.size();i++){
			if (i+1==ids.size()){
				id=id+ids.get(i);
			}else
				id =id +ids.get(i)+",";
		}
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] has the preferred neighbors ["+id+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void changeOfOptimistically(int id){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] has the optimistically unchoked neighbor ["+id+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unchoked(int id){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] is unchoked by Peer ["+id+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void choked(int remoteId){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] is choked by Peer ["+remoteId+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receive_have(int remoteId,int packet){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] received the 'have' message from ["+remoteId+"]"+" for the piece ["+packet+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receive_interested(int remoteId){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] received the 'interested' message from Peer ["+remoteId+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receive_notInterested(int remoteId){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] received the 'not interested' message from Peer ["+remoteId+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void downloaded(int packet,int remoteId,int pieces){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] has downloaded the piece[" +packet+
				"] from ["+remoteId+"]. Now the number of pieces it has is["+pieces+"]\n";
		try {
			fos.write(result.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void complete(){
		d1=new java.util.Date();
		String result= "["+d.format(d1)+"]: Peer ["+currPeer+"] has downloaded the complete file\n"; 
		try {
			fos.write(result.getBytes());
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
