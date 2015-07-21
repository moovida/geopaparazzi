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
package eu.geopaparazzi.mapsforge.mapsdirmanager.maps.tiles;
import eu.geopaparazzi.spatialite.database.spatial.core.tables.AbstractSpatialTable;
import eu.geopaparazzi.spatialite.database.spatial.core.enums.SpatialDataType;
import jsqlite.Database;
/**
 * A cutom tiles producer table.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 *  adapted to work with custom tiles databases [mapsforge] Mark Johnson (www.mj10777.de)
 */
@SuppressWarnings("nls")
public class CustomTileTable extends AbstractSpatialTable {

    private static final long serialVersionUID = 1L;
    private String tileQuery;

    /**
     * constructor.
     * 
     * <p>vector_key and vector_value
     * <ol>
     * <li>vector_key[map]='databaseFileNameNoExtension;0;Mapurl;title;abstract' = length=5[0-4]</li>
     * <li>vector_data[map]='minZoom;maxZoom;srid;0' = length=4[0-3]</li>
     * <li>vector_extent[map]='0;extent_0-6;?,?,?;getDatabasePath()' = length=4[0-3]</li>
     * 
     * @param spatialite_db the class 'Database' connection [will be null].
     * @param vector_key major Parameters  needed for creation in AbstractSpatialTable.
     * @param vector_value minor Parameters needed for creation in AbstractSpatialTable.
     */
    public CustomTileTable(Database spatialite_db,String vector_key,String  vector_value) {
         super(spatialite_db,vector_key,vector_value);
    }

    // /**
    // * Retrieve CustomTileDownloader
    // *
    // * <p>created during CustomTileDatabasesManager
    // *
    // * @return customTileDownloader
    // */
    // public CustomTileDownloader getCustomTileDownloader() {
    // return customTileDownloader;
    // }

    /**
     * Function to check and correct bounds / zoom level [for 'SpatialiteDatabaseHandler']
     *
     * @param mapCenterLocation [point/zoom to check] (most probably result of PositionUtilities.getMapCenterFromPreferences(preferences,true,true);)
     * @param doCorrectIfOutOfRange if <code>true</code>, change mapCenterLocation values if out of range.
     * @return 0=inside valid area/zoom ; i_rc > 0 outside area or zoom ; i_parm=0 no corrections ; 1= correct tileBounds values.
     */
    public int checkCenterLocation( double[] mapCenterLocation, boolean doCorrectIfOutOfRange ) {
        int i_rc = 0; // inside area
        if (((mapCenterLocation[0] < boundsWest) || (mapCenterLocation[0] > boundsEast))
                || ((mapCenterLocation[1] < boundsSouth) || (mapCenterLocation[1] > boundsNorth))
                || ((mapCenterLocation[2] < minZoom) || (mapCenterLocation[2] > maxZoom))) {
            if (((mapCenterLocation[0] >= boundsWest) && (mapCenterLocation[0] <= boundsEast))
                    && ((mapCenterLocation[1] >= boundsSouth) && (mapCenterLocation[1] <= boundsNorth))) {
                /*
                 *  We are inside the Map-Area, but Zoom is not correct
                 */
                if (mapCenterLocation[2] < minZoom) {
                    i_rc = 1;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[2] = minZoom;
                    }
                }
                if (mapCenterLocation[2] > maxZoom) {
                    i_rc = 2;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[2] = maxZoom;
                    }
                }
            } else {
                if (mapCenterLocation[2] < minZoom) {
                    i_rc = 11;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[2] = minZoom;
                    }
                }
                if (mapCenterLocation[2] > maxZoom) {
                    i_rc = 12;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[2] = maxZoom;
                    }
                }
                if ((mapCenterLocation[0] < boundsWest) || (mapCenterLocation[0] > boundsEast)) {
                    i_rc = 13;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[0] = centerX;
                    }
                }
                if ((mapCenterLocation[1] < boundsSouth) || (mapCenterLocation[1] > boundsNorth)) {
                    i_rc = 14;
                    if (doCorrectIfOutOfRange) {
                        mapCenterLocation[1] = centerY;
                    }
                }
            }
        }
        return i_rc;
    }

    /**
     * Get the tile retrieve query with place holders for zoom, column and row.
     *
     * @return the query to use for this raster set.
     */
    public String getTileQuery() {
        return tileQuery;
    }

    @Override
    public double[] longLat2Srid(double lon, double lat) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public boolean isEditable() {
        return false;
    }

}
