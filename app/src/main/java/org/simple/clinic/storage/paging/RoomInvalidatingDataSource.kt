package org.simple.clinic.storage.paging

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import androidx.paging.toObservable
import androidx.room.InvalidationTracker
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.simple.clinic.AppDatabase

class RoomInvalidatingDataSource<T>(
    private val appDatabase: AppDatabase,
    private val source: PositionalDataSource<T>,
    invalidationTables: Set<String>
) : PositionalDataSource<T>(), Disposable {

  private val invalidationTracker = object : InvalidationTracker.Observer(invalidationTables.toTypedArray()) {
    override fun onInvalidated(tables: MutableSet<String>) {
      invalidate()
    }
  }

  init {
    appDatabase.invalidationTracker.addObserver(invalidationTracker)
  }

  private var disposed: Boolean = false

  override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
    source.loadInitial(params, callback)
  }

  override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
    source.loadRange(params, callback)
  }

  override fun dispose() {
    appDatabase.invalidationTracker.removeObserver(invalidationTracker)
    disposed = true
  }

  override fun isDisposed() = disposed

  class Factory<T>(
      private val appDatabase: AppDatabase,
      private val factory: DataSource.Factory<Int, T>,
      private val invalidationTables: Set<String>
  ) : DataSource.Factory<Int, T>() {

    private val disposable = CompositeDisposable()
    private var dataSource: RoomInvalidatingDataSource<T>? = null

    fun asObservable(
        config: PagedList.Config,
        detaches: Observable<Unit>
    ): Observable<PagedList<T>> {
      disposable.add(
          detaches.subscribe {
            dataSource?.dispose()
            dataSource = null
            disposable.clear()
          }
      )

      return toObservable(config)
    }

    override fun create(): DataSource<Int, T> {
      val originalSource = factory.create() as PositionalDataSource<T>
      dataSource = RoomInvalidatingDataSource(appDatabase, originalSource, invalidationTables)
      return dataSource!!
    }
  }
}
