package stroom.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import stroom.kafka.KafkaSpringConfig;

@Configuration
@Import({
//        stroom.benchmark.BenchmarkSpringConfig.class,
        stroom.cache.CacheSpringConfig.class,
        stroom.cache.PipelineCacheSpringConfig.class,
//        stroom.cluster.ClusterSpringConfig.class,
//        stroom.cluster.MockClusterSpringConfig.class,
        stroom.connectors.ConnectorsSpringConfig.class,
        stroom.connectors.elastic.ElasticSpringConfig.class,
//        KafkaSpringConfig.class,
        stroom.dashboard.DashboardSpringConfig.class,
//        stroom.dashboard.MockDashboardSpringConfig.class,
        stroom.dashboard.logging.LoggingSpringConfig.class,
        stroom.datafeed.DataFeedSpringConfig.class,
//        stroom.datafeed.MockDataFeedSpringConfig.class,
//        stroom.datafeed.TestDataFeedServiceImplConfiguration.class,
        stroom.datasource.DatasourceSpringConfig.class,
        stroom.dictionary.DictionarySpringConfig.class,
        stroom.dispatch.DispatchSpringConfig.class,
        stroom.docstore.DocstoreSpringConfig.class,
        stroom.docstore.db.DBSpringConfig.class,
        stroom.document.DocumentSpringConfig.class,
        stroom.elastic.ElasticSpringConfig.class,
        stroom.entity.EntitySpringConfig.class,
//        stroom.entity.EntityTestSpringConfig.class,
        stroom.entity.cluster.EntityClusterSpringConfig.class,
        stroom.entity.event.EntityEventSpringConfig.class,
//        stroom.entity.util.EntityUtilSpringConfig.class,
        stroom.explorer.ExplorerSpringConfig.class,
//        ExternalDocRefSpringConfig.class,
        stroom.feed.FeedSpringConfig.class,
//        stroom.feed.MockFeedSpringConfig.class,
//        stroom.headless.HeadlessConfiguration.class,
//        stroom.headless.HeadlessSpringConfig.class,
        stroom.importexport.ImportExportSpringConfig.class,
        stroom.index.IndexSpringConfig.class,
//        stroom.index.MockIndexSpringConfig.class,
//        stroom.internalstatistics.MockInternalStatisticsSpringConfig.class,
        stroom.io.IOSpringConfig.class,
//        stroom.jobsystem.ClusterLockTestSpringConfig.class,
        stroom.jobsystem.JobSystemSpringConfig.class,
//        stroom.jobsystem.MockJobSystemSpringConfig.class,
        KafkaSpringConfig.class,
        stroom.lifecycle.LifecycleSpringConfig.class,
        stroom.logging.LoggingSpringConfig.class,
//        stroom.node.MockNodeServiceSpringConfig.class,
        stroom.node.NodeServiceSpringConfig.class,
        stroom.node.NodeSpringConfig.class,
//        stroom.node.NodeTestSpringConfig.class,
//        stroom.pipeline.MockPipelineSpringConfig.class,
        stroom.pipeline.PipelineSpringConfig.class,
        stroom.pipeline.destination.DestinationSpringConfig.class,
        stroom.pipeline.errorhandler.ErrorHandlerSpringConfig.class,
        stroom.pipeline.factory.FactorySpringConfig.class,
        stroom.pipeline.filter.FilterSpringConfig.class,
        stroom.pipeline.parser.ParserSpringConfig.class,
        stroom.pipeline.reader.ReaderSpringConfig.class,
        stroom.pipeline.source.SourceSpringConfig.class,
        stroom.pipeline.state.PipelineStateSpringConfig.class,
        stroom.pipeline.stepping.PipelineSteppingSpringConfig.class,
        stroom.pipeline.task.PipelineStreamTaskSpringConfig.class,
        stroom.pipeline.writer.WriterSpringConfig.class,
        stroom.pipeline.xsltfunctions.XsltFunctionsSpringConfig.class,
        stroom.policy.PolicySpringConfig.class,
        stroom.properties.PropertySpringConfig.class,
        stroom.proxy.repo.RepoSpringConfig.class,
        stroom.query.QuerySpringConfig.class,
        stroom.refdata.ReferenceDataSpringConfig.class,
//        stroom.resource.MockResourceSpringConfig.class,
        stroom.resource.ResourceSpringConfig.class,
        stroom.ruleset.RulesetSpringConfig.class,
        stroom.script.ScriptSpringConfig.class,
        stroom.search.SearchSpringConfig.class,
//        stroom.search.SearchTestSpringConfig.class,
        stroom.search.extraction.ExtractionSpringConfig.class,
        stroom.search.shard.ShardSpringConfig.class,
        stroom.security.MockSecurityContextSpringConfig.class,
//        stroom.security.MockSecurityContextSpringConfig.class,
        stroom.security.SecuritySpringConfig.class,
        stroom.servicediscovery.ServiceDiscoverySpringConfig.class,
        stroom.servlet.ServletSpringConfig.class,
        stroom.spring.MetaDataStatisticConfiguration.class,
        stroom.spring.PersistenceConfiguration.class,
//        stroom.spring.ProcessTestServerComponentScanConfiguration.class,
        stroom.spring.ScopeConfiguration.class,
//        stroom.spring.ScopeTestConfiguration.class,
//        stroom.spring.ServerComponentScanConfiguration.class,
//        stroom.spring.ServerComponentScanTestConfiguration.class,
//        stroom.spring.ServerConfiguration.class,
//        stroom.startup.AppSpringConfig.class,
        stroom.statistics.internal.InternalStatisticsSpringConfig.class,
        stroom.statistics.spring.StatisticsConfiguration.class,
        stroom.statistics.sql.SQLStatisticSpringConfig.class,
        stroom.statistics.sql.datasource.DataSourceSpringConfig.class,
        stroom.statistics.sql.internal.InternalSpringConfig.class,
        stroom.statistics.sql.pipeline.filter.FilterSpringConfig.class,
        stroom.statistics.sql.rollup.SQLStatisticRollupSpringConfig.class,
        stroom.statistics.sql.search.SearchSpringConfig.class,
        stroom.statistics.stroomstats.entity.StroomStatsEntitySpringConfig.class,
        stroom.statistics.stroomstats.internal.InternalSpringConfig.class,
        stroom.statistics.stroomstats.kafka.KafkaSpringConfig.class,
        stroom.statistics.stroomstats.pipeline.filter.FilterSpringConfig.class,
        stroom.statistics.stroomstats.rollup.StroomStatsRollupSpringConfig.class,
//        stroom.streamstore.MockStreamStoreSpringConfig.class,
        stroom.streamstore.StreamStoreSpringConfig.class,
        stroom.streamstore.fs.FSSpringConfig.class,
//        stroom.streamstore.tools.ToolsSpringConfig.class,
//        stroom.streamtask.MockStreamTaskSpringConfig.class,
        stroom.streamtask.StreamTaskSpringConfig.class,
        stroom.task.TaskSpringConfig.class,
        stroom.task.cluster.ClusterTaskSpringConfig.class,
//        stroom.test.AbstractCoreIntegrationTestSpringConfig.class,
//        stroom.test.AbstractProcessIntegrationTestSpringConfig.class,
//        stroom.test.DatabaseTestControlSpringConfig.class,
//        stroom.test.SetupSampleDataComponentScanConfiguration.class,
//        stroom.test.SetupSampleDataSpringConfig.class,
//        stroom.test.TestSpringConfig.class,
        stroom.upgrade.UpgradeSpringConfig.class,
        stroom.util.cache.CacheManagerSpringConfig.class,
//        stroom.util.spring.MockUtilSpringConfig.class,
//        stroom.util.spring.StroomBeanLifeCycleTestConfiguration.class,
        stroom.util.spring.UtilSpringConfig.class,
//        stroom.util.task.TaskScopeTestConfiguration.class,
        stroom.visualisation.VisualisationSpringConfig.class,
//        stroom.volume.MockVolumeSpringConfig.class,
        stroom.volume.VolumeSpringConfig.class,
//        stroom.xml.XmlSpringConfig.class,
        stroom.xml.converter.ds3.DS3SpringConfig.class,
        stroom.xml.converter.json.JsonSpringConfig.class,
//        stroom.xmlschema.MockXmlSchemaSpringConfig.class,
        stroom.xmlschema.XmlSchemaSpringConfig.class
})
public class ToolSpringConfiguration {
}
