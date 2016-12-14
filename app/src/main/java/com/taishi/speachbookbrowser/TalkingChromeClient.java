package com.taishi.speachbookbrowser;

import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class TalkingChromeClient extends WebChromeClient
{
	Window window;
	public TalkingChromeClient(Window aWindow)
	{
		window = aWindow;
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress)
	{
		window.setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);

	}
}