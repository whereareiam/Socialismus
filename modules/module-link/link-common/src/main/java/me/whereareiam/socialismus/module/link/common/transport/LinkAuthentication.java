package me.whereareiam.socialismus.module.link.common.transport;

import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Proves that a server knows the link's secret without sending it: the proxy sends a random challenge, and the server
 * answers with the challenge signed by the secret.
 */
final class LinkAuthentication {
	static final int CHALLENGE_BYTES = 32;

	private static final SecureRandom RANDOM = new SecureRandom();

	private final SecretKeySpec key;

	LinkAuthentication(@NotNull String secret) {
		if (secret.isBlank()) throw new IllegalArgumentException("The link's secret cannot be blank");

		this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

	static byte @NotNull [] challenge() {
		byte[] challenge = new byte[CHALLENGE_BYTES];
		RANDOM.nextBytes(challenge);
		return challenge;
	}

	byte @NotNull [] answer(byte @NotNull [] challenge) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(key);
			return mac.doFinal(challenge);
		} catch (GeneralSecurityException failure) {
			throw new IllegalStateException("HmacSHA256 is not available", failure);
		}
	}

	boolean accepts(byte @NotNull [] challenge, byte @NotNull [] answer) {
		return MessageDigest.isEqual(answer(challenge), answer);
	}
}
