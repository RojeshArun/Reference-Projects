package com.hb.floatingfragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class SupportFragment extends Fragment implements OnClickListener,
		IParseListener {

	// TopBar Variables
	private ImageView mBackBtn, mRightBtn, mRightBut1;
	private TextView mHeaderTitle;
	private TextView mBottomLine;

	private WebView mWebView;
	private View mRootView;
	private String desctext;
	private MainActivity mMainAcitivity;
	private Context mContext;
	private static final int SUPPORT_PAGE = 999;

	public static final int PORT = 5222;

	public SupportFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_support, null);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
		setWebView();
		if (Validations.isNetworkAvailable(getActivity())) {
			callSupportWS();
		} else {
//			Validations.showAlerDialog(
//					getActivity().getString(
//							R.string.no_internet_connection_please_try_again),
//					getActivity());
			Validations.showSingleBtnDialog(
					"No internet Connection.Please Try again later",
					getActivity());
		}

		return mRootView;
	}

	private void callSupportWS() {
		mMainAcitivity.showProgress();
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.SUPPORT_PAGE, SUPPORT_PAGE,
				this);
	}

	private void setWebView() {

		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mBottomLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mBottomLine.setVisibility(View.GONE);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.support);
		mWebView = (WebView) mRootView.findViewById(R.id.webView_SupportText);

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

	private void slideBack() {

		getFragmentManager().popBackStack();

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		mMainAcitivity.hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		switch (requestCode) {
		case SUPPORT_PAGE:

			try {
				JSONObject mSupport = new JSONObject(
						response.getString("settings"));

				if (mSupport.getString("success").equalsIgnoreCase("1")) {
					JSONArray mData = new JSONArray(response.getString("data"));
					JSONObject mContent = mData.getJSONObject(0);
					String mContentString = mContent.getString("text");

					String mainStrng = "<html><head>"
							+ "<style type=\"text/css\">body{color: #497C6F; background-color: #ffffff;}"
							+ "</style></head>" + "<body>" + mContentString
							+ "</body></html>";

					mWebView.loadData(mainStrng, "text/html", "UTF-8");

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
