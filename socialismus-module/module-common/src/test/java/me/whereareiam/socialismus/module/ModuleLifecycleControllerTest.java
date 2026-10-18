package me.whereareiam.socialismus.module;

import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModuleLifecycleControllerTest {
	@Test
	void givesTheReasonAModuleStatesItself() {
		assertEquals("Failed to connect to Redis",
				ModuleLifecycleController.refusalReason(new ModuleLifecycleException("Failed to connect to Redis")));
	}

	@Test
	void findsTheModulesReasonInsideAnInjectionError() {
		ProvisionException failure = new ProvisionException(List.of(
				new Message(List.of(), "Error injecting constructor", new ModuleLifecycleException("Failed to connect to Redis"))));

		assertEquals("Failed to connect to Redis", ModuleLifecycleController.refusalReason(failure));
	}

	@Test
	void findsTheModulesReasonAsTheCauseOfAnotherFailure() {
		RuntimeException failure = new RuntimeException("wrapper", new ModuleLifecycleException("No database type"));

		assertEquals("No database type", ModuleLifecycleController.refusalReason(failure));
	}

	@Test
	void fallsBackToTheFailureWithoutAStatedReason() {
		assertEquals("dev.example.Main", ModuleLifecycleController.refusalReason(new ClassNotFoundException("dev.example.Main")));
	}
}
