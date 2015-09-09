package com.chao.classtalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.chao.classtalk.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.support.v4.app.*;

public class MainActivity extends Activity implements Observer, LocationListener  {
	
	Spinner BuildingSpinner;
	Spinner RoomSpinner;
	Button EnterButt;
	Button Logout;
	String inputBuilding;
	String inputRoom;
	Model model;
	ArrayList<String[]> Rooms;
	String[] buildings = {"DC","MC"};
	ArrayList<String> DCrooms;
	ArrayList<String> MCrooms;
	ArrayAdapter<String> adapter, adapter1, adapter2;
	String personName;
	Client client;
	GoogleMap map;
	Fragment google_map;
	MainActivity mainac;
	AlertDialog welcomeDialog;
//	FragmentTransaction Fragmenttr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("MainActivity", "MainActivity:onCreat");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.MainActivity);
		rl.setBackgroundResource(R.drawable.dc);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		//client = new Client("ubuntu1204-004.student.cs.uwaterloo.ca",33787,this);
		mainac = this;
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			//do things on resume mode
			Intent i = getIntent();
			personName = extras.getString("PersonName");
			model = (Model)i.getSerializableExtra("Model");
			MCrooms = model.getMCrooms();
			DCrooms = model.getDCrooms();
			Log.d("MainActivity: 77", "get MC and DC's rooms");
			if(MCrooms != null)
			Log.d("MC room: ",MCrooms.get(0));
		}else{
			Log.d("MainActivity: 79", "didn't get MC and DC's rooms");
		}
		
		
		BuildingSpinner = (Spinner) findViewById(R.id.Building_Spinner);
		RoomSpinner = (Spinner) findViewById(R.id.Room_Spinner);
		EnterButt = (Button) findViewById(R.id.Enter_Button);
		Logout = (Button) findViewById(R.id.logout_button);
		google_map = (Fragment) getFragmentManager().findFragmentById(R.id.map);
//		Fragmenttr = getFragmentManager().beginTransaction();
		
		//adapters for spinnners
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,buildings);
		BuildingSpinner.setAdapter(adapter);
		adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,DCrooms);
		adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,MCrooms);
		
		EnterButt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Start(arg0);
			}
		});
		
		Logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		
		// google map setup
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
        }else { 
            MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            map = fm.getMap();
            map.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newLatLng, 16);
//    		map.animateCamera(update);
        }
		
		BuildingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.MainActivity);
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				if(BuildingSpinner.getSelectedItem().toString().equalsIgnoreCase("DC")){
					RoomSpinner.setAdapter(adapter1);	
					rl.setBackgroundResource(R.drawable.dc);
				}
				else{
					RoomSpinner.setAdapter(adapter2);
					rl.setBackgroundResource(R.drawable.mc);
				}
				inputBuilding = BuildingSpinner.getSelectedItem().toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}		
		});
		
		RoomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				inputRoom = RoomSpinner.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//model = new Model();
		model.addObserver(this);
		model.initObservers();
		this.welcome();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	public void Start(View v) {
		Intent intent = new Intent(this, Talk.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("BuildingET" , inputBuilding);
		intent.putExtra("RoomET", inputRoom);
		intent.putExtra("PersonName", personName);
		Log.d("MainActivity!!!!!!!!!!!!!" , inputBuilding + inputRoom);
		startActivity(intent);
	}
	
	public void logout(){
//		Intent intent = new Intent(this, Login.class);
//		startActivity(intent);
		finish();
		onBackPressed();
	}

	public void Exit(View v) {
		finish();
		System.exit(0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("chaochen","back here");
		Bundle extras = getIntent().getExtras();
		Intent i = getIntent();
		if(extras != null) {
			//do things on resume mode
			personName = extras.getString("PersonName");
			MCrooms = extras.getStringArrayList("MCrooms");
			DCrooms = extras.getStringArrayList("DCrooms");
			model = (Model)i.getSerializableExtra("Model");
		}
	}
	
	//called by xml show_map_button
	public void Hide_Map(View v) {
//		Fragmenttr.show(google_map);
		if(model.getVisible() == 0) {
			google_map.getView().setVisibility(View.VISIBLE);
			model.setVisible();
		}
		else {
			google_map.getView().setVisibility(View.INVISIBLE);
			model.setInvisible();
		}
		
	}
	
	void welcome(){
		mainac = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						mainac);
		 
					// set title
					alertDialogBuilder.setTitle("ClassTalk");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Welcome to ClassTalk\n"+personName+"\nPlease select your room")
						.setCancelable(false)
						.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						welcomeDialog = alertDialogBuilder.create();
						welcomeDialog.show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*for rotation*/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//save all the data
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//do all restore stuff
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
