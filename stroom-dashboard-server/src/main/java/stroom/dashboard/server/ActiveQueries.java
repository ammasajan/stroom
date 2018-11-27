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

package stroom.dashboard.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.dashboard.shared.DashboardQueryKey;
import stroom.datasource.DataSourceProviderRegistry;
import stroom.query.api.v2.DocRef;
import stroom.query.api.v2.QueryKey;
import stroom.security.SecurityContext;
import stroom.security.SecurityHelper;
import stroom.security.UserTokenUtil;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ActiveQueries {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveQueries.class);

    private final ConcurrentHashMap<DashboardQueryKey, ActiveQuery> activeQueries = new ConcurrentHashMap<>();

    private final SecurityContext securityContext;
    private final DataSourceProviderRegistry dataSourceProviderRegistry;

    ActiveQueries(final DataSourceProviderRegistry dataSourceProviderRegistry,
                  final SecurityContext securityContext) {
        this.dataSourceProviderRegistry = dataSourceProviderRegistry;
        this.securityContext = securityContext;
    }

    void destroyUnusedQueries(final Set<DashboardQueryKey> keys) {
        // Kill off any searches that are no longer required by the UI.
        Iterator<Entry<DashboardQueryKey, ActiveQuery>> iterator = activeQueries.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<DashboardQueryKey, ActiveQuery> entry = iterator.next();
            final DashboardQueryKey queryKey = entry.getKey();
            final ActiveQuery activeQuery = entry.getValue();
            if (keys == null || !keys.contains(queryKey)) {
                try (final SecurityHelper securityHelper = SecurityHelper.asUser(securityContext, UserTokenUtil.create(activeQuery.getUserId(), null))) {
                    // Terminate the associated search task.
                    Boolean success = dataSourceProviderRegistry.getDataSourceProvider(activeQuery.getDocRef())
                            .map(provider -> provider.destroy(new QueryKey(queryKey.getUuid())))
                            .orElseGet(() -> {
                                LOGGER.warn("Unable to destroy query with key {} as provider {} cannot be found",
                                        queryKey.getUuid(),
                                        activeQuery.getDocRef().getType());
                                return Boolean.TRUE;
                            });

                    if (Boolean.TRUE.equals(success)) {
                        // Remove the collector from the available searches as it is no longer required by the UI.
                        iterator.remove();
                    }
                }
            }
        }
    }

    ActiveQuery getExistingQuery(final DashboardQueryKey queryKey) {
        return activeQueries.get(queryKey);
    }

    ActiveQuery addNewQuery(final DashboardQueryKey queryKey, final DocRef docRef) {
        final String userId = securityContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("No user is currently logged in");
        }
        final ActiveQuery activeQuery = new ActiveQuery(docRef, userId);
        final ActiveQuery existing = activeQueries.put(queryKey, activeQuery);
        if (existing != null) {
            throw new RuntimeException(
                    "Existing active query found in active query map for '" + queryKey.toString() + "'");
        }
        return activeQuery;
    }

    public void destroy() {
        destroyUnusedQueries(null);
    }
}
