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

import java.util.regex.Matcher

/**
 * <p>Version parser</p>
 * <p>Parses a version from a string.</p>
 */
@CompileStatic
class VersionParser {

    /**
     * The input string of this parser
     */
    private final String input

    /**
     * Constructs a {@code VersionParser} instance
     * with the input string to parse.
     *
     * @param input the input string to parse
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    VersionParser(String input) {
        if(! input) {
            throw new IllegalArgumentException("Input string is NULL or empty")
        }
        this.input = input
    }

    Version getVersion(VersionType type = VersionType.threeDigits) {
        if(! input) {
            throw new IllegalArgumentException("Input string is NULL or empty")
        } else {
            return parseVersion(input, type)
        }
    }

    /**
     * Parse the (@literal <partial version> non-terminal.
     *
     * {@literal
     * <valid version> ::= <version core>
     *                  |  <version core> "-" <builddata>
     *                  |  <version core> "-" <branchdata> "-" <builddata>
     * }
     * </pre>
     *
     * @return a valid version object
     */
     static Version parseVersion(String inputStr, VersionType type = VersionType.threeDigits) {
        String[] parsedVersions = new String[3]

        int i = inputStr.indexOf('-')
        String v = i > 0 ? inputStr.substring(0, i) : inputStr
        String metadata = i > 0 ? inputStr.substring(i + 1) : ''

        if(v =~ /^\d+\.\d+\.\d+\.\d+/) {
            type = VersionType.fourDigits
        }

        def versionMatcher = type == VersionType.threeDigits ? /^\d+\.?\d*\.?\d*$/ : /^\d+\.?\d*\.?\d*\.?\d*$/

        if(v =~ versionMatcher) {
            parsedVersions[0] = v
            parsedVersions[1] = ''
            parsedVersions[2] = ''
        } else {
            throw new ParserException("No valid version found in ${inputStr}!")
        }

        VersionExtension extension = VersionExtension.NONE

        if(metadata) {
            String testStr = metadata.toLowerCase()
            String metadataStr = metadata

            if(testStr.endsWith('snapshot')) {
                if(testStr == 'snapshot') {
                    extension = VersionExtension.SNAPSHOT
                    metadataStr = ''
                } else if(testStr.endsWith('-snapshot')) {
                    extension = VersionExtension.SNAPSHOT
                    metadataStr = metadata.substring(0, metadata.length() - '-snapshot'.length())
                }
            } else if(testStr.endsWith('local')) {

                if(testStr == 'local') {
                    extension = VersionExtension.LOCAL
                    metadataStr = ''
                } else if(testStr.endsWith('-local')) {
                    extension = VersionExtension.LOCAL
                    metadataStr = metadata.substring(0, metadata.length() - '-local'.length())
                }
            }
            if(metadataStr) {
                Matcher buildInfoGroup = (metadataStr =~ /([A-za-z]+\.?[\d]+$)/)
                if (buildInfoGroup && buildInfoGroup.hasGroup()) {
                    parsedVersions[2] = buildInfoGroup.group(1)
                    if (metadataStr - parsedVersions[2]) {
                        parsedVersions[1] = metadataStr - "-${parsedVersions[2]}"
                    }
                } else {
                    parsedVersions[1] = metadataStr
                }
            }
        }

        return new Version(parseVersionStr(parsedVersions[0], type), parseBranchData(parsedVersions[1]), parseBuildData(parsedVersions[2]), extension, inputStr)
    }

    /**
     * Parses the {@literal <version core>} non-terminal.
     *
     * <pre>
     * {@literal
     * <version core> ::= <major> "." <minor> "." <patch> ("." <hotfix>)
     * }
     * </pre>
     *
     * @return a valid normal version object
     */
    private static NormalVersion parseVersionStr(String versionStr, VersionType type) {
        List<String> vnumbers = []
        vnumbers.addAll(versionStr.split('\\.'))
        int start = Math.min(4, vnumbers.size())

        (new Integer(start)).upto(4) {
            vnumbers.add('0')
        }

        if(type == VersionType.fourDigits) {
            return new NormalVersion(parseDigit(vnumbers[0], versionStr), parseDigit(vnumbers[1], versionStr),
                    parseDigit(vnumbers[2], versionStr), parseDigit(vnumbers[3], versionStr))
        } else {
            return new NormalVersion(parseDigit(vnumbers[0], versionStr), parseDigit(vnumbers[1], versionStr),
                    parseDigit(vnumbers[2], versionStr))
        }
    }

    /**
     * Parses the {@literal <prefix>} non-terminal.
     *
     * <pre>
     * {@literal
     * <prefix> ::= <dot-separated prefix identifiers>
     *
     * <dot-separated prefix identifier> ::= <prefix identifier>
     *    | <prefix identifier> <dot-separated prefix identifiers>
     * }
     * </pre>
     *
     * @return a valid prefix version object
     */
    static MetadataVersion parseBranchData(String branchData) {
        return parseMetadataVersion(branchData)
    }

    /**
     * Parses the {@literal <suffix>} non-terminal.
     *
     * <pre>
     * {@literal
     * <prefix> ::= <dot-separated suffix identifiers>
     *
     * <dot-separated suffix identifier> ::= <suffix identifier>
     *    | <suffix identifier> "." <dot-separated suffix identifiers>
     * }
     * </pre>
     *
     * @return a valid suffix version object
     */
    static MetadataVersion parseBuildData(String buildData) {
        return parseMetadataVersion(buildData)
    }

    /**
     * Parses the version extension.
     *
     * @param extension
     * @return a valid version extension
     */
    static VersionExtension parseVersionExtension(String extension) {
        return VersionExtension.valueOf(extension)
    }

    private static MetadataVersion parseMetadataVersion(String metadataInput) {
        MetadataVersion mdVersion = MetadataVersion.NULL

        if(metadataInput) {
            List idents = []

            Matcher number = (metadataInput =~ /[A-za-z]+\.?([\d]+)$/)
            if(number.size() > 0) {
                idents.add(metadataInput - (number[0] as List)[1])
                idents.add((number[0] as List)[1])
                int identNo = parseDigit((number[0] as List)[1].toString(), metadataInput)
                idents[idents.size() - 1] = Integer.toString(identNo)
            } else {
                idents.add(metadataInput)
            }
            if(idents.size() > 0) {
                return new MetadataVersion(idents as String[])
            }
        }
        return mdVersion
    }

    private static int parseDigit(String digit, String input) {
        if(!digit) {
            throw new ParserException('One part of the version is empty')
        }
        if(digit.length() > 1 && digit.startsWith('0')) {
            throw new ParserException("Numeric identifier MUST NOT contain leading zeroes (${digit} in ${input})")
        }
        try {
            return Integer.parseInt(digit)
        } catch (NumberFormatException nfe) {
            throw new ParserException("It was not possible to parse ${digit} of ${input}", nfe)
        }
    }
}
