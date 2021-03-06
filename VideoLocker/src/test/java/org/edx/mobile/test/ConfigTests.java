package org.openedu.www.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import tw.openedu.www.util.Config;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Test;

import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * Created by aleffert on 2/6/15.
 */
public class ConfigTests extends BaseTestCase {
    //TODO - should we place constant at a central place?
    /* Config keys */
    private static final String COURSE_ENROLLMENT = "COURSE_ENROLLMENT";
    private static final String SOCIAL_SHARING = "SOCIAL_SHARING";
    private static final String ZERO_RATING = "ZERO_RATING";
    private static final String FACEBOOK = "FACEBOOK";
    private static final String GOOGLE = "GOOGLE";
    private static final String FABRIC = "FABRIC";
    private static final String NEW_RELIC = "NEW_RELIC";
    private static final String SEGMENT_IO = "SEGMENT_IO";
    private static final String WHITE_LIST_OF_DOMAINS = "WHITE_LIST_OF_DOMAINS";

    private static final String ENABLED = "ENABLED";
    private static final String DISABLED_CARRIERS = "DISABLED_CARRIERS";
    private static final String CARRIERS = "CARRIERS";
    private static final String COURSE_SEARCH_URL = "COURSE_SEARCH_URL";
    private static final String TYPE = "TYPE";
    private static final String COURSE_INFO_URL_TEMPLATE = "COURSE_INFO_URL_TEMPLATE";
    private static final String FACEBOOK_APP_ID = "FACEBOOK_APP_ID";
    private static final String FABRIC_KEY = "FABRIC_KEY";
    private static final String FABRIC_BUILD_SECRET = "FABRIC_BUILD_SECRET";
    private static final String NEW_RELIC_KEY = "NEW_RELIC_KEY";
    private static final String SEGMENT_IO_WRITE_KEY = "SEGMENT_IO_WRITE_KEY";
    private static final String DOMAINS = "DOMAINS";

    private static final String PARSE = "PARSE";
    private static final String PARSE_ENABLED = "NOTIFICATIONS_ENABLED";
    private static final String PARSE_APPLICATION_ID = "APPLICATION_ID";
    private static final String PARSE_CLIENT_KEY = "CLIENT_KEY";


