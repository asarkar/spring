package org.abhijitsarkar.camel.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Param;
import feign.RequestLine;
import lombok.Data;

import java.util.List;

/**
 * @author Abhijit Sarkar
 */
public interface GitHub {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Repository {
        private String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Commit {
        private String sha;
        private String message;
        private Commit commit;
        private Committer committer;
        private List<File> files;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class Committer {
            private String name;
            @JsonProperty("date")
            private String dateTime;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        public static class File {
            private String filename;
        }
    }

    @RequestLine("GET /users/{username}/repos?sort=pushed&direction=desc")
    List<Repository> repos(@Param("username") String username);

    @RequestLine("GET /repos/{username}/{repo}/commits")
    List<Commit> commits(@Param("username") String username, @Param("repo") String repo);

    @RequestLine("GET /repos/{username}/{repo}/commits/{sha}")
    Commit commit(@Param("username") String username, @Param("repo") String repo, @Param("sha") String sha);
}
