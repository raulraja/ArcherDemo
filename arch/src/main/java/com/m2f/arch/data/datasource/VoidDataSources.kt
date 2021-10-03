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

class VoidDataSource<Q, V> : GetDataSource<Q, V>, PutDataSource<Q, V>, DeleteDataSource<Q> {
    override suspend fun get(query: Q): Either<Failure, V> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun getAll(query: Q): Either<Failure, List<V>> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun put(query: Q, value: V?): Either<Failure, V> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun putAll(query: Q, value: List<V>?): Either<Failure, List<V>> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun delete(query: Q) = Either.Left(Failure.UnsupportedOperation)

    override suspend fun deleteAll(query: Q) = Either.Left(Failure.UnsupportedOperation)
}

class VoidGetDataSource<Q, V> : GetDataSource<Q, V> {
    override suspend fun get(query: Q): Either<Failure, V> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun getAll(query: Q): Either<Failure, List<V>> =
        Either.Left(Failure.UnsupportedOperation)
}

class VoidPutDataSource<Q, V> : PutDataSource<Q, V> {
    override suspend fun put(query: Q, value: V?): Either<Failure, V> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun putAll(query: Q, value: List<V>?): Either<Failure, List<V>> =
        Either.Left(Failure.UnsupportedOperation)
}

class VoidDeleteDataSource<Q> : DeleteDataSource<Q> {
    override suspend fun delete(query: Q): Either<Failure, Unit> =
        Either.Left(Failure.UnsupportedOperation)

    override suspend fun deleteAll(query: Q): Either<Failure, Unit> =
        Either.Left(Failure.UnsupportedOperation)
}