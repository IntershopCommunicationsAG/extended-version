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
 * Version extension that will be supported by this
 * version implementation. It is based on SemVer.
 * This adds special version extension - SNAPSHOT and LOCAL.
 */
@CompileStatic
enum VersionExtension {
    LOCAL {
        @Override
        public String toString() {
            return 'LOCAL'
        }
    },
    SNAPSHOT {
        @Override
        public String toString() {
            return 'SNAPSHOT'
        }
    },
    NONE {
        @Override
        public String toString() {
            return ''
        }
    }

    public static VersionExtension getEnum(String s){
        if(SNAPSHOT.toString().equals(s.trim().toLowerCase())){
            return SNAPSHOT
        }else if(LOCAL.toString().equals(s.trim().toLowerCase())){
            return LOCAL
        }
        return NONE
    }
}