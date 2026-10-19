package com.abrebo.tabletennishub.ui.viewmodel.MainPage

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LogInViewModel @Inject constructor (var repository: Repository,
                                          application: Application): AndroidViewModel(application){
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

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
    fun checkRegisteredEmail(email: String, onResult: (Boolean?) -> Unit) {
        viewModelScope.launch {
            val isRegistered = try {
                withContext(Dispatchers.IO) { repository.isUserEmailRegistered(email) }
            } catch (_: Exception) {
                null
            }
            onResult(isRegistered)
        }
    }
    fun sendPasswordResetEmail(auth: FirebaseAuth,email:String, onComplete: () -> Unit = {}){
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(context,
                    context.getString(R.string.Passwordresetemailhasbeensent),
                    Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            onComplete()
        }
    }
    fun signInWithEmailAndPassword(auth: FirebaseAuth,email: String,password:String,progressBar:ProgressBar,it:View){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.GONE
                Navigation.findNavController(it).navigate(R.id.action_logInFragment_to_mainPageActivity)
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
