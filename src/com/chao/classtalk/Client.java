package com.chao.classtalk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

public class Client{
	  String dstAddress;
	  int dstPort;
	  String ServerHost;
	  int ServerPort;
	  String response = "";
	  Socket clientSocket = null;
	  PrintWriter out = null;
	  BufferedReader input = null;
	  BufferedReader input2 = null;
	  Model model;
	  Talk talk;
	  String personName;
	  Login login;
	  boolean done = false;
	  String password;
	  String hostname;
	  String port;
	  boolean doneBinder = false;
	  boolean hostget = false;
	  boolean portget = false;
	  boolean login_fail = false;
	  boolean signup_fail = false;
	  
	  Client(String addr, int port, Model m,  Login login) throws UnknownHostException, IOException{
		  dstAddress = addr;
		  dstPort = port;
		  this.login = login;
		  model = m;
		  done = false;

	  }
	  Client(String addr, int port, Model m, Talk t, int binder_server){
		 if(binder_server == 0){
			 dstAddress = addr;
			 dstPort = port;
		 }else{
		 	ServerHost = addr;
		 	ServerPort = port;
		 }
	   model = m;
	   talk = t;
	   //new Thread(new ConnectToServer()).start();
	  }
	  
	  
	  class RequestSever implements Runnable{
		  Client client;
		  public RequestSever(Client client) {
			  this.client = client;
		  }
		@Override
		public void run() {
			try{
				client.clientSocket = new Socket(dstAddress,dstPort);
				if(client.clientSocket == null){
					Log.d("Socket","creation failed");
					System.exit(-1);
				}
				DataOutputStream out_stream = new DataOutputStream(client.clientSocket.getOutputStream());
				out_stream.writeInt(3);
				out_stream.writeBytes(model.GetRoom());
				if(model.GetBuilding().equals("DC")){
					out_stream.writeInt(2);
				}else if(model.GetBuilding().equals("MC")){
					out_stream.writeInt(1);
				}
				
				//"ubuntu1204-002.student.cs.uwaterloo.ca"
				String l = "ubuntu1204-004";
				int length = l.length();
				byte[] buffer2 = new byte[length];
				InputStream is = clientSocket.getInputStream();
				is.read(buffer2);
				hostname = new String(buffer2);
				hostname = hostname + ".student.cs.uwaterloo.ca";
				hostget = true;
				Log.d("Client: 85", "host: "+hostname+"length: "+length);
				
				int rval = -1;
				Vector<Integer> v = new Vector<Integer>();
				do{
					rval = is.read();
					if(rval != -1){
						if(port == null){
							port = ""+(char)rval;
						}else{
							port += (char)rval;
						}
					}
				}while(rval != -1);
				Log.d("Client: 89", "port: "+port);
				portget = true;
				client.clientSocket.close();
			}catch(Exception e){
				//do something
			}
		}	  
	  }
	  
	  String getHost(){
		  while(!hostget){
			  //Log.d("waiting","server host");
		  }
		  return hostname;
	  }
	  
	  String getPort(){
		  while(!portget){
			  //Log.d("waiting","server port");
		  }
		  return port;
	  }
	  
