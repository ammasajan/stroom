package stroom.pipeline.refdata;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import stroom.util.cache.CacheConfig;
import stroom.util.config.annotations.RequiresRestart;
import stroom.util.io.ByteSize;
import stroom.util.shared.AbstractConfig;
import stroom.util.time.StroomDuration;

import javax.inject.Singleton;
import javax.validation.constraints.Min;

@Singleton
public class ReferenceDataConfig extends AbstractConfig {
    private static final int MAX_READERS_DEFAULT = 100;
    private static final int MAX_PUTS_BEFORE_COMMIT_DEFAULT = 1000;

    private String localDir = "${stroom.temp}/refDataOffHeapStore";
    private int maxPutsBeforeCommit = MAX_PUTS_BEFORE_COMMIT_DEFAULT;
    private int maxReaders = MAX_READERS_DEFAULT;
    private ByteSize maxStoreSize = ByteSize.ofGibibytes(50);
    private StroomDuration purgeAge = StroomDuration.ofDays(30);
    private boolean isReadAheadEnabled = true;
    private CacheConfig effectiveStreamCache = new CacheConfig.Builder()
            .maximumSize(1000L)
            .expireAfterAccess(StroomDuration.ofMinutes(10))
            .build();

    @RequiresRestart(RequiresRestart.RestartScope.SYSTEM)
    @JsonPropertyDescription("The full directory path to use for storing the reference data store. It MUST be on " +
            "local disk, NOT network storage, due to use of memory mapped files. The directory will be created " +
            "if it doesn't exist.")
    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(final String localDir) {
        this.localDir = localDir;
    }

    @Min(1)
    @JsonPropertyDescription("The maximum number of puts into the store before the transaction is committed. " +
            "There is only one write transaction available and long running transactions are not desirable.")
    public int getMaxPutsBeforeCommit() {
        return maxPutsBeforeCommit;
    }

    @SuppressWarnings("unused")
    public void setMaxPutsBeforeCommit(final int maxPutsBeforeCommit) {
        this.maxPutsBeforeCommit = maxPutsBeforeCommit;
    }

    @Min(1)
    @RequiresRestart(RequiresRestart.RestartScope.SYSTEM)
    @JsonPropertyDescription("The maximum number of concurrent readers/threads that can use the off-heap store.")
    public int getMaxReaders() {
        return maxReaders;
    }

    @SuppressWarnings("unused")
    public void setMaxReaders(final int maxReaders) {
        this.maxReaders = maxReaders;
    }

    @RequiresRestart(RequiresRestart.RestartScope.SYSTEM)
    @JsonPropertyDescription("The maximum size for the ref loader off heap store. There must be " +
        "available space on the disk to accommodate this size. It can be larger than the amount of available RAM " +
        "and will only be allocated as it is needed. Can be expressed in IEC units (multiples of 1024), " +
        "e.g. 1024, 1024B, 1024bytes, 1KiB, 1KB, 1K, etc.")
    public ByteSize getMaxStoreSize() {
        return maxStoreSize;
    }

    public void setMaxStoreSize(final ByteSize maxStoreSize) {
        this.maxStoreSize = maxStoreSize;
    }

    @JsonPropertyDescription("The time to retain reference data for in the off heap store. The time is taken " +
        "from the time that the reference stream was last accessed, e.g. a lookup was made against it. " +
        "In ISO-8601 duration format, e.g. 'P1DT12H'")
    public StroomDuration getPurgeAge() {
        return purgeAge;
    }

    @SuppressWarnings("unused")
    public void setPurgeAge(final StroomDuration purgeAge) {
        this.purgeAge = purgeAge;
    }

    @RequiresRestart(RequiresRestart.RestartScope.SYSTEM)
    @JsonPropertyDescription("Read ahead means the OS will pre-fetch additional data from the disk in the " +
            "expectation that it will be used at some point. This generally improves performance as more data is " +
            "available in the page cache. Read ahead is enabled by default. It may be worth disabling it if " +
            "the actively used ref data is larger than the available RAM, as this will stop it evicting hot " +
            "ref entries to make space for pre-fetched data.")
    public boolean isReadAheadEnabled() {
        return isReadAheadEnabled;
    }

    public void setReadAheadEnabled(final boolean isReadAheadEnabled) {
        this.isReadAheadEnabled = isReadAheadEnabled;
    }

    public CacheConfig getEffectiveStreamCache() {
        return effectiveStreamCache;
    }

    public void setEffectiveStreamCache(final CacheConfig effectiveStreamCache) {
        this.effectiveStreamCache = effectiveStreamCache;
    }

    @Override
    public String toString() {
        return "RefDataStoreConfig{" +
                "localDir='" + localDir + '\'' +
                ", maxPutsBeforeCommit=" + maxPutsBeforeCommit +
                ", maxReaders=" + maxReaders +
                ", maxStoreSize='" + maxStoreSize + '\'' +
                ", purgeAge='" + purgeAge + '\'' +
                ", isReadAheadEnabled=" + isReadAheadEnabled +
                '}';
    }
}
