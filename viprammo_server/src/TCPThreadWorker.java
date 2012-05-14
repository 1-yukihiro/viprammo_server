import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import viprammo.message.CharacterModifMessage;
import viprammo.message.ChatMessage;
import viprammo.message.CommandMessage;
import viprammo.message.Message;
import viprammo.message.MessageHeader;
import viprammo.message.MessageKIND;
import viprammo.message.UserInputMessage;


public class TCPThreadWorker extends Thread {

	Socket socket;
	OutputStream os;
	ArrayList<Byte[]> data = new ArrayList<Byte[]>();
	public String name = null;
	
	int x = 300;
	int y = 300;
	
	private boolean flg = true;
	private String muki = "s0";
	
	public void run() {

		int rnum = 0;
		InputStream is = null;
		try {
			is = socket.getInputStream();
//			byte[] bf = new byte[256];
//			while (true) {
//				rnum = is.read(bf);
//				this.name = (new String(bf, 0, rnum).split("AM")[1]).replaceAll("\r\n", "");
//				System.out.println(name);
//				if ((rnum == -1) || (name != null)) {
//					break;
//				}
//				
//			}
//			System.out.println("AISATUOWARI NAME=" + this.name);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			os = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		new InputStreamKanshi(is).start();	
		
		try {
			while (flg) {

				try {
					
					for (int i = 0; i < data.size(); i++) {
						
						byte[] d = new byte[data.get(i).length];
						for (int j = 0; j < d.length; j++) {
							d[j] = data.get(i)[j];
						}
						
						os.write(d);
						os.flush();
						data.remove(i);
					}
					
				} catch (SocketException e) {
					e.printStackTrace();
					flg = false;
				} catch (IOException e) {
					flg = false;
					e.printStackTrace();
				}

			}
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("OWATA");
		
		ThreadList.getInstance().deleteWorkerByName(this.name);
		
	}
	
	class InputStreamKanshi extends Thread {
		InputStream kis;
		public InputStreamKanshi(InputStream kis_src) {kis = kis_src;}
		public void run() {
			
			ObjectInputStream ois = null;
			
			try {
				ois = new ObjectInputStream(kis);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			int length = 0;
			byte ident = 0;
			
			while (true) {
				
				try {
					
					//読み込み
					length = ois.readInt();
					ident = ois.readByte();
					System.out.println("length=" + length);
					System.out.println("ident=" + ident);
					
					CommandMessage cmessage = (CommandMessage)ois.readObject();
				
					//処理開始
					TCPThreadWorker tcptw = ThreadList.getInstance().getWorkerByName(name);
					
					for (Message m : cmessage.getMessageList()) {
						
						switch (m.getKIND()) {
						case MessageKIND.KIND_USERINPUT:
							UserInputMessage uimessage = (UserInputMessage)m;
							char c = uimessage.getKeyChar();
							
							switch (c) {
							case 'w':
								tcptw.addy(-2);
								break;
							case 'a':
								tcptw.addx(-2);
								break;
							case 's':
								tcptw.addy(2);
								break;
							case 'd':
								tcptw.addx(2);
								break;
							}
							
							tcptw.setMuki(String.valueOf(c));
							
							for (TCPThreadWorker tcptww : ThreadList.getInstance().getThreadList()) {
								CommandMessage c_message_send = new CommandMessage();
								c_message_send.setMessageHeader(new MessageHeader());
								CharacterModifMessage cmm = new CharacterModifMessage();
								cmm.setUser(tcptww.getNameM());
								cmm.setX(tcptww.getX());
								cmm.setY(tcptww.getY());
								c_message_send.addMessage(cmm);
								tcptww.send(c_message_send);
							}
							break;
						case MessageKIND.KIND_CHAT_MESSAGE:
							
							ChatMessage chatmessage = (ChatMessage)m;
							
							CommandMessage c_message_send = new CommandMessage();
							c_message_send.setMessageHeader(new MessageHeader());
							c_message_send.addMessage(chatmessage);
							
							for (TCPThreadWorker tcptww : ThreadList.getInstance().getThreadList()) {
								tcptww.send(c_message_send);
							}
							
							break;
						}
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void send(CommandMessage cm) {
		
		ByteArrayOutputStream bytearray = null;
		
		try {
			bytearray = new ByteArrayOutputStream();
			ObjectOutputStream oss = new ObjectOutputStream(bytearray);
			oss.writeObject(cm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buff = bytearray.toByteArray();
		
		int length = buff.length;
		byte ident = 1;
		ByteBuffer bf = ByteBuffer.allocate(5 + buff.length);
		bf.putInt(length);
		bf.put(ident);
		bf.put(buff);
		
		this.send(bf.array());
		
	}
	
	public void send(byte[] data) {

		System.out.println(new String(data));
		Byte[] bd = new Byte[data.length];
		for (int i = 0; i < data.length; i++) {
			bd[i] = data[i];
		}
		this.data.add(bd);
		
		System.out.println("send iam = " + name);
	}
	
	public TCPThreadWorker(Socket socket) {
		Collections.synchronizedList(this.data);
		this.socket = socket;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void addx(int i) {
		this.x+=i;
	}
	
	public void addy(int i) {
		this.y+=i;
	}

	public void setMuki(String str) {
		if (str.equals("w")) {
			if (this.muki.equals("w1")) {
				this.muki = "w2";
			} else {
				this.muki = "w1";
			}
		} else if (str.equals("a")) {
			if (this.muki.equals("a1")) {
				this.muki = "a2";
			} else {
				this.muki = "a1";
			}
		} else if (str.equals("s")) {
			if (this.muki.equals("s1")) {
				this.muki = "s2";
			} else {
				this.muki = "s1";
			}
		} else if (str.equals("d")) {
			if (this.muki.equals("d1")) {
				this.muki = "d2";
			} else {
				this.muki = "d1";
			}
		}
	}
	
	public String getMuki() {
		return this.muki;
	}
	
	public String getNameM() {
		return this.name;
	}
}
