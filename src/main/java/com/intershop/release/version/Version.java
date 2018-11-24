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

import com.google.common.base.Strings;

/**
 * This is the version object with all additional extensions for feature branches and milestones.
 * It is based on SemVer and extended for versioning schema with four digits.
 */
public class Version implements Comparable<Version> {

    /**
     * The normal version.
     */
    private final NormalVersion normal;

    /**
     * The branch data
     */
    private final MetadataVersion branchData;

    /**
     * The build data
     */
    private final MetadataVersion buildData;

    /**
     * Version extension
     */
    private final VersionExtension extension;

    /**
     * Original Version-String
     */
    private final String orgString;

    /**
     * A separator that separates the pre-release
     * version from the normal version.
     */
    public static final String METADATA_SEPARATOR = "-";

    /**
     * A mutable builder for the immutable {@code Version} class.
     */
    public static class Builder {

        /**
         * The normal version string.
         */
        private NormalVersion normal;

        /**
         * The build version string.
         */
        private MetadataVersion branchData;

        /**
         * The branch metadata string.
         */
        private MetadataVersion buildData;

        /**
         * Format of the version - four or three digits
         */
        private VersionType type;

        /**
         * Version extension for the version object
         */
        private VersionExtension extension;

        /**
         * Constructs a {@code Builder} instance.
         */
        public Builder() {
            this(VersionType.threeDigits);
        }

        /**
         * Constructs a {@code Builder} instance.
         *
         * @param type - format of version, four or three digits (default value is true)
         */
        public Builder(VersionType type) {
            this((type == VersionType.threeDigits) ? new NormalVersion(1,0,0) : new NormalVersion(1,0,0,0), type);
        }

        /**
         * Constructs a {@code Builder} instance with the
         * string representation of the normal version.
         *
         * @param normal the string representation of the normal version
         * @param type - format of version, four or three digits (default value is true)
         */
        public Builder(NormalVersion normal, VersionType type) {
            this.type = type;
            this.normal = normal;
            this.extension = VersionExtension.NONE;
        }

        /**
         * Sets the normal version.
         *
         * @param normal the string representation of the normal version
         * @return this builder instance
         */
        public Builder setNormalVersion(NormalVersion normal) {
            this.type = VersionType.threeDigits;
            this.normal = normal;
            return this;
        }

        /**
         * Sets the normal version.
         *
         * @param normal the string representation of the normal version
         * @param type the version type
         * @return this builder instance
         */
        public Builder setNormalVersion(NormalVersion normal, VersionType type) {
            this.type = type;
            this.normal = normal;
            return this;
        }

        /**
         * Sets the pre-release version.
         *
         * @param buildData the string representation of the pre-release version
         * @return this builder instance
         */
        public Builder setBuildMetadata(MetadataVersion buildData) {
            this.buildData = buildData;
            return this;
        }

        /**
         * Sets the build metadata.
         *
         * @param branchData the string representation of the build metadata
         * @return this builder instance
         */
        public Builder setBranchMetadata(MetadataVersion branchData) {
            this.branchData = branchData;
            return this;
        }

        /**
         * Sets the version extension
         *
         * @param extension representation of the version extension
         * @return this builder instance
         */
        public Builder setVersionExtension(VersionExtension extension) {
            this.extension = extension;
            return this;
        }

        /**
         * Builds a {@code Version} object.
         *
         * @return a newly built {@code Version} instance
         */
        public Version build() {
            return new Version(normal, branchData, buildData, extension);
        }
    }

    /**
     * Constructs a {@code Version} instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     * @param branchData the branch acronym (default value is MetadataVersion.NULL)
     * @param buildData the build metadata (default value is MetadataVersion.NULL)
     * @param extension the extension
     */
    public Version(NormalVersion normal, MetadataVersion branchData, MetadataVersion buildData, VersionExtension extension, String orgString) {
        this.normal     = normal;
        this.branchData = branchData;
        this.buildData  = buildData;
        this.extension  = extension;
        this.orgString  = orgString;
    }

    /**
     * Constructs a {@code Version} instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     */
    public Version(NormalVersion normal) {
        this(normal, MetadataVersion.NULL, MetadataVersion.NULL, VersionExtension.NONE, "");
    }

    /**
     * Constructs a {@code Version} instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     * @param branchData the branch acronym (default value is MetadataVersion.NULL)
     * @param buildData the build metadata (default value is MetadataVersion.NULL)
     */
    public Version(NormalVersion normal, MetadataVersion branchData, MetadataVersion buildData) {
        this(normal, branchData, buildData, VersionExtension.NONE, "");
    }

    /**
     * Constructs a {@code Version} instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     * @param branchData the branch acronym (default value is MetadataVersion.NULL)
     * @param buildData the build metadata (default value is MetadataVersion.NULL)
     * @param extension the extension
     */
    public Version(NormalVersion normal, MetadataVersion branchData, MetadataVersion buildData, VersionExtension extension) {
        this(normal, branchData, buildData, extension, "");
    }

