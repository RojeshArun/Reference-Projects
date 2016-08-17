package com.hb.floatingfragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.adapters.BlockedUsersListAdapter;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.models.NearByUsersData;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class BlockedUsesListFragment extends Fragment implements
		OnClickListener, IParseListener {
	private ImageView mBackBtn;
	private TextView mHeaderTitle;
	private TextView mBottomLine;

	TextView mTextView;

	private static final int CODE_MAP = 102;
	private static final int BLOCKED_USER = 103;
	// private NearByUsersListAdapter mListAdapter;
	// private List<NearByUsersData> mListArray = new
	// ArrayList<NearByUsersData>();
	private BlockedUsersListAdapter mListAdapter;
	private List<NearByUsersData> mListArray = new ArrayList<NearByUsersData>();
	ListView mBlockUserList;
	private MainActivity mMainAcitivity;
	private Context mContext;
	private ImageView mImgView_User;
	private EditText mEdtText_FullName, mEdtText_FirstName, mEdtText_LastName,
			mEdtText_EmailId, mEdtText_StreetName, mEdtText_CityName,
			mEdtText_ZipCode, mEdtText_ISDCode, mEdtText_PhoneNumber;

	private View mRootView;
	SharedPreferences mUserDetails;
	private SharedPreferences userDetails;
	String user_id;
	String current_longitude;
	String current_latitude;

	// Profile Variables
	public BlockedUsesListFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_blockusers, null);

		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mBottomLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mBottomLine.setVisibility(View.GONE);
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.blocked_users);
		mBlockUserList = (ListView) mRootView
				.findViewById(R.id.blockeduserlist);

		mTextView = (TextView) mRootView.findViewById(R.id.txtView_no_result);

		// if (mUserDetails.contains("display_user_details")) {
		// String mDisplayName = mUserDetails.getString(
		// "display_user_details", "def");
		// // ////////////////////////////////////////////////////////////////
		//
		// user_id = mUserDetails.getString("new_user_id", "def");
		// display_user_name = mUserDetails.getString("display_user_details",
		// "def");
		// display_email = mUserDetails.getString("email_id", "def");
		//
		// }

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "");
			current_latitude = userDetails.getString("UserLatitude", "");
			current_longitude = userDetails.getString("UserLongitude", "def");

		}
		// setProfileFragment();
		setOnClickListeners();
		checkNetworkAndCallWS();

		return mRootView;
	}

	private void checkNetworkAndCallWS() {
		if (Validations.isNetworkAvailable(getActivity()))
			callBlockedUsersWS();
		else
			// Validations.showAlerDialog(
			// getActivity().getString(
			// R.string.no_internet_connection_please_try_again),
			// getActivity());
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.no_internet_connection_please_try_again),
					getActivity());
	}

	private void setProfileFragment() {

		// <<<<<<< .mine
		// // mImgView_User = (ImageView) mRootView
		// // .findViewById(R.id.imgView_Photo_myProfile);
		// // mEdtText_FullName = (EditText) mRootView
		// // .findViewById(R.id.edtText_FullName_myProfile);
		// // mEdtText_FirstName = (EditText) mRootView
		// // .findViewById(R.id.edtText_UserName_myProfile);
		// // mEdtText_LastName = (EditText) mRootView
		// // .findViewById(R.id.edtText_Surname_myProfile);
		// // mEdtText_EmailId = (EditText) mRootView
		// // .findViewById(R.id.edttext_EmailId_myProfile);
		// // mEdtText_CityName = (EditText) mRootView
		// // .findViewById(R.id.edtText_City_myProfile);
		// // mEdtText_StreetName = (EditText) mRootView
		// // .findViewById(R.id.edtText_Street_myProfile);
		// // mEdtText_ISDCode = (EditText) mRootView
		// // .findViewById(R.id.edtText_ISDCode_myProfile);
		// // mEdtText_PhoneNumber = (EditText) mRootView
		// // .findViewById(R.id.edtText_PhoneNum_myProfile);
		// =======
		// mImgView_User = (ImageView) mRootView
		// .findViewById(R.id.imgView_Photo_myProfile);
		// mEdtText_FullName = (EditText) mRootView
		// .findViewById(R.id.edtText_FullName_myProfile);
		// mEdtText_FirstName = (EditText) mRootView
		// .findViewById(R.id.edtText_UserName_myProfile);
		// mEdtText_LastName = (EditText) mRootView
		// .findViewById(R.id.edtText_Surname_myProfile);
		// mEdtText_EmailId = (EditText) mRootView
		// .findViewById(R.id.txtView_EmailId_myProfile);
		// mEdtText_CityName = (EditText) mRootView
		// .findViewById(R.id.edtText_City_myProfile);
		// mEdtText_StreetName = (EditText) mRootView
		// .findViewById(R.id.edtText_Street_myProfile);
		// mEdtText_ISDCode = (EditText) mRootView
		// .findViewById(R.id.edtText_ISDCode_myProfile);
		// mEdtText_PhoneNumber = (EditText) mRootView
		// .findViewById(R.id.edtText_PhoneNum_myProfile);
		// >>>>>>> .r161

	}

	private void setOnClickListeners() {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();

			break;

		default:
			break;
		}

	}

	public void slideBack() {

		getFragmentManager().popBackStack();

	}

	private void callBlockedUsersWS() {

		showProgress();
		Bundle mBundle = new Bundle();

		mBundle.putString("user_id", user_id);
		mBundle.putString("latitude", current_latitude);
		mBundle.putString("longitude", current_longitude);
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.BLOCKED_USER, mBundle),
				BLOCKED_USER, this);

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		hideProgress();

	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		switch (requestCode) {
		case CODE_MAP:
			// parseMapsData(response);
			break;
		case BLOCKED_USER:
			parseBlockList(response);
		default:
			break;
		}
		hideProgress();
	}

	@SuppressLint("ResourceAsColor")
	private void parseBlockList(JSONObject response) {
		try {
			JSONObject mJsonObject = new JSONObject(
					response.getString("settings"));

			if (mJsonObject.getString("success").equalsIgnoreCase("1")) {

				JSONArray mArray = new JSONArray(response.getString("data"));
				if (mArray.length() == 0) {
					mBlockUserList.setVisibility(View.GONE);
					mTextView.setSingleLine(true);
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setTextSize(16);
					mTextView.setTextColor(getResources().getColor(
							R.color.text_color_fadedgreen));
					mTextView.setText("No blocked users found.");
				} else {

					mListArray.clear();
					// mListAdapter.clearData();
					for (int i = 0; i < mArray.length(); i++) {
						JSONObject mJsonData = mArray.getJSONObject(i);
						NearByUsersData mData = new NearByUsersData(mJsonData);
						mListArray.add(mData);
					}

					mListAdapter = new BlockedUsersListAdapter(mContext,
							mListArray, BlockedUsesListFragment.this);
					mBlockUserList.setAdapter(mListAdapter);
					mListAdapter.notifyDataSetChanged();

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void showNoTextResult() {
		mTextView.setVisibility(View.VISIBLE);
	}

	public void hideNoTextResult() {
		mTextView.setVisibility(View.GONE);
	}

	public void showProgress() {
		((LinearLayout) mRootView.findViewById(R.id.loading_view))
				.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		((LinearLayout) mRootView.findViewById(R.id.loading_view))
				.setVisibility(View.GONE);
	}

}
