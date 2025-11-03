package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleManager.loadLocale(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
