package com.chao.classtalk;

import java.util.Observable;
import java.util.Observer;

import com.chao.classtalk.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Heading extends LinearLayout implements Observer{
	
	Model model;
	
	TextView Building;
	TextView Room;
	
	public Heading(Context context, Model m) {
		super(context);
		
		model = m;
		
		model.addObserver(this);
		
		View.inflate(context, R.layout.activity_heading, this);
		Log.d("Heading" , " get into heading" );
		
		Building = (TextView)findViewById(R.id.Building);
		Room = (TextView)findViewById(R.id.Room);
		
		Log.d("Heading" , " finish heading" );
	}
	
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		Log.d("Heading" , "Heading model building "+ model.GetBuilding());
		Log.d("Heading???????????" , model.GetBuilding() + model.GetRoom());
		// TODO Auto-generated method stub
		//blaite: improve appearence
		Room.setText(model.GetRoom());
		Building.setText(model.GetBuilding());
	}
	

}
