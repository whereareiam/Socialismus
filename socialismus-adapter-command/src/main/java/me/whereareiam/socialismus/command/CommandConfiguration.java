package me.whereareiam.socialismus.command;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.service.CommandService;

public class CommandConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(CommandService.class).to(DefaultCommandService.class);
		Multibinder.newSetBinder(binder(), RuntimeLifecycle.class).addBinding().to(DefaultCommandService.class);
	}
}
