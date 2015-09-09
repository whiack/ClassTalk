package com.chao.classtalk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Observable;
import java.util.Observer;

import com.chao.classtalk.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

@SuppressLint("NewApi")
public class Chat extends LinearLayout implements Observer{

	private TextView chat;
	private ScrollView scroll;
	private EditText input;
	private Button send;
	private String inputString = "";
		
	Model model;
	Client client;
	PrintWriter out;
	String received_message;
	Talk talk;
	Chat chatt;
	int sendBuilding = 1;
	Socket clientSocket = null;
	BufferedReader inputBuffer = null;
	String hostname;
	int PortNumber;
	
	public Chat(Context context , Model m, Talk t) throws IOException {
		super(context);
		model = m;
		talk = t;
		model.addObserver(this);
		

		chatt = this;
		View.inflate(context, R.layout.activity_chat, this);
		
		Log.d("Chat" , " get into chat" );
		
		chat = (TextView) findViewById(R.id.textView1);	
		scroll = (ScrollView) findViewById(R.id.scrollView1);
		input = (EditText) findViewById(R.id.input);
		input.setBackground(getResources().getDrawable(R.drawable.login_rec));
		send = (Button) findViewById(R.id.send);
		
		input.addTextChangedListener( new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				inputString = "";
				inputString = input.getText().toString();
				Log.d("MainActivity" , "Building "+inputString);
				
				int l = 0;
				char lastChar;
				if(inputString != null){
					l = inputString.length()-1;
				}
				if(l>0){ 
					lastChar = inputString.charAt(l);
					if(lastChar == '\n'){
						inputString = inputString.substring(0, l);
						chatt.sendMessage();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
//				inputString = input.getText().toString();

			}
			
		});
	
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				String name = model.getName();
//				if (sendBuilding == 1) {
//					//String BuildRoom = model.GetBuilding() + model.GetRoom();
//					
//					sendBuilding = 0;
//				}
//				if(out != null){
//					out.write(name +":"+ inputString);
//					out.flush();
//				}
//				
//				if((inputString.equals(""))||(inputString.equals(" ")) || (inputString.equals("	"))){
//					talk.emptyText();
//				}
//				else{
//					model.setBuffer(name + ":"+ inputString);
//					model.initObservers();
//					input.setText("");
//				}
				chatt.sendMessage();
			}
		});
		Log.d("Chat" , " here chat" );
		String host = talk.getServerHost();
		int port = Integer.parseInt(talk.getServerPort());
		Log.d("Chat: 102","host: "+ host+" port: "+port);
		client = new Client(host,port, model,talk,1);
		client.connectServer();
		//String message = null;
		Log.d("Chat" , " connect server" );
		
		//client.execute();
		Log.d("Chat" , " execute server" );
		out = client.getPrintWriter();
		DataOutputStream out_stream = new DataOutputStream(client.clientSocket.getOutputStream());
		Log.d("Chat" , " out server" );
		String BuildRoom = model.GetBuilding() + model.GetRoom();
		out.println(BuildRoom);
		out.flush();
		out.println(model.getName());
		Log.d("Chat 117" , "person name " + model.getName() );
		//Log.d("Chat" , " write server" );
		//out.flush();
		//out.write("Chao");
		out.flush();
		Log.d("Chat 122 " , " finish chat" );
	}

	public void receivedMessage(String s){
		received_message = s;
	}
	
	void sendMessage(){
		String name = model.getName();
		if (sendBuilding == 1) {
			//String BuildRoom = model.GetBuilding() + model.GetRoom();
			
			sendBuilding = 0;
		}
		
		if((inputString.equals(""))||(inputString.equals(" ")) || (inputString.equals("	"))){
			talk.emptyText();
		}
		else{
			if(out != null){
				out.write(name +":"+ inputString);
				out.flush();
			}
			model.setBuffer(name + ":"+ inputString);
			model.initObservers();
			input.setText("");
		}
	}
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(chat != null){
			chat.setText(model.getBuffer());
			scroll.scrollTo(0,chat.getBottom());
		}
	}

}
