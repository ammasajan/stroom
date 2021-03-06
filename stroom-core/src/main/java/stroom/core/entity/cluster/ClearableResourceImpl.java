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

package stroom.core.entity.cluster;

import stroom.node.api.NodeCallUtil;
import stroom.node.api.NodeInfo;
import stroom.node.api.NodeService;
import stroom.util.jersey.WebTargetFactory;
import stroom.util.shared.ResourcePaths;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

class ClearableResourceImpl implements ClearableResource {
    private final NodeService nodeService;
    private final NodeInfo nodeInfo;
    private final WebTargetFactory webTargetFactory;
    private final ClearEventHandler clearEventHandler;

    @Inject
    ClearableResourceImpl(final NodeService nodeService,
                          final NodeInfo nodeInfo,
                          final WebTargetFactory webTargetFactory,
                          final ClearEventHandler clearEventHandler) {
        this.nodeService = nodeService;
        this.nodeInfo = nodeInfo;
        this.webTargetFactory = webTargetFactory;
        this.clearEventHandler = clearEventHandler;
    }

    @Override
    public Boolean clear(final String nodeName) {
        // If this is the node that was contacted then just return the latency we have incurred within this method.
        if (NodeCallUtil.shouldExecuteLocally(nodeInfo, nodeName)) {
            clearEventHandler.clearLocally();
            return true;
        } else {
            final String url = NodeCallUtil.getBaseEndpointUrl(nodeService, nodeName)
                    + ResourcePaths.buildAuthenticatedApiPath(
                    ClearableResource.BASE_PATH,
                    nodeName);

            try {
                final Response response = webTargetFactory
                        .create(url)
                        .request(MediaType.APPLICATION_JSON)
                        .get();
                if (response.getStatus() != 200) {
                    throw new WebApplicationException(response);
                }
                final Boolean success = response.readEntity(Boolean.class);
                Objects.requireNonNull(success, "Null success");
                return success;
            } catch (Throwable e) {
                throw NodeCallUtil.handleExceptionsOnNodeCall(nodeName, url, e);
            }
        }
    }
}