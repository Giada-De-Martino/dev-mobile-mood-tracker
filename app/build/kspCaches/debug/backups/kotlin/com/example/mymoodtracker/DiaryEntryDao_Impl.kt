package com.example.mymoodtracker

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
public class DiaryEntryDao_Impl(
  __db: RoomDatabase,
) : DiaryEntryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDiaryEntry: EntityInsertAdapter<DiaryEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfDiaryEntry = object : EntityInsertAdapter<DiaryEntry>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `DiaryEntry` (`date`,`content`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DiaryEntry) {
        statement.bindLong(1, entity.date)
        statement.bindText(2, entity.content)
      }
    }
  }

  public override suspend fun insert(entry: DiaryEntry): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDiaryEntry.insert(_connection, entry)
  }

  public override suspend fun getByDate(date: Long): DiaryEntry? {
    val _sql: String = "SELECT * FROM DiaryEntry WHERE date = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, date)
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _result: DiaryEntry?
        if (_stmt.step()) {
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          _result = DiaryEntry(_tmpDate,_tmpContent)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<DiaryEntry> {
    val _sql: String = "SELECT * FROM DiaryEntry ORDER BY date DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _result: MutableList<DiaryEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiaryEntry
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          _item = DiaryEntry(_tmpDate,_tmpContent)
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
