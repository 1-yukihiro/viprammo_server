
public class VIPMMOSV {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TCPServerSocket ts = new TCPServerSocket();
		ts.start();
		ThreadList.getInstance().start();
		
	}

}
