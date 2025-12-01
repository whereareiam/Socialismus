package me.whereareiam.socialismus.command;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.output.command.CommandService;

public class CommandConfiguration extends AbstractModule {
    @Override
    protected void configure() {
		bind(CommandService.class).to(DefaultCommandService.class);
    }
}
