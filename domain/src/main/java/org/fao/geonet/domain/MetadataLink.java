/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

package org.fao.geonet.domain;

import org.fao.geonet.entitylistener.MetadataLinkEntityListenerManager;
import org.fao.geonet.entitylistener.OperationAllowedEntityListenerManager;

import javax.annotation.Nonnull;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * An entity that represents the relationship between a metadata and a metadata link.
 */
@Entity
@Table(name = MetadataLink.TABLE_NAME,
    indexes = {
        @Index(name = "idx_metadatalink_metadataid", columnList = "metadataid"),
        @Index(name = "idx_metadatalink_linkid", columnList = "linkid")})
@Access(AccessType.PROPERTY)
@EntityListeners(MetadataLinkEntityListenerManager.class)
public class MetadataLink extends GeonetEntity {
    /**
     * Name of the MetadataLink table.
     */
    public static final String TABLE_NAME = "MetadataLink";

    private MetadataLinkId _id = new MetadataLinkId();

    private String xpath;

    /**
     * Constructor for use by JPA.
     */
    public MetadataLink() {
    }

    /**
     * Constructor for use by developers to easily create an instance.
     */
    public MetadataLink(@Nonnull MetadataLinkId id) {
        this._id = id;
    }

    /**
     * Return the Id object.
     */
    @EmbeddedId
    public MetadataLinkId getId() {
        return _id;
    }

    /**
     * Set the Id object.
     *
     * @param id new id
     */
    public void setId(MetadataLinkId id) {
        this._id = id;
    }

    @Override
    public String toString() {
        return "MetadataLinkId: [" + _id.toString() + "]";
    }

    public MetadataLink setId(Metadata metadata, Link link) {
        setId(new MetadataLinkId(metadata.getId(), link.getId()));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataLink that = (MetadataLink) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id != null ? _id.hashCode() : 0;
    }


    /**
     * Optional xpath which points to where the link is in the metadata record.
     * That suppose that the link is in one location only. To improve ?
     *
     * @return The xpath
     */
    @Column(nullable = true, length = 2000)
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}
