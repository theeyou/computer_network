import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Vector;


public class ClientInfo {
	public int status=0;//status: 0 for choke,1 for unchoke,
	  //e.g. if status is 0, then the currPeer 
	  //will not uploads piece to the remote.
	public int remoteStatus=0;//remoteStatus: 0 for choke, 1 for unchoke
	//to tag the status of currPeer.
	//e.g. if remoteStatus is 0, then the remote 
	//will not uploads piece to the currPeer.
	
	public int fileStatus=-1;//to tell if currPeer file is complete.-1 for no, 1 for yes
	public int optimist=-1;//to tell if remote Peer is optimistic, -1 for no, 1 for yes
	
	public int fin=-1;//-1 for currPeer has not whole piece. 1 for have the whole piece, 2 for already send whole peers for fin. 
	
	public int remoteId=-1;
	public int id=-1;
	public DataOutputStream dos=null;
	public DataInputStream dis=null;
	public byte[] buffer=null;
	public FileExe fe=null;
	public Vector<ClientConn> cc=null;
	public Log log=null;
	
	public int packets=0;//this is for calculate the downloaded packets from 
						//remote peer during unchoke time
	
	public int applyedPacketId=-1;
	//this is to tell which packet the requester has applyed.
	public int havePacket=-1;
	//this is to tell which packet has just received,need to publish to others.
	
	public boolean request=false;
	//the sendMessage only send request msg when request is true;
	
	public boolean piece=false;
	//the sendMessage only send piece msg when piece is true;
	
	public boolean interested=false;
	//tell if the currPeer intereted in remote./just flag no use for currPeer
	public boolean ninterested=false;
	//tell if the currPeer intereted in remote./just flag no use for currPeer
	public int triggerOfInterested=-1;
	//this is the trigger to tell if the SendMessage Thread needs to send Interested or Not Interested msg
	//Once used, it needs to be reassign to -1;
	// 1 to send interested 2 to send not interested
	
	public int pieceNumber=-1;
	
	public boolean remoteInterest=false;
	//tell if the remote is interested in currPeer.
	
	public boolean have=false;
	//tell if the currPeer have new Packet.
	
}
