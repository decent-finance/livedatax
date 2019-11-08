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

import android.os.Handler
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ThrottlingLiveDataTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Mock
    lateinit var handler: Handler

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val executor = Executors.newSingleThreadScheduledExecutor()

        doAnswer {
            val runnable = it.getArgument<Runnable>(0)
            val delay = it.getArgument<Long>(1)
            executor.schedule(runnable, delay, TimeUnit.MILLISECONDS)
            true
        }.whenever(handler).postDelayed(any(), any())
    }

    @After
    fun tearDown() {
        reset(handler)
    }

    @Test
    fun throttleItemsAfterFirst() {
        val given = MutableLiveData<Int>()

        given.throttleFirst(handler, 5, TimeUnit.SECONDS)
            .test()
            .apply {
                given.value = 1
                given.value = 2
                given.value = 3
            }
            .assertValue { it == 1 }
            .assertHistorySize(1)
    }

    @Test
    fun throttleOneItem() {
        val given = MutableLiveData<Int>()

        given.throttleFirst(handler, 10, TimeUnit.MILLISECONDS)
            .test()
            .apply {
                given.value = 1
                given.value = 2
                Thread.sleep(100)
                given.value = 3
            }
            .assertValue { it == 3 }
            .assertHistorySize(2)
            .valueHistory()
            .let { assertThat(it).containsExactly(1, 3) }
    }

    @Test
    fun dontThrottleItemsAfterTimeout() {
        val given = MutableLiveData<Int>()

        given.throttleFirst(handler, 10, TimeUnit.MILLISECONDS)
            .test()
            .apply {
                given.value = 1
                Thread.sleep(100)
                given.value = 2
                Thread.sleep(100)
                given.value = 3
            }
            .assertValue { it == 3 }
            .assertHistorySize(3)
            .valueHistory()
            .let { assertThat(it).containsExactly(1, 2, 3) }
    }
}
