package me.whereareiam.socialismus.module.bubbler.common.util;

import com.github.retrooper.packetevents.util.Vector3d;
import me.whereareiam.socialismus.model.position.Position;

public class PacketUtil {
	public static Vector3d toVector3d(Position p) {
		return new Vector3d(
				(float) p.getX(),
				(float) p.getY(),
				(float) p.getZ()
		);
	}
}
