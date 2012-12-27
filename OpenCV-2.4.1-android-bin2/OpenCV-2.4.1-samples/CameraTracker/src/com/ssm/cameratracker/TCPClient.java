package com.ssm.cameratracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.util.Log;

public class TCPClient {
	int size=4096;
	byte[] buf=new byte[size];
	FileInputStream fis;
	DataOutputStream dos;
	DataInputStream dis;
	ObjectOutputStream oos;
	Socket socket;
	FileInfo fi;
	String coordinate[];
	float vert[]={0,0,0,0,0,0,0,0};
	
	public TCPClient(File f, String fName, Socket sock){
		// TODO Auto-generated constructor stub
		fi=new FileInfo();
		fi.fileName=fName;
		fi.fileSize=f.length();
		fi.data=f;
		socket=sock;
		try {
			fis=new FileInputStream(fi.data);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void fileSend(float test[]){
		try {
			while(!dis.readLine().endsWith("ACK")){}
			dos.write(fi.fileName.getBytes());
			dos.flush();
			
			while(!dis.readLine().endsWith("ACK")){}
			dos.write(String.valueOf(fi.fileSize).getBytes());
			dos.flush();
			while (fis.read(buf) != -1) {
				while(!dis.readLine().endsWith("ACK")){}
				dos.write(buf, 0, buf.length);
				Log.d("TCP Client", "complete");
			}
			
			dos.write("EOF".getBytes());
			//getting OpenGL Coordinate
			coordinate=dis.readUTF().split(" ");
//			for(int i=0;!coordinate[i].endsWith("\n");i++)
				for(int i=0;i<8;i++)
			{
				Log.i("sysout", i+" : "+coordinate[i+1]);
				test[i]=Float.parseFloat(coordinate[i+1]);
				Log.i("sysout", "vert : "+vert[i]);
			}
			//return vert;

			//r.onDrawFrame(gl);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return vert;
	}
}
