package net.tomasfiers.zoro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.tomasfiers.zoro.zotero_api.CollectionJSON
import net.tomasfiers.zoro.zotero_api.ZoteroAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionViewModel : ViewModel() {
    val collections = MutableLiveData<String>("Loading collections..")

    init {
        ZoteroAPI.client.getSomeCollections().enqueue(
            object : Callback<List<CollectionJSON>> {
                override fun onFailure(call: Call<List<CollectionJSON>>, t: Throwable) {
                    collections.value = "Failure: ${t.message}"
                }

                override fun onResponse(
                    call: Call<List<CollectionJSON>>,
                    response: Response<List<CollectionJSON>>
                ) {
                    collections.value = response.body().toString()
                }
            }
        )
    }
}
