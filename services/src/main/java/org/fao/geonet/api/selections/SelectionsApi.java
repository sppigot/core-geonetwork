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
package org.fao.geonet.api.selections;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jeeves.server.UserSession;
import org.fao.geonet.api.API;
import org.fao.geonet.api.ApiUtils;
import org.fao.geonet.kernel.SelectionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Set;

import static org.fao.geonet.api.ApiParams.API_PARAM_RECORD_UUIDS;

/**
 * Select a list of elements stored in session.
 */
@RequestMapping(value = {
    "/api/selections",
    "/api/" + API.VERSION_0_1 +
        "/selections"
})
@Api(value = "selections",
    tags = "selections",
    description = "Selection related operations")
@Controller("selections")
public class SelectionsApi {

    @ApiOperation(value = "Get current selection",
        nickname = "get")
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/{bucket}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE
        })
    public
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Set<String> get(
        @ApiParam(value = "Bucket name",
            required = true,
            example = "metadata")
        @PathVariable
            String bucket,
        HttpSession httpSession
    )
        throws Exception {
        SelectionManager selectionManager =
            SelectionManager.getManager(ApiUtils.getUserSession(httpSession));

        synchronized (selectionManager.getSelection(bucket)) {
            return selectionManager.getSelection(bucket);
        }
    }


    @ApiOperation(value = "Select one or more items",
        nickname = "add")
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/{bucket}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE
        })
    public
    @ResponseBody
    ResponseEntity<Integer> add(
        @ApiParam(value = "Bucket name",
            required = true,
            example = "metadata")
        @PathVariable
            String bucket,
        @ApiParam(value = "A query to select a subset of records.",
            required = false)
        @RequestParam(required = false)
            String q,
        @ApiParam(value = "One or more record UUIDs. If null, select all in current search if bucket name is 'metadata' (TODO: remove this limitation?).",
            required = false)
        @RequestParam(required = false)
            String[] uuid,
        HttpSession httpSession,
        HttpServletRequest request
    )
        throws Exception {
        UserSession session = ApiUtils.getUserSession(httpSession);
        int nbSelected = SelectionManager.updateSelection(bucket,
            session,
            uuid != null ?
                SelectionManager.ADD_SELECTED :
                SelectionManager.ADD_ALL_SELECTED,
            uuid != null ?
                Arrays.asList(uuid) : null,
            q, ApiUtils.createServiceContext(request));

        return new ResponseEntity<>(nbSelected, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Clear selection or remove items",
        nickname = "clear")
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/{bucket}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE
        })
    public
    @ResponseBody
    ResponseEntity<Integer> clear(
        @ApiParam(value = "Bucket name",
            required = true,
            example = "metadata")
        @PathVariable
            String bucket,
        @ApiParam(
            value = API_PARAM_RECORD_UUIDS,
            required = false)
        @RequestParam(required = false)
            String[] uuid,
        HttpSession httpSession,
        HttpServletRequest request
    )
        throws Exception {

        int nbSelected = SelectionManager.updateSelection(bucket,
            ApiUtils.getUserSession(httpSession),
            uuid != null ?
                SelectionManager.REMOVE_SELECTED :
                SelectionManager.REMOVE_ALL_SELECTED,
            uuid != null ?
                Arrays.asList(uuid) : null,
            null, ApiUtils.createServiceContext(request));

        return new ResponseEntity<>(nbSelected, HttpStatus.OK);
    }
}
