package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

/**
 * Represents a single line of text within a bubble.
 * Each line has its own content and display duration.
 */
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class BubbleLine {
  private Component content;
  private long displayTime;
}
