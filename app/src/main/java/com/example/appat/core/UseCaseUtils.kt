package com.example.appat.core

fun interface UseCaseSuspend<Params, Return> {
    suspend operator fun invoke(params: Params) : Return
}