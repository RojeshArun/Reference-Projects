package com.hb.floatingfragments;

import com.hb.barter.DisplayCreatedAccountScreen;
import com.hb.barter.R;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateAccountFragment extends Fragment implements
		android.view.View.OnClickListener {

	private View mRootView;
	private EditText mEdtText_CAFFirstName, mEdtText_CAFLastName,
			mEdtText_CAFAddress, mEdtText_CAFZipCode, mEdtText_CAFCity,
			mEdtText_CAFISDCode, mEdtText_CAFMobileNum;
	private TextView mTxtView_CAFSkipTxtBtn, mTxtView_CAFState,
			mTxtView_CAFCountry, mTxtView_CAFDateOfBirth;
	private ImageView mImgView_CAFUserImage;
	
	private static final int STATE = 501;
	private static final int COUNTRY = 502;

	private TextView mTitle;
	private ImageView mBackButton;

	public CreateAccountFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_create_twiter_account,
				null);

		setCreateFullAccountFragment();
		setOnClickListener();

		return mRootView;
	}

	private void setOnClickListener() {
		mTxtView_CAFSkipTxtBtn.setOnClickListener(this);
		mTxtView_CAFDateOfBirth.setOnClickListener(this);
		mTxtView_CAFState.setOnClickListener(this);
		mTxtView_CAFCountry.setOnClickListener(this);
		mBackButton.setOnClickListener(this);

	}

	private void setCreateFullAccountFragment() {

		mBackButton = (ImageView) mRootView.findViewById(R.id.slidedown);
		mEdtText_CAFFirstName = (EditText) mRootView
				.findViewById(R.id.imgView_Photo);
		mEdtText_CAFLastName = (EditText) mRootView
				.findViewById(R.id.edtText_LastnameCreateFullAccount);
		mEdtText_CAFAddress = (EditText) mRootView
				.findViewById(R.id.edttext_AddressCreateFullAccount);
		mEdtText_CAFISDCode = (EditText) mRootView
				.findViewById(R.id.edtText_ISDCodeCreateFullAccount);
		mEdtText_CAFMobileNum = (EditText) mRootView
				.findViewById(R.id.edtText_PhoneNumCreateFullAccount);
		mEdtText_CAFZipCode = (EditText) mRootView
				.findViewById(R.id.edtText_ZipCodeCreateFullAccount);
		mEdtText_CAFCity = (EditText) mRootView
				.findViewById(R.id.edtText_CityCreateFullAccount);

		mTxtView_CAFCountry = (TextView) mRootView
				.findViewById(R.id.txtView_CountryCreateFullAccount);
		mTxtView_CAFDateOfBirth = (TextView) mRootView
				.findViewById(R.id.txtView_DateOfBirthCreateFullAccount);
		mTxtView_CAFState = (TextView) mRootView
				.findViewById(R.id.txtView_StateCreateFullAccount);
		mTxtView_CAFSkipTxtBtn = (TextView) mRootView
				.findViewById(R.id.txtView_SkipCreateFullAccount);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtView_SkipCreateFullAccount:
			
			break;

		case R.id.slidedown:
			Intent mIntent = new Intent(getActivity(),
					DisplayCreatedAccountScreen.class);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mIntent);
			break;
			
		default:
			break;
		}

	}
}
