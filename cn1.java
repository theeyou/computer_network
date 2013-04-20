import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class cn1 {
	static ArrayList<Peer> peerInfo=new ArrayList<Peer>();
	static PeerBase pb;
	static CommDefs cd=new CommDefs();
	static FileExe file=null;
	static Log log=null;
	public static void getCommon(){
		try {
			
			BufferedReader br=new BufferedReader(new FileReader("Common.cfg"));
			
			cd.NumberOfPreferredNeighhbors=Integer.valueOf(br.readLine().split(" ")[1]);
			cd.UnchokingInterval=Integer.valueOf(br.readLine().split(" ")[1]);
			cd.OptimisticUnchokingInterval=Integer.valueOf(br.readLine().split(" ")[1]);
			cd.FileName=br.readLine().split(" ")[1];
			cd.FileSize=br.readLine().split(" ")[1];
			cd.PieceSize=br.readLine().split(" ")[1];
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("File Common.cfg not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * get the Peers that stored in the cfg.
	 */
	public static void getPeers(){
		try {
			BufferedReader br=new BufferedReader(new FileReader("PeerInfo.cfg"));
			String str;
			while((str=br.readLine())!=null){
				Peer p = new Peer();
				p.PeerID=str.split(" ")[0];
				p.PeerName=str.split(" ")[1];
				p.PeerPort=str.split(" ")[2];
				p.status=str.split(" ")[3];
				peerInfo.add(p);
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("File Common.cfg not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getCommon();
		getPeers();
		/*
		 * Just temporary use for test in solo pc. Distinct each peer by using 
		 * different port
		 */
		String port=args[0];
		String hostIp=null;
		hostIp="127.0.0.1";//InetAddress.getLocalHost().toString().split("/")[1];
		ArrayList<Peer> pServer=new ArrayList<Peer>();
		ArrayList<Peer> pClient=new ArrayList<Peer>();
		int i=0;
		Peer currPeer=null;
		/*for the peers in the PeerInfo
		* when the peer is in front of current peer, the current peer will be
		* the client to this peer.
		* when the peer is in the back of current peer, the current peer will be
		* the server to this peer. 
		*/
		
		for(;i<peerInfo.size();i++){
			if((peerInfo.get(i).PeerName.equals(hostIp))&&(peerInfo.get(i).PeerPort.equals(port))){
				currPeer=peerInfo.get(i);
				i++;
				break;
			}
			pClient.add(peerInfo.get(i));
		}
		for(;i<peerInfo.size();i++){
			pServer.add(peerInfo.get(i));
		}
		cd.peerNumber=pServer.size()+pClient.size();
		file=new FileExe(cd,currPeer);
		log=new Log(Integer.valueOf(currPeer.PeerID));
		pb=new PeerBase(pServer, pClient,currPeer,file,cd,log);
		/*
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
