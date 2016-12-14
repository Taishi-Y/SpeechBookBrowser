package com.taishi.speachbookbrowser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;


public class TalkingBrowserActivity extends AppCompatActivity implements View.OnKeyListener {

	public SeekBar seekPitch;
	public SeekBar seekSpeed;
	public double pitch=1.0;
	public double speed=1.0;

	public static String TAG = "Talking Browser";
	private WebView webView;
	ViewGroup viewGroup;

	FloatingSearchView floatingSearchView;
	FloatingActionButton fabPlay;


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);

		getSupportActionBar().hide();

	viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);


//		fabPlay = (FloatingActionButton) viewGroup.findViewById(R.id.fab_play);
//		fabPlay.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Toast.makeText(TalkingBrowserActivity.this, "ffff", Toast.LENGTH_SHORT).show();
//			}
//		});

		floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
		floatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
			@Override
			public void onActionMenuItemSelected(MenuItem item) {
				// ダイアログを表示する
				DialogFragment newFragment = new TestDialogFragment();
				newFragment.show(getFragmentManager(), "test");
			}
		});

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		/*
		 * Setup the webView
		 */
		webView = (WebView) findViewById(R.id.webQuickView);
		webView.setWebChromeClient(new TalkingChromeClient(this.getWindow())); // TODO:
		// check
		// it

		MyApplication myApplication = (MyApplication) getApplication();

		webView.setWebViewClient(new WebViewClientCallback(webView, this
				.getWindow(),myApplication,viewGroup));

		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(true);
		webSettings.setSaveFormData(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setNeedInitialFocus(false);



		webView.setVisibility(View.VISIBLE);

		webView.loadUrl("http://freenovelsonline.net/red-queen/page-1-1025975.html");



		webView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				// Makes it so that we can click and scroll inside the browser
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					view.requestFocus();
				}
				// We want to be able to send the click down through it anyway
				return false; // true if it consumed event false if otherwise
			}
		});
		webView.setOnKeyListener(this);
	}


	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		webView.setWebViewClient(new WebViewClientCallback(webView, this
				.getWindow(),(MyApplication) getApplication(),viewGroup));
		View view = webView.getFocusedChild();
		// unless we are in a textview send it to the search bar
		if (view instanceof TextView) {
			return false;
		}
		return true;
	}

	@SuppressLint("ValidFragment")
	public class TestDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View content = inflater.inflate(R.layout.dialog_setting, null);

			builder.setView(content);

			builder.setMessage("Text to Speech Settings")
					.setNegativeButton("close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});
			// Create the AlertDialon object and return it

			seekPitch = (SeekBar) content.findViewById(R.id.seek_bar_pitch);
			seekPitch.setThumbOffset(5);
			seekPitch.setProgress((int) (((MyApplication)getApplication()).getGlobalPitch()*50));

			seekPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
					pitch = (float) i / (seekBar.getMax() / 2);
					Log.v("progress Barrrr", String.valueOf(pitch));
//					((MyApplication) getActivity().getApplication()).setGlobalPitch(pitch);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					((MyApplication) getActivity().getApplication()).setGlobalPitch(pitch);
				}
			});


			seekSpeed = (SeekBar) content.findViewById(R.id.seek_bar_speed);
			seekSpeed.setProgress((int) (((MyApplication)getApplication()).getGlobalSpeed()*50));
			seekSpeed.setThumbOffset(5);
			seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
					speed = (float) i / (seekBar.getMax() / 4);
					Log.v("progress Barrrr", String.valueOf(speed));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					((MyApplication) getActivity().getApplication()).setGlobalSpeed(speed);
				}
			});
			return builder.create();
		}
	}

}
