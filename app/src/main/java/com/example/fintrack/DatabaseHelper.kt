package com.example.fintrack

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context): SQLiteOpenHelper(context, "finansial.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nama TEXT UNIQUE NOT NULL," +
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

    fun loadData(): ArrayList<String> {
        val listAkun = ArrayList<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nama FROM accounts", null)

        if (cursor.moveToFirst()) {
            do {
                val nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
                listAkun.add(nama)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listAkun
    }

    fun buatAkun(nama: String, password: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nama", nama)
            put("password", password)
        }
        db.insert("accounts", null, values)
    }

    fun getPasswordByAkun(namaAkun: String): String? {
        val db = this.readableDatabase
        var password: String? = null

        val query = "SELECT password FROM accounts WHERE nama = ?"
        val cursor = db.rawQuery(query, arrayOf(namaAkun))

        if (cursor.moveToFirst()) {
            password = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return password
    }

    fun cekDuplikasiAkun(namaAkun: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT 1 FROM accounts WHERE nama = ?"
        val cursor = db.rawQuery(query, arrayOf(namaAkun))
        val isExist = cursor.count > 0

        cursor.close()
        db.close()
        return isExist
    }

}