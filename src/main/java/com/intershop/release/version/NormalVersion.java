/*
 * Copyright 2015 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * This file includes code fragments from semver (https://github.com/zafarkhaja/jsemver),
 * which is licensed under the MIT license.
 */
package com.intershop.release.version;

import javax.annotation.Nonnull;

/**
 * This is the version object without any extension.
 * It is based on SemVer and extended for versioning
 * schema with four digits.
 */
public class NormalVersion implements Comparable<NormalVersion> {

    /**
     * The major version number.
     */
    private final int major;

    /**
     * The minor version number.
     */
    private final int minor;

    /**
     * The patch version number.
     */
    private final int patch;

    /**
     * The hotfix version number.
     */
    private final int hotfix;

    /**
     * Format of the version - four or three digits
     */
    private final VersionType type;

    /**
     * Constructs a {@code NormalVersion} with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param hotfix the hotfix version number
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    public NormalVersion(int major, int minor, int patch, int hotfix) {
        if (major < 0 || minor < 0 || patch < 0 || hotfix < 0) {
            throw new IllegalArgumentException(
                    "Major, minor, patch and hotfix versions MUST be non-negative integers."
            );
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.hotfix = hotfix;

        type = VersionType.fourDigits;
    }

    /**
     * Constructs a {@code NormalVersion} with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    public NormalVersion(int major, int minor, int patch) {
        hotfix = 0;
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException(
                    "Major, minor, patch and hotfix versions MUST be non-negative integers."
            );
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;

        type = VersionType.threeDigits;
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Returns the patch version number.
     *
     * @return the patch version number
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Returns the hotfix version number.
     *
     * @return the hotfix version number
     */
    public int getHotfix() {
        return hotfix;
    }

    /**
     * Returns the version type
     *
     * @return version type
     */
    public VersionType getVersionType() {
        return type;
    }

    /**
     * Increments the major version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementMajor() {
        if(type == VersionType.fourDigits) {
            return new NormalVersion(major + 1, 0, 0, 0);
        } else {
            return new NormalVersion(major + 1, 0, 0);
        }
    }

    /**
     * Increments the minor version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementMinor() {
        if(type == VersionType.fourDigits) {
            return new NormalVersion(major, minor + 1, 0, 0);
        } else {
            return new NormalVersion(major, minor + 1, 0);
        }
    }

    /**
     * Increments the patch version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementPatch() {
        if(type == VersionType.fourDigits) {
            return new NormalVersion(major, minor, patch + 1, 0);
        } else {
            return new NormalVersion(major, minor, patch + 1);
        }
    }

    /**
     * Increments the hotfix version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementHotfix() {
        if(type == VersionType.fourDigits) {
            return new NormalVersion(major, minor, patch, hotfix + 1);
        } else {
            throw  new UnsupportedOperationException("This normal version does not support four digits");
        }
    }

    /**
     * Increment the latest non 0 version number.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementLatest() {
        return incrementLatest(type == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX);
    }

    /**
     * Increment the latest non 0 version number.
     *
     * @param pos the digit position
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementLatest(DigitPos pos) {
        switch (pos) {
            case HOTFIX:
                return incrementPatch();
            case PATCH:
                return incrementMinor();
            case MINOR:
                return incrementMajor();
            case MAJOR:
                return incrementMajor();
        }
        return this;
    }

    /**
     * Increment the version for pos.
     *
     * @return a new instance of the {@code NormalVersion} class
     */
    public NormalVersion incrementVersion(DigitPos pos) {
        if (pos == null) {
            pos = type == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX;
        }

        switch (pos) {
            case HOTFIX:
                return incrementHotfix();
            case PATCH:
                return incrementPatch();
            case MINOR:
                return incrementMinor();
            case MAJOR:
                return incrementMajor();
        }
        return this;
    }

    /**
     * Returns the shortened string representation of this normal version.
     *
     * @return the shortened string representation of this normal version
     */
    public String toStringFor(int firstDigits) {
        switch (firstDigits) {
            case 1:
                return String.format("%d", major);
            case 2:
                return String.format("%d.%d", major, minor);
            case 3:
                return String.format("%d.%d.%d", major, minor, patch);
            case 4:
                if(type == VersionType.threeDigits) {
                    throw new UnsupportedOperationException("The number of digits must be less than 4.");
                } else {
                    return String.format("%d.%d.%d.%d", major, minor, patch, hotfix);
                }
            default:
                throw new UnsupportedOperationException("The number of digits must be greater than 0 and less than 3 or 4, depends on the version type.");
        }
    }

    /**
     * Clone an existing object.
     *
     * @return a new instance of NormalVersion
     */
    public NormalVersion clone() {
        if(this.type == VersionType.threeDigits) {
            return new NormalVersion(this.major, this.minor, this.patch);
        } else {
            return new NormalVersion(this.major, this.minor, this.patch, this.hotfix);
        }
    }

    /**
     * Returns the string representation of this normal version.
     *
     * A normal version number MUST take the form X.Y.Z where X, Y, and Z are
     * non-negative integers. X is the major version, Y is the minor version,
     * and Z is the patch version. (SemVer p.2)
     *
     * @return the string representation of this normal version
     */
    @Override
    public String toString() {
        if(type == VersionType.fourDigits) {
            return String.format("%d.%d.%d.%d", major, minor, patch, hotfix);
        } else {
            return String.format("%d.%d.%d", major, minor, patch);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull NormalVersion other) {
        int result = major - other.major;
        if (result == 0) {
            result = minor - other.minor;
            if (result == 0) {
                result = patch - other.patch;
                if (result == 0 && type == VersionType.fourDigits) {
                    result = hotfix - other.hotfix;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NormalVersion)) {
            return false;
        }
        return compareTo((NormalVersion) other) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + major;
        hash = 31 * hash + minor;
        hash = 31 * hash + patch;
        if(type == VersionType.fourDigits) {
            hash = 31 * hash + hotfix;
        }
        return hash;
    }
}
