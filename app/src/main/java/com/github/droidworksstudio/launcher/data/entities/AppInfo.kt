package com.github.droidworksstudio.launcher.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app")
data class AppInfo(
    @PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int = 0,

    @field:ColumnInfo(name = "app_name")
    var appName: String,

    @field:ColumnInfo(name = "package_name")
    var packageName: String,

    @field:ColumnInfo(name = "is_favorite")
    var favorite: Boolean,

    @field:ColumnInfo(name = "is_hidden")
    var hidden: Boolean,

    @ColumnInfo(name = "is_lock")
    var lock: Boolean,

    @ColumnInfo(name = "is_work")
    var work: Boolean,

    @ColumnInfo(name = "create_time")
    var createTime: String,

    @ColumnInfo(name = "app_order")
    var appOrder: Int = -1
)