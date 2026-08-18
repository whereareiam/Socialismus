package me.whereareiam.socialismus.module.bubbler.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.bubbler.api.type.BubbleType;

@Getter
@Setter
@ToString
public class BubblerSettings {
	private BubbleType bubbleType;
	private int minRecipients;
	private int maxQueueSize;
	private Notify notify;
	private Animation animation;

	@Getter
	@Setter
	@ToString
	public static class Notify {
		private boolean notifyNoPlayers;
		private boolean notifyNoNearbyPlayers;
		private boolean notifyNoBubbleSelected;
	}

	@Getter
	@Setter
	@ToString
	public static class Animation {
		private int popoutDelay;
		private Expansion expansion;
		private Contraction contraction;

		@Getter
		@Setter
		@ToString
		public static class Expansion {
			private float startScale;
			private int duration;
		}

		@Getter
		@Setter
		@ToString
		public static class Contraction {
			private float endScale;
			private int duration;
		}
	}
}
