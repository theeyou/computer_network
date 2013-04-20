import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;




public class PeerServer implements Runnable {
	private ArrayList<Peer> peerSer;
	private int port=0;
	private CommDefs cd;
	private ServerSocket server = null;
	private Peer currPeer;
	private FileExe fe;
	private Log log;
	private Vector<ClientConn> cc=null;
	private int num=0;
	PeerServer(Vector <ClientConn> _cc,Peer cp,ArrayList<Peer> pSer,FileExe _fe,CommDefs _cd,Log _log) throws IOException{
		currPeer=cp;
		this.port=Integer.valueOf(cp.PeerPort);
		this.cd=_cd;
		this.peerSer=pSer;
		this.fe=_fe;
		this.cc=_cc;
		this.log=_log;
		server = new ServerSocket(this.port);
		(new Thread(this)).start();
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			Socket s;
			try {
				if(num==peerSer.size()){
					server.close();
					break;
				}
				s = server.accept();
				num++;
				if(cc.size()==0){
					cc.add(new ClientConn(s,currPeer,fe,cc,2,log,cd));
				}else if(cc.size()<(cd.NumberOfPreferredNeighhbors+1))
					cc.add(new ClientConn(s,currPeer,fe,cc,1,log,cd));
				else
					cc.add(new ClientConn(s,currPeer,fe,cc,0,log,cd));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
