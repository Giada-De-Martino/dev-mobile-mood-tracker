package com.example.mymoodtracker

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DailyMoodDao_Impl(
  __db: RoomDatabase,
) : DailyMoodDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDailyMood: EntityInsertAdapter<DailyMood>

  private val __deleteAdapterOfDailyMood: EntityDeleteOrUpdateAdapter<DailyMood>
  init {
    this.__db = __db
    this.__insertAdapterOfDailyMood = object : EntityInsertAdapter<DailyMood>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `DailyMood` (`date`,`value`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyMood) {
        statement.bindLong(1, entity.date)
        statement.bindLong(2, entity.value.toLong())
      }
    }
    this.__deleteAdapterOfDailyMood = object : EntityDeleteOrUpdateAdapter<DailyMood>() {
      protected override fun createQuery(): String = "DELETE FROM `DailyMood` WHERE `date` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DailyMood) {
        statement.bindLong(1, entity.date)
      }
    }
  }

  public override suspend fun insert(dailyMood: DailyMood): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDailyMood.insert(_connection, dailyMood)
  }

  public override suspend fun insertAll(dailyMoods: List<DailyMood>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDailyMood.insert(_connection, dailyMoods)
  }

  public override suspend fun delete(moodValue: DailyMood): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfDailyMood.handle(_connection, moodValue)
  }

  public override suspend fun getAll(): List<DailyMood> {
    val _sql: String = "SELECT * FROM DailyMood"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _result: MutableList<DailyMood> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyMood
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpValue: Int
          _tmpValue = _stmt.getLong(_columnIndexOfValue).toInt()
          _item = DailyMood(_tmpDate,_tmpValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun loadAllByIds(moodDate: Long): List<DailyMood> {
    val _sql: String = "SELECT * FROM DailyMood WHERE date = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, moodDate)
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _result: MutableList<DailyMood> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyMood
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpValue: Int
          _tmpValue = _stmt.getLong(_columnIndexOfValue).toInt()
          _item = DailyMood(_tmpDate,_tmpValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
