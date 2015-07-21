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
package eu.geopaparazzi.spatialite.database.spatial.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;

import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.spatialite.database.spatial.core.tables.SpatialVectorTable;
import jsqlite.Database;
import jsqlite.Stmt;

/**
 * SpatialiteUtilities class.

 * @author Mark Johnson
 */
@SuppressWarnings("nls")
public class SpatialiteUtilities {
    /**
     * Name of the table field that s used to identify the record.
     */
    public static final String SPATIALTABLE_ID_FIELD = "ROWID"; //$NON-NLS-1$

    /**
     * Array of fields that will be ingored in attributes handling.
     */
    public static String[] IGNORED_FIELDS = {SPATIALTABLE_ID_FIELD, "PK_UID", "_id"};
     
    /**
     * Name/path separator for spatialite table names.
     */
    public static final String UNIQUENAME_SEPARATOR = "#"; //$NON-NLS-1$

    /**
     * Active Connection to a  Spatialite Database
    * <p/>
     * <p>Indented for general use for Transformation etc.
     */
    private static Database dbSpatialite=null;
    /**
     * Active Connection to a  Spatialite Database
    * <p/>
     * <p>Indented for general use for Transformation etc.
     */
    private static Database dbMemSpatialite=null;
    
     /**
     * Retrieve an active spatialite Connection
     * @param i_parm 0:[default] retrieve only ; 1=create Memory-db if not active
     * @return dbSpatialite for general use
     */
    public static Database getDatabase(int i_parm) {
        if (dbSpatialite == null)
        { 
         if (i_parm == 1)
         { // TODO: create Memory Database with Spatialite 'SELECT InitSpatialMetadata(1);'
          dbSpatialite=getMemoryDatabase();
         }
        }
        return dbSpatialite;
    }
    /**
     * Retrieve an active spatialite-memory Connection
     *  'mapsforge_extracted.sqlite' without full 'spatial_ref_sys' 151.5 KB
     * --> not usable for Transformations
     *  INSERT INTO spatial_ref_sys SELECT * FROM a.spatial_ref_sys WHERE srid NOT IN (-1,0,4326)
     *  'mapsforge_extracted.sqlite' with full 'spatial_ref_sys' 5.3 MB
     * @return dbSpatialite for general use
     */
    public static Database getMemoryDatabase() {
        if (dbMemSpatialite == null)
        { 
         dbMemSpatialite = new jsqlite.Database();
            try {
                dbMemSpatialite.open("file::memory:?cache=shared", jsqlite.Constants.SQLITE_OPEN_READWRITE | jsqlite.Constants.SQLITE_OPEN_CREATE);
                dbMemSpatialite.exec("SELECT InitSpatialMetadata(1)", null);
            } catch (Exception e) {
                GPLog.androidLog(4, "SpatialiteUtilities.getMemoryDatabase[file::memory:?cache=shared'].open has failed", e);
            }
         }
        return dbMemSpatialite;
    }
    
    /**
     * Set an active spatialite Connection for genral use
     * - should only be done when sure that it a valid Spatialite Database ( version >= 4)
     * -- done in DatabaseCreationAndProperties.checkDatabaseTypeAndValidity()
     * @return dbSpatialite
     */
    public static void setDatabase(Database spatialite_db) {
        if ((dbSpatialite == null) && (spatialite_db != null))
        { 
         dbSpatialite=spatialite_db;
        }
    }

