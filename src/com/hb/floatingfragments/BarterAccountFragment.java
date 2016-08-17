package com.hb.floatingfragments;

import com.hb.barter.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BarterAccountFragment extends Fragment implements OnClickListener {

	private View mRootView;

	private TextView mTxtView_BarterName, mTxtView_BarterEmail,
			mTxtView_BarterGoBtn;
	private EditText mEdtText_BarterEnterPIN;
	private ImageView mImageUser;
	public BarterAccountFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_barter_account, null);

		setBarterAccountFragment();
		setOnClickListener();

		return mRootView;

	}

	private void setOnClickListener() {

		mTxtView_BarterGoBtn.setOnClickListener(this);

	}

	private void setBarterAccountFragment() {

		mTxtView_BarterEmail = (TextView) mRootView
				.findViewById(R.id.txtView_BarterEmailId);
		mTxtView_BarterGoBtn = (TextView) mRootView
				.findViewById(R.id.txtView_GoBtn);
		mTxtView_BarterName = (TextView) mRootView
				.findViewById(R.id.txtView_John);
		mEdtText_BarterEnterPIN = (EditText) mRootView
				.findViewById(R.id.edtText_BarterEnterPin);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtView_GoBtn:
			break;
		default:
			break;
		}

	}
}
