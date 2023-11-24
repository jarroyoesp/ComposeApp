import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jarroyo.feature.home.shared.RootView
import com.jarroyo.feature.home.shared.di.initKoin

fun main() {

    initKoin(enableNetworkLogs = false)
    application {
        Window(
            title = "Compose APP",
            onCloseRequest = ::exitApplication
        ) {
            RootView()
        }
    }
}