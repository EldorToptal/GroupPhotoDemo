package com.groupphoto.app.data.repository.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.groupphoto.app.data.repository.local.entity.BackupEntity

@Database(entities = [BackupEntity::class], version = 11, exportSchema = false)
abstract class BackupRoomDatabase : RoomDatabase() {
    abstract fun backupDao(): BackupDao


    companion object {

        /**
         * This is just for singleton pattern
         */
        @Volatile
        private var INSTANCE: BackupRoomDatabase? = null

        fun getDatabase(context: Context): BackupRoomDatabase {
            if (INSTANCE == null) {
                synchronized(BackupRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BackupRoomDatabase::class.java, "backup_room_database"
                        )
                            .addMigrations(MIGRATION_10_11)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE backups_table ADD COLUMN creationDate TEXT NOT NULL DEFAULT ' '"
                )
            }
        }
    }
}