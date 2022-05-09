package com.michaeldang.countingcalories.fstorage

import android.content.Context
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.michaeldang.countingcalories.PreferencesImpl.getUserId

// TODO: Dagger
object CloudStorageImpl {
    val storage = Firebase.storage

    // TODO: Flow
    suspend fun getFilesForUser(context: Context): List<String> {
        val userImageRef = storage.reference.root.child("images/${getUserId(context)}")
        var allItems: List<String> = emptyList()

        userImageRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                allItems = items.map { it.path }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to find items for user.", Toast.LENGTH_SHORT)
                    .show()
            }
        return allItems
    }
}