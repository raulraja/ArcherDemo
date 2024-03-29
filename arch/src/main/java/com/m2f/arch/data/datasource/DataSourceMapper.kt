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
 *
 * @param getDataSource Data source with get operations
 * @param putDataSource Data source with put operations
 * @param deleteDataSource Data source with delete operations
 * @param toOutMapper Mapper to map data source objects to repository objects
 * @param toInMapper Mapper to map repository objects to data source objects
 */
class DataSourceMapper<Q, In, Out>(
    getDataSource: GetDataSource<Q, In>,
    putDataSource: PutDataSource<Q, In>,
    private val deleteDataSource: DeleteDataSource<Q>,
    toOutMapper: (In) -> Out,
    toInMapper: (Out) -> In
) : GetDataSource<Q, Out>, PutDataSource<Q, Out>, DeleteDataSource<Q> {

    private val getDataSourceMapper = GetDataSourceMapper(getDataSource, toOutMapper)
    private val putDataSourceMapper = PutDataSourceMapper(putDataSource, toOutMapper, toInMapper)

    override suspend fun get(query: Q) = getDataSourceMapper.get(query)

    override suspend fun getAll(query: Q) = getDataSourceMapper.getAll(query)

    override suspend fun put(query: Q, value: Out?) = putDataSourceMapper.put(query, value)

    override suspend fun putAll(query: Q, value: List<Out>?) =
        putDataSourceMapper.putAll(query, value)

    override suspend fun delete(query: Q) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Q) = deleteDataSource.deleteAll(query)
}

class GetDataSourceMapper<Q, In, Out>(
    private val getDataSource: GetDataSource<Q, In>,
    private val toOutMapper: (In) -> Out
) : GetDataSource<Q, Out> {

    override suspend fun get(query: Q) = getDataSource.get(query).map(toOutMapper)

    override suspend fun getAll(query: Q) =
        getDataSource.getAll(query).map { it.map(toOutMapper) }
}

class PutDataSourceMapper<Q, In, Out>(
    private val putDataSource: PutDataSource<Q, In>,
    private val toOutMapper: (In) -> Out,
    private val toInMapper: (Out) -> In
) : PutDataSource<Q, Out> {

    override suspend fun put(query: Q, value: Out?): Either<Failure, Out> {
        val mapped = value?.let { toInMapper(it) }
        return putDataSource.put(query, mapped)
            .map { toOutMapper(it) }
    }

    override suspend fun putAll(query: Q, value: List<Out>?): Either<Failure, List<Out>> {
        val mapped = value?.map(toInMapper)
        return putDataSource.putAll(query, mapped)
            .map { it.map(toOutMapper) }
    }
}
