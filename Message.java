
public class Message {
	private byte[] message_length=new byte[4];
	private byte message_type;
	private byte[] message_payload=null;
	Message(int value){
		message_type=(byte) value;
	}
	public void setPayload(byte[] pay){
		message_payload=new byte[pay.length];
		for (int i=0;i<pay.length;i++){
			message_payload[i]=pay[i];
		}
	}
	public void setPayload(int index){
		message_payload=new byte[4];
		message_payload[0]=(byte) (index >> 24 & 0xff);
		message_payload[1]=(byte) (index >> 16 & 0xff);
		message_payload[2]=(byte) (index >> 8 & 0xff);
		message_payload[3]=(byte) (index & 0xff);
	}
	public byte[] getMessage(){
		if(message_payload!=null){
			int length=(1+message_payload.length);
			message_length[0]= (byte) (length >> 24 & 0xff);
			message_length[1]= (byte) (length >> 16 & 0xff);
			message_length[2]= (byte) (length >> 8 & 0xff);
			message_length[3]= (byte) (length& 0xff);
			
			byte [] msg=new byte[(int) (length+4)];
			for(int i=0;i<4;i++){
				msg[i]=message_length[i];
			}
			msg[4]=message_type;
			for (int i=0;i<message_payload.length;i++){
				msg[i+5]=message_payload[i];
			}
			return msg;
		}else{
			int length=1;
			message_length[0]= (byte) (length >> 24 & 0xff);
			message_length[1]= (byte) (length >> 16 & 0xff);
			message_length[2]= (byte) (length >> 8 & 0xff);
			message_length[3]= (byte) (length& 0xff);
			byte [] msg=new byte[(int) (length+4)];
			for(int i=0;i<4;i++){
				msg[i]=message_length[i];
			}
			msg[4]=message_type;
			return msg;
		}
	}
	public int getLength(){
		return 1+message_payload.length;
	}
	public static int getNumber(byte[] b){
		int l=   b[3] & 0xff 
	               | (b[2] & 0xff) << 8 
	               | (b[1] & 0xff) << 16
	               | (b[0] & 0xff) << 24;
		return l;
	}
}
