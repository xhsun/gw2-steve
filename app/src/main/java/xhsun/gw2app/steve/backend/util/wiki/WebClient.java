package xhsun.gw2app.steve.backend.util.wiki;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import timber.log.Timber;
import xhsun.gw2app.steve.view.fragment.WikiFragment;

/**
 * Custom {@link WebViewClient} for {@link WikiFragment}<br/>
 * -this is used to check if an url need to be redirected to a browser<br/>
 * -remove cluster from gw2 wiki page<br/>
 * -toggle availability for back/forward button<br/>
 *
 * @author xhsun
 * @since 2017-02-03
 */

public class WebClient extends WebViewClient {
	private static final String URL = "wiki.guildwars2.com";
	private ResourceProvider provider;

	private String page = "<html>\n" +
			"<head>\n" +
			"\t<script>\n" +
			"\twindow.onload = function(){\n" +
			"\tdocument.getElementById('column-one').style.display='none';\n" +
			"\tdocument.getElementById('column-content').style.cssFloat='none';\n" +
			"\tdocument.getElementById('content').style.cssText = 'width: 95%; margin-top: 0px !important;';\n" +
			"\t};\n" +
			"\n" +
			"\twindow.loadPage = function (url) {\n" +
			"\t\t$.get(url, function(page) {\n" +
			"\t\t\t$('html').replace(page);\n" +
			"\t\t});\n" +
			"\t};\n" +
			"\t</script>\n" +
			"</head>\n" +
			"</html>";

	public WebClient(ResourceProvider listener) {
		super();
		this.provider = listener;
	}

	/**
	 * If the url provided is not from gw2 wiki, redirect to an actual browser
	 *
	 * @param view    WebView
	 * @param request web reource request
	 * @return false if it is the intended url, otherwise true (redirect)
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		return isWiki(request.getUrl(), view);
	}

	/**
	 * If the url provided is not from gw2 wiki, redirect to an actual browser<br/>
	 * note: this is still here is because the other version aren't so backward compatible
	 *
	 * @param view webView
	 * @param url  url of web page
	 * @return false if it is the intended url, otherwise true (redirect)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return isWiki(Uri.parse(url), view);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		view.setVisibility(View.GONE);
		provider.getProgressBar().setVisibility(View.VISIBLE);
		Timber.i("Show progress bar and hide web view");
		super.onPageStarted(view, url, favicon);
	}

	/**
	 * If we can go forward/backward in the page, enable forward/backward button
	 *
	 * @param view webView
	 * @param url  url of web page
	 */
	@Override
	public void onPageFinished(WebView view, String url) {
		injectCSS(view);

		//display website
		view.setVisibility(View.VISIBLE);
		provider.getProgressBar().setVisibility(View.GONE);
		Timber.i("Hide progress bar and show web view");

		//check go back
		if (view.canGoBack()) provider.enable(0);
		else provider.disable(0);
		//check go forward
		if (view.canGoForward()) provider.enable(1);
		else provider.disable(1);

		super.onPageFinished(view, url);
	}

	private void injectCSS(WebView view) {
		view.loadUrl("javascript:(function() { " +
				"document.getElementById('column-one').style.display='none';" +
				"document.getElementById('column-content').style.cssFloat='none';" +
				"document.getElementById('content').style.cssText = 'width: 95%; margin-top: 0px !important;';" +
				"})()");
		try {
			Thread.sleep(100);
			Timber.i("Load web page with out cluster");
		} catch (InterruptedException e) {
			Timber.d("Interrupted while wait to inject js");
		}
	}

	//If the url provided is not from gw2 wiki, redirect to an actual browser
	private boolean isWiki(Uri url, WebView view) {
		Timber.i("Judging if we need to redirect");
		if (url.getHost().equals(URL)) return false;
		//Link is not for GW2 Wiki, launch another Activity that handles URLs
		Timber.i("Redirect to actual browser");
		Intent intent = new Intent(Intent.ACTION_VIEW, url);
		view.getContext().startActivity(intent);
		return true;
	}
}
