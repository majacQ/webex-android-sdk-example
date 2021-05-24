package com.ciscowebex.androidsdk.kitchensink.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ciscowebex.androidsdk.kitchensink.BaseViewModel
import com.ciscowebex.androidsdk.kitchensink.WebexRepository
import com.ciscowebex.androidsdk.kitchensink.messaging.spaces.SpaceModel
import com.ciscowebex.androidsdk.kitchensink.messaging.spaces.SpacesRepository
import com.ciscowebex.androidsdk.space.Space
import io.reactivex.android.schedulers.AndroidSchedulers

class SearchViewModel(private val searchRepo: SearchRepository, private val spacesRepo: SpacesRepository, private val webexRepo: WebexRepository) : BaseViewModel() {
    private val tag = "SearchViewModel"
    private val _spaces = MutableLiveData<List<SpaceModel>>()
    val spaces: LiveData<List<SpaceModel>> = _spaces

    private val _searchResult = MutableLiveData<List<Space>>()
    val searchResult: LiveData<List<Space>> = _searchResult

    private val _spaceEventLiveData = MutableLiveData<Pair<WebexRepository.SpaceEvent, Any?>>()

    val titles =
            listOf("Call", "Search", "History", "Spaces")

    var name = listOf(
            "Bharath Balan",
            "Adam Ranganathan",
            "Rohit Sharma",
            "Manoj Nuthakki",
            "Linda Nixon",
            "Akshay Agarwal",
            "Lalit Sharma",
            "Manu Jain",
            "Ankit Batra",
            "Jasna Ibrahim"
    )

    init {
        webexRepo._spaceEventLiveData = _spaceEventLiveData
    }

    fun getSpaceEvent() = webexRepo._spaceEventLiveData

    fun isSpaceCallStarted() = webexRepo.isSpaceCallStarted
    fun spaceCallId() = webexRepo.spaceCallId

    fun loadData(taskType: String, maxSpaceCount: Int) {
        when (taskType) {
            SearchCommonFragment.Companion.TaskType.TaskCallHistory -> {
                searchRepo.getCallHistory().observeOn(AndroidSchedulers.mainThread()).subscribe({
                    Log.d(tag, "Size of $taskType is ${it?.size?.or(0)}")
                    _spaces.postValue(it)
                }, {
                    _spaces.postValue(emptyList())
                }).autoDispose()
            }
            SearchCommonFragment.Companion.TaskType.TaskSearchSpace -> {
                search("")
            }
            SearchCommonFragment.Companion.TaskType.TaskListSpaces -> {
                spacesRepo.fetchSpacesList(null, maxSpaceCount).observeOn(AndroidSchedulers.mainThread()).subscribe({ spacesList ->
                    _spaces.postValue(spacesList)
                }, { _spaces.postValue(emptyList()) }).autoDispose()
            }
        }
    }

    fun search(query: String?) {
        query?.let { searchQuery ->
            searchRepo.search(searchQuery).observeOn(AndroidSchedulers.mainThread()).subscribe({
                _searchResult.postValue(it)
            }, {
                _searchResult.postValue(emptyList())
            }).autoDispose()
        }
    }
}