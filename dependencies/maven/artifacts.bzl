#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

artifacts = [
    "ch.qos.logback:logback-classic",
    "com.google.code.findbugs:jsr305",
    "com.google.guava:guava",
    "com.google.protobuf:protobuf-java",
    "io.grpc:grpc-api",
    "io.grpc:grpc-core",
    "io.grpc:grpc-protobuf",
    "io.grpc:grpc-stub",
    "io.grpc:grpc-netty",
    "io.grpc:grpc-testing",
    "io.netty:netty-codec-http2",
    "io.netty:netty-handler",
    "io.netty:netty-tcnative-boringssl-static",
    "javax.annotation:javax.annotation-api",
    "junit:junit",
    "org.hamcrest:hamcrest",
    "org.mockito:mockito-core",
    "org.slf4j:slf4j-api"
]

# Override libraries conflicting with versions defined in @vaticle_dependencies
artifacts_repo = {
    "io.netty:netty-all": "4.1.38.Final",
    "io.netty:netty-codec-http2": "4.1.38.Final",
    "io.netty:netty-handler": "4.1.38.Final",
    "io.netty:netty-handler-proxy": "4.1.38.Final",
    "io.netty:netty-buffer": "4.1.38.Final",
    "io.netty:netty-codec": "4.1.38.Final",
    "io.netty:netty-codec-http": "4.1.38.Final",
    "io.netty:netty-codec-socks": "4.1.38.Final",
    "io.netty:netty-common": "4.1.38.Final",
    "io.netty:netty-transport": "4.1.38.Final",
    "io.netty:netty-resolver": "4.1.38.Final",
}