    /**
     * Creates a new instance of {@code Version} as a
     * result of parsing the specified version string.
     *
     * @param version the version string to parse
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public static Version valueOf(String version) {
        return VersionParser.parseVersion(version);
    }

    /**
     * Creates a new instance of {@code Version} as a
     * result of parsing the specified version string.
     *
     * @param version the version string with a partially version string to parse
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public static Version forString(String version) {
        return forString(version, VersionType.threeDigits);
    }

    /**
     * Creates a new instance of {@code Version} as a
     * result of parsing the specified version string.
     *
     * @param version the version string with a partially version string to parse
     * @param type the version type
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public static Version forString(String version, VersionType type) {
        return VersionParser.parseVersion(version, type);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major) {
        return forIntegers(major, VersionType.threeDigits);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param type the version type
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major, VersionType type) {
        return forIntegers(major, 0, type);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major, int minor) {
        return forIntegers(major, minor, VersionType.threeDigits);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param type the version type
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major, int minor, VersionType type) {
        return forIntegers(major, minor, 0, type);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major, int minor, int patch) {
        return forIntegers(major, minor, patch, VersionType.threeDigits);
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param type the version  type
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     * @since 0.7.0
     */
    public static Version forIntegers(int major, int minor, int patch, VersionType type) {
        if(type == VersionType.fourDigits) {
            return forIntegers(major, minor, patch, 0);
        } else {
            return new Version(new NormalVersion(major, minor, patch));
        }
    }

    /**
     * Creates a new instance of {@code Version}
     * for the specified version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param hotfix the hotfix version number
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if a negative integer is passed
     */
    public static Version forIntegers(int major, int minor, int patch, int hotfix) {
        return new Version(new NormalVersion(major, minor, patch, hotfix));
    }

    /**
     * Increments the major version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMajorVersion() {
        return incrementMajorVersion("", "", "");
    }

    /**
     * Increments the major version.
     *
     * @param branchData branch metadata
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMajorVersion(String branchData) {
        return incrementMajorVersion(branchData, "", "");
    }

    /**
     * Increments the major version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMajorVersion(String branchData, String buildData, String versionExtension) {
        return increment(normal.incrementMajor(), branchData, buildData, versionExtension, false);
    }

    /**
     * Increments the minor version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMinorVersion() {
        return incrementMinorVersion("", "", "");
    }

    /**
     * Increments the minor version.
     *
     * @param branchData branch metadata
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMinorVersion(String branchData) {
        return incrementMinorVersion(branchData, "", "");
    }

    /**
     * Increments the minor version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMinorVersion(String branchData, String buildData, String versionExtension) {
        return increment(normal.incrementMinor(), branchData, buildData, versionExtension, false);
    }

    /**
     * Increments the patch version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementPatchVersion() {
        return incrementPatchVersion("", "", "");
    }

    /**
     * Increments the patch version.
     *
     * @param branchData branch metadata
     * @return a new instance of the {@code Version} class
     */
    public Version incrementPatchVersion(String branchData) {
        return incrementPatchVersion(branchData, "", "");
    }

    /**
     * Increments the patch version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementPatchVersion(String branchData, String buildData, String versionExtension) {
        return increment(normal.incrementPatch(), branchData, buildData, versionExtension, false);
    }

    /**
     * Increments the hotfix version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementHotfixVersion() {
        return incrementHotfixVersion("", "", "");
    }

    /**
     * Increments the hotfix version.
     *
     * @param branchData branch metadata
     * @return a new instance of the {@code Version} class
     */
    public Version incrementHotfixVersion(String branchData) {
        return incrementHotfixVersion(branchData, "", "");
    }

    /**
     * Increments the hotfix version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementHotfixVersion(String branchData, String buildData, String versionExtension) {
        return increment(normal.incrementHotfix(), branchData, buildData, versionExtension, false);
    }

    /**
     * Increment latest non 0 digit
     * return a new instance of the {@code Version} class
     */
    public Version incrementLatest() {
        return incrementLatest(normal.getVersionType() == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX,
                    "",
                     "",
                "");
    }

    /**
     * Increment latest non 0 digit
     * return a new instance of the {@code Version} class
     */
    public Version incrementLatest(DigitPos pos, String branchData, String buildData, String versionExtension) {
         return increment(normal.incrementLatest(pos), branchData, buildData, versionExtension, true);
    }

    /**
     * Increment given position
     * @return a new instance of the {@code Version} class
     */
    public Version incrementVersion() {
        return incrementVersion(normal.getVersionType() == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX);
    }

    /**
     * Increment given position
     * @param increment the position to increment
     * @return a new instance of the {@code Version} class
     *
     */
    public Version incrementVersion(DigitPos increment) {
        return incrementVersion(increment,
                "",
                "",
                "");
    }

    /**
     * Increment given position
     * @return a new instance of the {@code Version} class
     */
    public Version incrementVersion(DigitPos increment, String branchData, String buildData, String versionExtension) {
        return increment(normal.incrementVersion(increment), branchData, buildData, versionExtension, true);
    }

