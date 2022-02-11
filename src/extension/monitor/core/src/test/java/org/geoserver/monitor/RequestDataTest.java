package org.geoserver.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.*;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class RequestDataTest {

    private static final CoordinateReferenceSystem CRS = DefaultGeographicCRS.WGS84;

    private static final String POLYGON_1 = "POLYGON ((120 35, 120 40, 115 40, 115 35, 120 35))";
    private static final String POLYGON_2 = "POLYGON ((117 37, 117 38, 116 38, 116 37, 117 37))";
    private static final String POLYGON_3 = "POLYGON ((1 1, 4 1, 4 3, 1 3, 1 1))";
    private static final String POLYGON_4 = "POLYGON ((2 2, 3 2, 3 4, 2 4, 2 2))";
    private static final String POLYGON_5 = "POLYGON ((2 4, 3 4, 3 5, 2 5, 2 4))";
    private static final String POLYGON_6 =
            "POLYGON ((-98.3411610007752 33.84983160588202,-98.3411610007752 32.92210622610406,-98.3411610007752 31.994380846326116,-96.78433429050405 31.994380846326116,-95.2275075802329 31.994380846326116,-95.2275075802329 32.92210622610406,-95.2275075802329 33.84983160588202,-96.78433429050405 33.84983160588202,-98.3411610007752 33.84983160588202))";

    private static final String CENTROID_1 = "POINT (117.5 37.5)";
    private static final String CENTROID_2 = "POINT (116.5 37.5)";
    private static final String CENTROID_3 = "POINT (2.5 2)";
    private static final String CENTROID_4 = "POINT (2.5 3)";
    private static final String CENTROID_5 = "POINT (2.5 4.5)";
    private static final String CENTROID_6 = "POINT (-97.04677502305373 32.90475063799967)";

    /*
     * RequestData.getBboxCentroidWkt() tests
     */

    @Test
    public void testBboxCentroidWkt1() {
        RequestData rd = new RequestData();
        String wkt = rd.getBboxCentroidWkt();

        assertNull(wkt);
    }

    @Test
    public void testBboxCentroidWkt2() {
        BoundingBox bbox =
                new ReferencedEnvelope(
                        -117.14141615693983,
                        -117.19950166515697,
                        37.034726090346105,
                        37.09281159856325,
                        CRS);
        RequestData rd = new RequestData();
        rd.setBbox(bbox);

        String expectedWkt = "POINT (-117.1704589110484 37.06376884445468)";
        String wkt = rd.getBboxCentroidWkt();

        assertEquals(expectedWkt, wkt);
    }

    /*
     * RequestData.getQueryCentroidWkts() tests
     */

    @Test
    public void testQueryCentroidWkt1() {
        RequestData rd = new RequestData();
        rd.setQueryString(null);

        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(Collections.emptyList(), wkts);
    }

    @Test
    public void testQueryCentroidWkt2() {
        RequestData rd = new RequestData();
        String queryString = String.format("INTERSECTS (attr1, %s)", POLYGON_1);
        rd.setQueryString(queryString);

        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(Collections.emptyList(), wkts);
    }

    @Test
    public void testQueryCentroidWkt3() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=INTERSECTS (attr2, %s);service=WFS;srsName=EPSG:4326",
                        POLYGON_2);
        rd.setQueryString(queryString);

        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(Collections.emptyList(), wkts);
    }

    @Test
    public void testQueryCentroidWkt4() {
        RequestData rd = new RequestData();
        String queryString = "CQL_FILTER=INCLUDE";
        rd.setQueryString(queryString);

        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(Collections.emptyList(), wkts);
    }

    @Test
    public void testQueryCentroidWkt5() {
        RequestData rd = new RequestData();
        String queryString = "CQL_FILTER=attr1 LIKE 'foo'";
        rd.setQueryString(queryString);

        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(Collections.emptyList(), wkts);
    }

    @Test
    public void testQueryCentroidWkt6() {
        RequestData rd = new RequestData();
        String queryString = String.format("CQL_FILTER=INTERSECTS (attr1, %s)", POLYGON_1);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList(CENTROID_1);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt7() {
        RequestData rd = new RequestData();
        String queryString = String.format("cql_filter=INTERSECTS (attr1, %s)", POLYGON_1);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList(CENTROID_1);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt8() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=INTERSECTS (attr1, %s)&service=WFS&srsName=EPSG:4326",
                        POLYGON_1);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList(CENTROID_1);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt9() {
        RequestData rd = new RequestData();
        String queryString =
                "CQL_FILTER=BBOX (attr1, -117.14141615693983, 37.034726090346105, -117.19950166515697, 37.09281159856325)";
        rd.setQueryString(queryString);

        List<String> expectedWkts =
                Collections.singletonList("POINT (-117.1704589110484 37.06376884445468)");
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt10() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=(INTERSECTS (attr1, %s)) AND (INTERSECTS (attr2, %s))",
                        POLYGON_1, POLYGON_2);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Arrays.asList(CENTROID_1, CENTROID_2);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt11() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=(INTERSECTS (attr1, %s)) AND (INTERSECTS (attr1, %s))",
                        POLYGON_1, POLYGON_2);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList(CENTROID_2);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt12() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=(INTERSECTS (attr1, %s)) AND (INTERSECTS (attr1, %s))",
                        POLYGON_3, POLYGON_4);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList("POINT (2.5 2.5)");
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt13() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=(INTERSECTS (attr1, %s)) OR (INTERSECTS (attr1, %s))",
                        POLYGON_3, POLYGON_5);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Arrays.asList(CENTROID_3, CENTROID_5);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }

    @Test
    public void testQueryCentroidWkt14() {
        RequestData rd = new RequestData();
        String queryString =
                String.format(
                        "CQL_FILTER=(INTERSECTS (attr1, %s)) AND (BBOX (attr1,-97.04690330744837,32.904622353605006,-97.04664673865905,32.90487892239433))&",
                        POLYGON_6);
        rd.setQueryString(queryString);

        List<String> expectedWkts = Collections.singletonList(CENTROID_6);
        List<String> wkts = rd.getQueryCentroidWkts();

        assertEquals(expectedWkts, wkts);
    }
}
