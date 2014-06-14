package it.unige.sosi.POIManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import it.unige.sosi.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class POIManagerActivity extends Activity {
	

	final static String SHOW_TABLE = "it.unige.sosi.intent.action.SHOW_TABLE";
	final static String SHOW_PATH = "it.unige.sosi.intent.action.SHOW_PATH";
	final static String LIST_TABLES = "it.unige.sosi.intent.action.LIST_TABLES";
	final static String GET_TABLE = "it.unige.sosi.intent.action.GET_TABLE";
	final static String ADD_POI = "it.unige.sosi.intent.action.ADD_POI";
	final static String RMV_POI = "it.unige.sosi.intent.action.RMV_POI";
	final static String CREATE_TABLE = "it.unige.sosi.intent.action.CREATE_TABLE";
	final static String DELETE_TABLE = "it.unige.sosi.intent.action.DELETE_TABLE";
	
	final static String ITEM_TABLENAME = "it.unige.sosi.POIViewer.TABLENAME";
	final static String ITEM_PATH = "it.unige.sosi.POIViewer.PATH";
	final static String ITEM_ALLTABLESNAMES = "it.unige.sosi.POIViewer.ALLTABLESNAMES";
	final static String ITEM_ALLPOIS = "it.unige.sosi.POIViewer.ALLPOIS";
	final static String ITEM_POI = "it.unige.sosi.POIViewer.POI";
	final static String RESPONSEVALUE = "it.unige.sosi.RESPONSEVALUE";
	
	final static String DB_URI = "content://it.unige.sosi";
	final static String TABLE_URI = "content://it.unige.sosi/table/";
	
	
	private final static String POINAME_KEY = "POIName";
	private final static String CHILDPOINAME_KEY = "ChildPOIName";
	private final static String POIACTION_KEY = "ChildPOIAction";
	private final static String POIPOINT_KEY = "ChildPOIPoint";

	private final static String[] GROUP_KEYS = { POINAME_KEY };
	private final static int[] GROUP_VIEW_IDS = { R.id.POIName };

	private final static String[] CHILD_KEYS = { CHILDPOINAME_KEY, POIACTION_KEY, POIPOINT_KEY };
	private final static int[] CHILD_VIEW_IDS = { R.id.ChildPOIName, R.id.ChildPOIAct, R.id.ChildPOIPoint };
	
	final static String PREFERENCES = "POIManagerPreferenceFile";
	final static String ACTUALTABLENAME = "ActualTable";
	final static String TABLEDIALOG = "isTableDialogOpen";
	final static String NEWTABLENAME = "NewTableName";
	final static String POIDIALOG = "isPOIDIalogOpen";
	final static String NEWPOINAME = "NewPoiName";
	final static String NEWPOIACT = "NewPoiAct";
	final static String NEWPOILATLON = "NewPoiLatLon";
	final static String EDITDIALOG = "isEditDialogOpen";
	final static String EDITMODE = "EditMode";
	final static String EDITPOINAME = "EditPoiName";
	final static String EDITPOIACT = "EditPoiAct";
	final static String EDITPOILATLON = "EditPoiLatLon";
	final static String POITOREPLACENAME = "PoiToReplaceName";
	final static String POITOREPLACEACT = "PoiToReplaceAct";
	final static String POITOREPLACELATLON = "PoiToReplaceLatLon";
	final static String POITOREPLACETABLE = "PoiToReplaceTable";
	final static String EXPANDEDPOIGROUPS = "ExpandedPoiGroups";
	
	final static int minimumButtonSizeDp = 100;

	
	String[] allTablesNames;	
	String actualTable;	
	ArrayList<OverlayItem> actualPOIs;	
	ArrayList<Button> tableButtons;
	LinearLayout tableButtonsContainer;
	ExpandableListView POIListView;
	Button addPOIButton;
	
	boolean tableButtonsScroll;
	
	String newPoiNameTmpString;
	String newPoiActTmpString;
	String newPoiLatLonTmpString;
	String newTableNameTmpString;
	
	boolean editMode;
	String editPoiNameTmpString;
	String editPoiActTmpString;
	String editPoiLatLonTmpString;
	String PoiToReplaceNameTmpString;
	String PoiToReplaceActTmpString;
	String PoiToReplaceLatLonTmpString;
	String PoiToReplaceTableTmpString;
	
	String expandedPoiGroups;
	
	AlertDialog.Builder POIAlert;
	AlertDialog POIDialog;
	boolean isPOIDialogOpen;
	AlertDialog.Builder TableAlert;
	AlertDialog TableDialog;
	boolean isTableDialogOpen;
	boolean isEditDialogOpen;
	
	boolean tableStateChanged;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        allTablesNames = null;
        actualTable = null;
        actualPOIs = new ArrayList<OverlayItem>();
        tableButtons = new ArrayList<Button>();
        tableButtonsContainer = (LinearLayout)findViewById(R.id.tabContainer);
        POIListView = (ExpandableListView)findViewById(R.id.POIListView);
        

        addPOIButton = new Button(this);
        addPOIButton.setText(R.string.addPOIButtonText);
        addPOIButton.setGravity(Gravity.CENTER);
        addPOIButton.setTextColor(Color.WHITE);
        addPOIButton.setTextSize(22);
        addPOIButton.setBackgroundColor(Color.TRANSPARENT);
        addPOIButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNewPoiButtonClick(v);
            }
        });
        addPOIButton.setEnabled(false);
        addPOIButton.setVisibility(1);
        addPOIButton.invalidate();
		POIListView.addFooterView(addPOIButton);
		POIListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			public void onGroupExpand(int groupPosition) {
				tableStateChanged = true;
			}
		});
		POIListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			public void onGroupCollapse(int groupPosition) {
				tableStateChanged = true;
			}
		});
		
		newPoiNameTmpString = null;
		newPoiActTmpString = null;
		newPoiLatLonTmpString = null;
		newTableNameTmpString = null;
		isPOIDialogOpen = false;
		isTableDialogOpen = false;
		isEditDialogOpen = false;
		
		tableStateChanged = false;
		
    	SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    	actualTable = preferences.getString(ACTUALTABLENAME, null);
    	isTableDialogOpen = preferences.getBoolean(TABLEDIALOG, false);
    	newTableNameTmpString = preferences.getString(NEWTABLENAME, null);
    	isPOIDialogOpen = preferences.getBoolean(POIDIALOG, false);
    	newPoiNameTmpString = preferences.getString(NEWPOINAME, null);
    	newPoiActTmpString = preferences.getString(NEWPOIACT, null);
    	newPoiLatLonTmpString = preferences.getString(NEWPOILATLON, null);
    	isEditDialogOpen = preferences.getBoolean(EDITDIALOG, false);
    	editMode = preferences.getBoolean(EDITMODE, false);
    	editPoiNameTmpString = preferences.getString(EDITPOINAME, null);
    	editPoiActTmpString = preferences.getString(EDITPOIACT, null);
    	editPoiLatLonTmpString = preferences.getString(EDITPOILATLON, null);
    	PoiToReplaceNameTmpString = preferences.getString(POITOREPLACENAME, null);
    	PoiToReplaceActTmpString = preferences.getString(POITOREPLACEACT, null);
    	PoiToReplaceLatLonTmpString = preferences.getString(POITOREPLACELATLON, null);
    	PoiToReplaceTableTmpString = preferences.getString(POITOREPLACETABLE, null);
    	
    	expandedPoiGroups = preferences.getString(EXPANDEDPOIGROUPS, null);
    	
    	if(isTableDialogOpen) onNewTableButtonClick(null);
    	if(isPOIDialogOpen) onNewPoiButtonClick(null);
    	if(isEditDialogOpen) OpenEditPOIDialog();
    	
    	Update();
    }
    
    
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    }
    

    @Override
    protected void onPause() {
    	super.onPause();
    	
    	SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putString(ACTUALTABLENAME, actualTable);
    	editor.putBoolean(TABLEDIALOG, isTableDialogOpen);
    	editor.putString(NEWTABLENAME, newTableNameTmpString);
    	editor.putBoolean(POIDIALOG, isPOIDialogOpen);
    	editor.putString(NEWPOINAME, newPoiNameTmpString);
    	editor.putString(NEWPOIACT, newPoiActTmpString);
    	editor.putString(NEWPOILATLON, newPoiLatLonTmpString);
    	editor.putBoolean(EDITDIALOG, isEditDialogOpen);
    	editor.putBoolean(EDITMODE, editMode);
    	editor.putString(EDITPOINAME, editPoiNameTmpString);
    	editor.putString(EDITPOIACT, editPoiActTmpString);
    	editor.putString(EDITPOILATLON, editPoiLatLonTmpString);
    	editor.putString(POITOREPLACENAME, PoiToReplaceNameTmpString);
    	editor.putString(POITOREPLACEACT, PoiToReplaceActTmpString);
    	editor.putString(POITOREPLACELATLON, PoiToReplaceLatLonTmpString);
    	editor.putString(POITOREPLACETABLE, PoiToReplaceTableTmpString);
    	
    	SaveListState();
    	
    	editor.putString(EXPANDEDPOIGROUPS, expandedPoiGroups);
    	
    	editor.commit();
    }
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(actualTable != null && tableButtonsScroll){
    		int pixelSpan = 0;
    		for(int i = 0; i < tableButtons.size(); i++){
        		if(tableButtons.get(i).getText().toString().compareTo(actualTable) == 0){
        			HorizontalScrollView scrollView = (HorizontalScrollView)findViewById(R.id.tabScroller);
        			int offset = getWindowManager().getDefaultDisplay().getWidth()/4;
        			if(pixelSpan > offset) pixelSpan -= offset;
        			else pixelSpan = 0;
        			scrollView.smoothScrollTo(pixelSpan, 0);
        			break;
    			}
        		else {
        			pixelSpan += tableButtons.get(i).getWidth();
        		}
        	}
    	}
    }
    
    private void LoadGraphic()
    {
    	List<Map<String, String>> groupMap = new ArrayList<Map<String, String>>();
		for(int i = 0; i < actualPOIs.size(); i++){
			Map<String,String> groupData = new HashMap<String,String>();
			groupData.put(POINAME_KEY, actualPOIs.get(i).getTitle());
			groupMap.add(groupData);
		}
		
		List<List<Map<String, String>>> dataMap = new ArrayList<List<Map<String, String>>>();
		for(int i = 0; i < actualPOIs.size(); i++){
			List<Map<String, String>> itemMap = new ArrayList<Map<String, String>>();
				Map<String,String> groupData = new HashMap<String,String>();
				groupData.put(CHILDPOINAME_KEY, actualPOIs.get(i).getTitle());
				groupData.put(POIACTION_KEY, actualPOIs.get(i).getSnippet());
				groupData.put(POIPOINT_KEY, actualPOIs.get(i).getPoint().toString());
				itemMap.add(groupData);	
			dataMap.add(itemMap);			
		}
    	
        ExpandableListAdapter expListAdapter = new SimpleExpandableListAdapter(
        		this,
        		groupMap, R.layout.collapsed_layout,  GROUP_KEYS, GROUP_VIEW_IDS,
        		dataMap, R.layout.child_layout,  CHILD_KEYS, CHILD_VIEW_IDS);
        POIListView.setAdapter(expListAdapter);
        
        
        
    }
    
    @SuppressWarnings("unchecked")
	@Override
    protected void onNewIntent(Intent intent) {
    	if(intent.getAction().compareTo(ResponsesReceiver.TABLES) == 0){
    		tableButtons = new ArrayList<Button>();
    		tableButtonsContainer.removeAllViews();
    		allTablesNames = intent.getExtras().getStringArray(ResponsesReceiver.ITEM_ALLTABLESNAMES);
    		if (allTablesNames != null){
    			float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minimumButtonSizeDp, getResources().getDisplayMetrics());
	    		for(int i = 0; i < allTablesNames.length; i++){
	    			Button newButton = new Button(this);
	    			newButton.setText(allTablesNames[i]);
	    			newButton.setMinWidth((int) pixel);
	    			newButton.setOnClickListener(new View.OnClickListener() {
	    	             public void onClick(View v) {
	    	                 onTableButtonClick(v);
	    	             }
	    	         });
	    			tableButtons.add(newButton);
	    			tableButtonsContainer.addView(newButton);
	    		}
    		}
    		Button newTableButton = new Button(this);
    		newTableButton.setText(R.string.newTableButtonText);
    		newTableButton.setBackgroundResource(R.drawable.manager_defaultbutton);
    		newTableButton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	                 onNewTableButtonClick(v);
	             }
	         });
    		tableButtonsContainer.addView(newTableButton);
    		tableButtonsContainer.invalidate();
    		

    		boolean tableFound = false;
    		addPOIButton.setEnabled(false);
    		addPOIButton.setVisibility(1);
    		addPOIButton.invalidate();
    		while(!tableFound){
    			if(actualTable == null || actualTable == "")
                	actualTable = (allTablesNames != null && allTablesNames.length > 0 ? allTablesNames[0] : null);
    			
    			if(actualTable != null){
    				for(String thisTableName : allTablesNames){
    					if(thisTableName.compareTo(actualTable) == 0){
    						tableFound = true;
    						addPOIButton.setEnabled(true);
    						addPOIButton.setVisibility(0);
    						addPOIButton.invalidate();
    						break;
    					}
    				}
    				if(tableFound){
	                	ChangeActualTable(actualTable, true);
    				}
                }
    			else{
    				break;
    			}
    		}
    		
    		
    	}
    	if(intent.getAction().compareTo(ResponsesReceiver.TABLE) == 0){
    		actualPOIs = new ArrayList<OverlayItem>();
    		String[] poiString = intent.getExtras().getStringArray(ResponsesReceiver.ITEM_ALLPOIS);
    		if(poiString != null){
    			for (int i = 0; i < poiString.length; i++){
    				String[] thisPoiString = poiString[i].split(";");
    				actualPOIs.add(new OverlayItem(new GeoPoint(Integer.parseInt(thisPoiString[2]),Integer.parseInt(thisPoiString[3])), thisPoiString[0], thisPoiString[1]));
    			}
    		}
    		
    		LoadGraphic();
    		
    		if(expandedPoiGroups != null && expandedPoiGroups.length() > 0){
    			String[] expgs = expandedPoiGroups.split(";");
    			int k = 0;
    			for(int i = 0; i < expgs.length; i++){
    				for(int j = 0; j < POIListView.getAdapter().getCount() - 1 -k; j++){
    					if(((HashMap<String,String>)(POIListView.getExpandableListAdapter().getGroup(j))).get(POINAME_KEY).compareTo(expgs[i]) == 0) {POIListView.expandGroup(j); k++; break;}
    				}
    			}
    		}
    	}
    	if(intent.getAction().compareTo(ResponsesReceiver.RESPONSE) == 0){
    		
    		if(editMode && intent.getExtras().getBoolean(ResponsesReceiver.RESPONSEVALUE)){
    			String[] addedcode = intent.getExtras().getString(ResponsesReceiver.ADDEDCODE).split(";");
				if(addedcode[0].compareTo("RMV") == 0 && addedcode[1].compareTo("POI") == 0 && addedcode[2].compareTo(editPoiNameTmpString) == 0){
					editMode = false;
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					int Lat;
					int Lon;
					String[] LatLonArray = PoiToReplaceLatLonTmpString.split(",");
					try{
						Lat = Integer.parseInt(LatLonArray[0]);
						Lon = Integer.parseInt(LatLonArray[1]);
					}
					catch (Exception e) {
						Lat = 0;
						Lon = 0;
					}
					AddNewPOIRequest(PoiToReplaceTableTmpString, PoiToReplaceNameTmpString, PoiToReplaceActTmpString, Lat, Lon);
					PoiToReplaceNameTmpString = null;
					PoiToReplaceActTmpString = null;
					PoiToReplaceLatLonTmpString = null;
					PoiToReplaceTableTmpString = null;
				}
    		}
    		else if (editMode && !intent.getExtras().getBoolean(ResponsesReceiver.RESPONSEVALUE)){
    			String[] addedcode = intent.getExtras().getString(ResponsesReceiver.ADDEDCODE).split(";");
    			if(addedcode[0].compareTo("RMV") == 0 && addedcode[1].compareTo("POI") == 0 && addedcode[2].compareTo(editPoiNameTmpString) == 0){
    				editMode = false;
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					PoiToReplaceNameTmpString = null;
					PoiToReplaceActTmpString = null;
					PoiToReplaceLatLonTmpString = null;
					PoiToReplaceTableTmpString = null;
    				ShowMessage(false);
				}
    		}
    		else if(intent.getExtras().getBoolean(ResponsesReceiver.RESPONSEVALUE)){
    			ShowMessage(true);
    			String[] addedcode = intent.getExtras().getString(ResponsesReceiver.ADDEDCODE).split(";");
    			
    			if(addedcode[0].compareTo("ADD") == 0 && addedcode[1].compareTo("TABLE") == 0) actualTable = addedcode[2];
    			
        		Update();
    		}
    		else ShowMessage(false);
    		
    	}
    	
    	
    	
    	super.onNewIntent(intent);
    }
    
    
    public void ShowMessage(boolean confirm){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	if(confirm){
    		alert.setTitle("Operazione eseguita con successo!");
    	}
    	else{
    		alert.setTitle("Errore durante l'interfacciamento con il database...");
    	}
		alert.setNeutralButton("OK",null);
		alert.show();
    }


	public void onTableButtonClick(View v) {
		Button selectedButton = (Button)v;
		
		if(selectedButton.getText().toString().compareTo(actualTable) != 0){
			newPoiNameTmpString = null;
			newPoiActTmpString = null;
			newPoiLatLonTmpString = null;
			expandedPoiGroups = null;
			ChangeActualTable(selectedButton.getText().toString(), false);
			
		}
	}
	
	private void ChangeActualTable(String newTable, boolean scrollToTable){
		actualTable = newTable;
		addPOIButton.setEnabled(true);
		addPOIButton.setVisibility(0);
		addPOIButton.invalidate();
		for(int i = 0; i < tableButtons.size(); i++){
    		if(tableButtons.get(i).getText().toString().compareTo(actualTable) == 0){
    			tableButtons.get(i).setBackgroundResource(R.drawable.selectedtab);
    			tableButtonsScroll = scrollToTable;
			}
    		else {
    			tableButtons.get(i).setBackgroundResource(R.drawable.unselectedtab);
    		}
    	}
    	
    	Intent updateIntent = new Intent();
        updateIntent.setAction(GET_TABLE);
        updateIntent.setData(Uri.parse(TABLE_URI + actualTable));
        sendBroadcast(updateIntent);
	}
	
	
	public void onNewTableButtonClick(View v){
		TableAlert = new AlertDialog.Builder(this);

		TableAlert.setTitle(R.string.addTableDialogTitle);

		final TextView requestNameTV = new TextView(this);
		requestNameTV.setText(R.string.addTableDialogNameLabel);
		
		final EditText inputName = new EditText(this);
		inputName.setText(newTableNameTmpString);
		inputName.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				newTableNameTmpString = inputName.getText().toString();
			}
		});
		
		final LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(1);
		dialogLayout.addView(requestNameTV);
		dialogLayout.addView(inputName);
		
		
		final ScrollView dialogScrollView = new ScrollView(this);
		dialogScrollView.addView(dialogLayout);
		
		TableAlert.setView(dialogScrollView);
		

		TableAlert.setPositiveButton(R.string.addTableConfirmButton, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String newTableName = inputName.getText().toString();
			
			boolean okToAdd = true;
			if(newTableName == null || newTableName.compareTo("") == 0 || newTableName.compareTo(" ") == 0){
				okToAdd = false;
				Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorEmptyName, Toast.LENGTH_LONG);
		        toast.show();
		        isTableDialogOpen = false;
		        return;
			}
			for(String thisTableName : allTablesNames){
				if(thisTableName.compareTo(newTableName) == 0) {
					okToAdd = false;
					break;
				}
			}
			if(!okToAdd){
				Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorAlreadyUsedName, Toast.LENGTH_LONG);
		        toast.show();
		        isTableDialogOpen = false;
		        return;
			}
			
			inputName.setText(null);
			isTableDialogOpen = false;
			newPoiNameTmpString = null;
			newPoiActTmpString = null;
			newPoiLatLonTmpString = null;
			expandedPoiGroups = null;
			AddNewTableRequest(newTableName);
		  }
		});

		TableAlert.setNegativeButton(R.string.addTableCancelButton,  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				isTableDialogOpen = false;
				inputName.setText(null);
			}
		});

		
		AlertDialog thisDialog = TableAlert.show();
		isTableDialogOpen = true;
		
		
		thisDialog.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				isTableDialogOpen = false;
				inputName.setText(null);
			}
		});
	}
	
	public void onNewPoiButtonClick(View v){
		POIAlert = new AlertDialog.Builder(this);

		POIAlert.setTitle(R.string.addPOIDialogTitle);

		final TextView requestNameTV = new TextView(this);
		requestNameTV.setText(R.string.addPOIDialogNameLabel);
		final TextView requestActionTV = new TextView(this);
		requestActionTV.setText(R.string.addPOIDialogActionLabel);
		final TextView requestLatLonTV = new TextView(this);
		requestLatLonTV.setText(R.string.addPOIDialogLatLonLabel);
		
		final EditText inputName = new EditText(this);
		final EditText inputAction = new EditText(this);
		final EditText inputLatLon = new EditText(this);
		
		TextWatcher tw = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				newPoiNameTmpString = inputName.getText().toString();
				newPoiActTmpString = inputAction.getText().toString();
				newPoiLatLonTmpString = inputLatLon.getText().toString();
			}
		};
		
		inputName.setText(newPoiNameTmpString);
		inputName.addTextChangedListener(tw);
		inputAction.setText(newPoiActTmpString);
		inputAction.addTextChangedListener(tw);
		inputLatLon.setText(newPoiLatLonTmpString);
		inputLatLon.addTextChangedListener(tw);
		
		final LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(1);
		dialogLayout.addView(requestNameTV);
		dialogLayout.addView(inputName);
		dialogLayout.addView(requestActionTV);
		dialogLayout.addView(inputAction);
		dialogLayout.addView(requestLatLonTV);
		dialogLayout.addView(inputLatLon);
		
		final ScrollView dialogScrollView = new ScrollView(this);
		dialogScrollView.addView(dialogLayout);
		
		POIAlert.setView(dialogScrollView);
		

		POIAlert.setPositiveButton(R.string.addPOIConfirmButton, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String newPOIName = inputName.getText().toString();
			String newPOIAction = inputAction.getText().toString();
			String newPOILatLon = inputLatLon.getText().toString();
			boolean okToAdd = true;
			if(newPOIName == null || newPOIName.compareTo("") == 0 || newPOIName.compareTo(" ") == 0){
				okToAdd = false;
				Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorEmptyName, Toast.LENGTH_LONG);
		        toast.show();
		        isPOIDialogOpen = false;
		        return;
			}
			for(OverlayItem thisPOI : actualPOIs){
				if(thisPOI.getTitle().compareTo(newPOIName) == 0) {
					okToAdd = false;
					break;
				}
			}
			if(!okToAdd){
				Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorAlreadyUsedName, Toast.LENGTH_LONG);
		        toast.show();
		        isPOIDialogOpen = false;
		        return;
			}
			int Lat = 0;
			int Lon = 0;
			if(newPOILatLon.contains(",")){
				String[] LatLonArray = newPOILatLon.split(",");
				try{
					Lat = Integer.parseInt(LatLonArray[0]);
					Lon = Integer.parseInt(LatLonArray[1]);
				}
				catch (Exception e) {
					okToAdd = false;
				}
			}
			else okToAdd = false;
			
			if(!okToAdd){
				Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorLatLonFormatException, Toast.LENGTH_LONG);
		        toast.show();
		        isPOIDialogOpen = false;
		        return;
			}
			
			inputName.setText(null);
			inputAction.setText(null);
			inputLatLon.setText(null);
			isPOIDialogOpen = false;
			AddNewPOIRequest(actualTable, newPOIName, newPOIAction, Lat, Lon);
		  }
		});

		POIAlert.setNegativeButton(R.string.addPOICancelButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				isPOIDialogOpen = false;
				inputName.setText(null);
				inputAction.setText(null);
				inputLatLon.setText(null);
			}
		});

		AlertDialog thisDialog = POIAlert.show();
		isPOIDialogOpen = true;
		
		thisDialog.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				isPOIDialogOpen = false;
				inputName.setText(null);
				inputAction.setText(null);
				inputLatLon.setText(null);
			}
		});
	}
	
	public void onShowTableClick(View v){
		if(actualTable == null || actualTable.compareTo("") == 0 || actualTable.compareTo(" ") == 0){
			return;
		}
		Intent intent = new Intent();
        intent.setAction(SHOW_TABLE);
        intent.setData(Uri.parse(TABLE_URI + actualTable));
        sendBroadcast(intent);
	}
	
	public void onRemoveTableButtonClick(View v){
		if(actualTable == null || actualTable.compareTo("") == 0 || actualTable.compareTo(" ") == 0){
			return;
		}
		AlertDialog.Builder DeleteAlert = new AlertDialog.Builder(this);

		DeleteAlert.setTitle(R.string.ConfirmLabel);
		
		DeleteAlert.setPositiveButton(R.string.OkLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				Intent intent = new Intent();
		        intent.setAction(DELETE_TABLE);
		        intent.setData(Uri.parse(TABLE_URI + actualTable));
		        actualTable = null;
		        sendBroadcast(intent);
			}
		});
		
		DeleteAlert.setNegativeButton(R.string.NoLabel, null);
		
		DeleteAlert.show();
	}
	
    
	private void AddNewPOIRequest(String tableName, String name, String action, int Lat, int Lon){
		Intent intent = new Intent();
        intent.setAction(ADD_POI);
        intent.setData(Uri.parse(TABLE_URI + tableName));
        intent.putExtra(ITEM_POI, String.format("%s;%s;%d;%d", name, action,Lat,Lon));
        sendBroadcast(intent);
	}
	
	private void AddNewTableRequest(String tableName){
		Intent intent = new Intent();
        intent.setAction(CREATE_TABLE);
        intent.setData(Uri.parse(TABLE_URI + tableName));
        sendBroadcast(intent);
	}
	
	
	/** Chiama gli intent necessari all'Update della activity */
	private void Update(){
		Intent updateIntent = new Intent();
        updateIntent.setAction(LIST_TABLES);
        updateIntent.setData(Uri.parse(DB_URI));
        sendBroadcast(updateIntent);
	}
	
	public void	onDeletePOIButtonClick(View v){
		LinearLayout parent = (LinearLayout)v.getParent().getParent();
		TextView POINameView = (TextView)parent.findViewById(R.id.ChildPOIName);
		final String POIToDelete = POINameView.getText().toString();
		
		AlertDialog.Builder DeleteAlert = new AlertDialog.Builder(this);

		DeleteAlert.setTitle(R.string.ConfirmLabel);
		
		DeleteAlert.setPositiveButton(R.string.OkLabel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				Intent intent = new Intent();
		        intent.setAction(RMV_POI);
		        intent.setData(Uri.parse(TABLE_URI + actualTable));	//TODO
		        intent.putExtra(ITEM_POI, POIToDelete);
		        sendBroadcast(intent);
			}
		});
		
		DeleteAlert.setNegativeButton(R.string.NoLabel, null);
		
		DeleteAlert.show();
	}
	
	public void onEditPOIButtonClick(View v){
		LinearLayout parent = (LinearLayout)v.getParent().getParent();
		editPoiNameTmpString = ((TextView)parent.findViewById(R.id.ChildPOIName)).getText().toString();
		editPoiActTmpString = ((TextView)parent.findViewById(R.id.ChildPOIAct)).getText().toString();
		editPoiLatLonTmpString = ((TextView)parent.findViewById(R.id.ChildPOIPoint)).getText().toString();
		newPoiNameTmpString = editPoiNameTmpString;
		newPoiActTmpString = editPoiActTmpString;
		newPoiLatLonTmpString = editPoiLatLonTmpString;
		
		OpenEditPOIDialog();
	}
	
	private void OpenEditPOIDialog()
	{
		PoiToReplaceNameTmpString = null;
		PoiToReplaceActTmpString = null;
		PoiToReplaceLatLonTmpString = null;
		PoiToReplaceTableTmpString = null;
		
		POIAlert = new AlertDialog.Builder(this);

		POIAlert.setTitle(R.string.EditPOIDialogTitle);

		final TextView requestNameTV = new TextView(this);
		requestNameTV.setText(R.string.addPOIDialogNameLabel);
		final TextView requestActionTV = new TextView(this);
		requestActionTV.setText(R.string.addPOIDialogActionLabel);
		final TextView requestLatLonTV = new TextView(this);
		requestLatLonTV.setText(R.string.addPOIDialogLatLonLabel);
		
		final EditText inputName = new EditText(this);
		final EditText inputAction = new EditText(this);
		final EditText inputLatLon = new EditText(this);
		
		TextWatcher tw = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {
				newPoiNameTmpString = inputName.getText().toString();
				newPoiActTmpString = inputAction.getText().toString();
				newPoiLatLonTmpString = inputLatLon.getText().toString();
			}
		};
		
		inputName.setText(newPoiNameTmpString);
		inputName.addTextChangedListener(tw);
		inputAction.setText(newPoiActTmpString);
		inputAction.addTextChangedListener(tw);
		inputLatLon.setText(newPoiLatLonTmpString);
		inputLatLon.addTextChangedListener(tw);
		
		final LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(1);
		dialogLayout.addView(requestNameTV);
		dialogLayout.addView(inputName);
		dialogLayout.addView(requestActionTV);
		dialogLayout.addView(inputAction);
		dialogLayout.addView(requestLatLonTV);
		dialogLayout.addView(inputLatLon);
		
		final ScrollView dialogScrollView = new ScrollView(this);
		dialogScrollView.addView(dialogLayout);
		
		POIAlert.setView(dialogScrollView);
		
		POIAlert.setPositiveButton(R.string.EditPOIConfirmButton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newPOIName = inputName.getText().toString();
				// String newPOIAction = inputAction.getText().toString();
				String newPOILatLon = inputLatLon.getText().toString();
				boolean okToAdd = true;
				if(newPOIName == null || newPOIName.compareTo("") == 0 || newPOIName.compareTo(" ") == 0){
					okToAdd = false;
					Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorEmptyName, Toast.LENGTH_LONG);
			        toast.show();
			        isEditDialogOpen = false;
			        inputName.setText(null);
					inputAction.setText(null);
					inputLatLon.setText(null);
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					editMode = false;
			        return;
				}
				for(OverlayItem thisPOI : actualPOIs){
					if(thisPOI.getTitle().compareTo(newPOIName) == 0 && thisPOI.getTitle().compareTo(editPoiNameTmpString) != 0) {
						okToAdd = false;
						break;
					}
				}
				if(!okToAdd){
					Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorAlreadyUsedName, Toast.LENGTH_LONG);
			        toast.show();
			        isEditDialogOpen = false;
			        inputName.setText(null);
					inputAction.setText(null);
					inputLatLon.setText(null);
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					editMode = false;
			        return;
				}
				int Lat = 0;
				int Lon = 0;
				if(newPOILatLon.contains(",")){
					String[] LatLonArray = newPOILatLon.split(",");
					try{
						Lat = Integer.parseInt(LatLonArray[0]);
						Lon = Integer.parseInt(LatLonArray[1]);
					}
					catch (Exception e) {
						okToAdd = false;
					}
				}
				else okToAdd = false;
				
				if(!okToAdd){
					Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorLatLonFormatException, Toast.LENGTH_LONG);
			        toast.show();
			        isEditDialogOpen = false;
			        inputName.setText(null);
					inputAction.setText(null);
					inputLatLon.setText(null);
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					editMode = false;
			        return;
				}
				
				
				PoiToReplaceNameTmpString = inputName.getText().toString();
				PoiToReplaceActTmpString = inputAction.getText().toString();
				PoiToReplaceLatLonTmpString = inputLatLon.getText().toString();
				PoiToReplaceTableTmpString = actualTable;
				inputName.setText(null);
				inputAction.setText(null);
				inputLatLon.setText(null);
				isEditDialogOpen = false;
				
				editMode = true;
				
					
				Intent intent = new Intent();
		        intent.setAction(RMV_POI);
		        intent.setData(Uri.parse(TABLE_URI + actualTable));	//TODO
		        intent.putExtra(ITEM_POI, editPoiNameTmpString);
		        sendBroadcast(intent);
			  }
			});

			POIAlert.setNegativeButton(R.string.EditPOICancelButton, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					isEditDialogOpen = false;
					inputName.setText(null);
					inputAction.setText(null);
					inputLatLon.setText(null);
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					editMode = false;
				}
			});

			AlertDialog thisDialog = POIAlert.show();
			isEditDialogOpen = true;
			
			thisDialog.setOnCancelListener(new OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					isEditDialogOpen = false;
					inputName.setText(null);
					inputAction.setText(null);
					inputLatLon.setText(null);
					editPoiNameTmpString = null;
					editPoiActTmpString = null;
					editPoiLatLonTmpString = null;
					editMode = false;
				}
			});
	}
	
	@SuppressWarnings("unchecked")
	private void SaveListState()
	{
		if(tableStateChanged){
			expandedPoiGroups = "";
			boolean first = true;
			for(int i = 0; i < POIListView.getChildCount(); i++){
				if(POIListView.isGroupExpanded(i)){
					if(!first) expandedPoiGroups = expandedPoiGroups.concat(";");
					expandedPoiGroups = expandedPoiGroups.concat(((HashMap<String,String>)(POIListView.getExpandableListAdapter().getGroup(i))).get(POINAME_KEY));
					first = false;
				}
			}
			tableStateChanged = false;
		}
	}
    
    
}