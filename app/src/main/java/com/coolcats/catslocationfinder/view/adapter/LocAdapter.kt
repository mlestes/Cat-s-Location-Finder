package com.coolcats.catslocationfinder.view.adapter

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.coolcats.catslocationfinder.R
import com.coolcats.catslocationfinder.model.ResultX
import com.coolcats.catslocationfinder.util.Utilities.Companion.myLog
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import kotlinx.android.synthetic.main.location_list_item.view.*

class LocAdapter(private val locDelegate: LocDelegate, private val placesClient: PlacesClient) :
    RecyclerView.Adapter<LocAdapter.LocViewHolder>() {

    private var list: List<ResultX> = listOf()

    interface LocDelegate {
        fun makeToast(message: String)
    }

    inner class LocViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateList(list: List<ResultX>) {
        this.list = emptyList()
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.location_list_item, parent, false)
        return LocViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: LocViewHolder, position: Int) {
//        myLog("${list[position].photos}")
        lateinit var ref: ResultX
        var attr: String? = null
        list[position].let { resultX ->
            holder.itemView.apply {
                location_name_txt.text = resultX.name
                location_latlng_txt.text = resultX.vicinity
                location_image.setImageResource(R.drawable.ic_broken_img)
                ref = resultX
            }.also {

                //this "block" of code found on android developer documentation
                // Define a Place ID.
                val placeId = ref.place_id

                // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
                val fields = listOf(Place.Field.PHOTO_METADATAS)

                // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
                val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
                placesClient.fetchPlace(placeRequest)
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place

                        // Get the photo metadata.
                        val metada = place.photoMetadatas
                        if (metada == null || metada.isEmpty()) {
                            myLog("No photo metadata.")
                            return@addOnSuccessListener
                        }
                        val photoMetadata = metada.first()

                        // Get the attribution text.
                        val attributions = photoMetadata?.attributions
                        val startIndex = attributions?.indexOf('>') ?: -1
                        val endIndex = attributions?.indexOf("</a>") ?: -1

                        //Start Index is INCLUSIVE. End Index is EXCLUSIVE
                        attr = attributions?.substring(startIndex + 1, endIndex)
                        attr?.let { myLog(it) }

                        // Create a FetchPhotoRequest.
                        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(200)
                            .setMaxHeight(400) // Optional.
                            .build()

                        // Perform the request
                        placesClient.fetchPhoto(photoRequest)
                            .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->

                                //Set on Success
                                val bitmap = fetchPhotoResponse.bitmap
                                holder.itemView.location_image.setImageBitmap(bitmap)
                            }.addOnFailureListener { exception: Exception ->

                                //Log the failure, set default
                                myLog("Place not found: " + exception.message)
                                holder.itemView.location_image.setImageResource(R.drawable.ic_broken_img)

                            }
                    }
            }
        }.also {

            //Toast the photo attributions, first have to convert HTML formatted text to String
            it.setOnClickListener {
                attr?.let { html ->
                    val msg = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
                    locDelegate.makeToast("Photo provided by: $msg")
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

}