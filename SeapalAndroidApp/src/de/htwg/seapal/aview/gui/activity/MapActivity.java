package de.htwg.seapal.aview.gui.activity;



import java.util.LinkedList;
import java.util.List;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.inject.Inject;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.htwg.seapal.R;
import de.htwg.seapal.aview.gui.fragment.MapDialogFragment;
import de.htwg.seapal.controller.IMarkController;
import de.htwg.seapal.controller.IWaypointController;


public class MapActivity extends BaseDrawerActivity
implements OnMapLongClickListener, OnMapClickListener, OnMarkerClickListener, 
MapDialogFragment.MapDialogListener {

	private enum SelectedOption {
		NONE, MARK, ROUTE, DISTANCE, GOAL
	}

	
	static {
		CBLURLStreamHandlerFactory.registerSelfIgnoreError();
		//needed for TouchDB
	}

	private GoogleMap map;
	public static Marker crosshairMarker = null;
	private Polyline route = null;
	private SelectedOption option = SelectedOption.NONE;
	private LatLng lastPos;
	private final List<Marker> calcDistanceMarker = new LinkedList<Marker>();
	private Polyline calcDistanceRoute = null;
	private double calcDistance;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment  = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
		map = myMapFragment.getMap();

        if (map != null) {


            map.setMyLocationEnabled(true);

            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            //myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            map.setOnMapClickListener(this);
            map.setOnMapLongClickListener(this);
            map.setOnMarkerClickListener(this);

            //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            //Criteria criteria = new Criteria();

            // Getting the name of the best provider
            //List<String> provider = locationManager.getAllProviders();

            // Getting Current Location
            //for(int i = 0; i < provider.size(); i++){

             //   Location location = locationManager.getLastKnownLocation(provider.get(i));

             //   if(location!=null)
            //    {
                    // Getting latitude of the current location
            //        double latitude = location.getLatitude();

                    // Getting longitude of the current location
             //       double longitude = location.getLongitude();

            //        Location loc = map.getMyLocation();
            //        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 14.0f));
            //        break;
            //    }
            //}



        }
	}

	@Override
	public void onMapClick(LatLng latlng) {
		switch (option) {
		case MARK:
			break;
		case ROUTE:
			map.addMarker(new MarkerOptions()
					.position(latlng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ann_route)));
			List<LatLng> routelst = route.getPoints();
			routelst.add(latlng);
			route.setPoints(routelst);
			break;
		case DISTANCE:
			calcDistance += calcDistance(lastPos, latlng);
			lastPos = latlng;
			calcDistanceMarker.add(map.addMarker(new MarkerOptions().
					position(latlng).
					icon(BitmapDescriptorFactory.fromResource(R.drawable.ann_distance))));
			List<LatLng> calclst = calcDistanceRoute.getPoints();
			calclst.add(latlng);
			calcDistanceRoute.setPoints(calclst);
			Toast.makeText(getApplicationContext(), Math.round(calcDistance) + "KM", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}


	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		option = SelectedOption.NONE;

		if(crosshairMarker != null) {
			crosshairMarker.remove();
		}
		route = null;
		crosshairMarker = map.addMarker(new MarkerOptions()
		.position(arg0)
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.haircross))
		.snippet(arg0.toString()));

		crosshairMarker.showInfoWindow();


	}

	@Override
	public boolean onMarkerClick(Marker arg0) {		
		MapDialogFragment f = new MapDialogFragment();
		f.show(getFragmentManager(), "dialog");
		return false;
	}

	@Override
	public void onDialogSetMarkClick(DialogFragment dialog) {
		option = SelectedOption.MARK;
		map.addMarker(new MarkerOptions().position(crosshairMarker.getPosition()));
		crosshairMarker.remove();
	}

	@Override
	public void onDialogSetRouteClick(DialogFragment dialog) {
		option = SelectedOption.ROUTE;
		if(route != null) {
			route = null;

		}
		map.addMarker(new MarkerOptions().
				position(crosshairMarker.getPosition()).
				icon(BitmapDescriptorFactory.fromResource(R.drawable.ann_route)));

		route = map.addPolyline(new PolylineOptions().
				add(crosshairMarker.getPosition()).
				width(5).color(Color.RED));


		crosshairMarker.remove();
	}

	@Override
	public void onDialogcalcDistanceClick(DialogFragment dialog) {
		option = SelectedOption.DISTANCE;
		if(calcDistanceRoute != null) {
			calcDistanceRoute.remove();
			for(Marker m : calcDistanceMarker) {
				m.remove();
			}
			calcDistanceMarker.clear();
		}
		lastPos = crosshairMarker.getPosition();
		calcDistance = 0.0;
		calcDistanceMarker.add(map.addMarker(new MarkerOptions().
				position(crosshairMarker.getPosition()).
				icon(BitmapDescriptorFactory.fromResource(R.drawable.ann_distance))));
        String ORANGE = "#FFBB03";
        calcDistanceRoute = map.addPolyline(new PolylineOptions().add(lastPos).width(5).color(Color.parseColor(ORANGE)));
		crosshairMarker.remove();
	}

	@Override
	public void onDialogSetTargetClick(DialogFragment dialog) {
		option = SelectedOption.GOAL;
	}

	@Override
	public void onDialogDeleteClick(DialogFragment dialog) {
		option = SelectedOption.NONE;
		crosshairMarker.remove();
	}

	Double calcDistance(LatLng pos1, LatLng pos2) {

		int R = 6371; // km earth radius
		double dLat = Math.toRadians(pos2.latitude - pos1.latitude);
		double dLon = Math.toRadians(pos2.longitude - pos1.longitude);
		double lat1 = Math.toRadians(pos1.latitude);
		double lat2 = Math.toRadians(pos2.latitude);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		return R * c;
	}
}
