package com.example.notesapp.utils

import android.content.Context
import com.example.notesapp.utils.Constants.CURRENT_USER_ID
import com.example.notesapp.utils.Constants.KEY_EMAIL
import com.example.notesapp.utils.Constants.KEY_PASSWORD
import com.example.notesapp.utils.Constants.PREFS_TOKEN_FILE
import com.example.notesapp.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//This class is used to save and get token using shared preference
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    /*
    To access shared preference we use getSharedPreference with context

    2 parameter -> key which is the name of file where data stored
    define mode so that our file is accessed by our app only
     */
    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    /*
    function to save token using editor obj that is given by shared preference
    putString(USER_TOKEN, token)
    1 -> key, acc to which token is save
    2 -> token
     */
    fun saveToken(token: String){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    //function to get token getString(USER_TOKEN, null) 1-> key 2-> default value
    fun getToken(): String?{
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserDetails(email: String){
        val editor = prefs.edit()
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String?{
        return prefs.getString(KEY_EMAIL, "")
    }

    fun clearCredentials() {
        val editor = prefs.edit()
        editor.remove(KEY_EMAIL)
        editor.remove(KEY_PASSWORD)
        editor.remove(USER_TOKEN)
        editor.remove(CURRENT_USER_ID)
        editor.apply()
    }

    fun saveUserId(userId: String){
        val editor = prefs.edit()
        editor.putString(CURRENT_USER_ID, userId)
        editor.apply()
    }
    fun getUserId(): String?{
        return prefs.getString(CURRENT_USER_ID, "")
    }
}