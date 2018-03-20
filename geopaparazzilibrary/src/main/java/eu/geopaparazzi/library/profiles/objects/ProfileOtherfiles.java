/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2016  HydroloGIS (www.hydrologis.com)
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
package eu.geopaparazzi.library.profiles.objects;

import android.os.Parcel;

/**
 * Created by hydrologis on 19/03/18.
 */
public class ProfileOtherfiles extends ARelativePathResource {
    public String url = "";
    public String modifiedDate = "";
    public long size = -1;

    public ProfileOtherfiles(){
    }

    protected ProfileOtherfiles(Parcel in) {
        relativePath = in.readString();
        url = in.readString();
        modifiedDate = in.readString();
        size = in.readLong();
    }

    public static final Creator<ProfileOtherfiles> CREATOR = new Creator<ProfileOtherfiles>() {
        @Override
        public ProfileOtherfiles createFromParcel(Parcel in) {
            return new ProfileOtherfiles(in);
        }

        @Override
        public ProfileOtherfiles[] newArray(int size) {
            return new ProfileOtherfiles[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(relativePath);
        dest.writeString(url);
        dest.writeString(modifiedDate);
        dest.writeLong(size);
    }
}
