package com.libgdx.game.slido;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fr.game.ActionResolver;
import com.fr.game.SkiFall;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements
		GameHelperListener, ActionResolver {
	private GameHelper gameHelper;
	private SharedPreferences sharedPref;
	private boolean firstTimeUser;
	private long maxScore;
	private boolean hasResult;
	private boolean hasRated;

	private static final String AD_UNIT_ID = "ca-app-pub-4709840871136981/8754450555";
	private static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/developer?id=TheInvader360";
	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-4709840871136981/1231183755";

	protected AdView adView;
	protected View gameView;

	private InterstitialAd interstitialAd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = false;
		cfg.useCompass = false;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		RelativeLayout layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);

		AdView admobView = createAdView();
		layout.addView(admobView);
		View gameView = createGameView(cfg);
		layout.addView(gameView);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
			}

			@Override
			public void onAdClosed() {
			}
		});

		setContentView(layout);
		startAdvertising(admobView);
		if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		}
		sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		boolean isSignedIn = sharedPref.getBoolean("SIGNED_IN", false);
		firstTimeUser = sharedPref.getBoolean("FIRST_TIME", true);
		if (firstTimeUser) {
			writeBooleanToPref("FIRST_TIME", false);
		}
		hasRated = sharedPref.getBoolean("RATED", false);
		gameHelper.setConnectOnStart(isSignedIn);
		gameHelper.setup(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null)
			adView.resume();
	}

	private void writeBooleanToPref(String key, boolean value) {
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onPause() {
		if (adView != null)
			adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}
	}

	@Override
	public void submitScoreGPGS(long score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(),
				"CgkI-Iyz9_MSEAIQAg", score);
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
		Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(
							gameHelper.getApiClient(), "CgkI-Iyz9_MSEAIQAg"),
					100);
		} else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void getAchievementsGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(
					Games.Achievements.getAchievementsIntent(gameHelper
							.getApiClient()), 101);
		} else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void signOutGPGS() {
		if (gameHelper.isSignedIn()) {
			gameHelper.signOut();
			if (!gameHelper.isSignedIn()) {
				writeBooleanToPref("SIGNED_IN", false);
			}
		}
	}

	@Override
	public void onSignInFailed() {

	}

	@Override
	public void onSignInSucceeded() {
		writeBooleanToPref("SIGNED_IN", true);
	}

	@Override
	public boolean isFirstTimeUser() {
		return firstTimeUser;
	}

	@Override
	public boolean isConnecting() {
		return gameHelper.isConnecting();
	}

	public long getMaxValue() {
		maxScore = -1;
		hasResult = false;
		loadScoreOfLeaderBoard();
		int numTries = 3000;
		while (!hasResult && numTries-- >= 0) {
		}
		return maxScore;
	}

	public void loadScoreOfLeaderBoard() {
		Games.Leaderboards.loadCurrentPlayerLeaderboardScore(
				gameHelper.getApiClient(), "CgkI-Iyz9_MSEAIQAg",
				LeaderboardVariant.TIME_SPAN_ALL_TIME,
				LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(
				new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
					public void onResult(
							final Leaderboards.LoadPlayerScoreResult scoreResult) {
						hasResult = true;
						if (isScoreResultValid(scoreResult)) {
							maxScore = scoreResult.getScore().getRawScore();
						}
					}
				});
	}

	private boolean isScoreResultValid(
			final Leaderboards.LoadPlayerScoreResult scoreResult) {
		return scoreResult != null
				&& GamesStatusCodes.STATUS_OK == scoreResult.getStatus()
						.getStatusCode() && scoreResult.getScore() != null;
	}

	private AdView createAdView() {
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(AD_UNIT_ID);
		adView.setId(12345); // this is an arbitrary id, allows for relative
								// positioning in createGameView()
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		adView.setLayoutParams(params);
		adView.setBackgroundColor(Color.BLACK);
		return adView;
	}

	private View createGameView(AndroidApplicationConfiguration cfg) {
		gameView = initializeForView(new SkiFall(this), cfg);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.BELOW, adView.getId());
		gameView.setLayoutParams(params);
		return gameView;
	}

	private void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"9224E6EF1C8F6F8A00D635FD23793344").build();
		adView.loadAd(adRequest);
	}

	@Override
	public void showOrLoadInterstital() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
					} else {
						AdRequest interstitialRequest = new AdRequest.Builder()
								.addTestDevice(
										"9224E6EF1C8F6F8A00D635FD23793344")
								.build();
						interstitialAd.loadAd(interstitialRequest);
					}
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public boolean hasRated() {
		return hasRated;
	}

	@Override
	public void setRated() {
		writeBooleanToPref("RATED", true);
	}

	@Override
	public boolean isNetConnected() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
		    return false;
		}
		return ni.isConnected();
	}
}
