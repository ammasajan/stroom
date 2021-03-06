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

package stroom.data.store.impl.fs;


import org.junit.jupiter.api.Test;
import stroom.data.shared.StreamTypeNames;
import stroom.meta.shared.Meta;
import stroom.test.AbstractCoreIntegrationTest;
import stroom.test.CommonTestScenarioCreator;
import stroom.test.common.util.test.FileSystemTestUtil;
import stroom.util.time.StroomDuration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestFileSystemStreamMaintenanceService extends AbstractCoreIntegrationTest {
    @Inject
    private FsDataStoreMaintenanceService streamMaintenanceService;
    @Inject
    private DataVolumeService dataVolumeService;
    @Inject
    private CommonTestScenarioCreator commonTestScenarioCreator;
    @Inject
    private DataStoreServiceConfig config;
    @Inject
    private Provider<FsCleanExecutor> fileSystemCleanTaskExecutor;

    @Test
    void testSimple() throws IOException {
        config.setFileSystemCleanOldAge(StroomDuration.ZERO);

        final String feedName = FileSystemTestUtil.getUniqueTestString();

        final Meta md = commonTestScenarioCreator.createSample2LineRawFile(feedName, StreamTypeNames.RAW_EVENTS);

        commonTestScenarioCreator.createSampleBlankProcessedFile(feedName, md);

        final List<Path> files = streamMaintenanceService.findAllStreamFile(md);

        assertThat(files.size() > 0).isTrue();

        final FindDataVolumeCriteria findStreamVolumeCriteria = FindDataVolumeCriteria.create(md);
        assertThat(dataVolumeService.find(findStreamVolumeCriteria).size() > 0).isTrue();

        final Path dir = files.iterator().next().getParent();

        final Path test1 = dir.resolve("badfile.dat");

        Files.createFile(test1);

        assertThat(Files.exists(test1)).isTrue();
        fileSystemCleanTaskExecutor.get().clean();
        assertThat(Files.exists(test1)).isFalse();
    }
}
