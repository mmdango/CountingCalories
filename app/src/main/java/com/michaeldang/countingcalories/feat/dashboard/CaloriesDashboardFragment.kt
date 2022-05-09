package com.michaeldang.countingcalories.feat.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.michaeldang.countingcalories.PreferencesImpl
import com.michaeldang.countingcalories.R
import com.michaeldang.countingcalories.fstorage.CloudStorageImpl

class CaloriesDashboardFragment : Fragment() {
    lateinit var dateTextView: TextView
    lateinit var imageViewPager: ViewPager2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dashboard_fragment, container, false)
        imageViewPager = view.findViewById(R.id.progress_pics_view_pager)
        val imageViewPagerAdapter = ImageViewPagerAdapter(requireContext())
        val userImageRef = CloudStorageImpl.storage.reference.root.child("images/${
            PreferencesImpl.getUserId(requireContext())
        }")

        userImageRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                imageViewPagerAdapter.imageList = items
                imageViewPagerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to find items for user.", Toast.LENGTH_SHORT)
                    .show()
            }

        imageViewPager.adapter = imageViewPagerAdapter
        return view
    }


    class ImageViewPagerAdapter(val context: Context) : RecyclerView.Adapter<ImageViewHolder>() {
        var imageList = listOf<StorageReference>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = View.inflate(context, R.layout.image_view_holder, null)
            view.layoutParams = ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            val viewHolder = ImageViewHolder(view)
            return viewHolder
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.textView.text = imageList[position].path
            imageList[position].downloadUrl.addOnSuccessListener {
                Glide.with(holder.imageView).load(it).into(holder.imageView)
            }

        }

        override fun getItemCount(): Int {
            return imageList.size
        }

    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val textView: TextView
        init {
            imageView = view.findViewById(R.id.progress_image_view)
            textView = view.findViewById(R.id.file_name)
        }

    }
 }