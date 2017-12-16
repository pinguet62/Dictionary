package fr.pinguet62.jsfring.common;

import org.junit.jupiter.api.Test;

import static fr.pinguet62.jsfring.common.UrlUtils.formatAuthorization;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @see UrlUtils
 */
public class UrlUtilsTest {

    /**
     * @see UrlUtils#formatAuthorization(String, String)
     */
    @Test
    public void test_formatAuthorization() {
        assertThat(formatAuthorization("foo", "bar"), is(equalTo("Zm9vOmJhcg==")));
        assertThat(formatAuthorization("username", "password"), is(equalTo("dXNlcm5hbWU6cGFzc3dvcmQ=")));
    }

    /**
     * Check that {@link NullPointerException} is thrown when at lest 1 argument is {@code null}.
     *
     * @see UrlUtils#formatAuthorization(String, String)
     */
    @Test
    public void test_formatAuthorization_null() {
        try {
            formatAuthorization(null, "bar");
            fail();
        } catch (NullPointerException e) {
        }

        try {
            formatAuthorization("foo", null);
            fail();
        } catch (NullPointerException e) {
        }

        try {
            formatAuthorization(null, null);
            fail();
        } catch (NullPointerException e) {
        }
    }

}