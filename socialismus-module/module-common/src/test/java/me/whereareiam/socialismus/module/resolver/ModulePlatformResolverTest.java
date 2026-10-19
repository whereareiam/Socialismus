package me.whereareiam.socialismus.module.resolver;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModulePlatform;
import me.whereareiam.socialismus.type.PlatformType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ModulePlatformResolverTest {
	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void acceptsAListedPlatformInAnyVersionWhenNoneIsGiven() {
		ModulePlatformResolver resolver = new ModulePlatformResolver(PlatformType.PAPER, () -> "26.1.2");

		assertTrue(resolver.resolve(runningOn(ModulePlatform.builder().type(PlatformType.PAPER).build())));
	}

	@Test
	void acceptsAListedPlatformInAnAcceptedVersion() {
		ModulePlatformResolver resolver = new ModulePlatformResolver(PlatformType.PAPER, () -> "26.1.2");

		assertTrue(resolver.resolve(runningOn(ModulePlatform.builder().type(PlatformType.PAPER).version("26\\.1(\\.[0-9]+)?").build())));
	}

	@Test
	void refusesAListedPlatformInAnotherVersion() {
		ModulePlatformResolver resolver = new ModulePlatformResolver(PlatformType.PAPER, () -> "1.21.4");

		assertFalse(resolver.resolve(runningOn(ModulePlatform.builder().type(PlatformType.PAPER).version("26\\.1(\\.[0-9]+)?").build())));
	}

	@Test
	void refusesAPlatformItDoesNotList() {
		ModulePlatformResolver resolver = new ModulePlatformResolver(PlatformType.FOLIA, () -> "26.1.2");

		assertFalse(resolver.resolve(runningOn(ModulePlatform.builder().type(PlatformType.PAPER).build())));
	}

	private static InternalModule runningOn(ModulePlatform platform) {
		return InternalModule.builder().name("Chirper").platforms(List.of(platform)).build();
	}
}
