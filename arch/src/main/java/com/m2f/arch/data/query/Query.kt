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

package com.m2f.arch.data.query

// Queries

sealed interface Query

object AllObjectsQuery : Query

open class IdQuery<out T>(val identifier: T) : KeyQuery(identifier.toString())

open class IdsQuery<out T>(val identifiers: Collection<T>) : KeyQuery(identifiers.toString())

// Key value queries
open class KeyQuery(val key: String /* key associated to the query */) : Query