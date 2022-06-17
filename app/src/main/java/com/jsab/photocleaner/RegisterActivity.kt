package com.jsab.photocleaner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jsab.photocleaner.ui.login.LoginActivity
import com.jsab.photocleaner.ui.login.User
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback
import java.util.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                println("++++++++++++++++++++++++++++++++++user id: ${users.size}++++++++++++++++++++++++++++++++++++++")
            } else {
                println("+++++++++++++++++++++++++++++++++++Parse Error: ${e.message}+++++++++++++++++++++++++++++++")
            }
        }


        var create_btn = findViewById<Button>(R.id.createAccountButton)
        create_btn.setOnClickListener {
            Toast.makeText(this@RegisterActivity, "registration success!", Toast.LENGTH_SHORT)
                .show()

            var userName = findViewById<EditText>(R.id.editUserName).text.toString()
            var firstName = findViewById<EditText>(R.id.editFirstName).text.toString()
            var lastName = findViewById<EditText>(R.id.editLastName).text.toString()
            var email = findViewById<EditText>(R.id.editTextTextEmailAddress2).text.toString()
            var userId = users.size + 1

            println("++++++++++++++++++++++++++++++++userid: ${userId}+++++++++++++++++++++++++++++++")
            // Configure Query
            val soccerPlayers = ParseObject("photoCleaner_users")

            // Store an object
            soccerPlayers.put("userId", userId)
            soccerPlayers.put("userName", userName)
            soccerPlayers.put("firstName", firstName)
            soccerPlayers.put("lastName", lastName)
            soccerPlayers.put("email", email)
            soccerPlayers.put("isRegistered", true)

            // Saving object
            soccerPlayers.saveInBackground(object : SaveCallback {
                override fun done(e: ParseException?) {
                    if (e == null) {
                        // Success
                    } else {
                        // Error
                    }
                }
            })

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}