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
import com.m2f.arch.data.query.IdQuery
import com.m2f.arch.data.query.IdsQuery
import com.m2f.arch.data.query.Query
import com.m2f.arch.data.repository.GetRepository
import com.m2f.arch.data.repository.PutRepository
import com.m2f.arch.data.repository.SingleDeleteDataSourceRepository
import com.m2f.arch.data.repository.SingleGetDataSourceRepository
import com.m2f.arch.data.repository.SinglePutDataSourceRepository
import com.m2f.arch.data.repository.withMapping

interface DataSource

// DataSources
interface GetDataSource<Q, V> : DataSource {
    suspend fun get(query: Q): Either<Failure, V>

    suspend fun getAll(query: Q): Either<Failure, List<V>>
}

interface PutDataSource<Q, V> : DataSource {
    suspend fun put(query: Q, value: V? = null): Either<Failure, V>

    suspend fun putAll(query: Q, value: List<V>? = emptyList()): Either<Failure, List<V>>
}

interface DeleteDataSource<Q> : DataSource {
    suspend fun delete(query: Q): Either<Failure, Unit>

    suspend fun deleteAll(query: Q): Either<Failure, Unit>
}

// Extensions
suspend fun <K, V> GetDataSource<IdQuery<K>, V>.get(id: K): Either<Failure, V> = get(IdQuery(id))

suspend fun <K, V> GetDataSource<IdsQuery<K>, V>.getAll(ids: List<K>): Either<Failure, List<V>> =
    getAll(IdsQuery(ids))

suspend fun <K, V> PutDataSource<IdQuery<K>, V>.put(id: K, value: V?): Either<Failure, V> =
    put(IdQuery(id), value)

suspend fun <K, V> PutDataSource<IdsQuery<K>, V>.putAll(ids: List<K>, values: List<V>?) =
    putAll(IdsQuery(ids), values)

suspend fun <K> DeleteDataSource<IdQuery<K>>.delete(id: K) = delete(IdQuery(id))

suspend fun <K> DeleteDataSource<IdsQuery<K>>.deleteAll(ids: List<K>) = deleteAll(IdsQuery(ids))

// Extensions to create
fun <Q, V> GetDataSource<Q, V>.toGetRepository() = SingleGetDataSourceRepository(this)

fun <Q, K, V> GetDataSource<Q, K>.withMapping(mapper: (K) -> V): GetDataSource<Q, V> =
    GetDataSourceMapper(this, mapper)

operator fun <Q, K, V> GetDataSource<Q, K>.plus(mapper: (K) -> V): GetDataSource<Q, V> =
    withMapping(mapper)

fun <Q, K, V> GetDataSource<Q, K>.toGetRepository(mapper: (K) -> V): GetRepository<Q, V> =
    toGetRepository().withMapping(mapper)

fun <Q, V> PutDataSource<Q, V>.toPutRepository() = SinglePutDataSourceRepository(this)

fun <Q, K, V> PutDataSource<Q, K>.toPutRepository(
    toMapper: (K) -> V,
    fromMapper: (V) -> K
): PutRepository<Q, V> = toPutRepository().withMapping(toMapper, fromMapper)

fun <Q, K, V> PutDataSource<Q, K>.withMapping(
    toMapper: (K) -> V,
    fromMapper: (V) -> K
): PutDataSource<Q, V> = PutDataSourceMapper(this, toMapper, fromMapper)

fun <Q> DeleteDataSource<Q>.toDeleteRepository() = SingleDeleteDataSourceRepository(this)