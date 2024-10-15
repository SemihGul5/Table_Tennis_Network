package com.abrebo.tabletennishub.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class SignUpViewModel @Inject constructor (var repository: Repository,
                                           application: Application):AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    fun saveUser(user: User){
        repository.saveUser(user)
    }
    fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,
                    context.getString(R.string.Emailhasbeensentpleaseverifyyouraccount),
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,
                    context.getString(R.string.Emailverificationfailed),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkUserNameAvailability(userName: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isAvailable = withContext(Dispatchers.IO) {
                repository.checkUserNameAvailability(userName)
            }
            callback(isAvailable)
        }
    }
    fun createUserWithEmailAndPassword(auth: FirebaseAuth, email:String, password:String,
                                       userObject:User, progressBar: ProgressBar){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification(auth.currentUser!!)
                saveUser(userObject)
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Kayıt Başarılı", Toast.LENGTH_SHORT).show()

            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Authentication failed - ${task.exception?.localizedMessage?.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}