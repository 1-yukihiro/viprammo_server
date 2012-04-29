import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSocket extends Thread {

	ServerSocket svsock;

	public void run() {

		while (true) {
			Socket sct = null;
			try {
				sct = svsock.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			TCPThreadWorker tcpsock_worker = new TCPThreadWorker(sct);
			ThreadList.getInstance().Tadd(tcpsock_worker);
			tcpsock_worker.start();
		}

	}

	public TCPServerSocket() {
		try {
			svsock = new ServerSocket(10001);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
