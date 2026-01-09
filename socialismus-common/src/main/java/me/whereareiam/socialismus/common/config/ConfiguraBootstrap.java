package me.whereareiam.socialismus.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.reader.ConfigReader;
import me.whereareiam.configura.type.Format;
import me.whereareiam.configura.writer.ConfigWriter;
import me.whereareiam.socialismus.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.*;
import me.whereareiam.socialismus.type.ConfigurationType;
import me.whereareiam.socialismus.type.Version;
import me.whereareiam.socialismus.common.config.adapter.ComponentAdapter;
import me.whereareiam.socialismus.common.config.adapter.VersionAdapter;
import net.kyori.adventure.text.Component;

@Singleton
public class ConfiguraBootstrap {
	@Inject
	public ConfiguraBootstrap(ConfigurationTypeResolver resolver) {
		// Resolve the preferred configuration format
		ConfigurationType type = resolver.getConfigurationType();
		Format format = (type == ConfigurationType.JSON) ? Format.JSON : Format.YAML;

		// Configure global reader/writer with chosen format
		ConfigReader reader = Config.reader(format);
		ConfigWriter writer = Config.writer(format);
		Config.setReader(reader);
		Config.setWriter(writer);

		// Register adapters
		Config.registerAdapter(Version.class, VersionAdapter.class);
		Config.registerAdapter(Component.class, ComponentAdapter.class);

		// Register polymorphic types
		Config.registerPolymorphic(Requirement.class)
				.inferByField("servers", ServerRequirement.class)
				.inferByField("worlds", WorldRequirement.class)
				.inferByField("chatIdentifiers", ChatRequirement.class)
				.inferByField("placeholders", PlaceholderRequirement.class)
				.inferByField("permissions", PermissionRequirement.class)
				.inferByField("triggers", TriggerRequirement.class)
				.inferByField("messages", MessageRequirement.class)
				.build();
	}
}