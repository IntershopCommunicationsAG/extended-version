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

import spock.lang.Specification

class VersionSpec extends Specification {

    def 'Check version builder - default'() {
        when:
        Version.Builder b = new Version.Builder()
        Version v = b.build()

        then:
        '1.0.0' == v.toString()
    }

    def 'Test clone'() {
        setup:
        Version v = Version.valueOf('1.0.0-fb-rc.1')

        when:
        Version clone = v.clone().setVersionExtension('SNAPSHOT')

        then:
        '1.0.0-fb-rc.1' == v.toString()
        '1.0.0-fb-rc.1-SNAPSHOT' == clone.toString()
    }

    def 'Test increment build extension'() {
        setup:
        Version v = Version.valueOf('1.0.0-fb-rc.1')

        when:
        Version clone = v.incrementLatest()

        then:
        '1.0.0-fb-rc.1' == v.toString()
        '1.0.0-fb-rc.2' == clone.toString()
    }

    def 'Test increment references'() {
        setup:
        Version v = Version.valueOf(versionStr)

        when:
        Version vInc = v.incrementVersion(inc)

        then:
        v.toString() == versionStr
        vInc.toString() == versionIncStr

        where:
        versionStr    | inc             | versionIncStr
        '1.0.0'       | DigitPos.PATCH  | '1.0.1'
        '1.1.1'       | DigitPos.MINOR  | '1.2.0'
        '1.1.1'       | DigitPos.MAJOR  | '2.0.0'
        '1.1.1-fb'    | DigitPos.PATCH  | '1.1.2-fb'
        '1.1.1-rc1'   | DigitPos.PATCH  | '1.1.1-rc2'
    }

    def 'Check version builder - four digits'() {
        when:
        Version.Builder b = new Version.Builder(VersionType.fourDigits)
        Version v = b.build()

        then:
        '1.0.0.0' == v.toString()
    }

    def 'Version has suffix'() {
        when:
        Version v = Version.valueOf('1.2.3.4-alpha')

        then:
        'alpha' == v.getBranchMetadata().toString()
    }

    def 'Version has prefix'() {
        when:
        Version v = Version.valueOf('1.2.3.4-featurebranch-rc1')

        then:
        'featurebranch' == v.getBranchMetadata().toString()
        'rc1' == v.getBuildMetadata().toString()
    }

    def 'Version is featurebranch with jira issue'() {
        when:
        Version v = Version.valueOf('1.2.3.4-featurebranch-rc1')

        then:
        'featurebranch' == v.getBranchMetadata().toString()
        'rc1' == v.getBuildMetadata().toString()
    }

    def 'Parse version string 4 digits'() {
        when:
        Version v = Version.valueOf(versionStr)

        then:
        v.equals(new Version(new NormalVersion(v1, v2, v3, v4), new MetadataVersion(v5 as String[]), new MetadataVersion(v6 as String[])))

        where:
        versionStr          | v1 | v2 | v3 | v4 | v5            | v6
        '1.0.0.0-alpha'     | 1  | 0  | 0  | 0  | ['alpha']     | null
        '1.0.0.0-rc.1'      | 1  | 0  | 0  | 0  | null          | ['rc.', '1']
        '1.0.0.0-fb.1-rc.1' | 1  | 0  | 0  | 0  | ['fb.', '1']  | ['rc.', '1']
    }

    def 'Parse version string 3 digits'() {
        when:
        Version v = Version.valueOf(versionStr)

        then:
        v.equals(new Version(new NormalVersion(v1, v2, v3), new MetadataVersion(v5 as String[]), new MetadataVersion(v6 as String[]), VersionExtension.valueOf(v7)))

        where:
        versionStr                  | v1 | v2 | v3 | v5            | v6             | v7
        '1.0.0-alpha'               | 1  | 0  | 0  | ['alpha']     | null           | 'NONE'
        '1.0.0-rc.1'                | 1  | 0  | 0  | null          | ['rc.', '1']   | 'NONE'
        '1.0.0-fb.1-rc.1'           | 1  | 0  | 0  | ['fb.', '1']  | ['rc.', '1']   | 'NONE'
        '1.0.0-fb.1-rc.1-SNAPSHOT'  | 1  | 0  | 0  | ['fb.', '1']  | ['rc.', '1']   | 'SNAPSHOT'
        '1.0.0-rc.1-LOCAL'          | 1  | 0  | 0  | null          | ['rc.', '1']   | 'LOCAL'
        '1.0.0-fb-1-SNAPSHOT'       | 1  | 0  | 0  | ['fb-1']      | null           | 'SNAPSHOT'

    }

