package me.whereareiam.socialismus.command.base;

import co.aikar.commands.BaseCommand;
import com.google.inject.Inject;

public abstract class CommandBase extends BaseCommand {
    @Inject
    protected CommandHelper commandHelper;

    public abstract boolean isEnabled();

    public abstract void addTranslations();

    public abstract void addReplacements();
}
