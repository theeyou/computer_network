import java.io.IOException;


public class ReceiveMessage implements Runnable {

	private ClientInfo cinfo=null;
	private CommDefs cd=null;
	ReceiveMessage(ClientInfo ci,CommDefs _cd){
		cinfo=ci;
		cd=_cd;
		(new Thread(this)).start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] lengthOfBuffer=new byte[4];
		byte[] type=new byte[1];
		byte[] payload = null;
		while(true){
			try {
				cinfo.dis.readFully(lengthOfBuffer);
				int length=Message.getNumber(lengthOfBuffer);
				
				if(length!=1)
					payload=new byte[length-1];
				cinfo.dis.readFully(type);
				if(type[0]==0){
					cinfo.remoteStatus=0;
					cinfo.log.choked(cinfo.remoteId);
					//System.out.println("Get choke from:"+cinfo.remoteId);
				}
				if(type[0]==1){
					cinfo.remoteStatus=1;
					cinfo.log.unchoked(cinfo.remoteId);
					//System.out.println("Get unchoke from:"+cinfo.remoteId);
				}
				if(type[0]==2){
					//cinfo.interested=1;
					cinfo.remoteInterest=true;
					cinfo.log.receive_interested(cinfo.remoteId);
					//System.out.println("Get interested from :"+cinfo.remoteId);
				}
				if(type[0]==3){
					//cinfo.interested=0;
					cinfo.remoteInterest=false;
					cinfo.log.receive_notInterested(cinfo.remoteId);
					//System.out.println("Get uninterested from:"+cinfo.remoteId);
				}
				if(type[0]==4){
				//System.out.println("Get have from:"+cinfo.remoteId);
					cinfo.dis.readFully(payload);
					//System.out.println("Finish Get have");
					cinfo.log.receive_have(cinfo.remoteId, Message.getNumber(payload));
					if(cinfo.fe.refresh(cinfo.remoteId, Message.getNumber(payload))==1){
						cinfo.interested=true;
						cinfo.request=true;
						cinfo.triggerOfInterested=1;
					}else{
						cinfo.triggerOfInterested=2;
						cinfo.ninterested=true;
					}
				}
				if(type[0]==5){
					//System.out.println("Get bitfield from:"+cinfo.remoteId);
					cinfo.dis.readFully(payload);
					//System.out.println("Finish Get bitfield");
					if(cinfo.fe.register(cinfo.remoteId, payload)==1){
						cinfo.interested=true;
						cinfo.request=true;
					}
					
					//System.out.println(cinfo.fe.getPeerStatus(cinfo.remoteId));
				}
				if(type[0]==6){
					//System.out.println("Get request from:"+cinfo.remoteId);
					if(cinfo.status==1){
						cinfo.dis.readFully(payload);
						//System.out.println("Finish Get request");
						cinfo.buffer=cinfo.fe.getPacket(Message.getNumber(payload));
						//System.out.println("Finish get packet");
						//cinfo.readyToReply=6;
						cinfo.piece=true;
						cinfo.pieceNumber=Message.getNumber(payload);
						//System.out.println("Finish get packet process");
					}
				}
				if(type[0]==7){
					//System.out.println("Get piece from:"+cinfo.remoteId);
					
					cinfo.dis.readFully(payload);
					
					//System.out.println("Finish Get piece");
					if (cinfo.fe.submit(cinfo.remoteId, cinfo.applyedPacketId, payload)==1){
						cinfo.log.downloaded(cinfo.applyedPacketId, cinfo.remoteId, cinfo.fe.getPacketNumber());
						System.out.println(cinfo.fe.getPercentage());
						cinfo.packets++;
						//cinfo.applyedPacketId=-1;
						//cinfo.receiveNewPacket=1;
						cinfo.havePacket=cinfo.applyedPacketId;
						cinfo.have=true;
						cinfo.request=true;
						if((cinfo.fe.getFin()==1)&&(cinfo.fin==-1)){
							cinfo.fin=1;
							cinfo.log.complete();
						}
					}
				}
				if(type[0]==64){
					cd.peerAlreadyFinish++;
					System.out.println("GETETETETET done msg:"+cd.peerAlreadyFinish);
					if((cd.peerAlreadyFinish==cd.peerNumber)&&(cinfo.fe.getFin()==1)){
						
						cd.allDone=true;
						break;
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
