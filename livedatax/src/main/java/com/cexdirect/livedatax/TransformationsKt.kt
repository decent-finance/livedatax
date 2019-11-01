/*
 *    Copyright 2019 CEX.​IO Ltd (UK)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
@file:JvmName("TransformationsKt")

package com.cexdirect.livedatax

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

fun <X> LiveData<X>.filter(predicate: (value: X) -> Boolean): LiveData<X> =
    MediatorLiveData<X>().apply {
        addSource(this@filter) {
            if (predicate.invoke(it)) {
                value = it
            }
        }
    }

fun <X, Y> LiveData<X>.map(mapFunction: (value: X) -> Y): LiveData<Y> =
    Transformations.map(this) { mapFunction.invoke(it) }

fun <X, Y> LiveData<X>.switchMap(switchMapFunction: (value: X) -> LiveData<Y>): LiveData<Y> =
    Transformations.switchMap(this) { input: X -> switchMapFunction.invoke(input) }

fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> = Transformations.distinctUntilChanged(this)

fun <X> LiveData<X>.distinctUntilChanged(compareFunction: (previous: X, current: X) -> Boolean): LiveData<X> =
    MediatorLiveData<X>().apply {
        addSource(this@distinctUntilChanged, object : Observer<X> {
            var firstTime = true

            override fun onChanged(current: X) {
                val previous = this@apply.value
                if (
                    firstTime
                    || (previous == null && current != null)
                    || (previous != null && compareFunction.invoke(previous, current))
                ) {
                    firstTime = false
                    this@apply.value = current
                }
            }
        })
    }
