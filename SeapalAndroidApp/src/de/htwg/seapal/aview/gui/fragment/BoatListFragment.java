package de.htwg.seapal.aview.gui.fragment;

import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.htwg.seapal.R;
import de.htwg.seapal.aview.gui.adapter.BoatListAdapter;
import de.htwg.seapal.controller.impl.BoatController;

public class BoatListFragment extends ListFragment {

	public static final String TAG = "FragmentList";
	private List<UUID> boatList;
    private View header;
	private ViewGroup mainView;
	private boolean tablet = false;

	private BoatController controller;
	

	public BoatListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState == null) {
			boatList = controller.getBoats();
		}
		

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mainView.removeView(header);
		this.onActivityCreated(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (boatList == null)
			boatList = controller.getBoats();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		boatList = controller.getBoats();
		getListView().setChoiceMode(1);		
		setListAdapter(null);
        BoatListAdapter adapter = new BoatListAdapter(getActivity(),
                boatList, controller);

		// add Header
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		header = inflater.inflate(R.layout.boatlistheader, null);
		mainView = (ViewGroup) getActivity().findViewById(
				R.id.linearLayout_default);
		if (mainView == null) {
			tablet = true;
			mainView = (ViewGroup) getActivity().findViewById(
					R.id.linearLayout_xlarge);
			mainView.addView(header, 1);
		} else
			mainView.addView(header, 0);

		// so the background color is white on older Android Versions
		// getListView().setBackgroundColor(Color.WHITE);
		
		this.setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// inform Activity
		if (!tablet)
			mainView.removeView(header);

		l.setItemChecked(position, true);

		UUID boat = (UUID) l.getAdapter().getItem(position);
		ListSelectedCallback callback = (ListSelectedCallback) getActivity();
		callback.selected(boat);

	}
	
	
	

	// Callback for Container Activity
	public interface ListSelectedCallback {
		public void selected(UUID boat);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.boatmenu, menu);
		MenuItem itemDelete = menu.findItem(R.id.boatmenu_delete);
		MenuItem itemSave = menu.findItem(R.id.boatmenu_save);
		itemDelete.setVisible(false);
		itemSave.setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.boatmenu_new) {
			
			// new button clicked
			final String[] in = new String[1];

			final EditText input = new EditText(getActivity());

			new AlertDialog.Builder(getActivity())
					.setTitle("New Boat")
					.setMessage("Please enter a new Boatname")
					.setView(input)
					.setPositiveButton("Create",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									in[0] = input.getText().toString();
									newBoat(in[0]);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

                                }
							}).show();
		}
		

		return super.onOptionsItemSelected(item);
	}

	private void newBoat(String input) {
		if (input.equals("")) {
			Toast.makeText(getActivity(), "Please enter a Boatname",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		UUID newBoat = controller.newBoat();
		controller.setBoatName(newBoat, input);

	}

	public void setController(BoatController controller) {
		this.controller = controller;
	}
	
	public int getBoatListSize() {
		return boatList.size();
	}
}
