package me.whereareiam.socialismus.common.condition;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.adapter.configura.ConfiguraSettings;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.common.condition.type.LanguageCondition;
import me.whereareiam.socialismus.common.condition.type.PermissionCondition;
import me.whereareiam.socialismus.common.condition.type.PlaceholderCondition;
import me.whereareiam.socialismus.common.condition.type.PlaceholderReader;
import me.whereareiam.socialismus.common.condition.type.ServerCondition;
import me.whereareiam.socialismus.common.condition.type.WorldCondition;
import me.whereareiam.socialismus.common.condition.type.message.ChatCondition;
import me.whereareiam.socialismus.common.condition.type.message.ContentCondition;
import me.whereareiam.socialismus.common.condition.type.message.TriggerCondition;
import me.whereareiam.socialismus.common.condition.type.recording.CooldownCondition;
import me.whereareiam.socialismus.common.condition.type.recording.Cooldowns;
import me.whereareiam.socialismus.common.condition.type.relation.NearbyCondition;
import me.whereareiam.socialismus.common.condition.type.relation.SameCondition;
import me.whereareiam.socialismus.common.condition.type.relation.SeesCondition;
import me.whereareiam.socialismus.common.phrase.DefaultPhrasebook;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import me.whereareiam.socialismus.service.phrase.Phrasebook;
import me.whereareiam.socialismus.store.Stores;
import me.whereareiam.socialismus.type.PlatformType;

import java.time.Clock;
import java.util.Arrays;
import java.util.Set;

/**
 * Socialismus's conditions: Conditia reading settings with Socialismus's Configura, and the types of the core under
 * plain names. Modules add theirs through their {@code ModuleContext}, prefixed with their id.
 */
public final class ConditionConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(ConditionTexts.class).to(DefaultConditionTexts.class);
		bind(Phrasebook.class).to(DefaultPhrasebook.class);
	}

	@Provides
	@Singleton
	public Conditions provideConditions(
			Configura configura,
			Provider<Set<Integration>> integrations,
			Provider<PlayerRegistry> players,
			Stores stores
	) {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new ConfiguraSettings(configura.mapper()))
				.logger(new ConditiaLog())
				.wordings(Arrays.stream(ConditionTexts.Word.values()).map(ConditionTexts.Word::wording).toList())
				.build());

		PlaceholderReader placeholders = new PlaceholderReader(integrations);

		conditions.register(new PermissionCondition());
		conditions.register(new ServerCondition());
		conditions.register(new PlaceholderCondition(placeholders));
		conditions.register(new LanguageCondition());

		conditions.register(new ChatCondition());
		conditions.register(new TriggerCondition());
		conditions.register(new ContentCondition());

		conditions.register(new SameCondition(players, placeholders));
		conditions.register(new SeesCondition());
		conditions.register(new CooldownCondition(new Cooldowns(stores, Clock.systemUTC())));
		if (!PlatformType.isGameServer()) return conditions;

		// A proxy knows neither worlds nor positions.
		conditions.register(new WorldCondition());
		conditions.register(new NearbyCondition());

		return conditions;
	}
}
