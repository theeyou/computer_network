import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;


public class PeerBase{
	private ArrayList<Peer> peerSer;
	private ArrayList<Peer> peerCli;
	
	private Peer currPeer;
	
	private Vector <ClientConn> cc;
	
	private PeerServer peerServer=null;
	private PeerClient peerClient=null;
	private FileExe fexe=null;
	private CommDefs cd;
	private Clients c=null;
	private Log log=null;
	private OptimisticChoke oc=null;
	private PreferredNeighborChoke pnc=null;
	PeerBase(ArrayList<Peer> pServer,ArrayList<Peer> pClient,Peer cPeer,FileExe fe,CommDefs _cd,Log _log){
		peerSer=pServer;
		peerCli=pClient;
		currPeer=cPeer;
		fexe=fe;
		cd=_cd;
		log=_log;
		cc=new Vector<ClientConn>();
		try {
			//establis server waiting for clients;
			peerServer=new PeerServer(cc,currPeer,peerSer,fexe,cd,log);
			//establish tcp to server;
			peerClient=new PeerClient(cc,currPeer,peerCli,fexe,cd,log);
			oc=new OptimisticChoke(cc,cd,log);
			pnc=new PreferredNeighborChoke(cc,cd,fexe,log);
		
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
