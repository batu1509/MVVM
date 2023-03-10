package com.batueksi.tekrar.data.repository

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.batueksi.tekrar.data.api.ApiService
import com.batueksi.tekrar.data.models.searchmodel.Search
import com.batueksi.tekrar.helper.Constants.apikey
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class SearchPagingSource constructor(
    private val apiService: ApiService,
    private val query: String
) : RxPagingSource<Int, Search>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Search>> {
        // starting page
        val page = params.key ?: 1
        return apiService.searchFilms(query, apikey, page)
            .subscribeOn(Schedulers.io())
            .map {
                LoadResult.Page(
                    data = it.results,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == it.total_pages.toInt() || it.total_pages.toInt()==0) null else page + 1
                ) as LoadResult<Int, Search>
            }
            .onErrorReturn {
                LoadResult.Error(it)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
        return state.anchorPosition
    }
}