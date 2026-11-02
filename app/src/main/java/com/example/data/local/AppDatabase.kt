package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CalendarEventEntity::class,
        ExpenseEntity::class,
        NoteEntity::class,
        TaskEntity::class,
        ChatMessageEntity::class,
        PrayerEntity::class,
        UserEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun chatDao(): ChatDao
    abstract fun prayerDao(): PrayerDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zheen_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            try {
                                val now = System.currentTimeMillis()
                                db.execSQL(
                                    "INSERT OR IGNORE INTO users (email, name, passwordHash, isDefault) VALUES ('fahad.alqahtani@enterprise.sa', 'فهد بن محمد القحطاني', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 1)"
                                )
                                db.execSQL(
                                    "INSERT OR IGNORE INTO tasks (title, description, priority, category, isCompleted, dueDateMillis) VALUES ('مراجعة ميزانية الربع الثالث مع المستشار المالي', 'اعتماد بنود الصرف ومطابقة الفواتير مع معايير ZATCA', 'HIGH', 'FINANCE', 0, ${now + 86400000L})"
                                )
                                db.execSQL(
                                    "INSERT OR IGNORE INTO tasks (title, description, priority, category, isCompleted, dueDateMillis) VALUES ('تحديث خارطة طريق الذكاء الاصطناعي لمنتجات المؤسسة', 'اجتماع مع فريق المطورين لمراجعة نماذج Gemini والتكامل السحابي', 'HIGH', 'PROJECT', 1, ${now - 86400000L})"
                                )
                            } catch (_: Exception) {}
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