    /**
     * Increments the build metadata.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementBuildMetadata() {
        return new Version(normal, branchData, buildData.increment());
    }

    private Version increment(NormalVersion normalNew, String branchDataNew, String buildDataNew, String extensionNew, boolean incrementBuild) {
        MetadataVersion branch;
        MetadataVersion build;
        VersionExtension extension;

        if(!Strings.isNullOrEmpty(branchDataNew)) {
            branch = VersionParser.parseBranchData(branchDataNew);
        } else {
            branch = this.branchData;
        }
        if(!Strings.isNullOrEmpty(buildDataNew)) {
            build = VersionParser.parseBuildData(buildDataNew);
        } else {
            build = this.buildData;
        }
        if(!Strings.isNullOrEmpty(extensionNew)) {
            extension = VersionParser.parseVersionExtension(extensionNew);
        } else {
            extension = this.extension;
        }

        if (incrementBuild && !MetadataVersion.isEmpty(build)) {
            return new Version(normal, branch, build.increment(), extension);
        }

        return new Version(normalNew, branch, build, extension);
    }

    /**
     * Sets the pre-release version.
     *
     * @param branchData the pre-release version to set
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public Version setBranchMetadata(String branchData) {
        return new Version(normal, VersionParser.parseBranchData(branchData), buildData);
    }

    /**
     * Sets the build metadata.
     *
     * @param buildData the build metadata to set
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public Version setBuildMetadata(String buildData) {
        return new Version(normal, branchData, VersionParser.parseBuildData(buildData));
    }

    /**
     * Sets extension for varions.
     *
     * @param extension extension
     * @return a new Instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or an unsupported extension
     */
    public Version setVersionExtension(String extension) {
        return new Version(normal, branchData, buildData, VersionParser.parseVersionExtension(extension));
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    public int getMajorVersion() {
        return normal.getMajor();
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    public int getMinorVersion() {
        return normal.getMinor();
    }

    /**
     * Returns the patch version number.
     *
     * @return the patch version number
     */
    public int getPatchVersion() {
        return normal.getPatch();
    }

    /**
     * Returns the hotfix version number.
     *
     * @return the hotfix version number
     */
    public int getHotfixVersion() {
        if(normal.getVersionType() == VersionType.fourDigits ) {
            return normal.getHotfix();
        } else {
            throw  new UnsupportedOperationException("This normal version does not support four digits");
        }
    }

    /**
     * Returns the representation of the normal version.
     *
     * @return the representation of the normal version
     */
    public NormalVersion getNormalVersion() {
        return normal;
    }

    /**
     * Returns the string representation of the pre-release version.
     *
     * @return the string representation of the pre-release version
     */
    public MetadataVersion getBuildMetadata() {
        return buildData;
    }

    /**
     * Returns the string representation of the build metadata.
     *
     * @return the string representation of the build metadata
     */
    public MetadataVersion getBranchMetadata() {
        return branchData;
    }

    public VersionExtension getVersionExtension() {
        return extension;
    }

    /**
     * Returns a shortened version for special reasons
     *
     * @return the shortened version string
     */
    public String toStringFor(int firstDigits) {
        return normal.toStringFor(firstDigits) + getSuffix();
    }

    /**
     * Returns the original input str if available
     * otherwise it returns toString
     */
    public String toStringFromOrg() {
        if(!Strings.isNullOrEmpty(orgString)) {
            return orgString;
        } else {
            return this.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return normal.toString() + getSuffix();
    }

    /**
     * @return the suffix string for the version
     */
    public String getSuffix() {
        String versionBranch = !MetadataVersion.isEmpty(getBranchMetadata()) ? METADATA_SEPARATOR + getBranchMetadata() : "";
        String versionBuild =  !MetadataVersion.isEmpty(getBuildMetadata()) ? METADATA_SEPARATOR + getBuildMetadata() : "";
        String versionExtension = !VersionExtension.isEmpty(getVersionExtension()) ? METADATA_SEPARATOR + getVersionExtension() : "";
        return versionBranch + versionBuild + versionExtension;
    }

    /**
     * Returns a new instance of the version object
     */
    public Version clone() {
        return new Version(this.normal.clone(), this.branchData.clone(), this.buildData.clone(), this.extension, this.orgString);
    }

    /**
     * Checks if this version equals the other version.
     *
     * The comparison is done by the {@code Version.compareTo} method.
     *
     * @param other the other version to compare to
     * @return {@code true} if this version equals the other version
     *         or {@code false} otherwise
     * @see #compareTo(Version other)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Version)) {
            return false;
        }
        return compareTo((Version) other) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + normal.hashCode();
        hash = 97 * hash + branchData.hashCode();
        hash = 97 * hash + buildData.hashCode();
        hash = 97 * hash + extension.hashCode();

        return hash;
    }

    /**
     * Compares this version to the other version.
     *
     * @param other the other version to compare to
     * @return a negative integer, zero or a positive integer if this version
     *         is less than, equal to or greater the the specified version
     */
    @Override
    public int compareTo(@Nonnull Version other) {
        int result = normal.compareTo(other.normal);
        if (result == 0) {
            result = branchData.compareTo(other.branchData);
        }
        if (result == 0) {
            result = buildData.compareTo(other.buildData);
        }
        if(result == 0) {
            result = extension.compareTo(other.extension);
        }
        return result;
    }
}