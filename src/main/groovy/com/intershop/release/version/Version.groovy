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
 * This is the version object with all extensions
 * for feature branches. It is based on SemVer and
 * extended for versioning schema with four digits.
 */
@CompileStatic
class Version implements Comparable<Version> {

    /**
     * The normal version.
     */
    private final NormalVersion normal

    /**
     * The branch data
     */
    private final MetadataVersion branchData

    /**
     * The build data
     */
    private final MetadataVersion buildData

    /**
     * Version extension
     */
    private final VersionExtension extension

    /**
     * A separator that separates the pre-release
     * version from the normal version.
     */
    public static final String METADATA_SEPARATOR = "-"

    /**
     * A mutable builder for the immutable {@code Version} class.
     */
    public static class Builder {

        /**
         * The normal version string.
         */
        private String normal

        /**
         * The build version string.
         */
        private String branchData

        /**
         * The branch metadata string.
         */
        private String buildData

        /**
         * Format of the version - four or three digits
         */
        private VersionType type

        /**
         * Version extension for the version object
         */
        private VersionExtension extension

        /**
         * Constructs a {@code Builder} instance.
         *
         * @param fourDigits - format of version, four or three digits (default value is true)
         */
        public Builder(VersionType type = VersionType.threeDigits) {
            this.type = type
            this.normal = type == VersionType.threeDigits ? '1.0.0' : '1.0.0.0'
            this.extension = VersionExtension.NONE
        }

        /**
         * Constructs a {@code Builder} instance with the
         * string representation of the normal version.
         *
         * @param normal the string representation of the normal version
         * @param fourDigits - format of version, four or three digits (default value is true)
         */
        public Builder(String normal, VersionType type = VersionType.threeDigits) {
            this.type = type
            this.normal = normal
            this.extension = VersionExtension.NONE
        }

        /**
         * Sets the normal version.
         *
         * @param normal the string representation of the normal version
         * @return this builder instance
         */
        public Builder setNormalVersion(String normal, VersionType type = VersionType.threeDigits) {
            this.type = type
            this.normal = normal;
            return this
        }

        /**
         * Sets the pre-release version.
         *
         * @param buildData the string representation of the pre-release version
         * @return this builder instance
         */
        public Builder setBuildMetadata(String buildData) {
            this.buildData = buildData
            return this
        }

        /**
         * Sets the build metadata.
         *
         * @param prefix the string representation of the build metadata
         * @return this builder instance
         */
        public Builder setBranchMetadata(String branchData) {
            this.branchData = branchData
            return this
        }

        /**
         * Sets the version extension
         *
         * @param string represention of the version extension
         * @return this builder instance
         */
        public Builder setVersionExtension(String extension) {
            this.extension = extension
            return this
        }

        /**
         * Builds a {@code Version} object.
         *
         * @return a newly built {@code Version} instance
         */
        public Version build() {

            StringBuilder sb = new StringBuilder();
            if (normal) {
                sb.append(normal);
            }
            if (branchData) {
                sb.append(branchData).append(METADATA_SEPARATOR)
            }
            if (buildData) {
                sb.append(METADATA_SEPARATOR).append(buildData)
            }
            if (extension) {
                sb.append(METADATA_SEPARATOR).append(extension)
            }
            return VersionParser.parseVersion(sb.toString())
        }
    }

    /**
     * Constructs a {@code Version} instance with the normal
     * version, the pre-release version and the build metadata.
     *
     * @param normal the normal version
     * @param branch data the branch acronym (default value is MetadataVersion.NULL)
     * @param build data the build metadata (default value is MetadataVersion.NULL)
     */
    Version(NormalVersion normal, MetadataVersion branchData = MetadataVersion.NULL, MetadataVersion buildData = MetadataVersion.NULL, VersionExtension extension = VersionExtension.NONE) {
        this.normal     = normal
        this.branchData = branchData
        this.buildData  = buildData
        this.extension  = extension
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
        return VersionParser.parseVersion(version)
    }

