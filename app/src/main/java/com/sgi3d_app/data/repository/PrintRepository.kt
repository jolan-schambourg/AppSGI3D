import com.sgi3d_app.data.remote.OctoRetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PrintRepository {

    private val _state = MutableStateFlow(PrintState())
    val state: StateFlow<PrintState> = _state

    suspend fun update(apiKey: String) {
        try {
            val response = OctoRetrofitInstance.api.getJobStatus(apiKey)

            _state.value = PrintState(
                status = response.state ?: "",
                progress = ((response.progress.completion ?: 0.0) * 100).toInt(),
                timeLeft = response.progress.printTimeLeft ?: 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}