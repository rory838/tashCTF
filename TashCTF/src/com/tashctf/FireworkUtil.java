package com.tashctf;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
public class FireworkUtil {
	public static FireworkEffect getSpawnEffect() {
		return FireworkEffect.builder().with(Type.BURST).withFlicker().withColor(Color.FUCHSIA).build();
	}
}
