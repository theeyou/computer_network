import java.io.IOException;
import java.util.Random;


public class SendMessage implements Runnable {
	
	private ClientInfo cinfo=null;
	private CommDefs cd=null;
	SendMessage(ClientInfo ci,CommDefs _cd){
		cinfo=ci;
		cd=_cd;
		(new Thread(this)).start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(cd.allDone){
				break;
			}
			try {
				Thread.sleep(1000-(new Random()).nextInt(300));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//send interested
			if(cinfo.interested){
				Message m=new Message(2);
				try {
					//System.out.println("Sending interested to:"+cinfo.remoteId);
					cinfo.dos.write(m.getMessage());
				     //System.out.println("Finish Sending interested");
					cinfo.interested=false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//send not interested
			if(cinfo.ninterested){
				Message m=new Message(3);
				try {
				    //System.out.println("Sending not interested to:"+cinfo.remoteId);
					cinfo.dos.write(m.getMessage());
					//System.out.println("Finish Sending not interested");
					cinfo.ninterested=false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			//send request;
			if ((cinfo.request)&&(cinfo.remoteStatus==1)){
				int packet=cinfo.fe.apply(cinfo.remoteId);
				if(packet==-1){
				//	System.out.println("Not interested by lack");
				//	System.out.println(cinfo.packets);
					cinfo.interested=false;
				}
				else{
					Message m=new Message(6);
					m.setPayload(packet);
					try {
						//System.out.println("Sending request:"+packet+" to:"+cinfo.remoteId);
						cinfo.dos.write(m.getMessage());
						cinfo.applyedPacketId=packet;
						cinfo.request=false;
						//System.out.println("Finish Sending request:"+packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
			//System.out.println(cinfo.piece);
			//send piece
			if((cinfo.piece)&&(cinfo.status==1)){
				//System.out.println("Sending piece:"+cinfo.pieceNumber+" to:"+cinfo.remoteId);
				Message m=new Message(7);
				m.setPayload(cinfo.buffer);
				try {
					cinfo.dos.write(m.getMessage());
					cinfo.piece=false;
					//System.out.println("Finish Sending piece:"+cinfo.pieceNumber);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(cinfo.have){
				for(int i=0;i<cinfo.cc.size();i++){
					try {
						cinfo.cc.get(i).have(cinfo.havePacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				cinfo.have=false;
				cinfo.havePacket=-1;
			}
			if(cinfo.fin==1){
				cinfo.fin=2;
				for(int i=0;i<cinfo.cc.size();i++){
					try {
						cinfo.cc.get(i).finish();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
		}
	}

}
