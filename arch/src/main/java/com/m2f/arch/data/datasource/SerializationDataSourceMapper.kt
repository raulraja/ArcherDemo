/*
 * Copyright (c) 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.m2f.arch.data.datasource

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.Query

/**
 * This data source uses mappers to map objects and redirects them to the contained data source, acting as a simple "translator".
 * This class map a List to a single Object when using putAll, in this case it will call the put method on the contained data source
 * This class map a Object to a List when using getAll, int his case it will call the get method on the contained data source
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toOutListMapper Mapper to map data source objects to repository object lists
 * @param toInMapper Mapper to map repository objects to data source objects
 * @param toInMapperFromList Mapper to map repository object lists to data source objects
 */
class SerializationDataSourceMapper<Q, SerializedIn, Out>(
    private val getDataSource: GetDataSource<Q, SerializedIn>,
    private val putDataSource: PutDataSource<Q, SerializedIn>,
    private val deleteDataSource: DeleteDataSource<Q>,
    private val toOutMapper: (SerializedIn) -> Out,
    private val toOutListMapper: (SerializedIn) -> List<Out>,
    private val toInMapper: (Out) -> SerializedIn,
    private val toInMapperFromList: (List<Out>) -> SerializedIn
) : GetDataSource<Q, Out>, PutDataSource<Q, Out>, DeleteDataSource<Q> {

    override suspend fun get(query: Q) = getDataSource.get(query).map(toOutMapper)

    override suspend fun getAll(query: Q) =
        getDataSource.get(query).map { toOutListMapper(it) }

    override suspend fun put(query: Q, value: Out?): Either<Failure, Out> {
        val mapped = value?.let { toInMapper(value) }
        return putDataSource.put(query, mapped)
            .map { toOutMapper(it) }
    }

    override suspend fun putAll(query: Q, value: List<Out>?): Either<Failure, List<Out>> {
        val mapped = value?.let { toInMapperFromList(value) }
        return putDataSource.put(query, mapped)
            .map { toOutListMapper(it) }
    }

    override suspend fun delete(query: Q) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Q) = deleteDataSource.deleteAll(query)
}
