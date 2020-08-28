/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2010  HydroloGIS (www.hydrologis.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.geopaparazzi.map.jts;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class for JTS.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class JtsUtilities {

    private static final double DELTA = 0.0000001;
    private static GeometryFactory gf = new GeometryFactory();

    /**
     * Create a {@link Polygon} from a list of coordinates.
     *
     * @param coordinatesList the list of coordinates.
     * @return the created polygon.
     */
    public static Polygon createPolygon(List<Coordinate> coordinatesList) {
        coordinatesList = new ArrayList<>(coordinatesList);
        Coordinate firstCoord = coordinatesList.get(0);
        coordinatesList.add(firstCoord);
        return gf.createPolygon(coordinatesList.toArray(new Coordinate[0]));
    }

    /**
     * Create a {@link LineString} from a list of coordinates.
     *
     * @param coordinatesList the list of coordinates.
     * @return the created line.
     */
    public static LineString createLineString(List<Coordinate> coordinatesList) {
        return gf.createLineString(coordinatesList.toArray(new Coordinate[0]));
    }

    /**
     * Create {@link MultiPoint}s from a list of coordinates.
     *
     * @param coordinatesList the list of coordinates.
     * @return the created points.
     */
    public static MultiPoint createPoints(List<Coordinate> coordinatesList) {
        return gf.createMultiPointFromCoords(coordinatesList.toArray(new Coordinate[0]));
    }

    /**
     * Create vertex points as polygons from a list of coordinates.
     *
     * @param coordinatesList the list of coordinates.
     * @return the created points.
     */
    public static Geometry createVertexBuffers(List<Coordinate> coordinatesList) {
        MultiPoint multiPoints = gf.createMultiPointFromCoords(coordinatesList.toArray(new Coordinate[0]));
        return multiPoints.buffer(0.0001);
    }

}
