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
import android.os.Looper
import androidx.lifecycle.MediatorLiveData
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal class ThrottlingLiveData<X> internal constructor(
    private val handler: Handler,
    private val duration: Long,
    private val timeUnit: TimeUnit
) : MediatorLiveData<X>() {

    private val isThrottling = AtomicBoolean(false)
    private val resetRunnable = Runnable {
        isThrottling.compareAndSet(true, false)
    }

    constructor(duration: Long, timeUnit: TimeUnit) : this(
        Handler(Looper.getMainLooper()),
        duration,
        timeUnit
    )

    override fun onInactive() {
        super.onInactive()
        handler.removeCallbacks(resetRunnable)
        isThrottling.set(false)
    }

    override fun setValue(value: X) {
        if (isThrottling.compareAndSet(false, true)) {
            super.setValue(value)
            handler.postDelayed(resetRunnable, timeUnit.toMillis(duration))
        }
    }

    override fun postValue(value: X) {
        if (!isThrottling.get()) {
            isThrottling.compareAndSet(false, true)
            super.postValue(value)
            handler.postDelayed(resetRunnable, timeUnit.toMillis(duration))
        }
    }
}
