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
 */


package com.intershop.release.version

import groovy.transform.CompileStatic

/**
 * This contains metadata for the version object. This can be a short
 * feature name or the additional version key for feature branches.
 */
@CompileStatic
class MetadataVersion implements Comparable<MetadataVersion> {

    /**
     * Null metadata, the implementation of the Null Object design pattern.
     */
    static final MetadataVersion NULL = new NullMetadataVersion();

    /**
     * The implementation of the Null Object design pattern.
     */
    private static class NullMetadataVersion extends MetadataVersion {

        /**
         * Constructs a {@code NullMetadataVersion} instance.
         */
        public NullMetadataVersion() {
            super(null);
        }

        /**
         * @throws NullPointerException as Null metadata cannot be incremented
         */
        @Override
        MetadataVersion increment() {
            throw new NullPointerException("Metadata version is NULL");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return ''
        }
    }

    /**
     * The array containing the version's identifiers.
     */
    private final String[] idents;

    /**
     * Constructs a {@code MetadataVersion} instance with identifiers.
     * @param identifiers the version's identifiers
     */
    MetadataVersion(String[] identifiers) {
        if(identifiers) {
            idents = identifiers
        }
    }

    /**
     * Increments the metadata version.
     *
     * @return a new instance of the {@code MetadataVersion} class
     */
    MetadataVersion increment() {
        if(idents) {
            String[] ids = (String[])idents.clone()
            String lastId = ids[ids.length - 1]

            if (isInt(lastId)) {
                int intId = Integer.parseInt(lastId)
                ids[ids.length - 1] = String.valueOf(++intId)
            }

            return new MetadataVersion(ids)
        } else {
            throw new NullPointerException("Metadata version is NULL");
        }
    }

    /**
     * Clone MetadataVersion
     *
     * @return new instance of this MetadataVersion
     */
    public MetadataVersion clone() {
        if(toString()) {
            new MetadataVersion((String[])this.idents.clone())
        } else {
            return new NullMetadataVersion()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if(idents) {
            StringBuilder sb = new StringBuilder();
            for (String ident : idents) {
                sb.append(ident);
            }
            return sb.toString();
        } else {
            return ''
        }
    }

    /**
     * Checks if the specified string is an integer.
     *
     * @param str the string to check
     * @return {@code true} if the specified string is an integer
     *         or {@code false} otherwise
     */
    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false
        }
        return true
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(MetadataVersion other) {
        if (other.toString() == MetadataVersion.NULL.toString() && this.toString() != MetadataVersion.NULL.toString()) {
            return -1
        } else if(other.toString() == MetadataVersion.NULL.toString() && this.toString() == MetadataVersion.NULL.toString()) {
            return 0
        } else if(other.toString() != MetadataVersion.NULL.toString() && this.toString() == MetadataVersion.NULL.toString()) {
            return 1
        }

        int result = compareIdentifierArrays(other.idents)
        if (result == 0) {
            result = (idents ? idents.size() : 0) - (other.idents ? other.idents.size() : 0)
        }
        return result
    }

    /**
     * Compares two arrays of identifiers.
     *
     * @param otherIdents the identifiers of the other version
     * @return integer result of comparison compatible with
     *         the {@code Comparable.compareTo} method
     */
    private int compareIdentifierArrays(String[] otherIdents) {
        int result = 0;
        int length = getLeastCommonArrayLength(idents, otherIdents);
        for (int i = 0; i < length; i++) {
            result = compareIdentifiers(idents[i], otherIdents[i]);
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
    private int getLeastCommonArrayLength(String[] arr1, String[] arr2) {
        return (arr1 ? arr1.size() : 0) <= (arr2 ? arr2.size() : 0) ? (arr1 ? arr1.size() : 0) : (arr2 ? arr2.size() : 0)
    }

    /**
     * Compares two identifiers.
     *
     * @param ident1 the first identifier
     * @param ident2 the second identifier
     * @return integer result of comparison compatible with
     *         the {@code Comparable.compareTo} method
     */
    private int compareIdentifiers(String ident1, String ident2) {
        if (isInt(ident1) && isInt(ident2)) {
            return Integer.parseInt(ident1) - Integer.parseInt(ident2)
        } else {
            return ident1.compareTo(ident2)
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
        if(idents) {
            return Arrays.hashCode(idents)
        } else {
            return 0
        }
    }
}