    @Test
    public void testZeroRatingNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getZeroRatingConfig().isEnabled());
        assertEquals(config.getZeroRatingConfig().getCarriers().size(), 0);
    }

    @Test
    public void testZeroRatingEmptyConfig() {
        JsonObject configBase = new JsonObject();
        JsonObject socialConfig = new JsonObject();
        configBase.add(ZERO_RATING, socialConfig);

        Config config = new Config(configBase);
        assertFalse(config.getZeroRatingConfig().isEnabled());
        assertEquals(config.getZeroRatingConfig().getCarriers().size(), 0);
    }

    @Test
    public void testZeroRatingConfig() {
        JsonObject configBase = new JsonObject();
        JsonObject zeroRatingConfig = new JsonObject();
        zeroRatingConfig.add(ENABLED, new JsonPrimitive(true));
        configBase.add(ZERO_RATING, zeroRatingConfig);

        ArrayList<String> carrierList = new ArrayList<String>();
        carrierList.add("12345");
        carrierList.add("foo");
        JsonArray carriers = new JsonArray();
        for (String carrier : carrierList) {
            carriers.add(new JsonPrimitive(carrier));
        }
        zeroRatingConfig.add(CARRIERS, carriers);

        ArrayList<String> domainList = new ArrayList<>();
        domainList.add("domain1");
        domainList.add("domain2");
        JsonArray domains = new JsonArray();
        for (String domain : domainList) {
            domains.add(new JsonPrimitive(domain));
        }
        zeroRatingConfig.add(WHITE_LIST_OF_DOMAINS, domains);

        Config config = new Config(configBase);
        assertTrue(config.getZeroRatingConfig().isEnabled());
        assertEquals(carrierList, config.getZeroRatingConfig().getCarriers());
        assertEquals(domainList, config.getZeroRatingConfig().getWhiteListedDomains());
    }

    @RunWith(value = Parameterized.class)
    public class EnrollmentConfigTests {

        private String course_enrollment_type;
        private boolean expected;

        public EnrollmentConfigTests(String course_enrollment_type, boolean expected) {
            this.course_enrollment_type = course_enrollment_type;
            this.expected = expected;
        }

        @Parameters(name= "{index}: willUseWebview({0})={1}")
        public Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"webview", true},
                    {"WEBVIEW", true},
                    {"native", false},
                    {"NATIVE", true},
                    {"invalid type", false},
            });
        }

        @Test
        public void testEnrollmentNoConfig() {
            JsonObject configBase = new JsonObject();
            Config config = new Config(configBase);
            assertFalse(config.getCourseDiscoveryConfig().isWebviewCourseDiscoveryEnabled());
            assertNull(config.getCourseDiscoveryConfig().getCourseSearchUrl());
            assertNull(config.getCourseDiscoveryConfig().getCourseInfoUrlTemplate());
        }

        @Test
        public void testEnrollmentEmptyConfig() {
            JsonObject configBase = new JsonObject();
            JsonObject enrollmentConfig = new JsonObject();
            configBase.add(COURSE_ENROLLMENT, enrollmentConfig);

            Config config = new Config(configBase);
            assertFalse(config.getCourseDiscoveryConfig().isWebviewCourseDiscoveryEnabled());
            assertNull(config.getCourseDiscoveryConfig().getCourseSearchUrl());
            assertNull(config.getCourseDiscoveryConfig().getCourseInfoUrlTemplate());
        }

        @Test
        public void testEnrollmentConfig() {
            JsonObject configBase = new JsonObject();
            JsonObject enrollmentConfig = new JsonObject();
            enrollmentConfig.add(TYPE, new JsonPrimitive(course_enrollment_type));
            enrollmentConfig.add(COURSE_SEARCH_URL, new JsonPrimitive("fake-url"));
            enrollmentConfig.add(COURSE_INFO_URL_TEMPLATE, new JsonPrimitive("fake-url-template"));
            configBase.add(COURSE_ENROLLMENT, enrollmentConfig);

            Config config = new Config(configBase);
            assertEquals(config.getCourseDiscoveryConfig().isWebviewCourseDiscoveryEnabled(), expected);
            assertEquals(config.getCourseDiscoveryConfig().getCourseSearchUrl(), "fake-url");
            assertEquals(config.getCourseDiscoveryConfig().getCourseInfoUrlTemplate(), "fake-url-template");
        }

    }

    @Test
    public void testFacebookNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getFacebookConfig().isEnabled());
        assertNull(config.getFacebookConfig().getFacebookAppId());
    }

    @Test
    public void testFacebookEmptyConfig() {
        JsonObject fbConfig = new JsonObject();

        JsonObject configBase = new JsonObject();
        configBase.add(FACEBOOK, fbConfig);

        Config config = new Config(configBase);
        assertFalse(config.getFacebookConfig().isEnabled());
        assertNull(config.getFacebookConfig().getFacebookAppId());
    }

    @Test
    public void testFacebookConfig() {
        String appId = "fake-app-id";

        JsonObject fbConfig = new JsonObject();
        fbConfig.add(ENABLED, new JsonPrimitive(true));
        fbConfig.add(FACEBOOK_APP_ID, new JsonPrimitive(appId));

        JsonObject configBase = new JsonObject();
        configBase.add(FACEBOOK, fbConfig);

        Config config = new Config(configBase);
        assertTrue(config.getFacebookConfig().isEnabled());
        assertEquals(appId, config.getFacebookConfig().getFacebookAppId());
    }

    @Test
    public void testGoogleNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getGoogleConfig().isEnabled());
    }

    @Test
    public void testGoogleEmptyConfig() {
        JsonObject googleConfig = new JsonObject();

        JsonObject configBase = new JsonObject();
        configBase.add(GOOGLE, googleConfig);

        Config config = new Config(configBase);
        assertFalse(config.getGoogleConfig().isEnabled());
    }

    @Test
    public void testGoogleConfig() {
        JsonObject googleConfig = new JsonObject();
        googleConfig.add(ENABLED, new JsonPrimitive(true));

        JsonObject configBase = new JsonObject();
        configBase.add(GOOGLE, googleConfig);

        Config config = new Config(configBase);
        assertTrue(config.getGoogleConfig().isEnabled());
    }


    @Test
    public void testParseNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getParseNotificationConfig().isEnabled());
        assertNull(config.getParseNotificationConfig().getParseApplicationId());
        assertNull(config.getParseNotificationConfig().getParseClientKey());
    }

    @Test
    public void testParseConfig() {
        String key = "fake-key";
        String secret = "fake-secret";

        JsonObject parseConfig = new JsonObject();
        parseConfig.add(PARSE_ENABLED, new JsonPrimitive(true));
        parseConfig.add(PARSE_APPLICATION_ID, new JsonPrimitive(key));
        parseConfig.add(PARSE_CLIENT_KEY, new JsonPrimitive(secret));

        JsonObject configBase = new JsonObject();
        configBase.add(PARSE, parseConfig);

        Config config = new Config(configBase);
        assertTrue(config.getParseNotificationConfig().isEnabled());
        assertEquals(key, config.getParseNotificationConfig().getParseApplicationId());
        assertEquals(secret, config.getParseNotificationConfig().getParseClientKey());
    }

    @Test
    public void testFabricNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getFabricConfig().isEnabled());
        assertNull(config.getFabricConfig().getFabricKey());
        assertNull(config.getFabricConfig().getFabricBuildSecret());
    }

    @Test
    public void testFabricEmptyConfig() {
        JsonObject fabricConfig = new JsonObject();

        JsonObject configBase = new JsonObject();
        configBase.add(FABRIC, fabricConfig);

        Config config = new Config(configBase);
        assertFalse(config.getFabricConfig().isEnabled());
        assertNull(config.getFabricConfig().getFabricKey());
        assertNull(config.getFabricConfig().getFabricBuildSecret());
    }

    @Test
    public void testFabricConfig() {
        String key = "fake-key";
        String secret = "fake-secret";

        JsonObject fabricConfig = new JsonObject();
        fabricConfig.add(ENABLED, new JsonPrimitive(true));
        fabricConfig.add(FABRIC_KEY, new JsonPrimitive(key));
        fabricConfig.add(FABRIC_BUILD_SECRET, new JsonPrimitive(secret));

        JsonObject configBase = new JsonObject();
        configBase.add(FABRIC, fabricConfig);

        Config config = new Config(configBase);
        assertTrue(config.getFabricConfig().isEnabled());
        assertEquals(key, config.getFabricConfig().getFabricKey());
        assertEquals(secret, config.getFabricConfig().getFabricBuildSecret());
    }

    @Test
    public void testNewRelicNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getNewRelicConfig().isEnabled());
        assertNull(config.getNewRelicConfig().getNewRelicKey());
    }

    @Test
    public void testNewRelicEmptyConfig() {
        JsonObject fabricConfig = new JsonObject();

        JsonObject configBase = new JsonObject();
        configBase.add(NEW_RELIC, fabricConfig);

        Config config = new Config(configBase);
        assertFalse(config.getNewRelicConfig().isEnabled());
        assertNull(config.getNewRelicConfig().getNewRelicKey());
    }

    @Test
    public void testNewRelicConfig() {
        String key = "fake-key";

        JsonObject newRelicConfig = new JsonObject();
        newRelicConfig.add(ENABLED, new JsonPrimitive(true));
        newRelicConfig.add(NEW_RELIC_KEY, new JsonPrimitive(key));

        JsonObject configBase = new JsonObject();
        configBase.add(NEW_RELIC, newRelicConfig);

        Config config = new Config(configBase);
        assertTrue(config.getNewRelicConfig().isEnabled());
        assertEquals(key, config.getNewRelicConfig().getNewRelicKey());
    }

    @Test
    public void testSegmentNoConfig() {
        JsonObject configBase = new JsonObject();
        Config config = new Config(configBase);
        assertFalse(config.getSegmentConfig().isEnabled());
        assertNull(config.getSegmentConfig().getSegmentWriteKey());
    }

    @Test
    public void testSegmentEmptyConfig() {
        JsonObject segmentConfig = new JsonObject();

        JsonObject configBase = new JsonObject();
        configBase.add(SEGMENT_IO, segmentConfig);

        Config config = new Config(configBase);
        assertFalse(config.getSegmentConfig().isEnabled());
        assertNull(config.getSegmentConfig().getSegmentWriteKey());
    }

    @Test
    public void testSegmentConfig() {
        String key = "fake-key";

        JsonObject segmentConfig = new JsonObject();
        segmentConfig.add(ENABLED, new JsonPrimitive(true));
        segmentConfig.add(SEGMENT_IO_WRITE_KEY, new JsonPrimitive(key));

        JsonObject configBase = new JsonObject();
        configBase.add(SEGMENT_IO, segmentConfig);

        Config config = new Config(configBase);
        assertTrue(config.getSegmentConfig().isEnabled());
        assertEquals(key, config.getSegmentConfig().getSegmentWriteKey());
    }
}
