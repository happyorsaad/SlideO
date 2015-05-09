package com.mygdx.game.desktop;

import com.fr.game.ActionResolver;

public class ActionResolverDesktop implements ActionResolver {
	boolean signedInStateGPGS = false;

	@Override
	public boolean getSignedInGPGS() {
		return signedInStateGPGS;
	}

	@Override
	public void loginGPGS() {
		System.out.println("loginGPGS");
		signedInStateGPGS = true;
	}

	@Override
	public void submitScoreGPGS(long score) {
		System.out.println("submitScoreGPGS " + score);
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
		System.out.println("unlockAchievement " + achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		System.out.println("getLeaderboardGPGS");
	}

	@Override
	public void getAchievementsGPGS() {
		System.out.println("getAchievementsGPGS");
	}

	@Override
	public void signOutGPGS() {
		System.out.println("Sign Out");
		signedInStateGPGS = false;
	}

	@Override
	public boolean isFirstTimeUser() {
		return true;
	}

	@Override
	public boolean isConnecting() {
		return false;
	}

	@Override
	public long getMaxValue() {
		return -1;
	}

	@Override
	public void showOrLoadInterstital() {
		System.out.println("showOrLoadInterstital");
	}

	@Override
	public boolean hasRated() {
		return false;
	}

	@Override
	public void setRated() {
		System.out.println("setRated");
	}

	@Override
	public boolean isNetConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
