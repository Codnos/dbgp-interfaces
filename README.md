# Interfaces and implementations for both client and server side of DBGp.

## Where to get from:

### Stable:
Maven Central
```xml
<dependency>
    <groupId>com.codnos</groupId>
    <artifactId>dbgp-interfaces</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Snapshots:
Sonatype snapshots: https://oss.sonatype.org/content/repositories/snapshots/
```xml
<dependency>
    <groupId>com.codnos</groupId>
    <artifactId>dbgp-interfaces</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## TODO:
 - [x] Rewrite commands tests to ScalaTest
 - [x] Rewrite feature test to ScalaTest
 - [x] Add unit tests for already existing commands/messages
 - [x] Add feature test scenarios for existing functionality
 - [x] Refactor commands into separate separate response/handler classes
 - [x] Introduce separate API package (or move all internal to internal)
 - [x] Refactor existing API for consumers so that it's not quirky to construct and doesn't violate a lot of conditions
 - [x] Introduce sync API for IDE
 - [x] Add proper logging with j.u.l
 - [x] Better handling of command parameter parsing
 - [x] Better/systematic handling of xml construction
 - [x] Refactor DBGPCommandHandler to return Optional<String> and remove ctx from arguments
 - [ ] Auto-discover (via reflection?) of MessageFactories and CommandHandlers (with priorities)
 - [ ] Add unit tests for main building blocks
 - [ ] Add missing argument/attribute handling for existing commands/responses
 - [ ] Proper configuration/parameter negotiation
 - [ ] Add additional protocol elements
     - [x] breakpoint_remove
     - [x] breakpoint_get
     - [ ] breakpoint_list
     - [ ] breakpoint_update
     - [ ] break
     - [ ] eval
     - [ ] feature_get
     - [ ] feature_set
     - [ ] stop
     - [ ] detach
     - [ ] context_names
     - [ ] typemap_get
     - [ ] property_get
     - [ ] property_set
     - [ ] source
     - [ ] stdout
     - [ ] stderr
     - [ ] stdin
     - [ ] expr
     - [ ] exec
     - [ ] spawnpoint_set
     - [ ] spawnpoint_get
     - [ ] spawnpoint_update
     - [ ] spawnpoint_remove
     - [ ] spawnpoint_list
     - [ ] notifications
     - [ ] interact
 - [ ] Introduce async API for IDE
 - [ ] Add proper JavaDoc and examples
 - [ ] Functional tests using PHP
 - [ ] Functional tests using eXistDB
 - [ ] Release to Maven Central (https://maven.apache.org/guides/mini/guide-central-repository-upload.html)
 - [ ] Exception/error support
 - [ ] Support for proxies
 - [ ] Multi-process/multi-thread support