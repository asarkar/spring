# How to deploy a subdirectory to Heroku without Heroku CLI

1. Create a new app by logging into Heroku.

2. Find the Git URL from _settings_ and add it to the local repo as a new remote.

   ```
   asarkar:java-ee$ git remote add heroku-ig-client git@heroku.com:ig-client.git
   ```

3. Commit changes.

4. Push.

   ```
   asarkar:java-ee$ git subtree push --prefix ig-client heroku-ig-client master
   ```

   `ig-client` is the name of the subdirectory that's to be deployed, `heroku-ig-client` is the remote name added in step 1 and `master` is the branch.
 
> If need to force update, use the following command:

  ```
  asarkar:java-ee$ git push heroku-ig-client $(git subtree split --prefix ig-client master):master --force
  ```
 
 
# How to fetch top Instagram posts

1. Go to [http://ig-client.herokuapp.com](http://ig-client.herokuapp.com). It will open the Instagram login page 
   (if already logged in, skip to step 3). 
   
2. Login with your username and password.

3. It will return your recent 20 posts with the image URLs and number of likes. The posts are sorted by number of likes.

# Running with bootRun

`./gradlew clean bootRun -DCLIENT_ID=<clientId> -DCLIENT_SECRET=<clientSecret> -Dspring.profiles.active=webClient`

> Profile `webClient` only exists to reproduce the bug [SPR-15080](https://jira.spring.io/browse/SPR-15080).
