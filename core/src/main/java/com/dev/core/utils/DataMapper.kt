package com.dev.core.utils

import com.dev.core.model.data.request.UserRequest
import com.dev.core.model.data.response.UserResponse
import com.dev.core.model.domain.UserDomain
import com.dev.core.model.presenter.User

object DataMapper {
    // user
    fun mapUserResponseToDomain(input: UserResponse): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major
        )

    fun mapUserDomainToPresenter(input: UserDomain): User =
        User(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major
        )

    fun mapPresenterToDomain(input: User): UserDomain =
        UserDomain(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major
        )

    fun mapDomainToRequest(input: UserDomain): UserRequest =
        UserRequest(
            name = input.name,
            mobile = input.mobile,
            education = input.education,
            major = input.major
        )
}