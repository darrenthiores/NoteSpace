package com.dev.core.domain

import com.dev.core.data.repository.IRepository

class Interactor(
    private val repository: IRepository
): UseCase {
}