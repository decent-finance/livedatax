# LiveDataX

![CircleCI](https://img.shields.io/circleci/build/github/decent-finance/livedatax?token=76b540311f5461a4524860369077af30aa910428) [![Maintainability](https://api.codeclimate.com/v1/badges/7c8b9a85f89925841800/maintainability)](https://codeclimate.com/github/decent-finance/livedatax/maintainability) ![Bintray](https://img.shields.io/bintray/v/decent-finance/utils/com.cexdirect.livedatax)

A set of extension functions which can be used to apply transformations to your LiveData to make it look like you are using RxJava.

## Example
```kotlin
MutableLiveData<Int>()
    .filter { it > 10 }
    .map { it + 10 }
    .switchMap { MutableLiveData(it.toString()) }
```

## Installation
Add the following repository to your top-level `build.gradle`

```groovy
allprojects {
    repositories {
        maven {	url 'https://dl.bintray.com/decent-finance/utils' }
    }
}
```

Add the following dependency to your module-level `buld.gradle`
```groovy
implementation "com.cexdirect:livedatax:$someVersion"
```

## Licence

```
   Copyright 2019 CEX.​IO Ltd (UK)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
