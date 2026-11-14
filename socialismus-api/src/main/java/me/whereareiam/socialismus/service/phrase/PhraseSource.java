package me.whereareiam.socialismus.service.phrase;

/**
 * A module's phrases as {@link Phrasebook#add} added them, until it closes them.
 */
public interface PhraseSource extends AutoCloseable {
	/**
	 * Removes the phrases, when the module stops. Closing again, or after the owner's phrases were added anew, does
	 * nothing.
	 */
	@Override
	void close();
}
