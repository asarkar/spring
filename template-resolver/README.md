# Summary
Thymeleaf templating with non-web Spring Boot.

## Placeholder resolution
Resolves placeholders from one or more of the following sources, in decreasing order of priority:
1. System properties (JVM parameters).
2. Environment variables.
3. YAML files that start with the template name, or if named `application.yml` (from Git or local).

## Directory structure
Expects the YAML files in a `properties` directory and templates in a `templates` directory under `template.baseUri`. 
Both `.yml` and `.yaml` extensions are supported; templates must end in `.template`. If given a 
comma-separated list of template names, tries them all until successful or the list runs out.

See `application.yml` and `TemplateProperties` for configurable properties.

## To run
1.
```
asarkar:template-resolver$ ./gradlew clean bootRepackage
```
2.
```
asarkar:template-resolver$ java -jar -Dspring.profiles.active=native \
-DBASE_URI=src/test/resources \
-Drc.name=test \
build/libs/template-resolver-1.0-SNAPSHOT.jar \
--template.names=nginx
```