package com.hb.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationUtility {
	private String currentLatitude = "";
	private String currentLongitude = "";
	private LocationManager mLocationManager = null;
	private final int TEN_SECOND = 0;
	private final int TEN_METRES = 0;
	private final int TWO_MINUTES = 1000 * 60 * 2; // Better Location

	public LocationUtility(Activity activity) {
		mLocationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

		getCurrentLocation();
	}

	private void getCurrentLocation() {
		Location gpsLocation = null;
		Location networkLocation = null;
		mLocationManager.removeUpdates(listener);
		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER);
		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER);

		if (gpsLocation != null && networkLocation != null) {
			updateLocation(getBetterLocation(gpsLocation, networkLocation));
		} else if (gpsLocation != null) {
			updateLocation(gpsLocation);
		} else if (networkLocation != null) {
			updateLocation(networkLocation);
		}
	}

	public String getCurrentLatitude() {
		return currentLatitude;
	}

	public String getCurrentLongitude() {
		return currentLongitude;
	}

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null)
				updateLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {

			if (provider != null)
				Log.d(provider + " provider", "disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (provider != null)
				Log.d(provider + " provider", "enabled");

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d("status", "" + status);
		}
	};

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private Location getBetterLocation(Location newLocation,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		} else if (newLocation == null) {
			// A new location is always better than no location
			return currentBestLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
				currentBestLocation.getProvider());

		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	private Location requestUpdatesFromProvider(String provider) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider, TEN_SECOND,
					TEN_METRES, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		}
		return location;
	}

	private void updateLocation(Location location) {
		try {
			Log.d("NEW Location",
					location.getLatitude() + " " + location.getLongitude());

			currentLatitude = String.valueOf(location.getLatitude());
			currentLongitude = String.valueOf(location.getLongitude());
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}
	}

	public void removeUpdates() {
		if (listener != null) {
			try {
				mLocationManager.removeUpdates(listener);
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		}
	}
}
