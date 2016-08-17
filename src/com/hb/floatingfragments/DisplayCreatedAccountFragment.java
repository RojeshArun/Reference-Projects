package com.hb.floatingfragments;

import com.hb.barter.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DisplayCreatedAccountFragment extends Fragment {

	private View mRootView;

	private EditText mEdtText_DiplayName, mEdtText_DisplayEmailId,
			mEdtText_DisplayPassword, mEdtText_DisplayRePassword;

	public DisplayCreatedAccountFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_display_create_account,
				null);
		
		setDisplayCreateFragment();

		return mRootView;
	}

	private void setDisplayCreateFragment() {

		mEdtText_DiplayName = (EditText) mRootView
				.findViewById(R.id.edtText_DisplayName);
		mEdtText_DisplayEmailId = (EditText) mRootView
				.findViewById(R.id.edtText_EmailId_Create_Account);
		mEdtText_DisplayPassword = (EditText) mRootView
				.findViewById(R.id.edtText_password_create_account);
		mEdtText_DisplayRePassword = (EditText) mRootView
				.findViewById(R.id.edtText_rePassword_create_account);

	}
}
