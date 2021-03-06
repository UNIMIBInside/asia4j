package it.unimib.disco.asia;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import it.unimib.disco.asia.model.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ASIA4JTest {

    private static final String asiaEndpoint = "http://localhost:9999/";
    private static ASIA4J client;

    @Rule
    public WireMockRule asiaService = new WireMockRule(9999);

    @BeforeClass
    public static void setUp() {
        client = ASIA4JFactory.getClient(asiaEndpoint);
    }

    @Test
    public void testFactory() {
        ASIA4J oldInstance = ASIA4JFactory.getClient(asiaEndpoint, GrafterizerClient.class);
        Assert.assertEquals(oldInstance, client);
        ASIA4J newInstance = ASIA4JFactory.getClient("http://localhost:8088");
        Assert.assertNotEquals(oldInstance, newInstance);

        System.setProperty("asiaEndpoint", asiaEndpoint);
        ASIA4J sameClient = ASIA4JFactory.getClient();
        Assert.assertEquals(client, sameClient);

        ASIA4J httpClient = ASIA4JFactory.getClient("http", GrafterizerClient.class);
        ASIA4J httpHashClient = ASIA4JFactory.getClient("http", GrafterizerHashtableClient.class);
        ASIA4J sameHttpClient = ASIA4JFactory.getClient("http", GrafterizerClient.class);
        Assert.assertTrue(httpClient instanceof GrafterizerClient);
        Assert.assertTrue(httpHashClient instanceof GrafterizerHashtableClient);
        Assert.assertEquals(httpClient, sameHttpClient);
    }

    @Test
    public void testHashClient() {
        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\":{\"query\":\"Berlin\",  \"type\":\"A.ADM1\", \"type_strict\":\"should\"}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": [\n" +
                                "            {\n" +
                                "                \"id\": \"2950157\",\n" +
                                "                \"name\": \"Land Berlin\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM1\",\n" +
                                "                        \"name\": \"A.ADM1\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 36.59111785888672,\n" +
                                "                \"match\": false\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Steinheim an der Murr\", \"type\":\"A.ADM4\", \"type_strict\":\"should\", \"properties\" : [\n" +
                        "      { \"p\" : \"http://www.geonames.org/ontology#countryCode\", \"v\" : \"DE\" }, { \"p\" : \"http://www.geonames.org/ontology#parentADM1\",\"v\" : { \"id\" : \"2951481\" } }\n" +
                        "    ]}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": [\n" +
                                "            {\n" +
                                "                \"id\": \"6557964\",\n" +
                                "                \"name\": \"Steinheim an der Murr\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM4\",\n" +
                                "                        \"name\": \"A.ADM4\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 137.01950073242188,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"2827988\",\n" +
                                "                \"name\": \"Steinheim am der Murr\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"P.PPL\",\n" +
                                "                        \"name\": \"P.PPL\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 120.79658508300781,\n" +
                                "                \"match\": false\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Steinheim an der Murr\", \"type\":\"A.ADM3\", \"type_strict\":\"should\", \"properties\" : [\n" +
                        "      { \"p\" : \"http://www.geonames.org/ontology#countryCode\", \"v\" : \"DE\" }, { \"p\" : \"http://www.geonames.org/ontology#parentADM1\",\"v\" : { \"id\" : \"2951481\" } }\n" +
                        "    ]}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": []\n" +
                                "    }\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Steinheim an der Murr\", \"type\":\"A.ADM3\", \"type_strict\":\"should\", \"properties\" : [\n" +
                        "      { \"p\" : \"http://www.geonames.org/ontology#parentADM1\",\"v\" : { \"id\" : \"2951481\" } }\n" +
                        "    ]}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": []\n" +
                                "    }\n" +
                                "}")));

        ASIA4J hClient = ASIA4JFactory.getClient(asiaEndpoint, GrafterizerHashtableClient.class);

        hClient.reconcile(
                new Annotation(
                        new SingleColumnReconciliation(
                                "Berlin",
                                0.1,
                                Collections.singletonList("A.ADM1")
                        ),
                        "geonames"));
        hClient.reconcile(
                new Annotation(
                        new SingleColumnReconciliation(
                                "Berlin",
                                0.1,
                                Collections.singletonList("A.ADM1")),
                        "geonames")); // HASHMAP HIT

        ((GrafterizerHashtableClient) hClient)
                .reconcileSingleColumn("Berlin", "A.ADM1", .1, "geonames"); // HASHMAP HIT

        Assert.assertEquals(
                1,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

        List<ReconciliationSupportColumn> rsc = new ArrayList<>();
        rsc.add(new ReconciliationSupportColumn(
                "DE",
                "http://www.geonames.org/ontology#countryCode",
                SimilarityMeasure.EDIT,
                0.5,
                null));
        rsc.add(new ReconciliationSupportColumn(
                "2951481",
                "http://www.geonames.org/ontology#parentADM1",
                SimilarityMeasure.EDIT,
                0.5,
                new Annotation(null, null, "geonames")));

        hClient.reconcile(
                new Annotation(
                        new MultiColumnReconciliation(
                                "Steinheim an der Murr",
                                0.1,
                                Collections.singletonList("A.ADM4"),
                                rsc
                        ), "geonames"));

        hClient.reconcile(
                new Annotation(
                        new MultiColumnReconciliation(
                                "Steinheim an der Murr",
                                0.1,
                                Collections.singletonList("A.ADM4"),
                                rsc
                        ), "geonames"));

        Assert.assertEquals(
                2,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

        client.reconcile(new Annotation(new SingleColumnReconciliation(
                "Berlin",
                0.1,
                Collections.singletonList("A.ADM1")
        ), "geonames"));

        Assert.assertEquals(
                3,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

        hClient.reconcile(new Annotation(new SingleColumnReconciliation(
                "Berlin",
                0.2, // threshold changes
                Collections.singletonList("A.ADM1")
        ), "geonames"));

        Assert.assertEquals(
                4,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

        hClient.reconcile(
                new Annotation(
                        new MultiColumnReconciliation(
                                "Steinheim an der Murr",
                                0.1,
                                Collections.singletonList("A.ADM3"), // type changes
                                rsc
                        ), "geonames"));

        Assert.assertEquals(
                5,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

        rsc.remove(0);
        hClient.reconcile(
                new Annotation(
                        new MultiColumnReconciliation(
                                "Steinheim an der Murr",
                                0.1,
                                Collections.singletonList("A.ADM3"),
                                rsc // support columns list changes
                        ), "geonames"));

        Assert.assertEquals(
                6,
                asiaService.countRequestsMatching(getRequestedFor(urlMatching("/reconcile?.*")).build()).getCount());

    }

    @Test
    public void testReconciliation() {

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Berlin\"}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": [\n" +
                                "            {\n" +
                                "                \"id\": \"6547539\",\n" +
                                "                \"name\": \"Berlin\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM4\",\n" +
                                "                        \"name\": \"A.ADM4\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 55.95427703857422,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"10332205\",\n" +
                                "                \"name\": \"Berlin\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"P.PPL\",\n" +
                                "                        \"name\": \"P.PPL\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 55.2410774230957,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"10332842\",\n" +
                                "                \"name\": \"Berlin\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"P.PPL\",\n" +
                                "                        \"name\": \"P.PPL\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 55.2410774230957,\n" +
                                "                \"match\": false\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\":{\"query\":\"Berlin\",  \"type\":\"A.ADM1\", \"type_strict\":\"should\"}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": [\n" +
                                "            {\n" +
                                "                \"id\": \"2950157\",\n" +
                                "                \"name\": \"Land Berlin\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM1\",\n" +
                                "                        \"name\": \"A.ADM1\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 36.59111785888672,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"9611689\",\n" +
                                "                \"name\": \"Mariehamns stad\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM1\",\n" +
                                "                        \"name\": \"A.ADM1\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 0,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"9611692\",\n" +
                                "                \"name\": \"Ålands landsbygd\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM1\",\n" +
                                "                        \"name\": \"A.ADM1\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 0,\n" +
                                "                \"match\": false\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("conciliator", equalTo("null"))
                .willReturn(badRequest()));

        Assert.assertEquals("", client.reconcile(null));
        Assert.assertEquals("",
                client.reconcile(new Annotation(new SingleColumnReconciliation(null, 0., null), null)));
        Assert.assertEquals("",
                client.reconcile(new Annotation(new SingleColumnReconciliation("Berlin", 0.1, null), null)));
        Assert.assertEquals("6547539",
                client.reconcile(new Annotation(new SingleColumnReconciliation("Berlin", 0.1, null), "geonames")));
        Assert.assertEquals("2950157",
                client.reconcile(new Annotation(new SingleColumnReconciliation("Berlin", 0.1, Collections.singletonList("A.ADM1")), "geonames")));

        GrafterizerClient gClient = (GrafterizerClient) client;
        Assert.assertEquals("", gClient.reconcileSingleColumn(null, null, 0., null));
        Assert.assertEquals("", gClient.reconcileSingleColumn("Berlin", null, 0.1, null));
        Assert.assertEquals("6547539",
                gClient.reconcileSingleColumn("Berlin", null, 0.1, "geonames"));
        Assert.assertEquals("2950157",
                gClient.reconcileSingleColumn("Berlin", "A.ADM1", 0.1, "geonames"));
    }

    @Test
    public void testMultiColReconciliation() {
        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Steinheim an der Murr\", \"properties\" : [\n" +
                        "      { \"p\" : \"http://www.geonames.org/ontology#countryCode\", \"v\" : \"DE\" }, { \"p\" : \"http://www.geonames.org/ontology#parentADM1\",\"v\" : { \"id\" : \"2953481\" } }\n" +
                        "    ]}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": []\n" +
                                "    },\n" +
                                "}")));

        asiaService.stubFor(get(urlMatching("/reconcile?.*"))
                .withQueryParam("queries", equalToJson("{\"q0\": {\"query\": \"Steinheim an der Murr\", \"properties\" : [\n" +
                        "      { \"p\" : \"http://www.geonames.org/ontology#countryCode\", \"v\" : \"DE\" }, { \"p\" : \"http://www.geonames.org/ontology#parentADM1\",\"v\" : { \"id\" : \"2951481\" } }\n" +
                        "    ]}}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"q0\": {\n" +
                                "        \"result\": [\n" +
                                "            {\n" +
                                "                \"id\": \"6557964\",\n" +
                                "                \"name\": \"Steinheim an der Murr\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"A.ADM4\",\n" +
                                "                        \"name\": \"A.ADM4\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 137.01950073242188,\n" +
                                "                \"match\": false\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"id\": \"2827988\",\n" +
                                "                \"name\": \"Steinheim am der Murr\",\n" +
                                "                \"type\": [\n" +
                                "                    {\n" +
                                "                        \"id\": \"P.PPL\",\n" +
                                "                        \"name\": \"P.PPL\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"score\": 120.79658508300781,\n" +
                                "                \"match\": false\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}")));

        Annotation parentADM1 = new Annotation(null, null, "geonames");

        List<ReconciliationSupportColumn> rscQ0 = new ArrayList<>();
        rscQ0.add(new ReconciliationSupportColumn("DE", "http://www.geonames.org/ontology#countryCode", SimilarityMeasure.EDIT, 0.5, null));
        rscQ0.add(new ReconciliationSupportColumn("2953481", "http://www.geonames.org/ontology#parentADM1", SimilarityMeasure.EDIT, 0.5, parentADM1));

        List<ReconciliationSupportColumn> rscQ1 = new ArrayList<>();
        rscQ1.add(new ReconciliationSupportColumn("DE", "http://www.geonames.org/ontology#countryCode", SimilarityMeasure.EDIT, 0.5, null));
        rscQ1.add(new ReconciliationSupportColumn("2951481", "http://www.geonames.org/ontology#parentADM1", SimilarityMeasure.EDIT, 0.5, parentADM1));

        Assert.assertEquals("",
                client.reconcile(new Annotation(new MultiColumnReconciliation("Steinheim an der Murr", 0., null, rscQ0), "geonames")));
        Assert.assertEquals("6557964",
                client.reconcile(new Annotation(new MultiColumnReconciliation("Steinheim an der Murr", 0., null, rscQ1), "geonames")));
    }

    @Test
    public void testExtension() {

        asiaService.stubFor(get(urlMatching("/extend?.*"))
                .withQueryParam("extend",
                        equalToJson("{\"ids\":[\"6554818\"],\"properties\":[{\"id\":\"parentADM1\"}]}"))
                .withQueryParam("conciliator", equalTo("geonames"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("{\n" +
                                "    \"meta\": [\n" +
                                "        {\n" +
                                "            \"id\": \"parentADM1\",\n" +
                                "            \"name\": \"parentADM1\",\n" +
                                "            \"type\": {\n" +
                                "                \"id\": \"A.ADM1\",\n" +
                                "                \"name\": \"A.ADM1\"\n" +
                                "            }\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"rows\": {\n" +
                                "        \"6554818\": {\n" +
                                "            \"parentADM1\": [\n" +
                                "                {\n" +
                                "                    \"id\": \"2847618\",\n" +
                                "                    \"name\": \"Rheinland-Pfalz\"\n" +
                                "                }\n" +
                                "            ]\n" +
                                "        }\n" +
                                "    }\n" +
                                "}\n")));


        asiaService.stubFor(get(urlMatching("/extend?.*"))
                .withQueryParam("extend",
                        equalToJson("{\"ids\":[\"null\"],\"properties\":[{\"id\":\"null\"}]}"))
                .willReturn(aResponse().withBody("{\n" +
                        "    \"meta\": [\n" +
                        "        null\n" +
                        "    ],\n" +
                        "    \"rows\": {\n" +
                        "        \"null\": {\n" +
                        "            \"null\": []\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")));

        asiaService.stubFor(get(urlMatching("/extend?.*"))
                .withQueryParam("extend",
                        equalToJson("{\"ids\":[\"null\"],\"properties\":[{\"id\":\"parentADM1\"}]}"))
                .willReturn(aResponse().withBody("{\n" +
                        "    \"meta\": [\n" +
                        "        {\n" +
                        "            \"id\": \"parentADM1\",\n" +
                        "            \"name\": \"parentADM1\",\n" +
                        "            \"type\": {\n" +
                        "                \"id\": \"A.ADM1\",\n" +
                        "                \"name\": \"A.ADM1\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"rows\": {\n" +
                        "        \"null\": {\n" +
                        "            \"parentADM1\": []\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")));

        asiaService.stubFor(get(urlMatching("/extend?.*"))
                .withQueryParam("extend",
                        equalToJson("{\"ids\":[\"6554818\"],\"properties\":[{\"id\":\"null\"}]}"))
                .willReturn(aResponse().withBody("{\n" +
                        "    \"meta\": [\n" +
                        "        null\n" +
                        "    ],\n" +
                        "    \"rows\": {\n" +
                        "        \"6554818\": {\n" +
                        "            \"null\": []\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")));

        asiaService.stubFor(get(urlMatching("/extend?.*"))
                .withQueryParam("conciliator", equalTo("null"))
                .willReturn(serverError()));

        Assert.assertEquals("", client.extendFromConciliator(null, null, null));
        Assert.assertEquals("", client.extendFromConciliator(null, null, "geonames"));
        Assert.assertEquals("", client.extendFromConciliator("6554818", null, null));
        Assert.assertEquals("", client.extendFromConciliator(null, "parentADM1", null));
        Assert.assertEquals("", client.extendFromConciliator("6554818", "parentADM1", null));
        Assert.assertEquals("2847618",
                client.extendFromConciliator("6554818", "parentADM1", "geonames"));
    }

    @Test
    public void testWeather() {
        asiaService.stubFor(get(urlMatching("/weather?.*"))
                .withQueryParam("ids", equalTo("null"))
                .willReturn(serverError()));

        asiaService.stubFor(get(urlMatching("/weather?.*"))
                .withQueryParam("dates", equalTo("null"))
                .willReturn(serverError()));

        asiaService.stubFor(get(urlMatching("/weather?.*"))
                .withQueryParam("weatherParams", equalTo("null"))
                .willReturn(serverError()));

        asiaService.stubFor(get(urlMatching("/weather?.*"))
                .withQueryParam("offsets", equalTo("null"))
                .willReturn(serverError()));

        asiaService.stubFor(get(urlMatching("/weather?.*"))
                .withQueryParam("ids", equalTo("2953481"))
                .withQueryParam("dates", equalTo("2018-07-25"))
                .withQueryParam("aggregators", equalTo("min"))
                .withQueryParam("weatherParams", equalTo("2t"))
                .withQueryParam("offsets", equalTo("1"))
                .willReturn(aResponse().withBody("[" +
                        "{\"geonamesId\":\"2953481\"," +
                        "\"date\":\"2018-07-25T00:00:00Z\"," +
                        "\"weatherParameters\":" +
                        "[" +
                        "{\"id\":\"2t\",\"minValue\":293.3830516582,\"maxValue\":297.5957928093,\"avgValue\":295.700742760475}],\"offset\":1}" +
                        "]")));

        Assert.assertEquals("",
                client.extendWeather(null, null, null, null, null));
        Assert.assertEquals("",
                client.extendWeather("2953481", null, null, "2t", "1"));
        Assert.assertEquals("",
                client.extendWeather("2953481", "2018-07-25", null, null, "1"));
        Assert.assertEquals("293.3830516582",
                client.extendWeather("2953481", "2018-07-25", "min", "2t", "1"));
        Assert.assertEquals("293.3830516582",
                client.extendWeather("2953481", "20180725", "min", "2t", "1"));
    }



    @Test
    public void testExtendSameAs() {
        asiaService.stubFor(get(urlMatching("/geoExactMatch?.*"))
                .withQueryParam("ids", equalTo("Milan"))
                .withQueryParam("source", equalTo("dbpedia"))
                .withQueryParam("target", equalTo("wikidata"))
                .willReturn(aResponse().withBody("{\n" +
                        "    \"meta\": {\n" +
                        "        \"id\": \"http://www.w3.org/2004/02/skos/core#exactMatch\",\n" +
                        "        \"name\": \"exactMatch\"\n" +
                        "    },\n" +
                        "    \"rows\": {\n" +
                        "        \"Milan\": {\n" +
                        "            \"exactMatch\": [\n" +
                        "                {\n" +
                        "                    \"str\": \"http://www.wikidata.org/entity/Q49295412\"\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")));
        asiaService.stubFor(get(urlMatching("/geoExactMatch?.*"))
                .withQueryParam("source", equalTo("null"))
                .willReturn(serverError()));
        asiaService.stubFor(get(urlMatching("/geoExactMatch?.*"))
                .withQueryParam("target", equalTo("null"))
                .willReturn(serverError()));
        asiaService.stubFor(get(urlMatching("/geoExactMatch?.*"))
                .withQueryParam("ids", equalTo("null"))
                .willReturn(serverError()));

        Assert.assertEquals("",
                client.extendSameAs(null, null, null));
        Assert.assertEquals("",
                client.extendSameAs("Milan", "dbpedia", null));
        Assert.assertEquals("",
                client.extendSameAs("Milan", null, "wikidata"));
        Assert.assertEquals("",
                client.extendSameAs(null, "dbpedia", "wikidata"));
        Assert.assertEquals("http://www.wikidata.org/entity/Q49295412",
                client.extendSameAs("Milan", "dbpedia", "wikidata"));
    }

}