    def 'compare versions' () {
        when:
        Version v1 = Version.valueOf('1.0.0.0')
        Version v2 = Version.valueOf('1.0.0.0-rc1')

        then:
        v2 < v1
    }

    def 'compare versions with more metadata' () {
        when:
        Version v1 = Version.valueOf('1.0.0.0-rc.2')
        Version v2 = Version.valueOf('1.0.0.0-rc.1')

        then:
        v1.compareTo(v2) > 0
    }

    def 'compare complex versions'() {
        when:
        Version v1 = Version.valueOf(vStr1)
        Version v2 = Version.valueOf(vStr2)

        then:
        v1.compareTo(v2) > 0

        where:
        vStr1                   | vStr2
        '1.0.0.0-rc.2'          | '1.0.0.0-rc.1'
        '1.0.0.0-fb-1-rc.2'     | '1.0.0.0-fb-1-rc.1'
        '1.0.0.0-fb-1-SNAPSHOT' | '1.0.0.0-fb-1-LOCAL'
        '1.0.0.0-fb-1'          | '1.0.0.0-fb-1-LOCAL'
        '1.0.0.0-fb-1'          | '1.0.0.0-fb-1-SNAPSHOT'
    }

    def 'increment major version 4 digits' () {
        when:
        Version v = Version.forString("1.2.3.4", VersionType.fourDigits)
        Version incrementedMajor = v.incrementMajorVersion()

        then:
        '2.0.0.0' == incrementedMajor.toString()
    }

    def 'increment major version 3 digits' () {
        when:
        Version v = Version.valueOf("1.2.3")
        Version incrementedMajor = v.incrementMajorVersion()

        then:
        '2.0.0' == incrementedMajor.toString()
    }

    def 'increment minor version 4 digits' () {
        when:
        Version v = Version.forString("1.2.3.4", VersionType.fourDigits)
        Version incrementedMinor = v.incrementMinorVersion()

        then:
        '1.3.0.0' == incrementedMinor.toString()
    }

    def 'increment minor version 3 digits' () {
        when:
        Version v = Version.valueOf("1.2.3")
        Version incrementedMinor = v.incrementMinorVersion()

        then:
        '1.3.0' == incrementedMinor.toString()
    }

    def 'increment patch version 4 digits' () {
        when:
        Version v = Version.forString("1.2.3.4", VersionType.fourDigits)
        Version incrementedPatch = v.incrementPatchVersion()

        then:
        '1.2.4.0' == incrementedPatch.toString()
    }

    def 'increment patch version 3 digits' () {
        when:
        Version v = Version.valueOf("1.2.3")
        Version incrementedPatch = v.incrementPatchVersion()

        then:
        '1.2.4' == incrementedPatch.toString()
    }

    def 'increment hotfix version 3 digits will fail' () {
        when:
        Version v = Version.valueOf("1.2.3")
        v.incrementHotfixVersion()

        then:
        thrown(UnsupportedOperationException)
    }

    def 'increment hotfix version 4 digits' () {
        when:
        Version v = Version.forString("1.2.3.4", VersionType.fourDigits)
        Version incrementedHotfix = v.incrementHotfixVersion()

        then:
        '1.2.3.5' == incrementedHotfix.toString()
    }

    def 'does not increment metadata' () {
        when:
        Version v = Version.forString("1.2.3.4-rc", VersionType.fourDigits)
        Version incrementedBuild = v.incrementVersion()

        then:
        '1.2.3.5-rc' == incrementedBuild.toString()
    }

    def 'increment metadata' () {
        when:
        Version v = Version.forString("1.2.3.4-rc.1", VersionType.fourDigits)
        Version incrementedBuild = v.incrementBuildMetadata()

        then:
        '1.2.3.4-rc.2' == incrementedBuild.toString()
    }

