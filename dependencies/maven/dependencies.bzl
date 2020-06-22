# Do not edit. bazel-deps autogenerates this file from dependencies/maven/dependencies.yaml.
def _jar_artifact_impl(ctx):
    jar_name = "%s.jar" % ctx.name
    ctx.download(
        output=ctx.path("jar/%s" % jar_name),
        url=ctx.attr.urls,
        sha256=ctx.attr.sha256,
        executable=False
    )
    src_name="%s-sources.jar" % ctx.name
    srcjar_attr=""
    has_sources = len(ctx.attr.src_urls) != 0
    if has_sources:
        ctx.download(
            output=ctx.path("jar/%s" % src_name),
            url=ctx.attr.src_urls,
            sha256=ctx.attr.src_sha256,
            executable=False
        )
        srcjar_attr ='\n    srcjar = ":%s",' % src_name

    build_file_contents = """
package(default_visibility = ['//visibility:public'])
java_import(
    name = 'jar',
    tags = ['maven_coordinates={artifact}'],
    jars = ['{jar_name}'],{srcjar_attr}
)
filegroup(
    name = 'file',
    srcs = [
        '{jar_name}',
        '{src_name}'
    ],
    visibility = ['//visibility:public']
)\n""".format(artifact = ctx.attr.artifact, jar_name = jar_name, src_name = src_name, srcjar_attr = srcjar_attr)
    ctx.file(ctx.path("jar/BUILD"), build_file_contents, False)
    return None

jar_artifact = repository_rule(
    attrs = {
        "artifact": attr.string(mandatory = True),
        "sha256": attr.string(mandatory = True),
        "urls": attr.string_list(mandatory = True),
        "src_sha256": attr.string(mandatory = False, default=""),
        "src_urls": attr.string_list(mandatory = False, default=[]),
    },
    implementation = _jar_artifact_impl
)

def jar_artifact_callback(hash):
    src_urls = []
    src_sha256 = ""
    source=hash.get("source", None)
    if source != None:
        src_urls = [source["url"]]
        src_sha256 = source["sha256"]
    jar_artifact(
        artifact = hash["artifact"],
        name = hash["name"],
        urls = [hash["url"]],
        sha256 = hash["sha256"],
        src_urls = src_urls,
        src_sha256 = src_sha256
    )
    native.bind(name = hash["bind"], actual = hash["actual"])


