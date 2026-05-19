package com.example.fintrack

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context): SQLiteOpenHelper(context, "finansial.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nama TEXT NOT NULL," +
                "password TEXT NOT NULL)")
        db?.execSQL("CREATE TABLE wallets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "account_id INT," +
                "tanggal DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "pemasukan INT," +
                "pengeluaran INT," +
                "saldo INT, " +
                "FOREIGN KEY(account_id) REFERENCES accounts(id))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS wallets")
        db?.execSQL("DROP TABLE IF EXISTS accounts")
        onCreate(db)
    }
}