package com.developersancho.rortycompose.data.remote.service

import com.developersancho.rortycompose.data.model.remote.location.LocationInfo
import com.developersancho.rortycompose.data.model.remote.location.LocationResponse
import retrofit2.http.*

interface LocationService {
    @GET(LOCATION)
    suspend fun getLocationList(
        @Query("page") page: Int,
        @QueryMap options: Map<String, String>? = null
    ): LocationResponse

    @GET("$LOCATION/{id}")
    suspend fun getLocation(
        @Path("id") locationId: Int
    ): LocationInfo

    @GET
    suspend fun getLocation(
        @Url url: String
    ): LocationInfo

    companion object {
        const val LOCATION = "location"
    }
}