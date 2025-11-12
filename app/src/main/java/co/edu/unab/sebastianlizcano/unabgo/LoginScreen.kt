package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay

private lateinit var auth: FirebaseAuth
private lateinit var googleSignInClient: GoogleSignInClient

@Composable
fun LoginScreen(
    navController: NavController? = null,
    onBackClick: () -> Unit = {}
) {
    val purpleTop = Color(0xFF8700DD)
    val purpleBottom = Color(0xFF490077)
    val purpleText = Color(0xFF490077)
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val context = LocalContext.current

    auth = FirebaseAuth.getInstance()

    // Redirigir automáticamente si ya está logueado
    val currentUser = auth.currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            delay(400)
            navController?.navigate(Routes.PERFIL) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    // Configurar Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher para el inicio de sesión con Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val email = account.email ?: ""
            if (email.endsWith("@unab.edu.co")) {
                firebaseAuthWithGoogle(context, account.idToken!!, navController)
            } else {
                Toast.makeText(context, "Solo se permiten cuentas @unab.edu.co", Toast.LENGTH_LONG).show()
                auth.signOut()
                googleSignInClient.signOut()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(purpleTop, purpleBottom)))
    ) {
        // Flecha atrás
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo UNAB GO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo UNAB",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.main_title),
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(50.dp))

            // Banu tocando el botón (unidos visualmente)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.medbanu),
                    contentDescription = "Búho Banu",
                    modifier = Modifier
                        .size(210.dp)
                        .offset(y = 65.dp), // Banu ligeramente sobre el botón
                    contentScale = ContentScale.Fit
                )

                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = purpleText
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .width(280.dp)
                        .height(55.dp)
                        .offset(y = (-35).dp) // Subimos el botón para que Banu lo toque
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Iniciar sesión con Google",
                            fontFamily = openSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Texto informativo centrado
            Text(
                text = "Recuerda que solo se puede iniciar sesión con tu correo universitario (@unab.edu.co)",
                fontFamily = openSans,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

private fun firebaseAuthWithGoogle(context: Context, idToken: String, navController: NavController?) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(context, "Bienvenido ${user?.displayName}", Toast.LENGTH_SHORT).show()
                // Redirigir al Perfil y eliminar el login del stack
                navController?.navigate(Routes.PERFIL) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
