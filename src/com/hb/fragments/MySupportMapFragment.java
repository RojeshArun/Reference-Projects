package com.hb.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class MySupportMapFragment extends SupportMapFragment {

	public MySupportMapFragment() {

	}

	public static MySupportMapFragment newInstatnce(Bundle b,
			MapMessage mMapMessage) {
		MySupportMapFragment m = new MySupportMapFragment(mMapMessage);
		m.setArguments(b);
		return m;
	}

	private GoogleMap mapView;
	private MapMessage mapMessage;

	public interface MapMessage {

		void setMap(GoogleMap googleMap);

		GoogleMap getMap();

		void onLocatinChange(Location mLocation);
	}

	public MySupportMapFragment(MapMessage mapMessage) {
		this.mapMessage = mapMessage;

	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

		try {
			Bundle b = getArguments();
			String[] latlon;// = new String[2];
			double[] latlongd = new double[2];

			if (b != null) {
				latlon = new String[2];

				latlon[0] = b.getString("lat");
				latlon[1] = b.getString("lon");
				try {
					latlongd[0] = Double.valueOf(latlon[0]);
					latlongd[1] = Double.valueOf(latlon[1]);
				} catch (Exception e) {
					latlongd[0] = 0.0f;
					latlongd[1] = 0.0f;
				}
			}
			mapMessage.setMap(getMap());
			mapView = mapMessage.getMap();
			// UiSettings s = mapView.getUiSettings();
			mapView.setMyLocationEnabled(true);

			if (b != null && !b.getBoolean("isGeolocation")) {
				// MarkerOptions markerOptions = new MarkerOptions();
				// LatLng latLng = new LatLng(latlongd[0], latlongd[1]);
				// //
				// markerOptions.position(latLng);
				// markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
				//
				// mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
				// 6));
				// mapView.addMarker(markerOptions);
				//

				// CameraPosition position = this.mapView.getCameraPosition();
				//
				// CameraPosition.Builder builder = new
				// CameraPosition.Builder();
				// builder.zoom(15);
				// builder.target(latLng);
				// mapView.animateCamera(CameraUpdateFactory
				// .newCameraPosition(builder.build()));
			} else {
				mapView.setLocationSource(new CurrentLocationProvider(
						getActivity()));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class CurrentLocationProvider implements LocationSource,
			LocationListener {
		private OnLocationChangedListener listener;
		private LocationManager locationManager;

		public CurrentLocationProvider(Context context) {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		@Override
		public void activate(OnLocationChangedListener listener) {
			this.listener = listener;
			LocationProvider gpsProvider = locationManager
					.getProvider(LocationManager.GPS_PROVIDER);
			if (gpsProvider != null) {
				locationManager.requestLocationUpdates(gpsProvider.getName(),
						0, 10, this);
			}

			LocationProvider networkProvider = locationManager
					.getProvider(LocationManager.NETWORK_PROVIDER);

			if (networkProvider != null) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 1000 * 60 * 5, 0,
						this);
			}
		}

		@Override
		public void deactivate() {
			locationManager.removeUpdates(this);
		}

		@Override
		public void onLocationChanged(Location location) {
			if (listener != null) {
				listener.onLocationChanged(location);
			}
			if (mapMessage != null)
				mapMessage.onLocatinChange(location);
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	
}
