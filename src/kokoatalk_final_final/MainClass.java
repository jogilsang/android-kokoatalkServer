package kokoatalk_final_final;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainClass {

	public static String ServerIP;
	private ServerSocket server;
	public static int serverPort = 30000;
	
	//����� ��ü���� �����ϴ� ArrayList
	ArrayList<UserClass> user_list;
	
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		
	    ServerIP = InetAddress.getLocalHost().getHostAddress();
		
		// ���� ������ ǥ��
		System.out.println(ServerIP + ":" + String.valueOf(serverPort));
		
		new MainClass();
	}	
    //���θ޼ҵ尡 static���� �Ǿ��ֱ� ������ �ٸ��͵��� �� static ���� �ϱ� ������ ������
	// ���� �����ڸ� ���� ���� - > ���ο����� ȣ�������� ��ɸ� �����ϴ°� ����.
	public MainClass() {
		try {
			user_list=new ArrayList<UserClass>();
			// ���� ����
			server=new ServerSocket(serverPort);
			// ����� ���� ��� ������ ����
			ConnectionThread thread= new ConnectionThread();
			thread.start();
		}catch(Exception e) {e.printStackTrace();}
	}
	
	class ConnectionThread extends Thread{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true) {
					System.out.println("����� ���� ���");
					Socket socket=server.accept();
					System.out.println("����ڰ� �����Ͽ����ϴ�.");
					// ����� �г����� ó���ϴ� ������ ����
					NickNameThread thread=new NickNameThread(socket);
					thread.start();
					
				}
			}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	class NickNameThread extends Thread{
		private Socket socket;
		
		public NickNameThread(Socket socket) {
			this.socket=socket;
		}
		public void run() {
			try {
				// ��Ʈ�� ����
				InputStream is = socket.getInputStream();
				OutputStream os= socket.getOutputStream();
				DataInputStream dis=new DataInputStream(is);
				DataOutputStream dos=new DataOutputStream(os);
				
				// ������ Ȯ��
				String data = dis.readUTF();
				
				final int splitNum = data.indexOf("&");
                final String ip = data.substring(0, splitNum);

                // ��ȣȭ�� �򹮹���
                final String nickName = data.substring(splitNum + 1);
	
                // ���� ip���
				if (ip.contains("175.45.176")) {
					System.out.println("403 : "+nickName+"���ٱ���");
					dos.writeUTF(nickName+" ���� ���ѻ���Դϴ�.");
				}
				// ���� ip�� �ƴ϶��
				else {
					
					// �г��� ����
					//String nickName=dis.readUTF();
					// ȯ�� �޼����� �����Ѵ�.
					System.out.println("200 : "+nickName+" ���� ������ �����մϴ�");
					System.out.println("�޼��� ���� : "+nickName+" �� ȯ���մϴ�.");
					dos.writeUTF(nickName+" �� ȯ���մϴ�.");
					// �� ���ӵ� ����ڵ鿡�� ���� �޼����� �����Ѵ�.
					sendToClient("���� : "+nickName+"���� �����Ͽ����ϴ�.");
					// ����� ������ �����ϴ� ��ü�� �����Ѵ�.
					UserClass user= new UserClass(nickName,socket);
					user.start();
					user_list.add(user);
					
				}
			
			}catch(Exception e) {e.printStackTrace();}
		}
			
	}
	
	class UserClass extends Thread {
		String nickName;
		Socket socket;
		DataInputStream dis;
		DataOutputStream dos;
		
		public UserClass(String nickName,Socket socket) {
			try {
			this.nickName=nickName;
			this.socket=socket;
			InputStream is=socket.getInputStream();
			OutputStream os=socket.getOutputStream();
			dis = new DataInputStream(is);
			dos=new DataOutputStream(os);
			
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		// ����ڷκ��� �޼����� ���Ź޴� ������
		public void run() {
			try {
				while(true) {
					//Ŭ���̾�Ʈ���� �޼����� ���Ź޴´�.
				
					String msg=dis.readUTF();
					System.out.println("�޼��� ���� : "+ msg);
					// ����ڵ鿡�� �޼����� �����Ѵ�
					sendToClient(nickName+ " : "+ msg); 
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void sendToClient(String msg) {
		try {
			// ������� ����ŭ �ݺ�
			for (UserClass user : user_list) {
				// �޼����� Ŭ���̾�Ʈ�鿡�� �����Ѵ�.
				System.out.println("��ε�ĳ��Ʈ : "+ msg);
				user.dos.writeUTF(msg);
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
	