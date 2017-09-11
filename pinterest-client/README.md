Uploads multiple files to a Pinterest board; apparently such a common feature isn't supported by default by Pinterest
(*rolls eyes* ).

Start here: https://pinterest-client.herokuapp.com/pinterest/pins

### How to deploy a subdirectory to Heroku without Heroku CLI

1. Create a new app by logging into Heroku.

2. Find the Git URL from _settings_ and add it to the local repo as a new remote.

   ```
   asarkar:spring$ git remote add heroku-pin git@heroku.com:pinterest-client.git
   ```

3. Commit changes.

4. Push.

   ```
   asarkar:spring$ git subtree push --prefix pinterest-client heroku-pin master
   ```

   `pinterest-client` is the name of the subdirectory that's to be deployed, `heroku-pin` is the remote name added in step 1 and `master` is the branch.
 
> If need to force update, use the following command:

  ```
  asarkar:spring$ git push heroku-pin $(git subtree split --prefix pinterest-client master):master --force
  ```