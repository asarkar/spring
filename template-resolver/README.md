# Summary
Thymeleaf templating with non-web Spring Boot. Resolves placeholders from YAML files, environment variables, or
JVM parameters. Selects YAML files that start with the template name, or if named `application.yml`. Fetches templates
and YAML files from Git by default but can be told to use local file system by activating Spring `native` profile.
Expects the YAML files in a `properties` directory and templates in a `templates` directory of `template.baseUri`. 
Both `.yml` and `.yaml` extensions are supported for YAML files; templates must end in `.template`. If given a 
comma-separated list of template names to resolve, tries them all until successful or the list runs out.

See `application.yml` and `TemplateProperties` for configurable properties.

### To run
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