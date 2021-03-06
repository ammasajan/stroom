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

package stroom.statistics.impl.sql.shared;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.fusesource.restygwt.client.DirectRestService;
import stroom.util.shared.ResourcePaths;
import stroom.util.shared.RestResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = "statistic/rollUp - /v1")
@Path("/statistic/rollUp" + ResourcePaths.V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface StatisticRollupResource extends RestResource, DirectRestService {

    @POST
    @Path("/bitMaskPermGeneration")
    @ApiOperation(
            value = "Create rollup bit mask",
            response = List.class)
    List<CustomRollUpMask> bitMaskPermGeneration(@ApiParam("fieldCount") Integer fieldCount);

    @POST
    @Path("/bitMaskConversion")
    @ApiOperation(
            value = "Get rollup bit mask",
            response = List.class)
    List<CustomRollUpMaskFields> bitMaskConversion(@ApiParam("maskValues") List<Short> maskValues);

    @POST
    @Path("/dataSourceFieldChange")
    @ApiOperation(
            value = "Change fields",
            response = StatisticsDataSourceData.class)
    StatisticsDataSourceData fieldChange(@ApiParam("request") StatisticsDataSourceFieldChangeRequest request);
}
