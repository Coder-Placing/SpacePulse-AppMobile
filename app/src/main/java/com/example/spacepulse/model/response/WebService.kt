package com.example.spacepulse.model.response

import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.model.beans.LoginRequest
import com.example.spacepulse.model.beans.LoginResponse
import com.example.spacepulse.model.beans.RegisterRequest
import com.example.spacepulse.model.beans.RegisterResponse
import com.example.spacepulse.model.beans.SpaceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WebService {
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/v1/space")
    suspend fun getSpaces(@Header("Authorization") token: String): Response<List<SpaceResponse>>

    @POST("api/v1/space")
    suspend fun createSpace(@Header("Authorization") token: String, @Body request: CreateSpaceRequest): Response<SpaceResponse>

    @DELETE("api/v1/space/{id}")
    suspend fun deleteSpace(@Header("Authorization") token: String, @Path("id") id: Long): Response<Unit>
}