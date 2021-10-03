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

package com.m2f.arch.data.datasource.memory

import arrow.core.Either
import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.error.Failure
import com.m2f.arch.data.query.KeyQuery

class InMemoryDataSource<V> : GetDataSource<KeyQuery, V>, PutDataSource<KeyQuery, V>, DeleteDataSource<KeyQuery> {

    private val objects: MutableMap<String, V> = mutableMapOf()
    private val arrays: MutableMap<String, List<V>> = mutableMapOf()

    override suspend fun get(query: KeyQuery): Either<Failure, V> {
      val item = objects[query.key]
      return if (item != null) {
        Either.Right<V>(item)
      } else {
        Either.Left(Failure.DataNotFound)
      }
    }

    override suspend fun getAll(query: KeyQuery): Either<Failure, List<V>> {
      val list = arrays[query.key]
      return if (!list.isNullOrEmpty()) {
        Either.Right(list)
      } else {
        Either.Left(Failure.DataNotFound)
      }
    }

    override suspend fun put(query: KeyQuery, value: V?): Either<Failure, V> =
      if (value != null) {
        objects[query.key] = value
        Either.Right<V>(value)
      } else {
        Either.Left(Failure.DataEmpty)
      }

    override suspend fun putAll(query: KeyQuery, value: List<V>?): Either<Failure, List<V>> =
      if (value != null) {
        arrays[query.key] = value
        Either.Right(value)
      } else {
        Either.Left(Failure.DataEmpty)
      }

    override suspend fun delete(query: KeyQuery): Either<Failure, Unit> {
      objects.remove(query.key)
      return Either.Right(Unit)
    }

    override suspend fun deleteAll(query: KeyQuery): Either<Failure, Unit> {
      arrays.remove(query.key)
      return Either.Right(Unit)
    }
}