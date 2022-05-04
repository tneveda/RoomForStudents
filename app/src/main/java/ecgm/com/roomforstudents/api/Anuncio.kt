package ecgm.com.roomforstudents.api

data class Anuncio(

    val id: Int,
    val morada: String,
    val telemovel: String,
    val qrcode: String,
    val latitude: Float,
    val longitude: Float,
    val n_quartos: Int,
    val ncasas_banho: Int,
    val fotografia: String,
    val preco: Float,
    val mobilado: String,
    val outros_atributos: String,
    val users_id: Int,
)
