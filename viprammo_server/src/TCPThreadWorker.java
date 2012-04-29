import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;


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
			byte[] bf = new byte[256];
			while (true) {
				rnum = is.read(bf);
				this.name = (new String(bf, 0, rnum).split("AM")[1]).replaceAll("\r\n", "");
				System.out.println(name);
				if ((rnum == -1) || (name != null)) {
					break;
				}
				
			}
			System.out.println("AISATUOWARI NAME=" + this.name);
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
			int readnum = 0;
			try {
				while ((readnum = kis.read()) != -1) {
					Thread.sleep(100);
				}
			} catch (IOException e) {
				flg = false;
			} catch (InterruptedException e) {
				
			} finally {
				flg = false;
			}
		}
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
				this.muki = "w1";
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
