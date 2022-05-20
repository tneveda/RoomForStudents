package ecgm.com.roomforstudents.api

data class OutputEditar(
    val users_id: Int,
    val morada: String,
    val n_quartos: Int,
    val latitude: Double,
    val longitude: Double,
    val fotografia: String,
    val preco: Double,
    val ncasas_banho: Int,
    val telemovel: String,
    val mobilado: String,
    val outros_atributos: String,
    val status: String,
    val MSG: String
)
