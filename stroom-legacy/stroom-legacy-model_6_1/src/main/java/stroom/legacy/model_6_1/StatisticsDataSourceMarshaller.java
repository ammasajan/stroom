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

package stroom.legacy.model_6_1;

@Deprecated
public class StatisticsDataSourceMarshaller extends EntityMarshaller<StatisticStoreEntity, StatisticsDataSourceData> {
    public StatisticsDataSourceMarshaller() {
    }

    @Override
    public StatisticsDataSourceData getObject(final StatisticStoreEntity entity) {
        return entity.getStatisticDataSourceDataObject();
    }

    @Override
    public void setObject(final StatisticStoreEntity entity, final StatisticsDataSourceData object) {
        // Fields may be stored in XML out of order so ensure the entity has them in order
        // This is required here as JAXB doesn't like using the setter
        object.reOrderStatisticFields();
        entity.setStatisticDataSourceDataObject(object);
    }

    @Override
    protected String getData(final StatisticStoreEntity entity) {
        return entity.getData();
    }

    @Override
    protected void setData(final StatisticStoreEntity entity, final String data) {
        entity.setData(data);
    }

    @Override
    protected Class<StatisticsDataSourceData> getObjectType() {
        return StatisticsDataSourceData.class;
    }

    @Override
    protected String getEntityType() {
        return StatisticStoreEntity.ENTITY_TYPE;
    }
}
