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
package com.intershop.release.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * <p>Version parser</p>
 * <p>Parses a version from a string.</p>
 */
public class VersionParser {
    /**
     * The input string of this parser
     */
    private final String input;

    /**
     * Constructs a {@code VersionParser} instance
     * with the input string to parse.
     *
     * @param input the input string to parse
     * @throws IllegalArgumentException if the input string is {@code NULL} or empty
     */
    public VersionParser(String input) {
        if(input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string is NULL or empty");
        }
        this.input = input;
    }

    public Version getVersion() {
        return getVersion(VersionType.threeDigits);
    }

    public Version getVersion(VersionType type) {
        if(input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string is NULL or empty");
        } else {
            return parseVersion(input, type);
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
    public static Version parseVersion(String inputStr) {
        return parseVersion(inputStr, VersionType.threeDigits);
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
    public static Version parseVersion(String inputStr, @Nonnull VersionType type) {
        String[] parsedVersions = new String[3];

        int i = inputStr.indexOf('-');
        String v = i > 0 ? inputStr.substring(0, i) : inputStr;
        String metadata = i > 0 ? inputStr.substring(i + 1) : "";

        if(Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+").matcher(v).matches()) {
            type = VersionType.fourDigits;
        }

        Pattern versionMatcher = (type == VersionType.threeDigits) ? Pattern.compile("^\\d+\\.?\\d*\\.?\\d*$") : Pattern.compile("^\\d+\\.?\\d*\\.?\\d*\\.?\\d*$");

        if(versionMatcher.matcher(v).matches()) {
            parsedVersions[0] = v;
            parsedVersions[1] = "";
            parsedVersions[2] = "";
        } else {
            throw new ParserException("No valid version found in "+ inputStr+ "!");
        }

        VersionExtension extension = VersionExtension.NONE;

        if(! metadata.isEmpty()) {
            String testStr = metadata.toLowerCase();
            String metadataStr = metadata;

            if(testStr.endsWith("snapshot")) {
                if(testStr.equals("snapshot")) {
                    extension = VersionExtension.SNAPSHOT;
                    metadataStr = "";
                } else if(testStr.endsWith("-snapshot")) {
                    extension = VersionExtension.SNAPSHOT;
                    metadataStr = metadata.substring(0, metadata.length() - "-snapshot".length());
                }
            } else if(testStr.endsWith("local")) {

                if(testStr.equals("local")) {
                    extension = VersionExtension.LOCAL;
                    metadataStr = "";
                } else if(testStr.endsWith("-local")) {
                    extension = VersionExtension.LOCAL;
                    metadataStr = metadata.substring(0, metadata.length() - "-local".length());
                }
            }
            if(! metadataStr.isEmpty()) {
                Matcher buildInfoGroup = Pattern.compile("([A-Za-z]+\\.?[\\d]+$)").matcher(metadataStr);
                if (buildInfoGroup.find()) {
                    parsedVersions[2] = buildInfoGroup.group(1);
                    String removeTail = buildInfoGroup.replaceAll("");
                    if(removeTail != null && ! removeTail.isEmpty()) {
                        parsedVersions[1] = metadataStr.replace("-" + parsedVersions[2], "");
                    }
                } else {
                    parsedVersions[1] = metadataStr;
                }
            }
        }

        return new Version(parseNormalVersion(parsedVersions[0], type),
                           parseBranchData(parsedVersions[1]),
                           parseBuildData(parsedVersions[2]),
                           extension,
                           inputStr);
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
    public static NormalVersion parseNormalVersion(String versionStr, VersionType type) {
        ArrayList<String> vnumbers = new ArrayList<>(Arrays.asList(versionStr.split("\\.")));

        int start = Math.min(4, vnumbers.size());

        for (int i = start; i <= 4; i++) {
            vnumbers.add("0");
        }

        if(type == VersionType.fourDigits) {
            return new NormalVersion(parseDigit(vnumbers.get(0), versionStr), parseDigit(vnumbers.get(1), versionStr),
                    parseDigit(vnumbers.get(2), versionStr), parseDigit(vnumbers.get(3), versionStr));
        } else {
            return new NormalVersion(parseDigit(vnumbers.get(0), versionStr), parseDigit(vnumbers.get(1), versionStr),
                    parseDigit(vnumbers.get(2), versionStr));
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
    public static MetadataVersion parseBranchData(String branchData) {
        return parseMetadataVersion(branchData);
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
    public static MetadataVersion parseBuildData(String buildData) {
        return parseMetadataVersion(buildData);
    }

    /**
     * Parses the version extension.
     *
     * @param extension the version extension.
     * @return a valid version extension
     */
    public static VersionExtension parseVersionExtension(String extension) {
        return VersionExtension.valueOf(extension);
    }

    public static MetadataVersion parseMetadataVersion(String metadataInput) {
        MetadataVersion mdVersion = MetadataVersion.NULL;

        if(metadataInput != null && ! metadataInput.isEmpty()) {
            List<String> identifiers = new ArrayList<>();

            Matcher metadataMatcher = Pattern.compile("^(?<name>[A-Za-z]+\\.?)(?<number>[\\d]+)$").matcher(metadataInput);
            if(metadataMatcher.matches()) {
                identifiers.add(metadataMatcher.group("name"));
                int identNo = new Integer(metadataMatcher.group("number"));
                identifiers.add(Integer.toString(identNo));
            } else {
                identifiers.add(metadataInput);
            }
            if(identifiers.size() > 0) {
                return new MetadataVersion(identifiers.toArray(new String[0]));
            }
        }
        return mdVersion;
    }

    private static int parseDigit(String digit, String input) {
        if(digit == null || digit.isEmpty()) {
            throw new ParserException("One part of the version is empty");
        }
        if(digit.length() > 1 && digit.startsWith("0")) {
            throw new ParserException("Numeric identifier MUST NOT contain leading zeroes (" + digit + " in " + input + ")");
        }
        try {
            return Integer.parseInt(digit);
        } catch (NumberFormatException nfe) {
            throw new ParserException("It was not possible to parse " + digit + " of " + input, nfe);
        }
    }
}
