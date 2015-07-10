/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapjson;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;

@EqualsAndHashCode
@Getter
public class LocationVector {
    private int x, y, z;
    private Float yaw;
    private Float pitch;

    public LocationVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocationVector(int x, int y, int z, float yaw, float pitch) {
        checkArgument(yaw <= 180 || yaw > -180);
        checkArgument(pitch <= 90 || pitch >= -90);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public String toString() {
        if (yaw == null || pitch == null) {
            return String.format("{'point': {'xyz': '%s, %s, %s'}}", x, y, z);
        }

        return String.format("{'point': {'xyz': '%s, %s, %s', 'yaw': %s, 'pitch': %s}}", x, y, z, yaw, pitch);
    }
}
