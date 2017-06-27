package org.abhijitsarkar.camel.github;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import static java.util.Arrays.asList;
import static org.abhijitsarkar.camel.github.Application.REPO;
import static org.abhijitsarkar.camel.github.GitHubTestUtil.newCommit;

/**
 * @author Abhijit Sarkar
 */
public class PrettyPrinterTest {
    @Test
    public void testPrint() {
        GitHub.Commit commit1 = newCommit("mockSha1");
        GitHub.Commit commit2 = newCommit("mockSha2");

        GitHub.Commit innerCommit1 = commit1.getCommit();
        GitHub.Commit.Committer committer1 = innerCommit1.getCommitter();

        GitHub.Commit.Committer newCommitter1 = new GitHub.Commit.Committer();
        BeanUtils.copyProperties(committer1, newCommitter1);

        commit1.setMessage(innerCommit1.getMessage());
        commit1.setCommitter(newCommitter1);
        commit1.setCommit(null);

        GitHub.Commit innerCommit2 = commit2.getCommit();
        GitHub.Commit.Committer committer2 = innerCommit2.getCommitter();

        GitHub.Commit.Committer newCommitter2 = new GitHub.Commit.Committer();
        BeanUtils.copyProperties(committer2, newCommitter2);

        commit2.setMessage(innerCommit2.getMessage());
        commit2.setCommitter(newCommitter2);
        commit2.setCommit(null);

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setBody(asList(commit1, commit2));
        ex.getIn().setHeader(REPO, "mockRepo");

        PrettyPrinter.print(ex);
    }
}