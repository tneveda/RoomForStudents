package ecgm.com.roomforstudents.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("/RoomForStudents/api/anuncios")
    fun getAnuncios(): Call<List<Anuncio>>

    @GET("/RoomForStudents/api/users/{id}/anuncio")
    fun getAnunciosByUser(@Path("id") users_id: Int): Call<List<Anuncio>>

    @GET("/RoomForStudents/api/anuncios_t1")
    fun getAnuncios_t1(): Call<List<Anuncio>>


    @FormUrlEncoded
    @POST("/RoomForStudents/api/anuncios")
    fun anunciar(@Field("users_id") users_id: Int?,
                 @Field("morada") morada: String?,
                 @Field("n_quartos") n_quartos: Int?,
                 @Field("latitude") latitude: Double?,
                 @Field("longitude") longitude: Double?,
                 @Field("fotografia") fotografia: String?,
                 @Field("preco") preco: Double?,
                 @Field("ncasas_banho") ncasas_banho: Int?,
                 @Field("telemovel") telemovel: String?,
                 @Field("mobilado") mobilado: String?,
                 @Field("outros_atributos") outros_atributos: String?,
                 @Field("qrcode") qrcode: String?): Call<OutputAnuncio>

    @FormUrlEncoded
   @POST("/RoomForStudents/api/user_login")
   fun login(@Field("username") username: String?, @Field("password") password: String?): Call<OutputLogin>

     @GET("/RoomForStudents/api/anuncios/{id}")
    fun getAnunciosById2(@Path("id") id: String?): Call<List<Anuncio>>

    @GET("/RoomForStudents/api/anuncios/{id}")
    fun getAnunciosById(@Path("id") id: String?): Call<Anuncio>

    @FormUrlEncoded
    @POST("/RoomForStudents/api/user")
    fun registar(@Field("username") username: String?,
                 @Field("email") email: String?,
                 @Field("password") password: String?): Call<OutputRegisto>
}