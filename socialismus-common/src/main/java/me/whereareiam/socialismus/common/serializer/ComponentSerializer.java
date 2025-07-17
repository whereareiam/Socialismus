package me.whereareiam.socialismus.common.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.Getter;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.input.serializer.ComponentService;
import me.whereareiam.socialismus.api.input.serializer.SerializationWorker;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.config.message.Messages;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import me.whereareiam.socialismus.api.model.serializer.SerializerPlaceholder;
import me.whereareiam.socialismus.api.output.integration.FormattingIntegration;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.type.SerializationType;
import me.whereareiam.socialismus.common.serializer.legacy.LegacyParsingAdapter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Singleton
public class ComponentSerializer implements ComponentService, SerializationWorker, Reloadable {
	private final Provider<Settings> settings;
	private final Provider<Messages> messages;
	private final Provider<Set<Integration>> integrations;

	private SerializationType serializationType;
	private boolean allowLegacyParsing;

	@Getter
	private final LinkedList<Worker<SerializerContent>> workers = new LinkedList<>();
	private Worker<SerializerContent> legacyWorker;

	@Inject
	public ComponentSerializer(
			Provider<Settings> settings,
			Provider<Messages> messages,
			Provider<Set<Integration>> integrations,
			Registry<Reloadable> registry
	) {
		this.settings = settings;
		this.messages = messages;
		this.integrations = integrations;

		loadSettings(settings.get());

		registry.register(this);
	}

	@Override
	public Component format(DummyPlayer dummyPlayer, String message) {
		return format(new SerializerContent(dummyPlayer, new ArrayList<>(), message));
	}

	@Override
	public Component format(SerializerContent content) {
		if (content.getMessage().isEmpty()) return Component.empty();

		content.setMessage(content.getMessage().replace("{prefix}", messages.get().getPrefix()));

		if (content.getDummyPlayer().getUsername() != null)
			content.setMessage(content.getMessage()
					.replace("{playerName}", content.getDummyPlayer().getUsername()));

		content.setMessage(hookIntegrations(content));

		for (SerializerPlaceholder ph : content.getPlaceholders())
			content.setMessage(content.getMessage()
					.replace(ph.getPlaceholder(), ph.getValue()));

		for (Worker<SerializerContent> worker : workers)
			content = worker.getFunction().apply(content);

		return serializationType.getSerializer().deserialize(content.getMessage());
	}

	@Override
	public boolean removeWorker(Worker<SerializerContent> worker) {
		if (!worker.isRemovable()) return false;
		return workers.remove(worker);
	}

	@Override
	public void addWorker(Worker<SerializerContent> worker) {
		if (workers.stream().anyMatch(w -> w.getPriority() == worker.getPriority())) return;
		workers.add(worker);
		workers.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
	}

	@Override
	public void reload() {
		loadSettings(settings.get());
	}

	private void loadSettings(Settings settings) {
		this.serializationType = settings.getSerializer();
		this.allowLegacyParsing = settings.getMisc().isAllowLegacyParsing();

		updateLegacyWorker();
	}

	private void updateLegacyWorker() {
		if (allowLegacyParsing && legacyWorker == null) {
			legacyWorker = createLegacyWorker();
			addWorker(legacyWorker);
		}

		if (!allowLegacyParsing && legacyWorker != null) {
			workers.remove(legacyWorker);
			legacyWorker = null;
		}
	}

	private Worker<SerializerContent> createLegacyWorker() {
		return new Worker<>(
				patchLegacy(),
				0,
				true,
				false
		);
	}

	private Function<SerializerContent, SerializerContent> patchLegacy() {
		return content -> {
			String transformed = LegacyParsingAdapter.transform(
					content.getMessage(),
					serializationType
			);
			content.setMessage(transformed);
			return content;
		};
	}

	private String hookIntegrations(SerializerContent content) {
		List<FormattingIntegration> formatters = integrations.get().stream()
				.filter(i -> i instanceof FormattingIntegration)
				.map(i -> (FormattingIntegration) i)
				.toList();

		for (FormattingIntegration f : formatters) {
			if (f.isAvailable()) {
				content.setMessage(f.format(content.getDummyPlayer(), content.getMessage()));
			}
		}
		return content.getMessage();
	}
}