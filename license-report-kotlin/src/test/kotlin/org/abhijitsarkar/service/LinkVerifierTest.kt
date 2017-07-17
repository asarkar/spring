package org.abhijitsarkar.service

import com.github.benmanes.caffeine.cache.Cache
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.ShouldSpec
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import reactor.test.StepVerifier
import java.time.Duration

/**
 * @author Abhijit Sarkar
 */
class LinkVerifierTest : ShouldSpec() {
    init {
        should("verify if link is valid") {
            val myTable = table(
                    headers("link", "valid"),
                    row("http://www.slf4j.org/license.html", true),
                    row("http://www.apache.org/licenses/LICENSE-2.0.txt", true),
                    row("https://glassfish.dev.java.net/nonav/public/CDDL+GPL.html", true),
                    row("http://doesnotexist.blah.com", false),
                    row("http://www.opensource.org/licenses/cddl1.php", true),
                    row("", false)
            )

            val linkVerifier = LinkVerifierImpl.newInstance()
            forAll(myTable) { link, valid ->
                StepVerifier.create(linkVerifier.isValid(link))
                        .expectNext(valid)
                        .expectComplete()
                        .verify(Duration.ofSeconds(3L))
            }
        }

        should("not make remote call if in cache") {
            val path = "www.apache.org/licenses/LICENSE-2.0.txt"

            @Suppress("UNCHECKED_CAST")
            val cache: Cache<String, Boolean> = mock(Cache::class.java) as Cache<String, Boolean>

            `when`(cache.getIfPresent(path))
                    .thenReturn(null, true)

            val myTable = table(
                    headers("link", "valid"),
                    row("http://$path", true),
                    row("https://$path", true)
            )

            val linkVerifier = LinkVerifierImpl.newInstance(cache = cache)
            forAll(myTable) { link, valid ->
                StepVerifier.create(linkVerifier.isValid(link))
                        .expectNext(valid)
                        .expectComplete()
                        .verify(Duration.ofSeconds(3L))
            }

            verify(cache, times(2)).getIfPresent(path)
            verify(cache).put(path, true)
        }

        should("not cache failed response") {
            val path = "doesnotexist.blah.com"
            @Suppress("UNCHECKED_CAST")
            val cache: Cache<String, Boolean> = mock(Cache::class.java) as Cache<String, Boolean>

            val linkVerifier = LinkVerifierImpl.newInstance(cache = cache)
            for (i in 1..2) {
                StepVerifier.create(linkVerifier.isValid("http://$path"))
                        .expectNext(false)
                        .expectComplete()
                        .verify(Duration.ofSeconds(3L))
            }

            verify(cache, times(2)).getIfPresent("$path")
            verify(cache, never()).put(eq("$path"), anyBoolean())
        }
    }
}