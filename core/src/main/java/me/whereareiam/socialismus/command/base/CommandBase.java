package me.whereareiam.socialismus.command.base;

import co.aikar.commands.BaseCommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public abstract class CommandBase extends BaseCommand {
    @Inject
    protected CommandHelper commandHelper;

    public abstract boolean isEnabled();

    public abstract void addReplacements();
}