	  class ConnectToBinder implements Runnable{
		  Client client;
		  String sign_log;
		  public ConnectToBinder(Client client, String sign_log) {
			  this.client = client;
			  this.sign_log = sign_log;
		  }
		@Override
		public void run() {
			try {
				client.clientSocket = new Socket(dstAddress,dstPort);
				if(client.clientSocket == null){
					Log.d("Socket","creation failed");
					doneBinder = true;
				}
				DataOutputStream out_stream = new DataOutputStream(client.clientSocket.getOutputStream());
				  
				  out_stream.writeInt(1);
				  out_stream.writeBytes(sign_log);
				  
				  Log.d("Client105105105" , sign_log);
				  
				  int length;
				  if(!sign_log.equals("FACEBOOK")){
					  length = personName.length();
					  out_stream.writeInt(length);
					  out_stream.writeBytes(personName);
					  out_stream.writeInt(password.length());
					  out_stream.writeBytes(password);
				  }

				//out.flush();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				Log.d("from server","auiwhduidhauiwd we are catched");
				doneBinder = true;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("from server","iudahwdiuwhdi we are catched");
				doneBinder = true;
			}

			  while(!doneBinder){
					String success = null;
					try {
						while(clientSocket == null){}
							Log.d("Socket ","create successfully");
						//BufferedReader inputs = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						InputStream is = clientSocket.getInputStream();
							//BufferedReader inputt =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
							byte[] buffer = new byte[1];
							is.read(buffer);
						//success = inputs.readLine();
							success = new String(buffer);
							Log.d("Client:103" , success);
						if(success.equals("F")) {
							Log.d("Client:105", "faileuresrlssf dsala!");
							if(sign_log.equals("SIGN_UPP")){
								login.feedback("F_Signup");
								signup_fail = true;
							}else{
								login.feedback("F");
								login_fail = true;
							}
							done = true;
							break;
						}
						else if(success.equals("N")) {
							login.feedback("N");
							done = true;
							break;
						}
						else if(success.equals("S")) {
							if(sign_log.equals("SIGN_UPP")){
								//dialog
								signup_fail = false;
								login.signupSuccessfull();
								done = true;
								break;
							}
							while(true){
								login_fail = false;
								int building;
								String room = null;
								byte[] buffer2 = new byte[1];
								is.read(buffer2);
								//success = inputs.readLine();
								success = new String(buffer2);
								Log.d("Client:line121", " read llllaaa  " +  success);
	
								if(success.equals("1")) {
									done = true;
									login.feedback("S");
									doneBinder = true;
									break;
								}
								else if(success.equals("0")) {
									Log.d("Client1010dfd01", " si ge");
									//input =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
									//inputs.read(buildings, 0, 4);
									byte[] buffer3 = new byte[2];
									int bu = is.read(buffer3);
									String buildings = new String(buffer3);
									
									Log.d("Client: 137 ","building is " + buildings) ;
									byte[] buffer4 = new byte[4];
									bu = is.read(buffer4);
									room = new String(buffer4);
									Log.d("Client: 141 ","the room number is "+room);
									
									login.addBuildingRooms(buildings, room);
									
								}
							}
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			  try {
				  if(client.clientSocket != null)
				client.clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	  }
	  
	  boolean doneconnecttobinder(Client client , String sign_log) throws IOException{
		  if(!sign_log.equals("FACEBOOK")){
			  personName = model.getName();
			  password = model.getPassword();
			  Log.d("Client105105105" , "person name is " + personName);
			  Log.d("Client105105105" , "password is " + password);
			  //Log.d("Client105105105" , "name length is " + personName.length());
			  //Log.d("Client105105105" , "password length is " + password.length());
		  }
		  done = false;
		  doneBinder = false;
		  new Thread(new ConnectToBinder(client, sign_log)).start();
		  //while(!doneBinder){Log.d("waiting for connecttion","binder no response yet");}
		return done;  
	  }
	  
	  boolean isConnectionToBinderDone(){
		  return done;
	  }
	  
	  void requestServerInfo(){
		  new Thread(new RequestSever(this)).start();
	  }
	  
	  void connectServer(){
		  new Thread(new ConnectToServer()).start();
	  }
	  
	  
	  // connect to server
	  class ConnectToServer implements Runnable{
		  	public ConnectToServer() {
		  		}
		@Override
		public void run() {
			// TODO Auto-generated method stub

			//PrintWriter out;
			try {
				Log.d("Client: 278", "before connect sever");
				clientSocket = new Socket(ServerHost,ServerPort);
				Log.d("Client: 278", "after connect sever");
				if(out == null)
				out  = new PrintWriter(clientSocket.getOutputStream(), true);
//				out.println("hello server\n");
//				out.flush();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				Log.d("Client 285","UnknownHost");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("Client 289","IO exception");

			}

			while(true){
				String message = null;
				try {
					while(clientSocket == null){}
					input =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					message = input.readLine();
					//while(clientSocket == null){}
					//input2 =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					//message = input.readLine();
					Log.d("from server ",message + " " + personName);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				talk.updateModel(message);
				Log.d("from server",message);
			}
		}
		  
	  }
	  
	  boolean loginFailed(){
		  return login_fail;
	  }
	  
	  boolean signupFailed(){
		  return signup_fail;
	  }
	  
	  void closeBinderSocket() throws IOException{
		  if(clientSocket !=null && clientSocket.isConnected()){
			  clientSocket.close();
		  }
	  }


	public PrintWriter getPrintWriter(){
		int i = 0;
		while(out == null){
			//Log.d("Client WAITING" , "print writer" + i );
			i++;
		}
		return out;
	}

}
