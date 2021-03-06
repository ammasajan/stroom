/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.data.retention.shared;

import stroom.util.shared.ResourcePaths;
import stroom.util.shared.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "dataRetentionRules - /v1")
@Path("/dataRetentionRules" + ResourcePaths.V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DataRetentionRulesResource extends RestResource, DirectRestService {

    @POST
    @Path("/read")
    @ApiOperation(
            value = "Get data retention rules",
            response = DataRetentionRules.class)
    DataRetentionRules read();

    @PUT
    @Path("/update")
    @ApiOperation(
            value = "Update data retention rules",
            response = DataRetentionRules.class)
    DataRetentionRules update(@ApiParam("dataRetentionRules") DataRetentionRules dataRetentionRules);

    @POST
    @Path("/impactSummary")
    @ApiOperation(
            value = "Get a summary of meta deletions with the passed data retention rules",
            response = DataRetentionDeleteSummary.class)
    DataRetentionDeleteSummaryResponse getRetentionDeletionSummary(
            @ApiParam("request") DataRetentionDeleteSummaryRequest request);

    @DELETE
    @Path("/impactSummary/{queryId}")
    @ApiOperation(
            value = "Delete a running query")
    Boolean cancelQuery(@PathParam("queryId") final String queryId);


}
