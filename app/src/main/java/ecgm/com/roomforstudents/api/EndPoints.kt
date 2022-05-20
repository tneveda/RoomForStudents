package ecgm.com.roomforstudents.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("anuncios")
    fun getAnuncios(): Call<List<Anuncio>>

    @GET("users/{id}/anuncio")
    fun getAnunciosByUser(@Path("id") users_id: Int): Call<List<Anuncio>>



    @FormUrlEncoded
    @POST("anuncios")
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
                 @Field("outros_atributos") outros_atributos: String?): Call<OutputAnuncio>

    @FormUrlEncoded
   @POST("user_login")
   fun login(@Field("username") username: String?, @Field("password") password: String?): Call<OutputLogin>

     @GET("anuncios/{id}")
    fun getAnunciosById2(@Path("id") id: String?): Call<List<Anuncio>>

    @GET("anuncios/{id}")
    fun getAnunciosById(@Path("id") id: String?): Call<Anuncio>

    @FormUrlEncoded
    @POST("user")
    fun registar(@Field("username") username: String?,
                 @Field("email") email: String?,
                 @Field("password") password: String?): Call<OutputRegisto>


    @FormUrlEncoded
    @POST("editar_anuncios/{id}")
    fun editar(@Path("id") id: Int?,
               @Field("users_id") users_id: Int?,
               @Field("morada") morada: String?,
               @Field("n_quartos") n_quartos: Int?,
               @Field("latitude") latitude: Double?,
               @Field("longitude") longitude: Double?,
               @Field("fotografia") fotografia: String?,
               @Field("preco") preco: Double?,
               @Field("ncasas_banho") ncasas_banho: Int?,
               @Field("telemovel") telemovel: String?,
               @Field("mobilado") mobilado: String?,
               @Field("outros_atributos") outros_atributos: String?): Call<OutputEditar>

    @POST("apagarAnuncios/{id}")
    fun apagarAnuncio(@Path("id") id: Int?): Call<OutputApagar>




}