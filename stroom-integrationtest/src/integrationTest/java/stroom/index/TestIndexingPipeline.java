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

package stroom.index;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import stroom.index.shared.Index;
import stroom.index.shared.IndexField;
import stroom.index.shared.IndexField.AnalyzerType;
import stroom.index.shared.IndexFields;
import stroom.index.shared.IndexShardKey;
import stroom.pipeline.PipelineService;
import stroom.pipeline.PipelineTestUtil;
import stroom.pipeline.XSLTService;
import stroom.pipeline.errorhandler.ErrorReceiverProxy;
import stroom.pipeline.errorhandler.FatalErrorReceiver;
import stroom.pipeline.factory.Pipeline;
import stroom.pipeline.factory.PipelineDataCache;
import stroom.pipeline.factory.PipelineFactory;
import stroom.pipeline.shared.PipelineEntity;
import stroom.pipeline.shared.XSLT;
import stroom.pipeline.shared.data.PipelineData;
import stroom.pipeline.shared.data.PipelineDataUtil;
import stroom.pipeline.state.StreamHolder;
import stroom.streamstore.shared.Stream;
import stroom.test.AbstractProcessIntegrationTest;
import stroom.test.StroomPipelineTestFileUtil;
import stroom.guice.PipelineScopeRunnable;
import stroom.util.io.StreamUtil;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.InputStream;
import java.util.Map;

public class TestIndexingPipeline extends AbstractProcessIntegrationTest {
    private static final String PIPELINE = "TestIndexingPipeline/TestIndexingPipeline.Pipeline.data.xml";
    private static final String SAMPLE_INDEX_INPUT = "TestIndexingPipeline/TestIndexes.out";

    private static final String SAMPLE_INDEX_XSLT = "TestIndexingPipeline/Indexes.xsl";

    @Inject
    private XSLTService xsltService;
    @Inject
    private IndexService indexService;
    @Inject
    private Provider<PipelineFactory> pipelineFactoryProvider;
    @Inject
    private Provider<ErrorReceiverProxy> errorReceiverProvider;
    @Inject
    private MockIndexShardWriterCache indexShardWriterCache;
    @Inject
    private PipelineService pipelineService;
    @Inject
    private Provider<StreamHolder> streamHolderProvider;
    @Inject
    private PipelineDataCache pipelineDataCache;
    @Inject
    private PipelineScopeRunnable pipelineScopeRunnable;

    @Before
    @After
    public void clear() {
        indexShardWriterCache.shutdown();
    }

    @Test
    public void testSimple() {
        pipelineScopeRunnable.scopeRunnable(() -> {
        // Setup the XSLT.
        XSLT xslt = xsltService.create("Indexing XSLT");
        xslt.setData(StreamUtil.streamToString(StroomPipelineTestFileUtil.getInputStream(SAMPLE_INDEX_XSLT)));
        xslt = xsltService.save(xslt);

        final IndexFields indexFields = IndexFields.createStreamIndexFields();
        // indexFields.add(IndexField.createIdField(IndexConstants.STREAM_ID));
        // indexFields.add(IndexField.createIdField(IndexConstants.EVENT_ID));
        indexFields.add(IndexField.createDateField("EventTime"));
        indexFields.add(IndexField.createField("UserId", AnalyzerType.KEYWORD));
        indexFields.add(IndexField.createField("Action"));
        indexFields.add(IndexField.createField("Generator"));
        indexFields.add(IndexField.createNumericField("DeviceLocationFloor"));
        indexFields.add(IndexField.createField("DeviceHostName"));
        indexFields.add(IndexField.createField("ProcessCommand"));

        // Setup the target index
        Index index = indexService.create("Test index");
        index.setIndexFieldsObject(indexFields);
        index = indexService.save(index);

        errorReceiverProvider.get().setErrorReceiver(new FatalErrorReceiver());

        // Set the stream for decoration purposes.
        final long id = (long) (Math.random() * 1000);
        final Stream stream = Stream.createStub(id);
        streamHolderProvider.get().setStream(stream);

        // Create the pipeline.
        PipelineEntity pipelineEntity = PipelineTestUtil.createTestPipeline(pipelineService,
                StroomPipelineTestFileUtil.getString(PIPELINE));
        pipelineEntity.getPipelineData().addProperty(PipelineDataUtil.createProperty("xsltFilter", "xslt", xslt));
        pipelineEntity.getPipelineData().addProperty(PipelineDataUtil.createProperty("indexingFilter", "index", index));
        pipelineEntity = pipelineService.save(pipelineEntity);

        // Create the parser.
        final PipelineData pipelineData = pipelineDataCache.get(pipelineEntity);
        final Pipeline pipeline = pipelineFactoryProvider.get().create(pipelineData);

        final InputStream inputStream = StroomPipelineTestFileUtil.getInputStream(SAMPLE_INDEX_INPUT);
        pipeline.process(inputStream);

        // Make sure we only used one writer.
        Assert.assertEquals(1, indexShardWriterCache.getWriters().size());

        // Get the writer from the pool.
        final Map<IndexShardKey, IndexShardWriter> writers = indexShardWriterCache.getWriters();
        final MockIndexShardWriter writer = (MockIndexShardWriter) writers.values().iterator().next();

        // Check that we indexed 4 documents.
        Assert.assertEquals(4, writer.getDocuments().size());
        Assert.assertEquals("Authenticate", writer.getDocuments().get(0).getField("Action").stringValue());
        Assert.assertEquals("Process", writer.getDocuments().get(1).getField("Action").stringValue());
        Assert.assertEquals("Process", writer.getDocuments().get(2).getField("Action").stringValue());
        Assert.assertEquals("Process", writer.getDocuments().get(3).getField("Action").stringValue());

        for (int i = 0; i < 4; i++) {
            final String streamId = writer.getDocuments().get(i).getField("StreamId").stringValue();
            final String eventId = writer.getDocuments().get(i).getField("EventId").stringValue();
            final String userId = writer.getDocuments().get(i).getField("UserId").stringValue();

            System.out.println(streamId + ":" + eventId);

            Assert.assertEquals(String.valueOf(id), streamId);
            Assert.assertEquals(Integer.toString(i + 1), eventId);
            Assert.assertEquals("user" + (i + 1), userId);
        }

        // // Return the writer to the pool.
        // indexShardManager.returnObject(poolItem, true);
        });
    }
}
