package com.fr.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class WorldUtils {
	public static class SpikeInfo {
		public float x1, x2, y;

		public SpikeInfo(float x1, float x2, float y) {
			this.x1 = x1;
			this.x2 = x2;
			this.y = y;
		}
	}

	public static boolean simulate = false;

	public static Array<SpikeInfo> spikeInfo = new Array<WorldUtils.SpikeInfo>();

	public static void loadSpikeInfo() {

		if (simulate) {
			float x1[] = { 10f, 6f };
			float x2[] = { 12.5f, 8.5f };
			int y = 10;
			int index = 0;
			for (int i = 0; i < 10000; i += 1) {
				spikeInfo
						.add(new WorldUtils.SpikeInfo(x1[index], x2[index], y));
				index = (index + 1) % x1.length;
				y -= 8;
			}
		} else {
			FileHandle level = Gdx.files.internal("data/level_final_two.txt");

			BufferedReader input = new BufferedReader(new InputStreamReader(
					level.read()));
			String line;
			try {
				float delta = 0;
				while ((line = input.readLine()) != null) {
					String lineDiv[] = line.split(" ");
					float xL = Float.parseFloat(lineDiv[1]);
					float xR = Float.parseFloat(lineDiv[2]);
					float y = Float.parseFloat(lineDiv[3]);
					int num = Integer.parseInt(lineDiv[0]);

					if (num % 20 == 0) {
						delta = (float) Math.min(0.003f, delta + 0.001f);
					}

					xL += (0.021f + delta);
					xR -= (0.021f + delta);

					spikeInfo.add(new WorldUtils.SpikeInfo(xL, xR, y));

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
