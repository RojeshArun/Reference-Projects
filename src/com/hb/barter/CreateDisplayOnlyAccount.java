package com.hb.barter;

import com.hb.barter.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class CreateDisplayOnlyAccount extends Fragment implements
		OnClickListener {

	private View mRootView;

	private EditText mEdtText_DisplayCreateAccount;

	public CreateDisplayOnlyAccount() {
		// TODO Auto-generated constructor stub
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

		setDisplayAccountFragment();
		setOnClickListener();

		return mRootView;

	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub

	}

	private void setDisplayAccountFragment() {

		mEdtText_DisplayCreateAccount = (EditText) mRootView
				.findViewById(R.id.edtText_DisplayName);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
