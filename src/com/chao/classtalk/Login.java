package com.chao.classtalk;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.*;

import com.chao.classtalk.R;
import com.facebook.*;
import com.facebook.Session.StatusCallback;
import com.facebook.android.*;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.model.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Login extends Activity implements Observer, OnClickListener {

	Button LoginButt;
	//ImageView LoginButt;
	Model model;
	EditText login_name;
	String personName="null";
	boolean getname = false;
	ImageView fb_connect_button;
	ImageView fb_login_button;
	facebook_helper fb_helper;
	Session activeSession;
	String id;
	DialogListener dialoglistener;
	Session session;
	View view;
	
	EditText signup_name;
	EditText real_name;
	EditText signup_password;
	EditText login_password;
	String user_password = "null";
	String personName_real =  "null";
	Button SignUpButt;
	Button goToSignUpButt;
	Button goToLogin;
	ArrayList<String> DCrooms;
	ArrayList<String> MCrooms;
	ArrayList<Button> buttonlist;
	ArrayList<EditText> edittextlist;
	ArrayList<ImageView> imageviewlist;
	Client client;
	String feedback1;
	Login login;
	AlertDialog alertDialog;
	ProgressBar spinner;
	boolean loginphase = true;
	Timer t;
	Timer t2;
	Timer timerForConnection;
	timer timertask;
	timer2 timertask2;
	TimerTaskForLogin timertaskForLogin;
	TimerTaskForSignup timertaskForSignup;
	int signupCounter = 0;
	int loginCounter = 0;
	int counter = 0;
	boolean isFBConnected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("MainActivity", "MainActivity:onCreat");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.login);
		rl.setBackgroundResource(R.drawable.dc);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		LoginButt = (Button)findViewById(R.id.login_button);
		login_name = (EditText)findViewById(R.id.login_name);
		login_name.setBackground(getResources().getDrawable(R.drawable.login_rec));
		login_password = (EditText)findViewById(R.id.login_password);
		login_password.setBackground(getResources().getDrawable(R.drawable.login_rec));
		signup_name = (EditText)findViewById(R.id.signup_name);
		signup_name.setBackground(getResources().getDrawable(R.drawable.login_rec));
		signup_password = (EditText)findViewById(R.id.signup_password);
		signup_password.setBackground(getResources().getDrawable(R.drawable.login_rec));
		SignUpButt = (Button)findViewById(R.id.signup);
		goToSignUpButt = (Button)findViewById(R.id.goToSignUp);
		goToLogin = (Button)findViewById(R.id.goToLogin);
		goToLogin.setVisibility(View.INVISIBLE);
