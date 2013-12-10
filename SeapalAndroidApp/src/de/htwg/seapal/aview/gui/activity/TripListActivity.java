package de.htwg.seapal.aview.gui.activity;

import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.inject.Inject;

import de.htwg.seapal.R;
import de.htwg.seapal.aview.gui.adapter.TripListAdapter;
import de.htwg.seapal.controller.impl.TripController;

public class TripListActivity extends BaseDrawerActivity {

	@Inject
	private TripController controller;
	private List<UUID> tripList;
    private ViewGroup mainView;
	private View header;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.triplist);

		Bundle extras = getIntent().getExtras();
        UUID boat = UUID.fromString(extras.getString("boat"));

		tripList = controller.getTrips(boat);

	}

	@Override
	public void onResume() {
		addListView();
		super.onResume();
	}
	
	@Override
	public void onStop() {
		mainView.removeView(header);
		super.onPause();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mainView.removeView(header);
		addListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addListView() {
		ListView listview = (ListView) findViewById(android.R.id.list);
		TripListAdapter adapter = new TripListAdapter(this,
                tripList, controller);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		header = inflater.inflate(R.layout.tripslistheader, null);
		mainView = (ViewGroup) findViewById(R.id.trip_list);
		mainView.addView(header, 0);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(TripListActivity.this,
						TripActivity.class);
				UUID trip = (UUID) arg0.getAdapter().getItem(arg2);
				intent.putExtra("trip", trip.toString());
				TripListActivity.this.startActivity(intent);
			}

		});
	}

}
