package it.unige.sosi.POIManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ResponsesReceiver extends BroadcastReceiver {

	final static String TABLES = "it.unige.sosi.intent.action.TABLES";
	final static String TABLE = "it.unige.sosi.intent.action.TABLE";
	final static String RESPONSE = "it.unige.sosi.intent.action.RESPONSE";
	
	final static String RESPONSEVALUE = "it.unige.sosi.RESPONSEVALUE";
	final static String ADDEDCODE = "it.unige.sosi.ADDEDCODE";
	final static String ITEM_ALLTABLESNAMES = "it.unige.sosi.POIViewer.ALLTABLESNAMES";
	final static String ITEM_ALLPOIS = "it.unige.sosi.POIViewer.ALLPOIS";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().compareTo(TABLES) == 0){
			Intent recallIntent = new Intent(context, POIManagerActivity.class);
			recallIntent.setAction(TABLES);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			recallIntent.putExtra(ITEM_ALLTABLESNAMES, intent.getExtras().getStringArray(ITEM_ALLTABLESNAMES));
			context.startActivity(recallIntent);
		}
		if(intent.getAction().compareTo(TABLE) == 0){
			Intent recallIntent = new Intent(context, POIManagerActivity.class);
			recallIntent.setAction(TABLE);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			recallIntent.putExtra(ITEM_ALLPOIS, intent.getExtras().getStringArray(ITEM_ALLPOIS));
			context.startActivity(recallIntent);
		}
		if(intent.getAction().compareTo(RESPONSE) == 0){
			Intent recallIntent = new Intent(context, POIManagerActivity.class);
			recallIntent.setAction(RESPONSE);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			recallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			recallIntent.putExtra(RESPONSEVALUE, intent.getExtras().getBoolean(RESPONSEVALUE));
			recallIntent.putExtra(ADDEDCODE, intent.getExtras().getString(ADDEDCODE));
			context.startActivity(recallIntent);
		}

	}

}
