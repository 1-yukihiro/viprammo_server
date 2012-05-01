import java.util.ArrayList;
import java.util.Collections;


public class ThreadList extends Thread {

	private ArrayList<TCPThreadWorker> tlist = new ArrayList<TCPThreadWorker>();
	
	private static ThreadList instance = new ThreadList();
	
	public void run() {
		while (true) {

			for (int i = 0; i < tlist.size(); i++) {
				if (!tlist.get(i).isAlive()) {
					System.out.println("REMOVE" + tlist.get(i).getNameM());
					tlist.remove(i);
				}
			}
		}
	}
	
	public static ThreadList getInstance() {
		return instance;
	}
	
	private ThreadList() {
		Collections.synchronizedList(tlist);
	}
	
	public void Tadd(TCPThreadWorker worker) {
		this.tlist.add(worker);
	}
	
	public ArrayList<TCPThreadWorker> getThreadList() {
		return this.tlist;
	}
	
	public TCPThreadWorker getWorkerByName(String name) {
		String nm = null;
		for (TCPThreadWorker ttw : tlist) {
			nm = ttw.getNameM();
			if (nm.equals(name)) {
				return ttw;
			}
		}
		return null;
	}
	
	public void deleteWorkerByName(String name) {
		String nm = null;
		for (int i = 0; i < tlist.size(); i++ ) { 
			nm = tlist.get(i).getNameM();
			if (nm.equals(name)) {
				tlist.remove(i);
				System.out.println("delete list = " + nm);
			}
		}
	}

}