    /**
     * Creates a new instance of {@code Version} as a
     * result of parsing the specified version string.
     *
     * @param version the version string with a partially version string to parse
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public static Version forString(String version, VersionType type = VersionType.threeDigits) {
        return VersionParser.parseVersion(version, type)
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
    public static Version forIntegers(int major, VersionType type = VersionType.threeDigits) {
        if(type == VersionType.fourDigits) {
            return new Version(new NormalVersion(major, 0, 0, 0))
        } else {
            return new Version(new NormalVersion(major, 0, 0))
        }
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
    public static Version forIntegers(int major, int minor, VersionType type = VersionType.threeDigits) {
        if(type == VersionType.fourDigits) {
            return new Version(new NormalVersion(major, minor, 0, 0))
        } else {
            return new Version(new NormalVersion(major, minor, 0))
        }
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
    public static Version forIntegers(int major, int minor, int patch, VersionType type = VersionType.threeDigits) {
        if(type == VersionType.fourDigits) {
            return new Version(new NormalVersion(major, minor, patch, 0))
        } else {
            return new Version(new NormalVersion(major, minor, patch))
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
    public Version incrementMajorVersion(String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branchData) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(buildData) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }
        return new Version(normal.incrementMajor(), branch, build, extension)
    }

    /**
     * Increments the minor version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementMinorVersion(String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branch) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(build) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }
        return new Version(normal.incrementMinor(), branch, build, extension)
    }

    /**
     * Increments the patch version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementPatchVersion(String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branch) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(build) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }
        return new Version(normal.incrementPatch(), branch, build, extension)
    }

    /**
     * Increments the hotfix version.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementHotfixVersion(String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branch) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(build) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }
        return new Version(normal.incrementHotfix(), branch, build, extension)
    }

    /**
     * Increment latest non 0 digit
     * return a new instance of the {@code Version} class
     */
    public Version incrementLatest(DigitPos pos = normal.versionType == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX,
                                   String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branchData) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(buildData) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }

        if(build != MetadataVersion.NULL) {
            return new Version(normal, branch, build.increment(), extension)
        }
        return new Version(normal.incrementLatest(pos), branch, build, extension)
    }

    /**
     * Increment special pos
     * return a new instance of the {@code Version} class
     */
    public Version incrementVersion(DigitPos increment = normal.versionType == VersionType.threeDigits ? DigitPos.PATCH : DigitPos.HOTFIX,
                                    String branchData = '', String buildData = '', String versionExtension = '') {
        MetadataVersion branch = MetadataVersion.NULL
        MetadataVersion build = MetadataVersion.NULL
        VersionExtension extension = VersionExtension.NONE

        if(branchData) {
            branch = VersionParser.parseBranchData(branchData)
        } else {
            branch = this.branchData
        }
        if(buildData) {
            build = VersionParser.parseBuildData(buildData)
        } else {
            build = this.buildData
        }
        if(versionExtension) {
            extension = VersionParser.parseVersionExtension(versionExtension)
        } else {
            extension = this.extension
        }

        if(build != MetadataVersion.NULL) {
            return new Version(normal, branch, build.increment(), extension)
        }
        return new Version(normal.incrementVersion(increment), branch, build, extension)
    }

    /**
     * Increments the build metadata.
     *
     * @return a new instance of the {@code Version} class
     */
    public Version incrementBuildMetadata() {
        return new Version(normal, branchData, buildData.increment())
    }

    /**
     * Sets the pre-release version.
     *
     * @param prefix the pre-release version to set
     * @return a new instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public Version setBranchMetadata(String branchData) {
        return new Version(normal, VersionParser.parseBranchData(branchData), buildData)
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
     * @param Version extension
     * @return a new Instance of the {@code Version} class
     * @throws IllegalArgumentException if the input string is {@code NULL} or an unsupported extension
     */
    public Version setVersionExtension(String extension) {
        return new Version(normal, branchData, buildData, VersionParser.parseVersionExtension(extension))
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
            return normal.getHotfix()
        } else {
            throw  new UnsupportedOperationException('This normal version does not support four digits')
        }
    }


    /**
     * Returns the representation of the normal version.
     *
     * @return the representation of the normal version
     */
    public NormalVersion getNormalVersion() {
        return normal
    }

    /**
     * Returns the string representation of the pre-release version.
     *
     * @return the string representation of the pre-release version
     */
    public String getBuildMetadata() {
        return buildData.toString()
    }

    /**
     * Returns the string representation of the build metadata.
     *
     * @return the string representation of the build metadata
     */
    public String getBranchMetadata() {
        return branchData.toString()
    }

    public String getVersionExtension() {
        return extension.toString()
    }

    /**
     * Returns a shortened version for special reasons
     *
     * @return the shortened version string
     */
    public String toStringFor(int firstdigits) {
        String branchData = getBranchMetadata() ? "${METADATA_SEPARATOR}${getBranchMetadata()}" : ''
        String buildData = getBuildMetadata() ? "${METADATA_SEPARATOR}${getBuildMetadata()}" : ''
        String versionExtension = getVersionExtension() ? "${METADATA_SEPARATOR}${getVersionExtension()}" : ''

        String result = "${normal.toStringFor(firstdigits)}${branchData ?: ''}${buildData ?: ''}${versionExtension ?: ''}"

        return result
    }

    /**
     * Returns a new instance of the version object
     */
    public Version clone() {
        return new Version(this.normalVersion.clone(), this.branchData.clone(), this.buildData.clone(), this.extension)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        String branchData = getBranchMetadata() ? "${METADATA_SEPARATOR}${getBranchMetadata()}" : ''
        String buildData = getBuildMetadata() ? "${METADATA_SEPARATOR}${getBuildMetadata()}" : ''
        String versionExtension = getVersionExtension() ? "${METADATA_SEPARATOR}${getVersionExtension()}" : ''

        String result = "${normal.toString()}${branchData ?: ''}${buildData ?: ''}${versionExtension ?: ''}"

        return result
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
        return compareTo((Version) other) == 0
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + normal.hashCode()
        hash = 97 * hash + branchData.hashCode()
        hash = 97 * hash + buildData.hashCode()
        hash = 97 * hash + extension.hashCode()

        return hash
    }

    /**
     * Compares this version to the other version.
     *
     * @param other the other version to compare to
     * @return a negative integer, zero or a positive integer if this version
     *         is less than, equal to or greater the the specified version
     */
    @Override
    public int compareTo(Version other) {
        int result = normal.compareTo(other.normal)
        if (result == 0) {
            result = branchData.compareTo(other.branchData)
        }
        if (result == 0) {
            result = buildData.compareTo(other.buildData)
        }
        if(result == 0) {
            result = extension.compareTo(other.extension)
        }
        return result
    }
}