    /**
     * Checks if a field needs to be ignores.
     * 
     * @param field the field to check. 
     * @return <code>true</code> if the field needs to be ignored.
     */
    public static boolean doIgnoreField( String field ) {
        for( String ingoredField : SpatialiteUtilities.IGNORED_FIELDS ) {
            if (field.equals(ingoredField)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Build a query to retrieve geometries from a table in a given bound.
     *
     * @param destSrid the destination srid.
     * @param withRowId if <code>true</code>, the ROWID is added in position 0 of the query.
     * @param table the table to use.
     * @param n north bound.
     * @param s south bound.
     * @param e east bound.
     * @param w west bound.
     * @return the query.
     */
    public static String buildGeometriesInBoundsQuery( String destSrid, boolean withRowId, SpatialVectorTable table, double n,
            double s, double e, double w ) {
        boolean doTransform = false;
        if (!table.getSrid().equals(destSrid)) {
            doTransform = true;
        }
        StringBuilder mbrSb = new StringBuilder();
        if (doTransform)
            mbrSb.append("ST_Transform(");
        mbrSb.append("BuildMBR(");
        mbrSb.append(w);
        mbrSb.append(",");
        mbrSb.append(n);
        mbrSb.append(",");
        mbrSb.append(e);
        mbrSb.append(",");
        mbrSb.append(s);
        if (doTransform) {
            mbrSb.append(",");
            mbrSb.append(destSrid);
            mbrSb.append("),");
            mbrSb.append(table.getSrid());
        }
        mbrSb.append(")");
        String mbr = mbrSb.toString();
        StringBuilder qSb = new StringBuilder();
        qSb.append("SELECT ");
        if (withRowId) {
            qSb.append(SPATIALTABLE_ID_FIELD).append(",");
        }
        qSb.append("ST_AsBinary(CastToXY(");
        if (doTransform)
            qSb.append("ST_Transform(");
        qSb.append(table.getGeomName());
        if (doTransform) {
            qSb.append(",");
            qSb.append(destSrid);
            qSb.append(")");
        }
        qSb.append("))");
        if (table.getStyle().labelvisible == 1) {
            qSb.append(",");
            qSb.append(table.getStyle().labelfield);
        }
        qSb.append(" FROM ");
        qSb.append(table.getTableName());
        // the SpatialIndex would be searching for a square, the ST_Intersects the Geometry
        // the SpatialIndex could be fulfilled, but checking the Geometry could return the result
        // that it is not
        qSb.append(" WHERE ST_Intersects(");
        qSb.append(table.getGeomName());
        qSb.append(", ");
        qSb.append(mbr);
        qSb.append(") = 1 AND ");
        qSb.append(table.getROWID());
        qSb.append("  IN (SELECT ");
        qSb.append(table.getROWID());
        qSb.append(" FROM Spatialindex WHERE f_table_name ='");
        qSb.append(table.getTableName());
        qSb.append("'");
        // if a table has more than 1 geometry, the column-name MUST be given, otherwise no results.
        qSb.append(" AND f_geometry_column = '");
        qSb.append(table.getGeomName());
        qSb.append("'");
        qSb.append(" AND search_frame = ");
        qSb.append(mbr);
        qSb.append(");");
        String q = qSb.toString();
        return q;
    }

    /**
     * Get the query to run for a bounding box intersection to retrieve features.
     * 
     * <p>This assures that the first element of the query is
     * the id field for the record as defined in {@link SpatialiteUtilities#SPATIALTABLE_ID_FIELD}
     * and the last one the geometry.
     * 
     * @param boundsSrid the srid of the bounds requested.
     * @param spatialTable the {@link SpatialVectorTable} to query.
     * @param n north bound.
     * @param s south bound.
     * @param e east bound.
     * @param w west bound.
     * @return the query to run to get all fields.
     */
    public static String getBboxIntersectingFeaturesQuery( String boundsSrid, SpatialVectorTable spatialTable, double n,
            double s, double e, double w ) {
        String query = null;
        boolean doTransform = false;
        String fieldNamesList = SpatialiteUtilities.SPATIALTABLE_ID_FIELD;
        // List of non-blob fields
        for( String field : spatialTable.getTableFieldNamesList() ) {
            boolean ignore = SpatialiteUtilities.doIgnoreField(field);
            if (!ignore)
                fieldNamesList += "," + field;
        }
        if (!spatialTable.getSrid().equals(boundsSrid)) {
            doTransform = true;
        }
        StringBuilder sbQ = new StringBuilder();
        sbQ.append("SELECT ");
        sbQ.append(fieldNamesList);
        sbQ.append(",ST_AsBinary(CastToXY(");
        if (doTransform)
            sbQ.append("ST_Transform(");
        sbQ.append(spatialTable.getGeomName());
        if (doTransform) {
            sbQ.append(",");
            sbQ.append(boundsSrid);
            sbQ.append(")");
        }
        sbQ.append("))");
        sbQ.append(" FROM ").append(spatialTable.getTableName());
        sbQ.append(" WHERE ST_Intersects(");
        if (doTransform)
            sbQ.append("ST_Transform(");
        sbQ.append("BuildMBR(");
        sbQ.append(w);
        sbQ.append(",");
        sbQ.append(s);
        sbQ.append(",");
        sbQ.append(e);
        sbQ.append(",");
        sbQ.append(n);
        if (doTransform) {
            sbQ.append(",");
            sbQ.append(boundsSrid);
            sbQ.append("),");
            sbQ.append(spatialTable.getSrid());
        }
        sbQ.append("),");
        sbQ.append(spatialTable.getGeomName());
        sbQ.append(");");

        query = sbQ.toString();
        return query;
    }

    /**
     * Collects bounds and center as wgs84 4326.
     * - Note: use of getEnvelopeInternal() insures that, after transformation,
     * -- possible false values are given - since the transformed result might not be square
     * 'dbSpatialite' is probly no nonger needed, when this function is called, 'dbSpatialite' must have been set
     * @param dbSpatialite Spatialite Database connection as fallback for Centeral Database connection
     * @param srid the source srid.
     * @param centerCoordinate the coordinate array to fill with the center.
     * @param boundsCoordinates the coordinate array to fill with the bounds as [w,s,e,n].
    */
    public static void collectBoundsAndCenter( Database Spatialitedb, String srid, double[] centerCoordinate,
            double[] boundsCoordinates ) {
        String centerQuery = "";
        try {
            Stmt centerStmt = null;
            double bounds_west = boundsCoordinates[0];
            double bounds_south = boundsCoordinates[1];
            double bounds_east = boundsCoordinates[2];
            double bounds_north = boundsCoordinates[3];
            /*
            SELECT ST_Transform(BuildMBR(14121.000000,187578.000000,467141.000000,48006927.000000,23030),4326);
             SRID=4326;POLYGON((
             -7.364919057793379 1.69098037889473,
             -3.296335497384673 1.695910088657131,
             -131.5972302288043 89.99882674963366,
             -131.5972302288043 89.99882674963366,
             -7.364919057793379 1.69098037889473))
            SELECT MbrMaxX(ST_Transform(BuildMBR(14121.000000,187578.000000,467141.000000,48006927.000000,23030),4326));
            -3.296335
            */
            try {
                WKBReader wkbReader = new WKBReader();
                StringBuilder centerBuilder = new StringBuilder();
                centerBuilder.append("SELECT ST_AsBinary(CastToXY(ST_Transform(MakePoint(");
                // centerBuilder.append("select AsText(ST_Transform(MakePoint(");
                centerBuilder.append("(" + bounds_west + " + (" + bounds_east + " - " + bounds_west + ")/2), ");
                centerBuilder.append("(" + bounds_south + " + (" + bounds_north + " - " + bounds_south + ")/2), ");
                centerBuilder.append(srid);
                centerBuilder.append("),4326))) AS Center,");
                centerBuilder.append("ST_AsBinary(CastToXY(ST_Transform(BuildMBR(");
                centerBuilder.append("" + bounds_west + "," + bounds_south + ", ");
                centerBuilder.append("" + bounds_east + "," + bounds_north + ", ");
                centerBuilder.append(srid);
                centerBuilder.append("),4326))) AS Envelope ");
                // centerBuilder.append("';");
                centerQuery = centerBuilder.toString();
                // GPLog.androidLog(-1, "SpatialiteUtilities.collectBoundsAndCenter Bounds[" +
                // centerQuery + "]");
                centerStmt = Spatialitedb.prepare(centerQuery);
                if (centerStmt.step()) {
                    byte[] geomBytes = centerStmt.column_bytes(0);
                    Geometry geometry = wkbReader.read(geomBytes);
                    Coordinate coordinate = geometry.getCoordinate();
                    centerCoordinate[0] = coordinate.x;
                    centerCoordinate[1] = coordinate.y;
                    geomBytes = centerStmt.column_bytes(1);
                    geometry = wkbReader.read(geomBytes);
                    Envelope envelope = geometry.getEnvelopeInternal();
                    boundsCoordinates[0] = envelope.getMinX();
                    boundsCoordinates[1] = envelope.getMinY();
                    boundsCoordinates[2] = envelope.getMaxX();
                    boundsCoordinates[3] = envelope.getMaxY();
                }
            } catch (java.lang.Exception e) {
                GPLog.error("SpatialiteUtilities","[" + dbSpatialite.getFilename() + "] collectBoundsAndCenter Bounds[" + centerQuery + "]", e);
            } finally {
                if (centerStmt != null)
                    centerStmt.close();
            }
        } catch (java.lang.Exception e) {
            GPLog.error("SpatialiteUtilities","[" + dbSpatialite.getFilename() + "] sql[" + centerQuery + "]", e);
        }
    }



}
