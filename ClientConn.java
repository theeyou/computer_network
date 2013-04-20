import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;


public class ClientConn{
	private Socket s;
	private Peer currPeer;
	private Peer expectedPeer=null;
	
	private ReceiveMessage receiveMessage=null;
	private SendMessage sendMessage=null;
	private CommDefs cd=null;
	public ClientInfo cinfo=new ClientInfo();
	
	ClientConn(Socket _s,Peer cp,FileExe _fe,Vector<ClientConn> _cc,int flag,Log _log,CommDefs _cd){
		this.s=_s;
		this.currPeer=cp;
		cinfo.fe=_fe;
		cinfo.id=Integer.valueOf(currPeer.PeerID);
		cinfo.cc=_cc;
		cinfo.fileStatus=cinfo.fe.getFin();
		cinfo.log=_log;
		cd=_cd;
		try {
			cinfo.dos = new DataOutputStream(s.getOutputStream());
			cinfo.dis =new DataInputStream(s.getInputStream());
			
			
			
			handshake hsmsg=new handshake(currPeer.PeerID);
			cinfo.dos.writeUTF(hsmsg.toString());
			cinfo.remoteId=Integer.valueOf(cinfo.dis.readUTF().split("0000000000")[1]);
			receiveMessage=new ReceiveMessage(cinfo,cd);
			
			Message m=new Message(5);
			if(cinfo.fe.getStatus()!=null){
				m.setPayload(cinfo.fe.getStatus());
				cinfo.dos.write(m.getMessage());
			}
			if(cinfo.fe.getFin()==1){
				finish();
			}
			if(flag==2){
				cinfo.optimist=1;
				unchoke();
			}else if(flag==1){
				unchoke();
			}
			
			sendMessage=new SendMessage(cinfo,cd);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//(new Thread(this)).start();
	}
	public void have(int packet) throws IOException{
		Message have=new Message(4);
		have.setPayload(packet);
		//System.out.println("Sending have to:"+cinfo.remoteId);
		cinfo.dos.write(have.getMessage());
	}
	public void choke() throws IOException{
		cinfo.status=0;
		Message choke=new Message(0);
		//System.out.println("Sending choke to:"+cinfo.remoteId);
		cinfo.dos.write(choke.getMessage());
	}
	public void unchoke() throws IOException{
		cinfo.status=1;
		cinfo.packets=0;
		Message unchoke=new Message(1);
		//System.out.println("Sending unchoke to:"+cinfo.remoteId);
		cinfo.dos.write(unchoke.getMessage());
	}
	public int getRate(){
		return cinfo.packets;
	}
	public void finish() throws IOException{
		Message finish=new Message(64);
		cinfo.dos.write(finish.getMessage());
	}
	
}

	

