package me.whereareiam.socialismus.module.chirper.api.model.announcer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class InternalAnnouncer extends Announcer {
    private int index;
}
