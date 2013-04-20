import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class FileExe {
	//Store the packet information(which packet havn't finished or in temp status)
	//  packet    status
	//  202         1  //1 for finish,0 for empty
 	private HashMap<Integer,Integer> packetMap=new HashMap<Integer,Integer>();
	//Store the packet and tag its postion
 	//  packet   path
 	//  204      path
	private HashMap<Integer,String> packetPaths=new HashMap<Integer,String>();
	//To store which peer has what kind of packet.
	//  id    hasPacket
	//  1001  1,2,3,4,5...
	private HashMap<Integer,ArrayList<Integer>> peerMap=new HashMap<Integer,ArrayList<Integer>>();

	
	private CommDefs cd;
	private Peer currPeer;
	private boolean done=false;
	private int packets=0;
	File f;
	String path;//store the temp directory path.
	String path1;
	FileExe(CommDefs _cd,Peer cPeer){
		this.cd=_cd;
		this.currPeer=cPeer;
		for(int i=0;i<(Integer.valueOf(cd.FileSize)/Integer.valueOf(cd.PieceSize)+1);i++){
			packetMap.put(i+1, 0);
			packetPaths.put(i+1, "");
		}
		path=System.getProperty("user.dir");
		
		if (cPeer.status.equals("1")){
			try {
				path1=path+"/temp";
				f=new File(path1);
				f.mkdir();//create directory to store piece files.
						  //will delete all these files and the directory when FileExe ends.
				
				partFile();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("No such file"+cd.FileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * apply is used when a receiver want to get a piece from remote,
	 * it called apply to get a random piece no that the remote has.
	 * -1 for the remote has no packet that the receiver interested.
	 */
	public int apply(int id){
		if(checkInterested(id)==0){
			//System.out.println("Because of checkInterested(id)");
			return -1;
		}
		ArrayList<Integer> peerPackets=peerMap.get(id);
		Random r = new Random();

		while(true){
			int x=r.nextInt(peerPackets.size());
			if(packetMap.get(peerPackets.get(x))!=1)
				return peerPackets.get(x);
		}
	}
	/**
	 * tag a packet to finish and del the packet from which 
	 * @param id
	 * @param packet
	 * @param buffer
	 * @throws IOException 
	 * @return int 1 for submit successfully 0 for no need to do this
	 */
	public synchronized int submit(int id,int packet,byte[] buffer) throws IOException{
		
		if(packetMap.get(packet)!=1){
			File fi;
			fi=new File(path+"/"+id);
			fi.mkdir();
			FileOutputStream fos=new FileOutputStream(path+"/"+id+"/"+packet);
			
			packetMap.put(packet, 1);
			packetPaths.put(packet, path+"/"+id+"/"+packet);
			//get all the peer that have this packet
			packets++;
			fos.write(buffer);
			
			fos.close();
			
			if ((done==false)&&(packets==packetMap.size())){
				done=true;
				mergeFile();
			}
			return 1;
		}
		return 0;
	}
	/**
	 * used to register the remoteId has which pieces.
	 * return 1 for interested,0 for not.
	 * @param payload
	 * 
	 */
	public int register(int remoteId,byte[] payload){
		int offset=0;
		//byte[] result=new byte[Integer.valueOf(cd.FileSize)/Integer.valueOf(cd.PieceSize)/8+1];
		ArrayList<Integer> re=new ArrayList<Integer>();
		
			for(int i=0;i<payload.length;i++){
				if(offset+8>packetMap.size()){
					for(int k=0;k<(packetMap.size()-offset);k++){
							if((payload[i]&(byte)(Math.pow(2, k)))==(byte)Math.pow(2, k)){
								re.add(offset+k+1);
						}
					}
					break;
				}
				for(int j=0;j<8;j++){
					if((payload[i]&(byte)(Math.pow(2, j)))==(byte)Math.pow(2, j)){
						re.add(8*i+j+1);
					}
				}
			
			offset+=8;
			}
		
		
		peerMap.put(remoteId, re);
		
		return checkInterested(remoteId);
	}
	/**
	 * for have msg to refresh remote's packets to peerMap
	 * return 1 for interested ,0 for not
	 * @param b
	 */
	public  int refresh(int remoteId,int packetId){
		if (peerMap.containsKey(remoteId)){
			ArrayList<Integer> buf=peerMap.get(remoteId);
			if(packetMap.get(packetId)!=1){
				if (!buf.contains(packetId)){
					buf.add(packetId);
					peerMap.put(remoteId, buf);
				}
			}
		}else{
			ArrayList<Integer>buf=new ArrayList<Integer>();
			buf.add(packetId);
			peerMap.put(remoteId, buf);
		}
		return checkInterested(remoteId);
	}
	
	/**
	 * get the packet that the peer has stored.
	 * @return
	 */
	public byte[] getStatus(){
		byte []buffer=new byte[packetMap.size()/8+1];
		int flag=0;
		for (int i=0;i<buffer.length;i++){
			buffer[i]=0;
		}	
		int offset=0;
		while((offset+8)<=packetMap.size()){
			for(int i=0;i<8;i++){
				if(packetMap.get((i+1)+offset)==1){
					buffer[offset/8]=(byte) (buffer[offset/8] ^(byte) (Math.pow(2, i)));
					flag=1;
				}
			}
			offset=offset+8;
		}
		for (int i=0;i<(packetMap.size()-offset);i++){
			if(packetMap.get((i+1)+offset)==1){
				buffer[offset/8]=(byte) (buffer[offset/8] ^(byte) (Math.pow(2, i)));
				flag=1;
			}
		}
		if(flag==0){
			buffer=null;
		}
		
		return buffer;
		
	}
	/**
	 * It returns the number the currPeer has
	 * @return
	 */
	public int getPacketNumber(){
		return packets;
	}
	/**
	 * get the specified packet, return null if the packet has not ready or nonexist.
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public byte[] getPacket(int packet) throws IOException{
		if(packetMap.get(packet)==1){
			byte[] buffer;
			if(packet==packetMap.size())
				buffer=new byte[Integer.valueOf(cd.FileSize)-Integer.valueOf(cd.PieceSize)*(packet-1)];
			else
				buffer=new byte[Integer.valueOf(cd.PieceSize)];
			String ppath=packetPaths.get(packet);
			FileInputStream fis=new FileInputStream(ppath);
			fis.read(buffer);
			return buffer;
		}else{
			return null;
		}
	}
	/**
	 * get the status of the complete of file 
	 * @return -1 for not fin, 1 for fin
	 */
	public int getFin(){
		for(int i=0;i<packetMap.size();i++){
			if (packetMap.get(i+1)==0)
				return -1;
		}
		return 1;
	}
	
	/**
	 * for test use only , get specified peer's status
	 * @return
	 */
	public BigDecimal getPercentage(){
		//return peerMap.get(remoteId);
		
		double per=Double.valueOf(Integer.toString(packets))/packetMap.size();
		return new BigDecimal(per).setScale(2, BigDecimal.ROUND_HALF_UP); 
		
	}
	

	
	
	public void close() throws IOException{
		if (currPeer.status.equals("1")){
			delFolder(path);
		}else{
			mergeFile();
		}
	}
	//check if the stored byte of peer packets is interested for currPeer
	//0 for not interested 1 for interested
	private int checkInterested(int peerId){
		ArrayList<Integer> buf=peerMap.get(peerId);
		ArrayList<Integer> re=new ArrayList<Integer>();
		int interested=0;
		if (buf==null){
			return interested;
		}
		for(int i=0;i<buf.size();i++){
			int x=buf.get(i);
			if (packetMap.get(x)==0){
				re.add(x);
				interested=1;
			}
		}
		
		peerMap.put(peerId, re);
		
		return interested;
	}
	private static void delFolder(String folderPath) {
	    try {
	       delAllFile(folderPath); 
	       String filePath = folderPath;
	       filePath = filePath.toString();
	       File myFilePath = new File(filePath);
	       myFilePath.delete(); 
	    }catch (Exception e) {
	      e.printStackTrace(); 
	    }
	}

	private static boolean delAllFile(String path) {
       boolean flag = false;
       File file = new File(path);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempList = file.list();
       File temp = null;
       for (int i = 0; i < tempList.length; i++) {
          if (path.endsWith(File.separator)) {
             temp = new File(path + tempList[i]);
          } else {
              temp = new File(path + File.separator + tempList[i]);
          }
          if (temp.isFile()) {
             temp.delete();
          }
          if (temp.isDirectory()) {
             delAllFile(path + "/" + tempList[i]);
             delFolder(path + "/" + tempList[i]);
             flag = true;
          }
       }
       return flag;
	}
	
	private void partFile() throws IOException{
		FileInputStream fis=new FileInputStream(cd.FileName);
		
		byte []buffer=new byte[Integer.valueOf(cd.PieceSize)];
		
		int i=0;
		int numOfPiece=1;
		
		
		while ((i=fis.read(buffer))!=-1){
			FileOutputStream fos=new FileOutputStream(path1+"/"+numOfPiece);
			packetMap.put(numOfPiece, 1); 
			packetPaths.put(numOfPiece,path1+"/"+numOfPiece);
			if(i==Integer.valueOf(cd.PieceSize))
				fos.write(buffer);
			else
				fos.write(buffer,0,i);
			numOfPiece++;
			fos.close();
		}
		fis.close();
		
		//System.out.println(i);
	}

	private synchronized void mergeFile() throws IOException{
		FileOutputStream fos=new FileOutputStream(cd.FileName);
		byte []buffer=new byte[Integer.valueOf(cd.PieceSize)];
		String ppath;
		for(int i=1;i<(packetPaths.size()+1);i++){
			ppath=packetPaths.get(i);
			
			FileInputStream fis=new FileInputStream(ppath);
			
			int x=fis.read(buffer);
			fos.write(buffer,0,x);
			
			fis.close();
		}
		fos.close();
	}

	
}
