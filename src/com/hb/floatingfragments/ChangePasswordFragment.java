package com.hb.floatingfragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class ChangePasswordFragment extends Fragment implements
		OnClickListener, IParseListener {

	// TopBar Variables
	private ImageView mBackBtn;
	private TextView mHeaderTitle, mTxtView_TopBarLine;

	private EditText mEdtText_OldPassword, mEdtText_NewPassword,
			mEdtText_ReEnterPassword;
	private TextView mTxtView_SaveBtn;

	private View mRootView;

	private MainActivity mMainAcitivity;
	private Context mContext;

	private static final int CHANGE_MY_PASSWORD = 401;

	public static final int PORT = 5222;

	private SharedPreferences userDetails;
	String user_id;

	public ChangePasswordFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_change_password, null);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
		setChangePasswordFragment();

		return mRootView;
	}

	private void setChangePasswordFragment() {
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.change_password);
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mEdtText_OldPassword = (EditText) mRootView
				.findViewById(R.id.edttext_OldPassword);
		mEdtText_NewPassword = (EditText) mRootView
				.findViewById(R.id.edttext_NewPassword);
		mEdtText_ReEnterPassword = (EditText) mRootView
				.findViewById(R.id.edttext_ReEnterPassword);

		mTxtView_SaveBtn = (TextView) mRootView
				.findViewById(R.id.txtView_SavepasswordBtn);
		mTxtView_TopBarLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mTxtView_TopBarLine.setVisibility(View.INVISIBLE);
		mTxtView_SaveBtn.setOnClickListener(this);

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");
		}

	}

	private void validatePassword() {
		String oldPassword = mEdtText_OldPassword.getText().toString().trim();
		String newPassword = mEdtText_NewPassword.getText().toString().trim();
		String reEnterPassword = mEdtText_ReEnterPassword.getText().toString()
				.trim();
		if (TextUtils.isEmpty(oldPassword)
				|| oldPassword.equalsIgnoreCase(getResources().getString(
						R.string.old_password))) {
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_enter_oldpassword),
					(FragmentActivity) getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(R.string.please_enter_oldpassword),
//					(FragmentActivity) getActivity());
			mEdtText_OldPassword.setFocusable(true);

		} else if (oldPassword.length() < 6) {
//			Validations
//					.showAlerDialog(
//							getString(R.string.password_should_contain_at_least_6_characters_),
//							getActivity());
			Validations.showSingleBtnDialog(
					getString(R.string.password_should_contain_at_least_6_characters_),
					getActivity());
			mEdtText_OldPassword.setFocusable(true);
			// Validations.showKeyboard(getActivity());
		}

		else if (TextUtils.isEmpty(newPassword)
				|| newPassword.equalsIgnoreCase(getResources().getString(
						R.string.new_password))) {

//			Validations.showAlerDialog(
//					getActivity().getString(R.string.please_enter_Newpassword),
//					(FragmentActivity) getActivity());
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_enter_Newpassword),
					(FragmentActivity) getActivity());
			mEdtText_NewPassword.setFocusable(true);
			// Validations.showKeyboard(getActivity());

		} else if (newPassword.length() < 6) {
//			Validations
//					.showAlerDialog(
//							getString(R.string.password_should_contain_at_least_6_characters_),
//							getActivity());
			Validations.showSingleBtnDialog(
					getString(R.string.password_should_contain_at_least_6_characters_),
					getActivity());
			mEdtText_NewPassword.setFocusable(true);
			// Validations.showKeyboard(getActivity());
		}

		else if (TextUtils.isEmpty(reEnterPassword)
				|| reEnterPassword.equalsIgnoreCase(getResources().getString(
						R.string.re_enter_password))) {
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.please_enter_ReEnterpassword),
					(FragmentActivity) getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(
//							R.string.please_enter_ReEnterpassword),
//					(FragmentActivity) getActivity());

			mEdtText_ReEnterPassword.setFocusable(true);
			// Validations.showKeyboard(getActivity());

		} else if (reEnterPassword.length() < 6) {
			Validations.showSingleBtnDialog(
					getString(R.string.password_should_contain_at_least_6_characters_),
					getActivity());
//			Validations
//					.showAlerDialog(
//							getString(R.string.password_should_contain_at_least_6_characters_),
//							getActivity());
			mEdtText_ReEnterPassword.setFocusable(true);
			// Validations.showKeyboard(getActivity());
		}

		else if (newPassword.equals(reEnterPassword)) {
			if (Validations.isNetworkAvailable(getActivity())) {
				callChangePasswordWS(oldPassword, newPassword);
				Validations.hideKeyboard(getActivity());
			} else
//				Validations
//						.showAlerDialog(
//								getActivity()
//										.getString(
//												R.string.no_internet_connection_please_try_again),
//								getActivity());
			Validations.showSingleBtnDialog(
					getActivity()
					.getString(
							R.string.no_internet_connection_please_try_again),
			getActivity());

		}
	}

	private void callChangePasswordWS(String oldPassword, String newPassword) {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		mBundle.putString("old_password", oldPassword);
		mBundle.putString("new_password", newPassword);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.CHANGE_MY_PASSWORD, mBundle),
				CHANGE_MY_PASSWORD, this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();
			break;

		case R.id.txtView_SavepasswordBtn:
			validatePassword();
			break;
		default:
			break;
		}
	}

	private void slideBack() {
		Validations.hideKeyboard(getActivity());
		getFragmentManager().popBackStack();

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		mMainAcitivity.hideProgress();

	}

	private  void showSingleBtnDialog( String message,Context context) {
		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title =  "Bartr";
		
		final Dialog alertDialog = new Dialog(context,R.style.AlertDialogCustom);
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

		if(title!=null){
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText("OK");
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				slideBack();
			}
		});

		alertDialog.show();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		String mChangePassword = "";
		switch (requestCode) {
		case CHANGE_MY_PASSWORD:

			try {
				JSONObject mSettings = new JSONObject(
						response.getString("settings"));
				mChangePassword = mSettings.getString("message");
				if (mSettings.getString("success").equalsIgnoreCase("1")) {

					// Toast.makeText(getActivity(),
					// "" + mSettings.getString("message"),
					// Toast.LENGTH_SHORT).show();
					showSingleBtnDialog(mChangePassword, getActivity());
				}

				else {
//					Validations.showAlerDialog(mChangePassword, getActivity());
					Validations.showSingleBtnDialog(mChangePassword, getActivity());
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		mMainAcitivity.hideProgress();
	}
}
