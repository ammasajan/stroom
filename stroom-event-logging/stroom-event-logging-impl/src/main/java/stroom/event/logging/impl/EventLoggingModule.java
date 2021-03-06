/*
 * Copyright 2018 Crown Copyright
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

package stroom.event.logging.impl;

import com.google.inject.AbstractModule;
import stroom.activity.api.CurrentActivity;
import stroom.event.logging.api.DocumentEventLog;
import stroom.event.logging.api.ObjectInfoProviderBinder;
import stroom.event.logging.api.StroomEventLoggingService;
import stroom.security.api.SecurityContext;
import stroom.util.BuildInfoProvider;
import stroom.util.shared.BuildInfo;

public class EventLoggingModule extends AbstractModule {
    @Override
    protected void configure() {
        requireBinding(CurrentActivity.class);
        requireBinding(SecurityContext.class);

        bind(BuildInfo.class).toProvider(BuildInfoProvider.class);
        bind(StroomEventLoggingService.class).to(StroomEventLoggingServiceImpl.class);
        bind(DocumentEventLog.class).to(DocumentEventLogImpl.class);

        ObjectInfoProviderBinder.create(binder());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}