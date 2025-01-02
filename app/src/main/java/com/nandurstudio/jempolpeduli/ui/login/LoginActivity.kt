@file:Suppress("DEPRECATION")

package com.nandurstudio.jempolpeduli.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nandurstudio.jempolpeduli.MainActivity
import com.nandurstudio.jempolpeduli.R
import com.nandurstudio.jempolpeduli.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null
    private var binding: ActivityLoginBinding? = null

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Redirect langsung ke HomeFragment
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus backstack
            startActivity(intent)
            finish()
        }
    }
    // [END on_start_check_user]

    private fun updateUI() {
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Inisialisasi Firebase Auth
        auth = Firebase.auth

        // Inisialisasi ViewModel
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        // Inisialisasi One Tap Client
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // Atur sesuai kebutuhan
                    .build()
            )
            .build()

        // Menggunakan binding untuk akses elemen UI
        val usernameEditText = binding!!.username
        val passwordEditText = binding!!.password
        val emailSignInButton = binding!!.emailSignInButton
        val googleSignInButton = binding!!.googleSignInButton
        val eyeIcon = binding!!.eyeIcon
        val loadingProgressBar = binding!!.loadingProgressBar

        googleSignInButton!!.setOnClickListener {

            // Tampilkan loading
            loadingProgressBar!!.visibility = View.VISIBLE
            // Set up Google Sign-In options
            // [START config_signin]
            // Configure Google Sign In

            // Requesting additional scopes
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestScopes(Scope(Scopes.PLUS_LOGIN)) // Untuk akses lebih, seperti gender, birthday
                .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            // [END config_signin]

            // [START initialize_auth]
            // Initialize Firebase Auth
            auth = Firebase.auth
            // [END initialize_auth]

            // Start Google Sign-In Intent
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        loginViewModel!!.loginFormState.observe(this) { loginFormState: LoginFormState? ->
            if (loginFormState == null) {
                return@observe
            }
            emailSignInButton!!.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordEditText.error = getString(loginFormState.passwordError!!)
            }
        }

        loginViewModel!!.loginResult.observe(this) { loginResult: LoginResult? ->
            if (loginResult == null) {
                return@observe
            }
            loadingProgressBar!!.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)
            finish()
        }

        // Listener untuk perubahan teks username dan password
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)

        // Tombol ikon mata untuk show/hide password
        eyeIcon!!.setOnClickListener { v: View? ->
            if (passwordEditText.inputType == (InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeIcon.setImageResource(R.drawable.visibility_off_24px) // Ganti dengan ikon mata tertutup
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                eyeIcon.setImageResource(R.drawable.visibility_24px) // Ganti dengan ikon mata terbuka
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        emailSignInButton!!.setOnClickListener { v: View? ->
            loadingProgressBar!!.visibility = View.VISIBLE
            loginViewModel!!.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )

            // Redirect jika login berhasil
            loginViewModel!!.loginResult.observe(this) { loginResult: LoginResult? ->
                loadingProgressBar.visibility = View.GONE // Sembunyikan loading
                if (loginResult?.success != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus backstack
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView?) {
        val welcome = getString(R.string.welcome) + model!!.displayName
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        Toast.makeText(applicationContext, errorString!!, Toast.LENGTH_SHORT).show()
    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In berhasil, autentikasi dengan Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                binding?.loadingProgressBar?.visibility = View.VISIBLE // Tampilkan loading
                firebaseAuthWithGoogle(account.idToken!!)
                handleSignInResult(task)
            } catch (e: ApiException) {
                // Google Sign In gagal
                binding?.loadingProgressBar?.visibility = View.GONE // Sembunyikan loading
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Google Sign-In was successful
            val account = completedTask.getResult(ApiException::class.java)

            // Fetch and log the basic profile data
            val name = account?.displayName ?: "No Name"
            val email = account?.email ?: "No Email"
            val photoUrl = account?.photoUrl?.toString() ?: "No Photo"
            val id = account?.id ?: "No ID"
            val givenName = account?.givenName ?: "No Given Name"
            val familyName = account?.familyName ?: "No Family Name"

            // Log the fetched data
            Log.d("GoogleLoginData", "Name: $name")
            Log.d("GoogleLoginData", "Email: $email")
            Log.d("GoogleLoginData", "Photo URL: $photoUrl")
            Log.d("GoogleLoginData", "User ID: $id")
            Log.d("GoogleLoginData", "Given Name: $givenName")
            Log.d("GoogleLoginData", "Family Name: $familyName")

        } catch (e: ApiException) {
            // Handle failure
            Log.w("GoogleLoginData", "signInResult:failed code=${e.statusCode}")
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding?.loadingProgressBar?.visibility = View.VISIBLE // Tampilkan loading
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding?.loadingProgressBar?.visibility = View.GONE // Sembunyikan loading
                if (task.isSuccessful) {
                    // Berhasil masuk
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // Gagal masuk
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI()
                }
            }
    }
    // [END auth_with_google]
}