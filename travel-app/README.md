# Spring Boot application that that allows users to find flights by entering airports and travel dates. Uses Couchbase travel-sample bucket.
Can be deployed on a Docker container or Heroku.

### Load Couchbase travel-sample Bucket:
`curl -u admin:admin123 --data-ascii '["travel-sample"]' http://$(docker-machine ip default):8091/sampleBuckets/install`

### Create Primary Index for Queries:
   * Run `/opt/couchbase/bin/cbq`
   * `CREATE PRIMARY INDEX ``travel-sample-primary-index`` ON ``travel-sample`` USING GSI;`
   * Exit by `CTRL + c`
   * Using Java SDK

   ```
   Query createIndex = Query.simple("CREATE PRIMARY INDEX ``travel-sample-primary-index`` ON ``travel-sample`` USING GSI;");
   bucket.query(createIndex);
   ```

### Build and Run Locally:
`./gradlew clean stage`

`./gradlew clean stage -Penv=heroku` to build with Heroku property file

### Deploy to Heroku:
   * Run `heroku local web` to verify that stuff works.
   * Run `heroku create abhijitsarkar-travel-app --remote abhijitsarkar-travel-app` from project root.
   * Run `git subtree push --prefix travel-app abhijitsarkar-travel-app master` from git root.
     (Read [this](http://brettdewoody.com/deploying-a-heroku-app-from-a-subdirectory/)).
   * If working from a branch, either first merge to master or run `git subtree push --prefix travel-app abhijitsarkar-travel-app yourbranch:master`
   * To log onto Heroku bash, `heroku run bash --app abhijitsarkar-travel-app`.

### Operate without Couchbase:
   * Activate Spring profile `noDB`. One way to do it is to set a system variable during build: `-Dspring.profiles.active=noDB`
   * In this mode, only airports `sea` and `sfo` are available for searching. All dates all available as departure dates.

### References:
[Mastering observables](http://developer.couchbase.com/documentation/server/4.0/sdks/java-2.2/observables.html)

[Working with N1QL](http://developer.couchbase.com/documentation/server/4.0/sdks/java-2.2/querying-n1ql.html)

[N1QL reference](http://developer.couchbase.com/documentation/server/4.0/n1ql/index.html)

