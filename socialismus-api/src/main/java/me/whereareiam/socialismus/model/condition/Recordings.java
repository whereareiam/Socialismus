package me.whereareiam.socialismus.model.condition;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.attribute.AttributeKey;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The passed verdicts an action collects on its way, such as a chat message's chat and format, recorded together
 * once the action happened, so a condition such as {@code cooldown} counts only messages that went out. A message
 * carries them as its {@link #ATTRIBUTE}:
 * <pre>{@code
 * Recordings.of(message).add(verdict);          // where a list let the message through
 * message.getAttribute(Recordings.ATTRIBUTE).ifPresent(Recordings::record);   // once it was sent
 * }</pre>
 */
public final class Recordings {
	/**
	 * The recordings a chat message carries.
	 */
	public static final AttributeKey<Recordings> ATTRIBUTE = AttributeKey.create("socialismus", "recordings", Recordings.class);

	private final List<Verdict.Passed> passed = new CopyOnWriteArrayList<>();

	/**
	 * Returns the recordings a message carries, giving it some when it has none.
	 *
	 * @param message the message
	 * @return its recordings
	 */
	public static @NotNull Recordings of(@NotNull ChatMessage message) {
		Recordings recordings = message.getAttribute(ATTRIBUTE).orElse(null);
		if (recordings != null) return recordings;

		Recordings created = new Recordings();
		message.setAttribute(ATTRIBUTE, created);

		return created;
	}

	/**
	 * Keeps a verdict to record later; a verdict that did not pass is not kept.
	 *
	 * @param verdict the verdict
	 */
	public void add(@NotNull Verdict verdict) {
		if (verdict instanceof Verdict.Passed held && !held.recordings().isEmpty()) passed.add(held);
	}

	/**
	 * Records every kept verdict once; a recording that fails is logged and the others still record.
	 */
	public void record() {
		List<Verdict.Passed> recorded = List.copyOf(passed);
		passed.clear();
		for (Verdict.Passed verdict : recorded) {
			try {
				verdict.record();
			} catch (RuntimeException failure) {
				Logger.severe("A condition could not record what happened: %s", failure.getMessage());
			}
		}
	}
}
