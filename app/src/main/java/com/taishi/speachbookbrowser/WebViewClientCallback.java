package com.taishi.speachbookbrowser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Locale;



public class WebViewClientCallback extends WebViewClient implements TextToSpeech.OnInitListener {
	WebView webView;
	Window window;
	Activity activity;
	MyApplication myApplication;
	ViewGroup viewGroup;
	private final static String TAG = TalkingBrowserActivity.TAG;

	double pitch,speed;

	private TextToSpeech tts;

	FloatingActionButton fabPlay;



	@JavascriptInterface
	public int speak(String innerText) {
		Log.d(TAG, "Nailed the JS man!" + innerText);

//		fabPlay.setImageResource(R.drawable.ic_pause_white_24dp);

		pitch = this.myApplication.getGlobalPitch();
		speed = this.myApplication.getGlobalSpeed();

		Log.e("pitch", String.valueOf(pitch));


		tts.setPitch((float)pitch);
		tts.setSpeechRate((float)speed);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");

		tts.speak(innerText, TextToSpeech.QUEUE_FLUSH, map);
		setTtsListener();
		
		return 0;
	}
	
	@SuppressLint("JavascriptInterface")
	public WebViewClientCallback(WebView aWebView, Window aWindow, MyApplication myApplication, ViewGroup viewGroup) {
		webView = aWebView;
		window = aWindow;
		this.myApplication = myApplication;
		this.viewGroup = viewGroup;

		fabPlay = (FloatingActionButton) viewGroup.findViewById(R.id.fab_play);
		fabPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tts.stop();
				webView.loadUrl("javascript:clearHighlight();");
				fabPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp);
			}
		});

		webView.addJavascriptInterface(this, "android"); //so I can access this via android now?
		tts = new TextToSpeech(window.getContext(),this);
	}


	@Override
	public void onInit(int i) {

		if (i == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.US);
		}

	}


	// 読み上げの始まりと終わりを取得
	private void setTtsListener(){
		// android version more than 15th
		// 市場でのシェアが15未満は数パーセントなので除外
		if (Build.VERSION.SDK_INT >= 15) {
			int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
				@Override
				public void onDone(String utteranceId)
				{
					Log.d(TAG,"SPEECH DONEEEEEEEEE " + utteranceId);
//					webView.loadUrl("javascript:"+onMouseUpUnhighlight+";");
					//hint: http://stackoverflow.com/questions/22607657/webview-methods-on-same-thread-error

					webView.post(new Runnable() {
						@Override
						public void run() {
							webView.loadUrl("javascript:readfinish();");
//							webView.loadUrl("javascript:clearHighlight();");
						}
					});
				}
				@Override
				public void onError(String utteranceId) {Log.d(TAG,"progress on Error " + utteranceId);}
				@Override
				public void onStart(String utteranceId) {Log.d(TAG,"progress on Start " + utteranceId);}

			});

			if (listenerResult != TextToSpeech.SUCCESS) {
				Log.e(TAG, "failed to add utterance progress listener");
			}
		} else {
			Log.e(TAG, "Build VERSION is less than API 15");
		}

	}
	
	String lastClicked = "";
	
	void loadTime(String url) {
			webView.loadUrl("");
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (lastClicked.equals(url)) {
			Log.d(TAG, "lastclicked =");
			loadTime(url);    
	    	return false;
        } else {
			Log.d(TAG, "lastclicked !=");
			lastClicked = url;
	    	return true;
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		window.setFeatureInt(Window.FEATURE_PROGRESS, 0);
	
		Log.d(TAG, "onPageFinished setting android interface");
		setupJscript();        
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);

		window.setFeatureInt(Window.FEATURE_PROGRESS, 10000);
	}

	String clearHighlight =
			"function clearHighlight(event)"+
					"{"+
						"console.log('clearHighlight');"+
						"preElement.nextElementSibling.style.background='';"+
					"}";
	
	String onMouseUpUnhighlight =
		"function readfinish(event)"+
				"{"+

//					"queueEl.shift().style.background = queueBackground.shift();"+

//					"preElement.nextElementSibling.style.background='#FFEB3B';"+
					"preElement.style.background='';"+
					"preElement = preElement.nextElementSibling;"+


					"preElement.style.background='#FFEB3B';"+
					"android.speak(preElement.innerText);"+

//					"preElement = preElement.nextElementSibling;"+
//					"console.log(preElement.innerText);"+
				"}";

	String getAllElements ="if (document.all !== undefined)" +
			"{" +
			"   allElements = document.all;" +
			"}" +
			"else" +
			"{" +
			"   allElements = document.getElementsByTagName('*');" +
			"};";
//			"for (var i=0; i<document.forms[0].length; i++)" +
//					"{" +
//					";"+
//					"}";

	String onMouseDownHighlight =
	"function (event)"+
		"{"+
			"queueEl.push(event.srcElement);" +
			"queueBackground.push(event.srcElement.style.background);" +
			"event.srcElement.style.background='#FFEB3B';"+
			"preElement = event.srcElement;"+
			"android.speak(preElement.innerText);"+

		"}";
	
	
	void setupJscript()
	{
		webView.loadUrl("javascript:var preElement = '';" +
				"var preBackground = '';" +
				"var nextElement = '';" +
				"var x=document.getElementsByTagName('body')[0]; "+
				"var queueEl = [];" +
				"var idMenu = '';" +
				"var queueBackground = [];" +


				"var allElements = document.getElementsByTagName('*');" +
				"console.log('WOOOOOOOO' + allElements);"+


				"function clearHighlight(event)"+
				"{"+
				"console.log('clearHighlight');"+
				"preElement.nextElementSibling.style.background='';"+
				"}"+

				"var array = [];"+
				"for (var i = 0; i < allElements.length; i++) {" +
					"if(allElements[i].tagName == 'P'){" +
					"  var current = allElements[i];" +
//					"console.log(current.innerText);"+
					"};"+
				"};"+

//				"allElements[0].style.background='#FFEB3B';"+

				"x.onclick = " + onMouseDownHighlight+";" +
				onMouseUpUnhighlight+";");
	}


}
