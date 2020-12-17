package org.simple.clinic.storage.paging

import androidx.paging.PositionalDataSource

class ItemTransformingDataSource<T, I>(
    private val source: PositionalDataSource<T>,
    private val transformer: ItemTransformer<T, I>
) : PositionalDataSource<I>() {

  private var additionalItemsAdded = 0

  override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
    additionalItemsAdded = 0

    val loadParamsForDatabaseSource = LoadInitialParams(
        params.requestedStartPosition,
        params.requestedLoadSize,
        params.pageSize,
        params.placeholdersEnabled
    )

    source.loadInitial(
        loadParamsForDatabaseSource,
        object : LoadInitialCallback<T>() {
          override fun onResult(data: MutableList<T>, position: Int, totalCount: Int) {
            val items = transformItems(loadParamsForDatabaseSource.requestedStartPosition, data)

            additionalItemsAdded += items.size - data.size

            callback.onResult(items, position)
          }

          override fun onResult(data: MutableList<T>, position: Int) {
            // Nothing happens here, source data source results are passed to onResult(data, position, totalCount)
          }
        }
    )
  }

  override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) {
    val loadParamsForDatabaseSource = LoadRangeParams(
        params.startPosition - additionalItemsAdded,
        params.loadSize
    )

    source.loadRange(loadParamsForDatabaseSource, object : LoadRangeCallback<T>() {
      override fun onResult(data: MutableList<T>) {
        val items = transformItems(loadParamsForDatabaseSource.startPosition, data)

        additionalItemsAdded += items.size - data.size

        callback.onResult(items)
      }
    })
  }

  private fun transformItems(
      startPosition: Int,
      data: List<T>
  ): List<I> {
    return data
        .mapIndexed { index, item -> index to item }
        .windowed(size = 2, step = 1, partialWindows = true)
        .map { items ->
          transformer.transform(
              itemPosition = startPosition + items.first().first,
              item = items.first().second,
              next = items.lastOrNull()?.second
          )
        }
        .flatten()
  }

  interface ItemTransformer<T, I> {
    fun transform(itemPosition: Int, item: T, next: T?): List<I>
  }
}
