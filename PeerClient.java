import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;


public class PeerClient implements Runnable {
	private ArrayList<Peer> peerCli=new ArrayList<Peer>();
	private Vector<ClientConn> cc=null;
	private CommDefs cd;
	private Socket s = null;
	private Peer currPeer=null;
	private FileExe fe;
	private Log log;
	PeerClient(Vector<ClientConn> _cc,Peer cp,ArrayList<Peer> pCli,FileExe _fe,CommDefs _cd,Log _log){
		for(int i=0;i<pCli.size();i++){
			peerCli.add(pCli.get(i));
		}
		currPeer=cp;
		this.cd=_cd;
		this.fe=_fe;
		this.cc=_cc;
		this.log=_log;
		(new Thread(this)).start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(peerCli.size()!=0){
			Peer p=null;
			try {
				p=peerCli.get(0);
				peerCli.remove(0);
				s=new Socket(p.PeerName,Integer.valueOf(p.PeerPort));
				if(cc.size()==0){
					cc.add(new ClientConn(s,currPeer,fe,cc,2,log,cd));
				}else if(cc.size()<=(cd.NumberOfPreferredNeighhbors+1))
					cc.add(new ClientConn(s,currPeer,fe,cc,1,log,cd));
				else
					cc.add(new ClientConn(s,currPeer,fe,cc,0,log,cd));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("2");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				peerCli.add(p);
				//System.out.println("One of the destination Peer hasn't start.ID:"+p.PeerID);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	

}