//		real_name = (EditText)findViewById(R.id.real_name);
		
		id = "855558154473328";
		//id = "355198514515820";
		fb_helper = new facebook_helper(id);
		
		fb_connect_button = (ImageView) findViewById(R.id.facebook_connect_button);
		
		fb_connect_button.setOnClickListener(this);
		
		fb_login_button = (ImageView) findViewById(R.id.facebook_login_button);
		fb_login_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					personName = fb_helper.getUserName();
					client.doneconnecttobinder(client, "FACEBOOK");
					login.Loading();
					login.resetTimerTask();
					timerForConnection.schedule(timertaskForLogin, 0,500);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		fb_login_button.setVisibility(View.INVISIBLE);
		
		spinner = (ProgressBar) findViewById(R.id.waittingSpinner);
		spinner.setVisibility(View.INVISIBLE);
		
		LoginButt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					personName = login_name.getText().toString();
					if(personName.equals("")){
						emptyName();
					}else{
						model.setName(personName);
						user_password = login_password.getText().toString();
						if(user_password.length() < 5){
							invalidPassword();
						}
						else{
							model.setPassword(user_password);
							client.doneconnecttobinder(client, "SIGN_INN");
							login.Loading();
							login.resetTimerTask();
							timerForConnection.schedule(timertaskForLogin, 0, 500);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("connect to binder","ERROR");
					e.printStackTrace();
				}
				
			}
		});
		
		
		SignUpButt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					personName = signup_name.getText().toString();
					if(personName.equals("")){
						emptyName();
					}else{
						model.setName(personName);
						user_password = signup_password.getText().toString();
						if(user_password.length() < 5){
							invalidPassword();
						}
						else{
							model.setPassword(user_password);
							client.doneconnecttobinder(client, "SIGN_UPP");
							login.Loading();
							login.resetTimerTask();
							timerForConnection.schedule(timertaskForSignup, 0, 500);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("User sign up","ERROR");
					e.printStackTrace();
				}
			
			}
		});
		SignUpButt.setVisibility(View.INVISIBLE);
		
		login_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
		
		login_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
		
		
		signup_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            	personName = signup_name.getText().toString();
            	model.setName(personName);
            }
        });
		signup_name.setVisibility(View.INVISIBLE);
		
		signup_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub              
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            	user_password = signup_password.getText().toString();
            	model.setPassword(user_password);
            }
        });
		signup_password.setVisibility(View.INVISIBLE);
		
		goToSignUpButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SignUpButt.setVisibility(View.VISIBLE);
				signup_name.setVisibility(View.VISIBLE);
				signup_password.setVisibility(View.VISIBLE);
				LoginButt.setVisibility(View.INVISIBLE);
				login_name.setVisibility(View.INVISIBLE);
				login_password.setVisibility(View.INVISIBLE);
				goToLogin.setVisibility(View.VISIBLE);
				goToSignUpButt.setVisibility(View.INVISIBLE);
				if(isFBConnected){
					fb_login_button.setVisibility(View.INVISIBLE);
				}else{
					fb_connect_button.setVisibility(View.INVISIBLE);
				}
				loginphase = false;
			}
		});
		
		goToLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isFBConnected){
					fb_login_button.setVisibility(View.VISIBLE);
				}else{
					fb_connect_button.setVisibility(View.VISIBLE);
				}
				SignUpButt.setVisibility(View.INVISIBLE);
				signup_name.setVisibility(View.INVISIBLE);
				signup_password.setVisibility(View.INVISIBLE);
				LoginButt.setVisibility(View.VISIBLE);
				login_name.setVisibility(View.VISIBLE);
				login_password.setVisibility(View.VISIBLE);
				goToSignUpButt.setVisibility(View.VISIBLE);
				goToLogin.setVisibility(View.INVISIBLE);
				loginphase = true;
			}
		});
		
		model = new Model();
		model.addObserver(this);
		model.initObservers();
		try {
			client = new Client("ubuntu1204-002.student.cs.uwaterloo.ca",59787, model, this);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//connect to binder
		t = new Timer();
		t2 = new Timer();
		timerForConnection = new Timer();
		timertask = new timer(this);
		timertask2 = new timer2(this);
		timertaskForLogin = new TimerTaskForLogin(this);
		timertaskForSignup = new TimerTaskForSignup(this);
		login = this;
		DCrooms = new ArrayList<String>();
		MCrooms = new ArrayList<String>();
		if(!DCrooms.isEmpty()){
			DCrooms.clear();
		}
		if(!MCrooms.isEmpty()){
			MCrooms.clear();
		}
		try{
			PackageInfo info = getPackageManager().getPackageInfo("com.chao.classtalk", PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	        }
		}catch(Exception e){
			
		}
		
	}//onCreate
	
	@Override
	protected void onResume() {
		super.onResume();
		DCrooms = new ArrayList<String>();
		MCrooms = new ArrayList<String>();
		if(!DCrooms.isEmpty()){
			DCrooms.clear();
		}
		if(!MCrooms.isEmpty()){
			MCrooms.clear();
		}
	}
	
	public void addBuildingRooms(String building,String room){
		if(building.equals("MC")){
			Log.d("Login: 263","add MC room into list");
			if(MCrooms == null){
				MCrooms = new ArrayList<String>();
			}
			MCrooms.add(room);
		}
		else if(building.equals("DC")){
			Log.d("Login: 267","add DC room into list");
			if(DCrooms == null){
				DCrooms = new ArrayList<String>();
			}
			DCrooms.add(room);
		}
		else{
			Log.d("Login: 270","add rooms failed");;
		}
		model.setMCrooms(MCrooms);
		model.setDCrooms(DCrooms);
	}
	
	public void Start() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("PersonName" , personName);
		intent.putExtra("passwrod", user_password);
		intent.putExtra("mcrooms" , MCrooms);
		intent.putExtra("dcrooms" , DCrooms);
		intent.putExtra("Model" , model);
		try {
			client.closeBinderSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startActivity(intent);
	}	
	
	void Loading(){
		if(loginphase){
			if(isFBConnected){
				fb_login_button.setVisibility(View.INVISIBLE);
			}else{
				fb_connect_button.setVisibility(View.INVISIBLE);
			}
			LoginButt.setVisibility(View.INVISIBLE);
			login_name.setVisibility(View.INVISIBLE);
			login_password.setVisibility(View.INVISIBLE);
			goToSignUpButt.setVisibility(View.INVISIBLE);
		}
		else{
			SignUpButt.setVisibility(View.INVISIBLE);
			signup_name.setVisibility(View.INVISIBLE);
			signup_password.setVisibility(View.INVISIBLE);
			goToLogin.setVisibility(View.INVISIBLE);
		}
		spinner.setVisibility(View.VISIBLE);
	}
	
	void LoadingDone(){
		if(loginphase){
			if(isFBConnected){
				fb_login_button.setVisibility(View.VISIBLE);
			}else{
				fb_connect_button.setVisibility(View.VISIBLE);
			}
			LoginButt.setVisibility(View.VISIBLE);
			login_name.setVisibility(View.VISIBLE);
			login_password.setVisibility(View.VISIBLE);
			goToSignUpButt.setVisibility(View.VISIBLE);
		}
		else{
			SignUpButt.setVisibility(View.VISIBLE);
			signup_name.setVisibility(View.VISIBLE);
			signup_password.setVisibility(View.VISIBLE);
			goToLogin.setVisibility(View.VISIBLE);
		}
		spinner.setVisibility(View.INVISIBLE);
	}
	
	public void feedback (String feedback) {
		feedback1 = feedback;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(feedback1.equals("F")) {
					Log.d("Client101001", "faileuresrlssf dsala!");
					login_name.setText("");
					login_password.setText("");
					login.loginFailed();
				}
				else if(feedback1.equals("F_Signup")){
					login.signupFailed();
				}
				else if(feedback1.equals("N")) {
					Log.d("Client101001", " yea yea no server la");
					finish();
				}
				else if(feedback1.equals("S")) {
					Log.d("Client101001", "s  sss s tttt araa r rrr t");
//					client.doneconnecttobinder(client, "SIGN_INN")
					//Start();
				}
			}
		});
		
	}
	
	void signupSuccessfull(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Congratulations!");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Signup successfully!")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
		
	void signupFailed(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Sign up failed");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("There is already such a user name!")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
	
	void loginFailed(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Login failed");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Login failed!")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
	
	void NoResponseFromBinder(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Connection to binder failed");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("The server may not be running at this time. Please contact kolachao.chen@gmail.com to run the server.")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
	
	void emptyName(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Empty Name");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Please do not leave your name empty")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
	
	void invalidPassword(){
		login = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						login);
		 
					// set title
					alertDialogBuilder.setTitle("Invalid Password");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Please enter at least 5 characters")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						alertDialog = alertDialogBuilder.create();
						alertDialog.show();
			}
		});
	}
	
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		
	}
	
	class timer extends TimerTask{
		Login login;
		public timer(Login login) {
			// TODO Auto-generated constructor stub
			this.login = login;
		}
		@Override
		public void run() {
			login.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					login.shortWaitingDone();
				}
			});
			
		}	
	}
	
	class timer2 extends TimerTask{
		Login login;
		public timer2(Login login) {
			this.login = login;
		}
		@Override
		public void run() {
			login.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					login.LoadingDone();
				}
			});
			
		}	
	}
	
	class TimerTaskForLogin extends TimerTask{
		Login login;
		public TimerTaskForLogin(Login login) {
			this.login = login;
		}
		@Override
		public void run() {
			login.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!client.isConnectionToBinderDone()){
						if(loginCounter > 5){
							Log.d("the login counter is ",Integer.toString(loginCounter));
							login.LoadingDone();
							NoResponseFromBinder();
							timerForConnection.cancel();
							loginCounter = 0;
						}else{
							loginCounter++;
						}
					}else{
						login.LoadingDone();
						timerForConnection.cancel();
						loginCounter = 0;
						if(!client.loginFailed()){
							Start();
						}
					}
				}
			});
			
		}	
	}
	
	class TimerTaskForSignup extends TimerTask{
		Login login;
		public TimerTaskForSignup(Login login) {
			this.login = login;
		}
		@Override
		public void run() {
			login.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!client.isConnectionToBinderDone()){
						if(signupCounter > 5){
							login.LoadingDone();
							timerForConnection.cancel();
							NoResponseFromBinder();
							signupCounter = 0;
						}else{
							signupCounter++;
						}
					}else{
						login.LoadingDone();
						signupCounter = 0;
						timerForConnection.cancel();
					}
				}
			});
			
		}	
	}

	void shortWaitingDone(){
		if(loginphase){
			LoginButt.setVisibility(View.VISIBLE);
			login_name.setVisibility(View.VISIBLE);
			login_password.setVisibility(View.VISIBLE);
			goToSignUpButt.setVisibility(View.VISIBLE);
		}
		else{
			SignUpButt.setVisibility(View.VISIBLE);
			signup_name.setVisibility(View.VISIBLE);
			signup_password.setVisibility(View.VISIBLE);
			goToLogin.setVisibility(View.VISIBLE);
		}
		spinner.setVisibility(View.INVISIBLE);
		fb_connect_button.setVisibility(View.VISIBLE);
	}
	
	Timer getConnectionTimer(){
		return timerForConnection;
	}
	
	TimerTaskForLogin getLoginTimerTask(){
		return timertaskForLogin;
	}
	
	TimerTaskForSignup getSignupTimerTask(){
		return timertaskForSignup;
	}
	
	void resetTimerTask(){
		if(timerForConnection != null){
			timerForConnection.cancel();
		}
		timerForConnection = new Timer();
		if(timertaskForLogin != null){
			timertaskForLogin.cancel();
		}
		if(timertaskForSignup != null){
			timertaskForSignup.cancel();
		}
		timertaskForSignup = new TimerTaskForSignup(this);
		timertaskForLogin = new TimerTaskForLogin(this);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(counter == 0){
			this.Loading();
			fb_connect_button.setVisibility(View.INVISIBLE);
			fb_helper.facebook_login(arg0, this);
			t.schedule(timertask, 1000);
			counter++;

			
		}else{
			counter++;
			this.Loading();
			t2.schedule(timertask2, 2000);
			fb_helper.facebook_login(arg0, this);
			fb_connect_button.setVisibility(View.INVISIBLE);
			isFBConnected = true;
		}
		//fb_login_button.setVisibility(View.VISIBLE);
	}
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }
}
