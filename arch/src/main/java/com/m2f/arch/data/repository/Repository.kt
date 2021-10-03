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

package com.m2f.arch.data.repository

import arrow.core.Either
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.operation.DefaultOperation
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.IdQuery
import com.m2f.arch.data.query.IdsQuery
import com.m2f.arch.data.query.Query

interface Repository {

    fun notSupportedQuery(): Nothing = throw UnsupportedOperationException("Query not supported")

    fun notSupportedOperation(): Nothing =
        throw UnsupportedOperationException("Operation not defined")
}

// Repositories
interface GetRepository<Q, V> : Repository {
    suspend fun get(query: Q, operation: Operation = DefaultOperation): Either<Failure, V>
    suspend fun getAll(
        query: Q,
        operation: Operation = DefaultOperation
    ): Either<Failure, List<V>>
}

interface PutRepository<Q, V> : Repository {
    suspend fun put(
        query: Q,
        value: V? = null,
        operation: Operation = DefaultOperation
    ): Either<Failure, V>

    suspend fun putAll(
        query: Q,
        value: List<V>? = emptyList(),
        operation: Operation = DefaultOperation
    ): Either<Failure, List<V>>
}

interface DeleteRepository<Q> : Repository {
    suspend fun delete(query: Q, operation: Operation = DefaultOperation): Either<Failure, Unit>
    suspend fun deleteAll(
        query: Q,
        operation: Operation = DefaultOperation
    ): Either<Failure, Unit>
}

// Extensions

suspend fun <K, V> GetRepository<IdQuery<K>, V>.get(id: K, operation: Operation = DefaultOperation) = get(
    IdQuery(id), operation
)

suspend fun <K, V> GetRepository<IdsQuery<K>, V>.getAll(ids: List<K>, operation: Operation = DefaultOperation) =
    getAll(
        IdsQuery(ids), operation
    )

suspend fun <K, V> PutRepository<IdQuery<K>, V>.put(id: K, value: V?, operation: Operation = DefaultOperation) =
    put(
        IdQuery(id), value, operation
    )

suspend fun <K, V> PutRepository<IdsQuery<K>, V>.putAll(
    ids: List<K>,
    values: List<V>? = emptyList(),
    operation: Operation = DefaultOperation
) = putAll(
    IdsQuery(ids), values,
    operation
)

suspend fun <K> DeleteRepository<IdQuery<K>>.delete(id: K, operation: Operation = DefaultOperation) = delete(
    IdQuery(id), operation
)

suspend fun <K> DeleteRepository<IdsQuery<K>>.deleteAll(ids: List<K>, operation: Operation = DefaultOperation) =
    deleteAll(
        IdsQuery(ids), operation
    )

fun <Q, K, V> GetRepository<Q, K>.withMapping(mapper: (K) -> V): GetRepository<Q, V> =
    GetRepositoryMapper(this, mapper)

operator fun <Q, K, V> GetRepository<Q, K>.plus(mapper: (K) -> V): GetRepository<Q, V> =
    withMapping(mapper)

fun <Q, K, V> GetRepository<Q, K>.toGetRepository(mapper: (K) -> V): GetRepository<Q, V> =
    withMapping(mapper)

fun <Q, K, V> PutRepository<Q, K>.withMapping(
    toMapper: (K) -> V,
    fromMapper: (V) -> K
): PutRepository<Q, V> = PutRepositoryMapper(this, toMapper, fromMapper)

fun <Q, K, V> PutRepository<Q, K>.toPutRepository(
    toMapper: (K) -> V,
    fromMapper: (V) -> K
): PutRepository<Q, V> = withMapping(toMapper, fromMapper)