package com.example.greencitylife.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.greencitylife.R

import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.greencitylife.adapter.GalleryImageAdapter
import com.example.greencitylife.adapter.GalleryImageClickListener
import com.example.greencitylife.adapter.Image
import com.example.greencitylife.fragment.GalleryFullscreenFragment

class GardenImageGallery : AppCompatActivity(), GalleryImageClickListener {
    // gallery column count
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garden_image_gallery)
        // init adapter
        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        // init recyclerview
        val gridlayout = GridLayoutManager(this, SPAN_COUNT)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.apply {
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = galleryAdapter
        }
        // load images
        loadImages()
    }
    private fun loadImages() {
        imageList.add(Image("https://i.ibb.co/wBYDxLq/beach.jpg", "Beach Houses"))
        imageList.add(Image("https://i.ibb.co/gM5NNJX/butterfly.jpg", "Butterfly"))
        imageList.add(Image("https://i.ibb.co/10fFGkZ/car-race.jpg", "Car Racing"))
        imageList.add(Image("https://i.ibb.co/ygqHsHV/coffee-milk.jpg", "Coffee with Milk"))
        imageList.add(Image("https://i.ibb.co/7XqwsLw/fox.jpg", "Fox"))
        imageList.add(Image("https://i.ibb.co/L1m1NxP/girl.jpg", "Mountain Girl"))
        imageList.add(Image("https://i.ibb.co/wc9rSgw/desserts.jpg", "Desserts Table"))
        imageList.add(Image("https://i.ibb.co/wdrdpKC/kitten.jpg", "Kitten"))
        imageList.add(Image("https://i.ibb.co/dBCHzXQ/paris.jpg", "Paris Eiffel"))
        imageList.add(Image("https://i.ibb.co/JKB0KPk/pizza.jpg", "Pizza Time"))
        imageList.add(Image("https://i.ibb.co/VYYPZGk/salmon.jpg", "Salmon "))
        imageList.add(Image("https://i.ibb.co/JvWpzYC/sunset.jpg", "Sunset in Beach"))
        galleryAdapter.notifyDataSetChanged()
    }
    override fun onClick(position: Int) {
        // handle click of image
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", position)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val galleryFragment = GalleryFullscreenFragment()
        galleryFragment.setArguments(bundle)
        galleryFragment.show(fragmentTransaction, "gallery")
    }
}