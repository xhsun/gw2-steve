package xhsun.gw2app.steve.wiki;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import xhsun.gw2app.steve.listener.WebHistoryListener;

/**
 * WebViewClient for wiki fragment's web view
 * -this is used to check if an url need to be redirected to a browser
 * -remove cluster from gw2 wiki page
 * -toggle availability for back/forward button
 *
 * @author xhsun
 * @version 0.4
 * @since 2017-02-03
 */

public class WikiWebViewClient extends WebViewClient {
	private static String URL = "wiki.guildwars2.com";
	private WebHistoryListener listener;
	private ProgressBar progressBar;

	/**
	 * constructor
	 *
	 * @param listener for control back/forward button
	 */
	public WikiWebViewClient(WebHistoryListener listener, ProgressBar progressBar) {
		super();
		this.listener = listener;
		this.progressBar = progressBar;
	}

	/**
	 * redirect to an actual browser
	 * if the url provided is not from gw2 wiki
	 *
	 * @param view    web view
	 * @param request web reource request
	 * @return false if it is the intended url, otherwise true
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		if (request.getUrl().getHost().equals(URL)) return false;
		// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
		Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
		view.getContext().startActivity(intent);
		return true;
	}

	/**
	 * redirect to an actual browser
	 * if the url provided is not from gw2 wiki
	 * note: this is still here is because the other version aren't so backward compatible
	 *
	 * @param view web view
	 * @param url  url of web page
	 * @return false if it is the intended url, otherwise true
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (Uri.parse(url).getHost().equals(URL)) return false;
		// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		view.getContext().startActivity(intent);
		return true;
	}

	/**
	 * a hacky way to remove clusters from the wiki page
	 *
	 * @param view web view
	 * @param url  url of website
	 */
	@Override
	public void onLoadResource(WebView view, String url) {
		view.loadUrl("javascript:(function() { " +
				"document.getElementById('column-one').style.display='none';" +
				"document.getElementById('column-content').style.cssFloat='none';" +
				"document.getElementById('content').style.cssText = 'width: 95%; margin-top: 0px !important;';})()");
	}

	/**
	 * check if we can go back/forward for given web view
	 * if yes, enable back/forward button; Otherwise, disable them
	 *
	 * @param view web view
	 * @param url  url of web page
	 */
	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		//display website
		view.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);

		//check go back
		if (view.canGoBack()) listener.switchEnable(0);
		else listener.switchDisable(0);
		//check go forward
		if (view.canGoForward()) listener.switchEnable(1);
		else listener.switchDisable(1);
	}
}
