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

import java.util.Arrays;

import com.google.common.base.Strings;

/**
 * This class contains metadata for the version object. It can be a short
 * feature name or the additional version key for feature branches.
 */
public class MetadataVersion implements Comparable<MetadataVersion> {

    /**
     * Null metadata, the implementation of the Null Object design pattern.
     */
    static final MetadataVersion NULL = new MetadataVersion(null);

    /**
     * The array containing the version's identifiers.
     */
    private final String[] identifiers;

    /**
     * Constructs a {@code MetadataVersion} instance with identifiers.
     * @param identifiers the version's identifiers
     */
    public MetadataVersion(String[] identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * Increments the metadata version.
     *
     * @return a new instance of the {@code MetadataVersion} class
     */
    public MetadataVersion increment() {
        if(identifiers != null) {
            String[] ids = identifiers.clone();
            String lastId = ids[ids.length - 1];

            if (isInt(lastId)) {
                int intId = Integer.parseInt(lastId);
                ids[ids.length - 1] = String.valueOf(++intId);
            }

            return new MetadataVersion(ids);
        } else {
            throw new NullPointerException("Metadata version is NULL");
        }
    }

    /**
     * @return true if there is no metadata.
     */
    public boolean isEmpty() {
        return identifiers == null || identifiers.length == 0;
    }

    /**
     * @param metadataVersion metadata to check
     * @return true if there is no metadata.
     */
    public static boolean isEmpty(MetadataVersion metadataVersion) {
        return metadataVersion == null || metadataVersion.isEmpty();
    }

    /**
     * Clone MetadataVersion
     *
     * @return new instance of this MetadataVersion
     */
    @Override
    public MetadataVersion clone() {
        if(!Strings.isNullOrEmpty(toString())) {
            return new MetadataVersion(this.identifiers.clone());
        } else {
            return NULL;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if(identifiers != null) {
            StringBuilder sb = new StringBuilder();
            for (String ident : identifiers) {
                sb.append(ident);
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Checks if the specified string is an integer.
     *
     * @param str the string to check
     * @return {@code true} if the specified string is an integer
     *         or {@code false} otherwise
     */
    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
   public int compareTo(MetadataVersion other) {
        if (other.isEmpty() && !this.isEmpty()) {
            return -1;
        } else if(other.isEmpty() && this.isEmpty()) {
            return 0;
        } else if(!other.isEmpty() && this.isEmpty()) {
            return 1;
        }

        int result = compareIdentifierArrays(other.identifiers);
        if (result == 0) {
            result = (identifiers != null ? identifiers.length : 0) - (other.identifiers != null ? other.identifiers.length : 0);
        }
        return result;
    }

    /**
     * Compares two arrays of identifiers.
     *
     * @param otherIdentifiers the identifiers of the other version
     * @return integer result of comparison compatible with
     *         the {@code Comparable.compareTo} method
     */
    private int compareIdentifierArrays(String[] otherIdentifiers) {
        int result = 0;
        int length = getLeastCommonArrayLength(identifiers, otherIdentifiers);
        for (int i = 0; i < length; i++) {
            result = compareIdentifiers(identifiers[i], otherIdentifiers[i]);
            if (result != 0) {
                break;
            }
        }
        return result;
    }

    /**
     * Returns the size of the smallest array.
     *
     * @param arr1 the first array
     * @param arr2 the second array
     * @return the size of the smallest array
     */
    private static int getLeastCommonArrayLength(String[] arr1, String[] arr2) {
        return (arr1 != null ? arr1.length : 0) <= (arr2 != null ? arr2.length : 0) ? (arr1 != null ? arr1.length : 0) : (arr2 != null ? arr2.length : 0);
    }

    /**
     * Compares two identifiers.
     *
     * @param ident1 the first identifier
     * @param ident2 the second identifier
     * @return integer result of comparison compatible with
     *         the {@code Comparable.compareTo} method
     */
    private static int compareIdentifiers(String ident1, String ident2) {
        if (isInt(ident1) && isInt(ident2)) {
            return Integer.parseInt(ident1) - Integer.parseInt(ident2);
        } else {
            return ident1.compareTo(ident2);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MetadataVersion)) {
            return false;
        }
        return compareTo((MetadataVersion) other) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if(identifiers != null) {
            return Arrays.hashCode(identifiers);
        } else {
            return 0;
        }
    }
}