def list_dependencies():
    return [
    {"artifact": "ch.qos.logback:logback-classic:1.2.3", "lang": "java", "sha1": "7c4f3c474fb2c041d8028740440937705ebb473a", "sha256": "fb53f8539e7fcb8f093a56e138112056ec1dc809ebb020b59d8a36a5ebac37e0", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar", "source": {"sha1": "cfd5385e0c5ed1c8a5dce57d86e79cf357153a64", "sha256": "480cb5e99519271c9256716d4be1a27054047435ff72078d9deae5c6a19f63eb", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3-sources.jar"} , "name": "ch-qos-logback-logback-classic", "actual": "@ch-qos-logback-logback-classic//jar", "bind": "jar/ch/qos/logback/logback-classic"},
    {"artifact": "ch.qos.logback:logback-core:1.2.3", "lang": "java", "sha1": "864344400c3d4d92dfeb0a305dc87d953677c03c", "sha256": "5946d837fe6f960c02a53eda7a6926ecc3c758bbdd69aa453ee429f858217f22", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar", "source": {"sha1": "3ebabe69eba0196af9ad3a814f723fb720b9101e", "sha256": "1f69b6b638ec551d26b10feeade5a2b77abe347f9759da95022f0da9a63a9971", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3-sources.jar"} , "name": "ch-qos-logback-logback-core", "actual": "@ch-qos-logback-logback-core//jar", "bind": "jar/ch/qos/logback/logback-core"},
    {"artifact": "junit:junit:4.12", "lang": "java", "sha1": "2973d150c0dc1fefe998f834810d68f278ea58ec", "sha256": "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/junit/junit/4.12/junit-4.12.jar", "source": {"sha1": "a6c32b40bf3d76eca54e3c601e5d1470c86fcdfa", "sha256": "9f43fea92033ad82bcad2ae44cec5c82abc9d6ee4b095cab921d11ead98bf2ff", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/junit/junit/4.12/junit-4.12-sources.jar"} , "name": "junit-junit", "actual": "@junit-junit//jar", "bind": "jar/junit/junit"},
    {"artifact": "net.bytebuddy:byte-buddy-agent:1.6.4", "lang": "java", "sha1": "f6e414aa655ae1649eb642f70ea67e2c52b196c4", "sha256": "14e602e74e8c1a072a71eb75184f45eb8014221bf4981896b8686c2034a29ef5", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/bytebuddy/byte-buddy-agent/1.6.4/byte-buddy-agent-1.6.4.jar", "source": {"sha1": "609e1f88a35606b8db4afcc35959b52bc099b7ba", "sha256": "a53d298ccc3670bce09632be32778479462abf7a37fe80b301ed569f1d8e593f", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/bytebuddy/byte-buddy-agent/1.6.4/byte-buddy-agent-1.6.4-sources.jar"} , "name": "net-bytebuddy-byte-buddy-agent", "actual": "@net-bytebuddy-byte-buddy-agent//jar", "bind": "jar/net/bytebuddy/byte-buddy-agent"},
    {"artifact": "net.bytebuddy:byte-buddy:1.6.4", "lang": "java", "sha1": "682e791335dede35d628f26465b66ccd5ba7b443", "sha256": "3798336d61857087a0c5970f9c5afae2c340939913a3a7dc98e2813387405ca8", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/bytebuddy/byte-buddy/1.6.4/byte-buddy-1.6.4.jar", "source": {"sha1": "073154e2215a8e09cdbf39b8914e750d3b28fc7d", "sha256": "22429809c08d608d87afdea50bdf4ad1c654887b1f6a94509102f54204ce57bf", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/net/bytebuddy/byte-buddy/1.6.4/byte-buddy-1.6.4-sources.jar"} , "name": "net-bytebuddy-byte-buddy", "actual": "@net-bytebuddy-byte-buddy//jar", "bind": "jar/net/bytebuddy/byte-buddy"},
    {"artifact": "org.hamcrest:hamcrest-core:1.3", "lang": "java", "sha1": "42a25dc3219429f0e5d060061f71acb49bf010a0", "sha256": "66fdef91e9739348df7a096aa384a5685f4e875584cce89386a7a47251c4d8e9", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar", "source": {"sha1": "1dc37250fbc78e23a65a67fbbaf71d2e9cbc3c0b", "sha256": "e223d2d8fbafd66057a8848cc94222d63c3cedd652cc48eddc0ab5c39c0f84df", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3-sources.jar"} , "name": "org-hamcrest-hamcrest-core", "actual": "@org-hamcrest-hamcrest-core//jar", "bind": "jar/org/hamcrest/hamcrest-core"},
    {"artifact": "org.hamcrest:hamcrest:2.2", "lang": "java", "sha1": "1820c0968dba3a11a1b30669bb1f01978a91dedc", "sha256": "5e62846a89f05cd78cd9c1a553f340d002458380c320455dd1f8fc5497a8a1c1", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar", "source": {"sha1": "a0a13cfc629420efb587d954f982c4c6a100da25", "sha256": "f49e697dbc70591f91a90dd7f741f5780f53f63f34a416d6a9879499d4d666af", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest/2.2/hamcrest-2.2-sources.jar"} , "name": "org-hamcrest-hamcrest", "actual": "@org-hamcrest-hamcrest//jar", "bind": "jar/org/hamcrest/hamcrest"},
    {"artifact": "org.mockito:mockito-core:2.6.4", "lang": "java", "sha1": "b0fa48f9f385948a1e067dd94ab813318abb0a9e", "sha256": "21c5536a3facfe718baa802609b0c38311fedf6660430da3fd29cce1cb00dbb0", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/mockito/mockito-core/2.6.4/mockito-core-2.6.4.jar", "source": {"sha1": "aa6a259b1917e2b964b87cc902058d14347e0409", "sha256": "f3ce9f4978692345ce09f9c28674279475577122cbf6cae8aca21a7e08b8195b", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/mockito/mockito-core/2.6.4/mockito-core-2.6.4-sources.jar"} , "name": "org-mockito-mockito-core", "actual": "@org-mockito-mockito-core//jar", "bind": "jar/org/mockito/mockito-core"},
    {"artifact": "org.objenesis:objenesis:2.5", "lang": "java", "sha1": "612ecb799912ccf77cba9b3ed8c813da086076e9", "sha256": "293328e1b0d31ed30bb89fca542b6c52fac00989bb0e62eb9d98d630c4dd6b7c", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/objenesis/objenesis/2.5/objenesis-2.5.jar", "source": {"sha1": "e2b450699731118d1498645e36577371afced20f", "sha256": "727eaf4bece2f9587702b3d64a7e091afb98ab38c87b3f36728e4fe456bdd6cb", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/objenesis/objenesis/2.5/objenesis-2.5-sources.jar"} , "name": "org-objenesis-objenesis", "actual": "@org-objenesis-objenesis//jar", "bind": "jar/org/objenesis/objenesis"},
    {"artifact": "org.slf4j:slf4j-api:1.7.28", "lang": "java", "sha1": "2cd9b264f76e3d087ee21bfc99305928e1bdb443", "sha256": "fb6e4f67a2a4689e3e713584db17a5d1090c1ebe6eec30e9e0349a6ee118141e", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.28/slf4j-api-1.7.28.jar", "source": {"sha1": "6444f3c8fce32e20f621e264807256c5e65f11c9", "sha256": "b1b8bfa4f2709684606001685d09ef905adc1b72ec53444ade90f44bfbcebcff", "repository": "https://repo.maven.apache.org/maven2/", "url": "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.28/slf4j-api-1.7.28-sources.jar"} , "name": "org-slf4j-slf4j-api", "actual": "@org-slf4j-slf4j-api//jar", "bind": "jar/org/slf4j/slf4j-api"},
    ]

def maven_dependencies(callback = jar_artifact_callback):
    for hash in list_dependencies():
        callback(hash)
