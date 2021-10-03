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

import com.m2f.arch.data.datasource.DeleteDataSource
import com.m2f.arch.data.datasource.GetDataSource
import com.m2f.arch.data.datasource.PutDataSource
import com.m2f.arch.data.operation.Operation
import com.m2f.arch.data.query.Query

class SingleDataSourceRepository<Q, T>(
    private val getDataSource: GetDataSource<Q, T>,
    private val putDataSource: PutDataSource<Q, T>,
    private val deleteDataSource: DeleteDataSource<Q>
) : GetRepository<Q, T>, PutRepository<Q, T>, DeleteRepository<Q> {

    override suspend fun get(query: Q, operation: Operation) = getDataSource.get(query)

    override suspend fun getAll(query: Q, operation: Operation) = getDataSource.getAll(query)

    override suspend fun put(query: Q, value: T?, operation: Operation) =
        putDataSource.put(query, value)

    override suspend fun putAll(query: Q, value: List<T>?, operation: Operation) =
        putDataSource.putAll(query, value)

    override suspend fun delete(query: Q, operation: Operation) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Q, operation: Operation) =
        deleteDataSource.deleteAll(query)
}

class SingleGetDataSourceRepository<Q, T>(private val getDataSource: GetDataSource<Q, T>) :
    GetRepository<Q, T> {

    override suspend fun get(query: Q, operation: Operation) = getDataSource.get(query)

    override suspend fun getAll(query: Q, operation: Operation) = getDataSource.getAll(query)
}

class SinglePutDataSourceRepository<Q, T>(private val putDataSource: PutDataSource<Q, T>) :
    PutRepository<Q, T> {
    override suspend fun put(query: Q, value: T?, operation: Operation) =
        putDataSource.put(query, value)

    override suspend fun putAll(query: Q, value: List<T>?, operation: Operation) =
        putDataSource.putAll(query, value)
}

class SingleDeleteDataSourceRepository<Q>(private val deleteDataSource: DeleteDataSource<Q>) :
    DeleteRepository<Q> {

    override suspend fun delete(query: Q, operation: Operation) = deleteDataSource.delete(query)

    override suspend fun deleteAll(query: Q, operation: Operation) =
        deleteDataSource.deleteAll(query)
}