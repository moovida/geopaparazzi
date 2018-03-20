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
public class ProfileTags extends ARelativePathResource {
    public String tagsUrl = "";
    public String tagsModifiedDate = "";
    public long tagsSize = -1;

    public ProfileTags(){}

    protected ProfileTags(Parcel in) {
        relativePath = in.readString();
        tagsUrl = in.readString();
        tagsModifiedDate = in.readString();
        tagsSize = in.readLong();
    }

    public static final Creator<ProfileTags> CREATOR = new Creator<ProfileTags>() {
        @Override
        public ProfileTags createFromParcel(Parcel in) {
            return new ProfileTags(in);
        }

        @Override
        public ProfileTags[] newArray(int size) {
            return new ProfileTags[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(relativePath);
        dest.writeString(tagsUrl);
        dest.writeString(tagsModifiedDate);
        dest.writeLong(tagsSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfileTags that = (ProfileTags) o;

        if (!relativePath.equals(that.relativePath)) return false;
        return tagsModifiedDate != null ? tagsModifiedDate.equals(that.tagsModifiedDate) : that.tagsModifiedDate == null;
    }

    @Override
    public int hashCode() {
        int result = relativePath.hashCode();
        result = 31 * result + (tagsModifiedDate != null ? tagsModifiedDate.hashCode() : 0);
        return result;
    }
}
