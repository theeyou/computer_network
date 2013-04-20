import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

public class PreferredNeighborChoke implements Runnable {
	private Vector <ClientConn> cc;
	private CommDefs cd;
	private int interval=0;
	private int numOfNeighbor=0;
	private FileExe fe=null;
	private Log log=null;
	PreferredNeighborChoke(Vector<ClientConn> _cc,CommDefs _cd,FileExe fe,Log _log){
		this.cc=_cc;
		this.cd=_cd;
		this.fe=fe;
		this.log=_log;
		interval=cd.UnchokingInterval*1000;
		numOfNeighbor=cd.NumberOfPreferredNeighhbors;
		(new Thread(this)).start();
	}
	static class ByValueComparator implements Comparator<Integer> {
		HashMap<Integer, Integer> base_map;

		public ByValueComparator(HashMap<Integer, Integer> base_map) {
			this.base_map = base_map;
		}

		public int compare(Integer arg0, Integer arg1) {
			if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
				return 0;
			}

			if (base_map.get(arg0) < base_map.get(arg1)) {
				return 1;
			} else if (base_map.get(arg0) == base_map.get(arg1)) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true){
			try {
				if(cd.allDone){
					break;
				}
				Thread.sleep(interval);
				if(cc.size()>numOfNeighbor+1){
					if (fe.getFin()==1){
						for( int i=0;i<cc.size();i++){
							if(cc.get(i).cinfo.status==1)
								cc.get(i).choke();
						}
						ArrayList <Integer> preferNeighbor=new ArrayList<Integer>();
						for (int i=0;i<cd.NumberOfPreferredNeighhbors;i++){
							while(true){
								int k=(new Random()).nextInt(cc.size());
								if ((cc.get(k).cinfo.status==0)&&(cc.get(k).cinfo.optimist==-1)){
									preferNeighbor.add(cc.get(k).cinfo.remoteId);
									cc.get(k).unchoke();
									break;
								}
							}
						}
						log.changeOfPreferredNeighbors(preferNeighbor);
					  
					}else{
						
							for( int i=0;i<cc.size();i++){
								if((cc.get(i).cinfo.status==1)&&(cc.get(i).cinfo.optimist==-1))
									cc.get(i).choke();
							}
							HashMap<Integer, Integer> datas = new HashMap<Integer, Integer>();
							for(int i=0;i<cc.size();i++){
								if(cc.get(i).cinfo.optimist==-1)
									datas.put(i, cc.get(i).cinfo.packets);
							}
							ByValueComparator bvc = new ByValueComparator(datas);
							List<Integer> keys = new ArrayList<Integer>(datas.keySet());
							Collections.sort(keys, bvc);
							ArrayList <Integer> preferNeighbor=new ArrayList<Integer>();
							int i=0;//numOfNeighbor;
							for(Integer key : keys) {
								if (i<numOfNeighbor){
									if(cc.get(key).cinfo.optimist==-1){
										cc.get(key).unchoke();
										preferNeighbor.add(cc.get(key).cinfo.remoteId);
										i++;
										}
								}else{
									break;
								}
								
								//System.out.printf("%s -> %d\n", key, datas.get(key));
							}
							log.changeOfPreferredNeighbors(preferNeighbor);
						}
						
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
