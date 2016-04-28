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

class VersionParserSpec extends Specification {

    def 'Parse version string to string'() {
        when:
        VersionParser parser = new VersionParser(versionStr)

        then:
        Version pv = parser.getVersion()
        pv.toString() == tv

        where:
        versionStr                       | tv
        '1.0.0'                          | '1.0.0'
        '1.0.0-branch-build'             | '1.0.0-branch-build'
        '1.0.0-build'                    | '1.0.0-build'
        '1.0.0-build.1'                  | '1.0.0-build.1'
        '10.0.0-JIRA-1234'               | '10.0.0-JIRA-1234'
        '10.0.0-JIRA-1234-rc.1'          | '10.0.0-JIRA-1234-rc.1'
        '10.10.10-JIRA-1234-rc.1'        | '10.10.10-JIRA-1234-rc.1'
        '10.10.10-SNAPSHOT'              | '10.10.10-SNAPSHOT'
        '10.10.10-LOCAL'                 | '10.10.10-LOCAL'
        '10.10.10-branch-SNAPSHOT'       | '10.10.10-branch-SNAPSHOT'
        '10.10.10-branch-LOCAL'          | '10.10.10-branch-LOCAL'
        '10.10.10-dev1-SNAPSHOT'         | '10.10.10-dev1-SNAPSHOT'
        '10.10.10-dev1-LOCAL'            | '10.10.10-dev1-LOCAL'
        '10.10.10-branch-dev1-SNAPSHOT'  | '10.10.10-branch-dev1-SNAPSHOT'
        '10.10.10-branch-dev1-LOCAL'     | '10.10.10-branch-dev1-LOCAL'
        '10'                             | '10.0.0'
        '10.0'                           | '10.0.0'
    }

    def 'Parse version string to string with versiontype'() {
        when:
        VersionParser parser = new VersionParser(versionStr)

        then:
        Version pv = parser.getVersion(type)
        pv.toString() == tv

        where:
        versionStr                     | tv                             | type
        '1.0.0'                        | '1.0.0'                        | VersionType.threeDigits
        '1.0.0-branch-build'           | '1.0.0-branch-build'           | VersionType.threeDigits
        '1.0.0-build'                  | '1.0.0-build'                  | VersionType.threeDigits
        '1.0.0-build.1'                | '1.0.0-build.1'                | VersionType.threeDigits
        '10.0.0-JIRA-1234'             | '10.0.0-JIRA-1234'             | VersionType.threeDigits
        '4-JIRA-1234'                  | '4.0.0-JIRA-1234'              | VersionType.threeDigits
        '10.0.0-JIRA-1234-rc.1'        | '10.0.0-JIRA-1234-rc.1'        | VersionType.threeDigits
        '10.10.10-JIRA-1234-rc.1'      | '10.10.10-JIRA-1234-rc.1'      | VersionType.threeDigits
        '10.10.10-SNAPSHOT'            | '10.10.10-SNAPSHOT'            | VersionType.threeDigits
        '10.10.10-LOCAL'               | '10.10.10-LOCAL'               | VersionType.threeDigits
        '10.10.10-branch-SNAPSHOT'     | '10.10.10-branch-SNAPSHOT'     | VersionType.threeDigits
        '10.10.10-branch-LOCAL'        | '10.10.10-branch-LOCAL'        | VersionType.threeDigits
        '10.10.10-dev1-SNAPSHOT'       | '10.10.10-dev1-SNAPSHOT'       | VersionType.threeDigits
        '10.10.10-dev1-LOCAL'          | '10.10.10-dev1-LOCAL'          | VersionType.threeDigits
        '10.10.10-branch-dev1-SNAPSHOT'| '10.10.10-branch-dev1-SNAPSHOT'| VersionType.threeDigits
        '10.10.10-branch-dev1-LOCAL'   | '10.10.10-branch-dev1-LOCAL'   | VersionType.threeDigits
        '1.0.0.0'                      | '1.0.0.0'                      | VersionType.fourDigits
        '1.0.0.0-branch-build'         | '1.0.0.0-branch-build'         | VersionType.fourDigits
        '1.0.0.0-build'                | '1.0.0.0-build'                | VersionType.fourDigits
        '1.0.0.0-build.1'              | '1.0.0.0-build.1'              | VersionType.fourDigits
        '10.0.0.0-JIRA-1234'           | '10.0.0.0-JIRA-1234'           | VersionType.fourDigits
        '10.0.0.0-JIRA-1234-rc.1'      | '10.0.0.0-JIRA-1234-rc.1'      | VersionType.fourDigits
        '10.10.10.10-JIRA-1234-rc.1'   | '10.10.10.10-JIRA-1234-rc.1'   | VersionType.fourDigits
        '10.0-JIRA-1234-rc.1'          | '10.0.0-JIRA-1234-rc.1'        | VersionType.threeDigits
        '10.10.10-SNAPSHOT'            | '10.10.10.0-SNAPSHOT'          | VersionType.fourDigits
        '10.10.10-LOCAL'               | '10.10.10.0-LOCAL'             | VersionType.fourDigits
        '10.10.10-branch-SNAPSHOT'     | '10.10.10.0-branch-SNAPSHOT'   | VersionType.fourDigits
        '10.10.10-branch-LOCAL'        | '10.10.10.0-branch-LOCAL'      | VersionType.fourDigits
        '10.10.10-dev1-SNAPSHOT'       | '10.10.10.0-dev1-SNAPSHOT'     | VersionType.fourDigits
        '10.10.10-dev1-LOCAL'          | '10.10.10.0-dev1-LOCAL'        | VersionType.fourDigits
        '10.0-branch-dev1-LOCAL'       | '10.0.0.0-branch-dev1-LOCAL'   | VersionType.fourDigits
        '10.0-branch-dev1-LOCAL'       | '10.0.0.0-branch-dev1-LOCAL'   | VersionType.fourDigits
    }

    def 'No leading zero'() {
        when:
        VersionParser parser = new VersionParser('01.0.0.0')
        parser.getVersion()

        then:
        def e = thrown(ParserException)
        e.message == 'Numeric identifier MUST NOT contain leading zeroes (01 in 01.0.0.0)'
        e.cause == null
    }

    def 'No leading zero for build data'() {
        when:
        VersionParser parser = new VersionParser('1.0.0.0-suffix.01')
        parser.getVersion()

        then:
        def e = thrown(ParserException)
        e.message == 'Numeric identifier MUST NOT contain leading zeroes (01 in suffix.01)'
        e.cause == null
    }

    def 'No empty digit'() {
        when:
        VersionParser parser = new VersionParser('1..0.0.0')
        parser.getVersion()

        then:
        def e = thrown(ParserException)
        e.message == 'One part of the version is empty'
        e.cause == null
    }

    def 'Version from partially versions with 4 digits'() {
        when:
        Version v = VersionParser.parseVersion('1.2', VersionType.fourDigits)

        then:
        v.toString() == '1.2.0.0'
    }

    def 'Version from partially versions with 3 digits'() {
        when:
        Version v = VersionParser.parseVersion('1.2', VersionType.threeDigits)

        then:
        v.toString() == '1.2.0'
    }
}
