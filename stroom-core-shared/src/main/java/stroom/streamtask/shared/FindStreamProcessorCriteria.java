/*
 * Copyright 2016 Crown Copyright
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

package stroom.streamtask.shared;

import stroom.entity.shared.BaseCriteria;
import stroom.entity.shared.EntityIdSet;
import stroom.pipeline.shared.PipelineEntity;

/**
 * Class used to find translations.
 */
public class FindStreamProcessorCriteria extends BaseCriteria {
    private static final long serialVersionUID = 1L;

    private EntityIdSet<PipelineEntity> pipelineIdSet = null;

    public FindStreamProcessorCriteria() {
        // Default constructor necessary for GWT serialisation.
    }

    public FindStreamProcessorCriteria(final PipelineEntity pipeline) {
        obtainPipelineIdSet().add(pipeline);
    }

    public EntityIdSet<PipelineEntity> getPipelineIdSet() {
        return pipelineIdSet;
    }

    public EntityIdSet<PipelineEntity> obtainPipelineIdSet() {
        if (pipelineIdSet == null) {
            pipelineIdSet = new EntityIdSet<>();
        }
        return pipelineIdSet;
    }
}
