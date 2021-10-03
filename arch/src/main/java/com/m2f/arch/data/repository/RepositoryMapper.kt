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
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.Query

/**
 * This repository uses mappers to map objects and redirects them to the contained repository, acting as a simple "translator".
 *
 * @param getRepository Repository with get operations
 * @param putRepository Repository with put operations
 * @param deleteRepository Repository with delete operations
 * @param toOutMapper Mapper to map data objects to domain objects
 * @param toInMapper Mapper to map domain objects to data objects
 */
class RepositoryMapper<Q, In, Out>(
    private val getRepository: GetRepository<Q, In>,
    private val putRepository: PutRepository<Q, In>,
    private val deleteRepository: DeleteRepository<Q>,
    private val toOutMapper: (In) -> Out,
    private val toInMapper: (Out) -> In
) : GetRepository<Q, Out>, PutRepository<Q, Out>, DeleteRepository<Q> {

    override suspend fun get(query: Q, operation: Operation) =
        getRepository.get(query, operation).map { toOutMapper(it) }

    override suspend fun getAll(query: Q, operation: Operation) =
        getRepository.getAll(query, operation).map { it.map(toOutMapper) }

    override suspend fun put(
        query: Q,
        value: Out?,
        operation: Operation
    ): Either<Failure, Out> {
        val mapped = value?.let { toInMapper(it) }
        return putRepository.put(query, mapped, operation).map(toOutMapper)
    }

    override suspend fun putAll(
        query: Q,
        value: List<Out>?,
        operation: Operation
    ): Either<Failure, List<Out>> {
        val mapped = value?.map(toInMapper)
        return putRepository.putAll(query, mapped, operation).map { it.map(toOutMapper) }
    }

    override suspend fun delete(query: Q, operation: Operation) =
        deleteRepository.delete(query, operation)

    override suspend fun deleteAll(query: Q, operation: Operation) =
        deleteRepository.deleteAll(query, operation)
}

class GetRepositoryMapper<Q, In, Out>(
    private val getRepository: GetRepository<Q, In>,
    toOutMapper: (In) -> Out
) : GetRepository<Q, Out>, (In) -> Out by toOutMapper {

    override suspend fun get(query: Q, operation: Operation): Either<Failure, Out> =
        getRepository.get(query, operation).map(this)

    override suspend fun getAll(query: Q, operation: Operation): Either<Failure, List<Out>> =
        getRepository.getAll(query, operation).map { it.map(this) }
}

class PutRepositoryMapper<Q, In, Out>(
    private val putRepository: PutRepository<Q, In>,
    private val toOutMapper: (In) -> Out,
    private val toInMapper: (Out) -> In
) : PutRepository<Q, Out> {

    override suspend fun put(
        query: Q,
        value: Out?,
        operation: Operation
    ): Either<Failure, Out> {
        val mapped = value?.let { toInMapper(it) }
        return putRepository.put(query, mapped, operation).map(toOutMapper)
    }

    override suspend fun putAll(
        query: Q,
        value: List<Out>?,
        operation: Operation
    ): Either<Failure, List<Out>> {
        val mapped = value?.map(toInMapper)
        return putRepository.putAll(query, mapped, operation).map { it.map(toOutMapper) }
    }
}