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
package com.cexdirect.livedatax

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class TransformationsKtTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Test
    fun filter() {
        val given = MutableLiveData<Int>()

        given.filter { it > 10 }.test().apply {
            given.value = 5
            given.value = 20
        }.assertNever { it <= 10 }.assertValue(20).assertHistorySize(1)
    }

    @Test
    fun map() {
        val given = MutableLiveData<Int>()

        given.map { it + 10 }.test().apply { given.value = 5 }.assertValue(15).assertHistorySize(1)
    }

    @Test
    fun switchMap() {
        val given = MutableLiveData<Int>()

        given.switchMap { MutableLiveData(it.toString()) }.test().apply {
            given.value = 100500
        }.assertValue("100500").assertHistorySize(1)
    }

    @Test
    fun distinctUntilChanged() {
        val given = MutableLiveData(1)

        given.distinctUntilChanged().test().apply {
            given.value = 1
            given.value = 1
            given.value = 10
        }.assertValue(10).assertHistorySize(2)
    }

    @Test
    fun distinctUntilChangedWithComparator() {
        val given = MutableLiveData(1)

        given.distinctUntilChanged { previous, current ->
            current > previous
        }.test().apply {
            given.value = -1
            given.value = 5
            given.value = 4
        }.assertNever { it == -1 }.assertValue(5).assertNever { it == 4 }
    }

    @Test
    fun combineLatestWith() {
        val given = MutableLiveData<Int>()
        val other = MutableLiveData<String>()

        given.combineLatestWith(other) { first, second ->
            "$first$second"
        }.test().apply {
            given.value = 1
            other.value = "A"
            other.value = "B"
            given.value = 2
        }.valueHistory().let {
            assertThat(it).containsExactly("1A", "1B", "2B")
        }
    }
}
