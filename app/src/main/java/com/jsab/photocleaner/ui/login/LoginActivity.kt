package com.jsab.photocleaner.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jsab.photocleaner.MainActivity
import com.jsab.photocleaner.databinding.ActivityLoginBinding

import com.jsab.photocleaner.R
import com.jsab.photocleaner.RegisterActivity
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        var users = getUsers()
        var register_btn = findViewById<Button>(R.id.register)
        register_btn.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
//                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())

                println("++++++++++++++++++++++++++++++++++first Name: ${users[0].firstName}++++++++++++++++++++++++++++++++++++++")
                var checkUser = User(1, "johnsmith", "John", "Smith", "johnsmith@email.com", true)
                var userName = username.text.toString()
                var pw = password.text.toString()

                var isActiveUser = false
                for(item in users){
                    if(item.userName == userName){
                        isActiveUser = true
                    }
                }

                if (isActiveUser) {
                    println("+++++++++++++++++++++++++user is registered: ${userName} | ${pw}+++++++++++++++++++++++++++++")
                    Toast.makeText(this@LoginActivity, "login successful! welcome, ${userName}.", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    println("+++++++++++++++++++++++++NOT REGISTERED: ${userName} | ${pw}+++++++++++++++++++++++++++++")
                    Toast.makeText(this@LoginActivity, "${userName} is NOT registered.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }


//                var intent = Intent(this@LoginActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
    }


    private fun getUsers(): ArrayList<User> {
        var users: ArrayList<User> = ArrayList()

        var query = ParseQuery.getQuery<ParseObject>("photoCleaner_users")
        // read data from users db
        query.selectKeys(
            java.util.List.of(
                "userId",
                "userName",
                "firstName",
                "lastName",
                "email",
                "isRegistered"
            )
        )
        query.findInBackground { objects: List<ParseObject>, e: ParseException? ->
            if (e == null) {
                for (obj in objects) {
                    var tempObj = User(
                        obj["userId"] as Int,
                        obj["userName"] as String,
                        obj["firstName"] as String,
                        obj["lastName"] as String,
                        obj["email"] as String,
                        obj["isRegistered"] as Boolean
                    )
                    users.add(tempObj);
                }
                println("++++++++++++++++++++++++++++++++++in the if statement count: ${users.size}++++++++++++++++++++++++++++++++++++++")
            } else {
                println("+++++++++++++++++++++++++++++++++++Parse Error: ${e.message}+++++++++++++++++++++++++++++++")
            }
        }
        return users
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}


class User(
    pUserId: Int,
    pUserName: String,
    pFirstName: String,
    pLastName: String,
    pEmail: String,
    pIsRegistered: Boolean
) {
    var userId: Int = pUserId
    var userName: String = pUserName
    var firstName: String = pFirstName
    var lastName: String = pLastName
    var email: String = pEmail
    var isRegistered: Boolean = pIsRegistered
}