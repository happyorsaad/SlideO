package com.fr.game;

public interface ActionResolver {
	public boolean getSignedInGPGS();

	public void loginGPGS();

	public void submitScoreGPGS(long gameScore);

	public void unlockAchievementGPGS(String achievementId);

	public void getLeaderboardGPGS();

	public void getAchievementsGPGS();

	public void signOutGPGS();

	public boolean isFirstTimeUser();

	public boolean hasRated();

	public void setRated();

	public boolean isConnecting();

	public long getMaxValue();

	public void showOrLoadInterstital();
	
	public boolean isNetConnected();

}
