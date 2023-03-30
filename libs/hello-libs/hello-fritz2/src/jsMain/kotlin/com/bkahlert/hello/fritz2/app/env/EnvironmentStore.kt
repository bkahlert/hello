package com.bkahlert.hello.fritz2.app.env

import dev.fritz2.core.RootStore

public class EnvironmentStore(initialData: Environment = Environment.EMPTY) : RootStore<Environment>(initialData)
