import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.math.*;

public class OptimisticChoke implements Runnable {
	private Vector <ClientConn> cc;
	private CommDefs cd;
	private int interval=0;
	private Log log=null;
	OptimisticChoke(Vector<ClientConn> _cc,CommDefs cd,Log _log){
		this.cc=_cc;
		this.cd=cd;
		this.log=_log;
		interval=cd.OptimisticUnchokingInterval*1000;
		(new Thread(this)).start();
	}
	@Override
	public void run() {
		Random r=new Random();
		int i=0;
		// TODO Auto-generated method stub
		while(true){
			try {
				if(cd.allDone){
					break;
				}
				Thread.sleep(interval);
				if(cc.size()>cd.NumberOfPreferredNeighhbors+1){
					for (int j=0;j<cc.size();j++){
						if (cc.get(j).cinfo.optimist==1){
							cc.get(j).cinfo.optimist=-1;
							cc.get(j).choke();
							break;
						}
					}
					int newOptimistic=0;
					while (true){
						if (cc.size()>0){
							i=r.nextInt(cc.size());
							if ((cc.get(i).cinfo.optimist==-1)&&(cc.get(i).cinfo.status==0)){
								cc.get(i).cinfo.optimist=1;
								newOptimistic=cc.get(i).cinfo.remoteId;
								cc.get(i).unchoke();
								break;
							}
						}
					}
					log.changeOfOptimistically(newOptimistic);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
	}

}
