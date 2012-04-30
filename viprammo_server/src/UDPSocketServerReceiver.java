import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import static viprammo.Constants.*;

public class UDPSocketServerReceiver extends Thread {

	DatagramSocket receiveSocket;
	DatagramPacket packet;

	public void run() {

		String command_str;
		String name;
		String cmd_char;
		StringBuilder sb = new StringBuilder();
		
		while (true) {
			try {
				receiveSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			command_str = new String(packet.getData(), 0, packet.getLength()).replaceAll("\r\n", "");
//			name = command_str.split(",")[0];
//			cmd_char = command_str.split(",")[2];
			String[] commands = command_str.split(",");
			name = commands[0];
			cmd_char = commands[2];
			
			TCPThreadWorker tcptw = ThreadList.getInstance().getWorkerByName(name);
			
			if (cmd_char.equals("w")) {
				tcptw.addy(-2);
			} else if (cmd_char.equals("a")) {
				tcptw.addx(-2);
			} else if (cmd_char.equals("s")) {
				tcptw.addy(2);
			} else if (cmd_char.equals("d")) {
				tcptw.addx(2);
			} else {
				continue;
			}
			
			int count = 0;
			for (TCPThreadWorker ttw : ThreadList.getInstance().getThreadList()) {
				
				sb.append("M,");
				sb.append(ttw.getNameM()); sb.append(",");
				sb.append(ttw.getX()); sb.append(",");
				sb.append(ttw.getY()); sb.append(",");
				if (ttw.getNameM().equals(name)) {
					//System.out.println(name);
					ttw.setMuki(cmd_char);
				}
				sb.append(ttw.getMuki());
				sb.append(",");
				count++;
				
			}
			sb.append("\r\n");
			
			for (TCPThreadWorker ttww : ThreadList.getInstance().getThreadList()) {
				ttww.send((count + "-" + sb.toString()).getBytes());	
			}

			sb.delete(0, sb.length());
		}
	}

	public UDPSocketServerReceiver() {
		try {
			receiveSocket = new DatagramSocket(kUDPPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte[] buff = new byte[1024];
		packet = new DatagramPacket(buff, buff.length);

	}
}