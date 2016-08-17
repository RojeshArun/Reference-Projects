package com.hb.floatingfragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import khandroid.ext.apache.http.HttpEntity;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.barter.BackgroundServices.GPSServiceUpdater;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class AboutAppFragment extends Fragment implements OnClickListener,
		OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener,
		OnLocationChangedListener, OnMarkerClickListener, IParseListener {

	// private WebView mWebView;
	private View mRootView;
	private String desctext;

	private ImageView mBackBtn;
	private TextView mHeaderTitle;
	private TextView btnSetLocation;
	private ImageView mRightMostImg;

	private MarkerOptions markerOption;
	private ArrayList<LatLng> markerPoint;
	private Marker marker;
	private LocationManager locationManager;
	private SharedPreferences userDetails;
	private GoogleMap mMap;
	private String addressString;
	private Editor editor;
	private LatLng savedLatlng = null;
	private boolean isCurrentGPSLocation = true;
	private double sharedLatitude = -1, sharedLongitude = -1;
	private MainActivity mActivity;

	private static final int USER_LOCATION = 001;

	public AboutAppFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		getPreSavedLatlng();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_about, null);
		mActivity = (MainActivity) getActivity();
		
		setMapFragment();
		gotoMapsScreen();
		return mRootView;
	}

	private void setMapFragment() {
		btnSetLocation = (TextView) mRootView.findViewById(R.id.btnSetLocation);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText("Set Location");
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mRightMostImg = (ImageView) mRootView
				.findViewById(R.id.rightMostButton);
		mRightMostImg.setVisibility(View.VISIBLE);
		mRightMostImg.setImageDrawable(getResources()
				.getDrawable(R.drawable.ok));
		mRightMostImg.setOnClickListener(this);
		btnSetLocation.setOnClickListener(this);
	}

	private void getPreSavedLatlng() {
		if (userDetails != null) {
			if (userDetails.contains("UserLatitudes")
					&& userDetails.contains("UserLongitudes")) {
				if (userDetails.getFloat("UserLatitudes", -1) != -1
						&& userDetails.getFloat("UserLongitudes", -1) != -1) {
					savedLatlng = new LatLng(userDetails.getFloat(
							"UserLatitudes", -1), userDetails.getFloat(
							"UserLongitudes", -1));
				}
			}
		}
	}

	private void gotoMapsScreen() {
		markerPoint = new ArrayList<LatLng>();
		mMap = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		mMap.setTrafficEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);

		List<String> providerslist = locationManager.getAllProviders();
		Location location = null;

		location = locationManager.getLastKnownLocation(provider);

		if (location == null) {
			if (providerslist != null && providerslist.size() > 0) {
				for (int i = 0; i < providerslist.size(); i++) {
					location = locationManager
							.getLastKnownLocation(providerslist.get(i));
					if (location != null) {
						break;
					}
				}
			}
		}

		// Based on service
		if (userDetails.getBoolean("isFromBackgroudSrevice", false)) {
			btnSetLocation.setVisibility(View.INVISIBLE);
			savedLatlng = null;
		} else {

			btnSetLocation.setVisibility(View.VISIBLE);
		}

		if (savedLatlng != null) {
			markerPoint.clear();

			mMap.setMyLocationEnabled(false);
			isCurrentGPSLocation = false;
			markerPoint.add(savedLatlng);
			if (markerPoint.size() == 1) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						sharedLatitude = savedLatlng.latitude;
						sharedLongitude = savedLatlng.longitude;
						setTheAddress(savedLatlng.latitude,
								savedLatlng.longitude);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setAfterAddresResult(true, savedLatlng,
										!isCurrentGPSLocation);
							}
						});
					}
				}).start();

			}
		} else {
			if (location != null) {
				final LatLng locationltlg = new LatLng(location.getLatitude(),
						location.getLongitude());
				markerPoint.clear();
				isCurrentGPSLocation = true;
				mMap.setMyLocationEnabled(true);
				btnSetLocation.setVisibility(View.GONE);
				markerPoint.add(locationltlg);
				if (markerPoint.size() == 1) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							setTheAddress(locationltlg.latitude,
									locationltlg.longitude);
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									setAfterAddresResult(true, locationltlg,
											!isCurrentGPSLocation);
								}
							});
						}
					}).start();

				}
			} else {

			}
		}

		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerDragListener(this);
		mMap.setOnMapLongClickListener(this);
		mMap.setOnMarkerClickListener(this);
	}

	private void setAfterAddresResult(boolean isCameraAnimate, LatLng ltlg,
			boolean isIconAvailable) {
		if (isCameraAnimate) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg, 8));
		} else {
			mMap.clear();
		}
		markerOption = new MarkerOptions();
		markerOption.draggable(true);
		markerOption.position(ltlg);
		markerOption.title("Current Location");
		markerOption.snippet(addressString);
		if (isIconAvailable) {
			mMap.setMyLocationEnabled(false);
			markerOption.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.map_pin_2));
			marker = mMap.addMarker(markerOption);
			marker.showInfoWindow();
		} else {
			mMap.setMyLocationEnabled(true);
		}

	}

	private double getRound(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		marker.hideInfoWindow();
	}

	@Override
	public void onMarkerDragEnd(Marker mMarker) {
		final double latitude = mMarker.getPosition().latitude;
		final double longitude = mMarker.getPosition().longitude;

		sharedLatitude = latitude;
		sharedLongitude = longitude;
		new Thread(new Runnable() {
			@Override
			public void run() {
				setTheAddress(latitude, longitude);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mMap.setMyLocationEnabled(false);
						setAfterAddresResult(false, new LatLng(latitude,
								longitude), true);
					}
				});
			}
		}).start();

		// isCurrentGPSLocation=false;
		// btnSetLocation.setVisibility(View.VISIBLE);
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		marker.hideInfoWindow();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}

	@Override
	public void onMapLongClick(LatLng mMarker) {
		final double latitude = mMarker.latitude;
		final double longitude = mMarker.longitude;

		sharedLatitude = latitude;
		sharedLongitude = longitude;
		markerPoint.clear();
		markerPoint.add(mMarker);
		if (markerPoint.size() == 1) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					setTheAddress(latitude, longitude);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mMap.setMyLocationEnabled(false);
							setAfterAddresResult(false, new LatLng(latitude,
									longitude), true);
						}
					});
				}
			}).start();
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {

	}

	public void setTheAddress(double RegLat, double RegLan) {
		double lat, lng;
		final String newLatitude = toString().valueOf(RegLat);
		final String newLongitude = toString().valueOf(RegLan);
		try {
			try {
				JSONObject jsonObj = parser_Json
						.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
								+ newLatitude
								+ ","
								+ newLongitude
								+ "&sensor=true");
				Log.e("Google APi", jsonObj.toString());
				String Status = jsonObj.getString("status");
				if (Status.equalsIgnoreCase("OK")) {
					JSONArray Results = jsonObj.getJSONArray("results");
					JSONObject zero = Results.getJSONObject(0);
					addressString = (String) zero.get("formatted_address");
					try {
						addressString = new String(
								addressString.getBytes("ISO-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						addressString = new String(
								addressString.getBytes("ISO-8859-1"), "UTF-8");
					}
				}else{
					addressString="";
					getAddressByGecoding(RegLat, RegLan);
				}

			} catch (Exception e) {
				e.printStackTrace();
				e.getMessage();
			}
		} catch (Exception e) {
			getAddressByGecoding(RegLat, RegLan);
		}
	}

	private void getAddressByGecoding(double RegLat, double RegLan) {
		try {
			Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(RegLat, RegLan,
					1);
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				String addressLine1 = address.getAddressLine(0);
				String addressLine2 = address.getAddressLine(1);
				String addressLine3 = address.getAddressLine(2);
				sb.append(addressLine1).append(",");
				sb.append(addressLine2).append(",");
				sb.append(addressLine3).append("\n");
				// sb.append(address.getCountryName());

				// RegLat = String.valueOf(address.getLatitude());
				// RegLan = String.valueOf(address.getLongitude());
			}
			addressString = sb.toString();
			try {
				addressString = new String(
						addressString.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e1) {

		}
	}

	public static class parser_Json {
		public static JSONObject getJSONfromURL(String url) {
			// initialize
			InputStream is = null;
			String result = "";
			JSONObject jObject = null;

			// http post
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

			// convert response to string
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}

			// try parse the string to a JSON object
			try {
				jObject = new JSONObject(result);
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data " + e.toString());
			}

			return jObject;
		}

	}

	@Override
	public void onLocationChanged(Location location) {
	}

	private void showTwoBtnDialog(final Activity activity,
			String positiveBtnTxt, String negativeBtnTxt, String message) {

		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(activity,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_exit_alert);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText(positiveBtnTxt);
		btnCancel = (TextView) alertDialog.findViewById(R.id.btnCancel);
		btnCancel.setText(negativeBtnTxt);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				showSingleBtnDialog("Location updated successfully",
						getActivity(), false);
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				if (userDetails != null) {
					if (userDetails.contains("UserLatitudes")
							&& userDetails.contains("UserLongitudes")) {
						editor = userDetails.edit();
						editor.putFloat("UserLatitudes", -1);
						editor.putFloat("UserLongitudes", -1);
						editor.putBoolean("isCustomLocation", false);
						editor.commit();

						startGPSservice();
					}
				}
				slideBack();
			}
		});

		btnCancel.setVisibility(View.VISIBLE);
		alertDialog.show();
	}

	private void showSingleBtnDialog(String message, Context context,
			boolean setLocation) {
		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(context,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_alert_info);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText("OK");
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				stopGPSService();
				Editor editor = userDetails.edit();
				editor.putFloat("UserLatitudes", (float) sharedLatitude);
				editor.putFloat("UserLongitudes", (float) sharedLongitude);
				editor.putString("UserLatitude", sharedLatitude + "");
				editor.putString("UserLongitude", sharedLongitude + "");
				editor.putBoolean("isCustomLocation", true);
				editor.commit();
				

				updateCustomLocation();

			}
		});

		alertDialog.show();
	}

	protected void updateCustomLocation() {
		// TODO Auto-generated method stub

		mActivity.showProgress();
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();

		Bundle mBundle = new Bundle();
		mBundle.putString("user_id",
				userDetails.getString("Loggedin_userid", ""));
		mBundle.putString("user_latitude", sharedLatitude + "");
		mBundle.putString("user_longitude", sharedLongitude + "");
		
		Log.e("Custom Lat & Long", sharedLatitude +"  " +sharedLongitude+"");

		mJsonRequestResponse
				.getResponse(Webservices.encodeUrl(
						Webservices.UPDATE_USERLOCATION, mBundle),
						USER_LOCATION, this);

	}

	// Start Background service
	protected void startGPSservice() {
		// TODO Auto-generated method stub

		Intent gpsService = new Intent(getActivity(), GPSServiceUpdater.class);
		getActivity().startService(gpsService);

	}

	// Stop Background Service
	protected void stopGPSService() {
		// mActivity.stopService(getView());

		Intent gpsService = new Intent(getActivity(), GPSServiceUpdater.class);
		getActivity().stopService(gpsService);

		// sendBroadcast(intent);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MapFragment f = (MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map);

		if (f != null) {
			getActivity().getFragmentManager().beginTransaction().remove(f)
					.commit();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();
			break;
		case R.id.btnSetLocation:
			showTwoBtnDialog(getActivity(), "YES", "NO", getResources()
					.getString(R.string.txtMapAlertMessage));
			break;
		case R.id.rightMostButton:
			showSingleBtnDialog("Location updated successfully", getActivity(),
					true);
			break;
		default:
			break;
		}
	}

	private void slideBack() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		mActivity.hideProgress();
		
		Log.e("Custom error response ", error.toString());

		slideBack();
		
	
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		// TODO Auto-generated method stub
		mActivity.hideProgress();
		Log.e("Custom success response ", response.toString());
		slideBack();
	}

}
