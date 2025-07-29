package com.michaeldang.countingcalories.fstorage

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.michaeldang.countingcalories.PreferencesImpl.getUserId

// TODO: Dagger
object CloudStorageImpl {
    val storage = Firebase.storage

    // TODO: Flow
    suspend fun getFilesForUser(context: Context): List<String> {
        val userImageRef = storage.reference.root.child("images/${getUserId(context)}")
        var allItems: List<String> = emptyList()

        userImageRef.listAll()
//            .addOnSuccessListener { (items) ->
//                allItems = items.map { it.path }
//            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to find items for user.", Toast.LENGTH_SHORT)
                    .show()
            }
        return allItems
    }
}