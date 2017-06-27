package xhsun.gw2app.steve.backend.util.support.dialog;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * On click listener to open QR code scanner
 *
 * @author xhsun
 * @since 2017-02-16
 */

public class QROnClickListener implements View.OnClickListener {
	private Fragment fragment;
	private String message = "Scan Guild Wars 2 API Key QR Code";

	public QROnClickListener(Fragment fragment, String msg) {
		this.fragment = fragment;
		message = msg;
	}

	@Override
	public void onClick(View v) {
		FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(fragment);
		integrator.setPrompt(message);
		integrator.setOrientationLocked(false);
		integrator.setBeepEnabled(false);
		integrator.initiateScan();
	}

	//needed so that the scanner actually come back to this fragment
	private class FragmentIntentIntegrator extends IntentIntegrator {
		private Fragment fragment;

		FragmentIntentIntegrator(Fragment fragment) {
			super(fragment.getActivity());
			this.fragment = fragment;
		}

		@Override
		protected void startActivityForResult(Intent intent, int code) {
			fragment.startActivityForResult(intent, code);
		}
	}
}
