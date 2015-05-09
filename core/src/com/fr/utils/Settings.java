package com.fr.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.badlogic.gdx.Gdx;

public class Settings {
	public static boolean soundOn = true;

	public final static long[] highscores = new long[] { 10, 0, 0 };
	public final static String file = ".slideO";

	public static void load() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Gdx.files.local(file)
					.read()));
			soundOn = Boolean.parseBoolean(in.readLine());
			for (int i = 0; i < highscores.length; i++) {
				highscores[i] = Long.parseLong(in.readLine());
			}
			System.out.println(soundOn);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public static void save() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(
					file).write(false)));
			out.write(Boolean.toString(soundOn)+"\n");
			for (int i = 0; i < highscores.length; i++) {
				out.write(Long.toString(highscores[i])+"\n");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void addScore(long score) {
		for (int i = 0; i < highscores.length; i++) {
			if (highscores[i] < score) {
				for (int j = highscores.length - 1; j > i; j--)
					highscores[j] = highscores[j - 1];
				highscores[i] = score;
				break;
			}
		}
	}
	
	public static void toggleSound(){
		soundOn = !soundOn;
		if(soundOn){
			Assets.playMusic();
		}else{
			Assets.pauseMusic();
		}
	}

}
