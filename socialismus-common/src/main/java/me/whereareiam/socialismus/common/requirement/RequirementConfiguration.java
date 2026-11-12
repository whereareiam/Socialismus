package me.whereareiam.socialismus.common.requirement;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.adapter.configura.ConfiguraSettings;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.model.Wording;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.common.phrase.DefaultPhraseService;
import me.whereareiam.socialismus.common.requirement.type.ChatCondition;
import me.whereareiam.socialismus.common.requirement.type.ContentCondition;
import me.whereareiam.socialismus.common.requirement.type.PermissionCondition;
import me.whereareiam.socialismus.common.requirement.type.PlaceholderCondition;
import me.whereareiam.socialismus.common.requirement.type.ServerCondition;
import me.whereareiam.socialismus.common.requirement.type.TriggerCondition;
import me.whereareiam.socialismus.common.requirement.type.WorldCondition;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.model.requirement.RequirementWording;
import me.whereareiam.socialismus.service.phrase.PhraseService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.socialismus.type.PlatformType;

import java.util.Set;

/**
 * Socialismus's conditions: Conditia reading settings with Socialismus's Configura, and the types of the core under
 * plain names. Modules add theirs through their {@code ModuleContext}, prefixed with their id.
 */
public final class RequirementConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(RequirementReasonService.class).to(RequirementReasonResolver.class);
		bind(PhraseService.class).to(DefaultPhraseService.class);
	}

	@Provides
	@Singleton
	public Conditions provideConditions(Configura configura, Provider<Set<Integration>> integrations) {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new ConfiguraSettings(configura.mapper()))
				.logger(new ConditiaLog())
				.wording(Wording.onFailure(RequirementWording.DENIED))
				.wording(Wording.onFailure(RequirementWording.HINT))
				.build());

		conditions.register(new PermissionCondition());
		conditions.register(new ServerCondition());
		conditions.register(new PlaceholderCondition(integrations));
		conditions.register(new ChatCondition());
		conditions.register(new TriggerCondition());
		conditions.register(new ContentCondition());
		if (PlatformType.isGameServer()) conditions.register(new WorldCondition());

		return conditions;
	}
}