    def 'increment latest' () {
        when:
        Version v = Version.forString(versionStr, VersionType.threeDigits)
        Version incrementedBuild = v.incrementLatest()

        then:
        result == incrementedBuild.toString()

        where:
        versionStr          | result
        '1.2.3-rc.1'        | '1.2.3-rc.2'
        '11.0.0-dev6'       | '11.0.0-dev7'
        '11.0.0-dev'       | '11.1.0-dev'
    }

    def 'increment version' () {
        when:
        Version v = Version.forString(versionStr, VersionType.threeDigits)
        Version incrementedBuild = v.incrementVersion()

        then:
        result == incrementedBuild.toString()

        where:
        versionStr          | result
        '1.2.3-rc.1'        | '1.2.3-rc.2'
        '11.0.0-dev6'       | '11.0.0-dev7'
    }

    def 'Version order 4 digits'() {
        when:
        Version v1 = Version.forString('1.0.0.0', VersionType.fourDigits)
        Version v2 = Version.forString('1.0.0.1', VersionType.fourDigits)
        Version v3 = Version.forString('1.0.1.0', VersionType.fourDigits)
        Version v4 = Version.forString('1.1.0.0', VersionType.fourDigits)
        Version v5 = Version.forString('1.2.0.0-rc.1-SNAPSHOT', VersionType.fourDigits)
        Version v6 = Version.forString('1.2.0.0-rc.1', VersionType.fourDigits)
        Version v7 = Version.forString('1.2.0.0-rc.2', VersionType.fourDigits)
        Version v8 = Version.forString('1.2.0.0', VersionType.fourDigits)

        then:
        v1 < v2
        v2 < v3
        v3 < v4

        v4 < v5
        v5 < v6
        v6 < v7
        v7 < v8

        v4 > v1
        v3 > v2
        v3 > v1

        v7 > v1
        v5 > v1
        v6 > v4
        v8 > v1
        v8 > v7
    }

    def 'Version order 3 digits'() {
        when:
        Version v1 = Version.valueOf('1.0.0')
        Version v2 = Version.valueOf('1.0.1')
        Version v3 = Version.valueOf('1.0.2')
        Version v4 = Version.valueOf('1.1.0')

        then:
        v1 < v2
        v2 < v3
        v3 < v4
        v4 > v1
        v2 < v3
        v3 > v1
    }

    def 'Version sort in a map'() {
        setup:
        Map test = [:]

        when:
        test.put(Version.forString('1.0.1.0', VersionType.fourDigits), '1.0.1.0')
        test.put(Version.forString('1.0.0.2', VersionType.fourDigits), '1.0.0.2')
        test.put(Version.forString('1.0.0.1', VersionType.fourDigits), '1.0.0.1')
        test.put(Version.forString('1.1.0.0', VersionType.fourDigits), '1.1.0.0')
        test.put(Version.forString('1.0.0.0', VersionType.fourDigits), '1.0.0.0')

        then:
        test.keySet().sort(true).reverse(true).get(0).toString() == '1.1.0.0'
    }

    def 'Partially version 4 digits'() {
        when:
        Version v1 = Version.forString('1.0')

        then:
        v1.equals(Version.valueOf('1.0.0.0'))

        when:
        Version v2 = Version.forString('1.1.2')

        then:
        v2.equals(Version.valueOf('1.1.2.0'))
    }

    def 'Partially version 3 digits'() {
        when:
        Version v1 = Version.forString('1.0', VersionType.threeDigits)

        then:
        v1.equals(Version.valueOf('1.0.0'))

        when:
        Version v2 = Version.forString('1.2', VersionType.threeDigits)

        then:
        v2.equals(Version.valueOf('1.2.0'))
    }

    def 'increase latest version 4 digits'() {
        when:
        Version v1 = Version.forString('1.3.0.0', VersionType.fourDigits)

        then:
        Version v2 = v1.incrementLatest()

        then:
        v2.equals(Version.forString('1.3.1.0', VersionType.fourDigits))
    }

    def 'test set branch metadata 1'() {
        when:
        Version v1 = Version.forString('1.0.0')
        Version v2 = v1.setBranchMetadata('IS-23188-ExcludeDomPackage')

        then:
        v2.toString() == "1.0.0-IS-23188-ExcludeDomPackage"
    }
}
