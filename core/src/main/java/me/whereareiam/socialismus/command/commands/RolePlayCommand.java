package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.model.roleplay.RolePlay;

public class RolePlayCommand extends CommandBase {
    private RolePlay rolePlay;

    public void setRolePlay(RolePlay rolePlay) {
        this.rolePlay = rolePlay;
    }

    @CommandAlias("%command.rolePlay")
    @CommandPermission("%permission.rolePlay")
    public void onCommand(CommandIssuer issuer) {
        issuer.sendMessage("Shiiit!");
    }

    @Override
    public boolean isEnabled() {
        return rolePlay.enabled;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(rolePlay.command, "command.rolePlay");
        commandHelper.addReplacement(rolePlay.requirements.writePermission, "permission.rolePlay");
    }
}
