package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Queue;

/**
 * Represents a complete bubble message ready for display.
 * Contains the sender, recipients, bubble configuration, and formatted content.
 */
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class BubbleMessage {
  private final SocialismusPlayer sender;
  private Collection<SocialismusPlayer> recipients;

  private Bubble bubble;
  private Queue<BubbleGroup> groups;

  private Component content;
  private ActivatorType activatorType;
  private boolean cancelled;
}
