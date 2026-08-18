package me.whereareiam.socialismus.module.bubbler.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A simple 3D vector with float components.
 * Used for representing scale, translation, and other vector-based properties.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vector {
    /** The X component of the vector */
    private float x;
    /** The Y component of the vector */
    private float y;
    /** The Z component of the vector */
    private float z;
}
