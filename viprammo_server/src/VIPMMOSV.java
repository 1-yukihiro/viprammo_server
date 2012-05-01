
public class VIPMMOSV {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UDPSocketServerReceiver us = new UDPSocketServerReceiver();
		us.start();
		TCPServerSocket ts = new TCPServerSocket();
		ts.start();
		ThreadList.getInstance().start();
		
	}

}
