package com.omelet.shadowdriends;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FirActivity extends Activity{
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fir);
		
		WebView browser = (WebView) findViewById(R.id.webview);
		browser.setWebViewClient(new WebViewClient());
//		browser.loadUrl("http://www.abolombon.org/nari/essential_mamla_gd.htm");
		WebSettings webSettings = browser.getSettings();
		webSettings.setJavaScriptEnabled(true);
		browser.loadUrl("file:///android_asset/fir.html");
	}
	 
//	 private String getContentHtml(){
//		return "অপরাধ বিচারের জন্য গ্রহণের মাধ্যমেই মামলার সূত্রপাত ঘটে। মামলা দায়েরের ক্ষেত্রে একটি গুরুত্বপূর্ণ শব্দ হলো আমলাযোগ্য অপরাধ (Cognizable Offence). মামলার ধরন বুঝার জন্য আমলাযোগ্য অপরাধ বিষয়ে জেনে নেয়া প্রয়োজন। তাই আমলাযোগ্য অপরাধ বিষয়ে আলোচনা নিম্নে তুলে ধরা হলোঃ" +
//				"<h1> আমলাযোগ্য অপরাধ (Cognizable Offence) </h1>ফৌজদারী কার্যবিধির ৪(চ) ধারায় বলা হয়েছে, এ আইনের দ্বিতীয় তফসিল অথবা বর্তমানে বলবত্‍ যে কোন আইনানুসারে যে অপরাধের জন্য পুলিশ অফিসার কাউকে বিনা পরোয়ানায় (ওয়ারেন্ট ব্যতীত) গ্রেফতার করতে পারে তাই আমলাযোগ্য অপরাধ। " +
//				"ফৌজদারী কার্যবিধির ১৪৯ ধারায় বলা হয়েছে, পুলিশ অফিসার আমলাযোগ্য অপরাধ সাধ্যমত প্রতিরোধের চেষ্টা করবেন।";
//	 }
}
