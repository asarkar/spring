package name.abhijitsarkar.javaee.gateway.web;

import com.netflix.client.ClientException;
import com.netflix.zuul.context.RequestContext;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author Abhijit Sarkar
 */
@RunWith(JMockit.class)
public class AuthAwareRoutingFilterTest {
    AuthAwareRoutingFilter filter = new AuthAwareRoutingFilter();

    @Before
    public void init() {
        filter.authAttributeName = "authenticated";
        filter.authServiceId = "auth-service";
        filter.isAuthenticationRequired = true;
    }

    @Test
    public void testFilterOrder(@Mocked RibbonRoutingFilter delegateFilter) {
        new Expectations() {{
            delegateFilter.filterOrder();
            result = 1;
        }};

        filter.delegateFilter = delegateFilter;

        assertEquals(delegateFilter.filterOrder(), filter.filterOrder());
    }

    @Test
    public void testShouldFilter(@Mocked RibbonRoutingFilter delegateFilter) {
        new Expectations() {{
            delegateFilter.shouldFilter();
            result = true;
        }};

        filter.delegateFilter = delegateFilter;

        assertTrue(filter.shouldFilter());
    }

    @Test
    public void testShouldNotFilter(@Mocked RibbonRoutingFilter delegateFilter) {
        new Expectations() {{
            delegateFilter.shouldFilter();
            result = false;
        }};

        filter.delegateFilter = delegateFilter;

        assertFalse(filter.shouldFilter());
    }

    @Test
    public void testFilterType() {
        assertEquals("route", filter.filterType());
    }

    @Test
    public void testAuthenticationSuccessful(@Mocked ClientHttpResponse response) throws Exception {
        new Expectations() {{
            response.getStatusCode();
            result = OK;
        }};

        assertTrue(filter.isAuthenticationSuccessful(response));
    }

    @Test
    public void testAuthenticationFailed(@Mocked ClientHttpResponse response) throws Exception {
        new Expectations() {{
            response.getStatusCode();
            result = UNAUTHORIZED;
        }};

        assertFalse(filter.isAuthenticationSuccessful(response));
    }

    @Test
    public void testAuthenticationNotRequired(@Mocked HttpServletRequest request) {
        filter.isAuthenticationRequired = false;

        assertFalse(filter.isAuthenticationRequired(null));

        filter.isAuthenticationRequired = true;

        new Expectations() {{
            request.getAttribute(filter.authAttributeName);
            result = true;
        }};

        RequestContext requestContext = new RequestContext();
        RequestContext.testSetCurrentContext(requestContext);
        requestContext.setRequest(request);

        assertFalse(filter.isAuthenticationRequired(requestContext));
    }

    @Test
    public void testIsAuthenticationRequired(@Mocked HttpServletRequest request) throws Exception {
        new Expectations() {{
            request.getAttribute(filter.authAttributeName);
            result = null;
        }};

        RequestContext requestContext = new RequestContext();
        RequestContext.testSetCurrentContext(requestContext);
        requestContext.setRequest(request);

        assertTrue(filter.isAuthenticationRequired(requestContext));
    }

    @Test
    public void testSetResponse(@Mocked ClientHttpResponse response, @Mocked ProxyRequestHelper helper) throws IOException, ClientException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{1});
        HttpHeaders headers = new HttpHeaders();
        headers.add("ho", "ha");

        new Expectations() {{
            response.getStatusCode();
            result = OK;

            response.getBody();
            result = is;

            response.getHeaders();
            result = headers;
        }};

        filter.helper = helper;

        filter.setResponse(response);

        new Verifications() {{
            filter.helper.setResponse(OK.value(), is, headers);
        }};
    }

    @Test
    public void testGetRequestUri() throws Exception {
        RequestContext requestContext = new RequestContext();
        RequestContext.testSetCurrentContext(requestContext);

        requestContext.set("requestURI", "abc");

        assertEquals("abc", filter.getRequestUri(requestContext));
    }

    @Test
    public void testRunWhenAuthenticationNotRequired(@Mocked RibbonRoutingFilter delegateFilter) {
        filter.delegateFilter = delegateFilter;
        filter.isAuthenticationRequired = false;

        filter.run();

        new Verifications() {{
            filter.delegateFilter.run();
        }};
    }

    @Test
    public void testRunWhenAuthenticationSuccessful(
            @Mocked RibbonRoutingFilter delegateFilter,
            @Mocked ClientHttpResponse response,
            @Mocked ProxyRequestHelper helper,
            @Mocked HttpServletRequest request) throws IOException, ClientException {
        filter.delegateFilter = delegateFilter;
        filter.helper = helper;

        RequestContext requestContext = new RequestContext();
        RequestContext.testSetCurrentContext(requestContext);
        requestContext.set("serviceId", "abc");
        requestContext.setRequest(request);

        new Expectations() {{
            response.getStatusCode();
            result = OK;

            delegateFilter.run();
            returns(response, (Object) null);
        }};

        Object output = filter.run();

        new Verifications() {{
            filter.helper.setResponse(anyInt(), any(InputStream.class), any(HttpHeaders.class));
            times = 0;

            filter.delegateFilter.run();
            times = 2;
        }};

        assertNull(output);
        assertEquals("abc", requestContext.get("serviceId"));
    }

    @Test
    public void testRunWhenAuthenticationFailure(
            @Mocked RibbonRoutingFilter delegateFilter,
            @Mocked ClientHttpResponse response,
            @Mocked ProxyRequestHelper helper,
            @Mocked HttpServletRequest request) throws IOException, ClientException {
        filter.delegateFilter = delegateFilter;
        filter.helper = helper;

        RequestContext requestContext = new RequestContext();
        RequestContext.testSetCurrentContext(requestContext);
        requestContext.set("serviceId", "abc");
        requestContext.setRequest(request);

        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{1});
        HttpHeaders headers = new HttpHeaders();
        headers.add("ho", "ha");

        new Expectations() {{
            delegateFilter.run();
            result = response;

            response.getStatusCode();
            result = UNAUTHORIZED;

            response.getBody();
            result = is;

            response.getHeaders();
            result = headers;
        }};

        Object output = filter.run();

        new Verifications() {{
            filter.helper.setResponse(UNAUTHORIZED.value(), is, headers);

            filter.delegateFilter.run();
        }};

        assertEquals(response, output);
        assertEquals("abc", requestContext.get("serviceId"));
    }
}