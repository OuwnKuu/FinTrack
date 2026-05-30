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
                "FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE CASCADE)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        /*
        db?.execSQL("DROP TABLE IF EXISTS wallets")
        db?.execSQL("DROP TABLE IF EXISTS accounts")
        onCreate(db)

         */
    }

    fun loadDataAkun(): ArrayList<String> {
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

    fun getAccountIdByUsername(username: String): Int {
        val db = this.readableDatabase
        var userId = -1
        val cursor = db.rawQuery("SELECT id FROM accounts WHERE nama = ?", arrayOf(username))
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return userId
    }

    fun loadDataKeuangan(accountId: Int): ArrayList<String> {
        val listData = ArrayList<String>()
        val db = this.readableDatabase

        val query = "SELECT id, tanggal, pemasukan, pengeluaran, saldo FROM wallets WHERE account_id = ? ORDER BY id DESC"
        val cursor = db.rawQuery(query, arrayOf(accountId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val tanggal = cursor.getString(1)
                val pemasukan = cursor.getInt(2)
                val pengeluaran = cursor.getInt(3)
                val saldo = cursor.getInt(4)
                listData.add("ID: $id\n" +
                        "Tanggal: $tanggal\n" +
                        "Pemasukan: Rp$pemasukan\n" +
                        "Pengeluaran: Rp$pengeluaran\n" +
                        "Saldo: Rp$saldo")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listData
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

    fun getLatestSaldo(accountId: Int): Int {
        val db = this.readableDatabase
        var latestSaldo = 0

        val query = "SELECT saldo FROM wallets WHERE account_id = ? ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(accountId.toString()))

        if (cursor.moveToFirst()) {
            latestSaldo = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return latestSaldo
    }

    fun catatKeuangan(accountId: Int, tanggal: String, pemasukan: Int, pengeluaran: Int, saldo: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("account_id", accountId)
            put("tanggal", tanggal)
            put("pemasukan", pemasukan)
            put("pengeluaran", pengeluaran)
            put("saldo", saldo)
        }
        return db.insert("wallets", null, values) != -1L
    }

    fun editKeuangan(id: Int, accountId: Int, tanggal: String, pemasukan: Int, pengeluaran: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("tanggal", tanggal)
            put("pemasukan", pemasukan)
            put("pengeluaran", pengeluaran)
        }
        val updateResult = db.update("wallets", values, "id=? AND account_id=?", arrayOf(id.toString(), accountId.toString()))

        if (updateResult > 0) {
            val query = "SELECT id, pemasukan, pengeluaran FROM wallets WHERE account_id = ? ORDER BY id ASC"
            val cursor = db.rawQuery(query, arrayOf(accountId.toString()))
            var runningSaldo = 0

            if (cursor.moveToFirst()) {
                do {
                    val rowId = cursor.getInt(0)
                    val pemasukan = cursor.getInt(1)
                    val pengeluaran = cursor.getInt(2)

                    runningSaldo = runningSaldo + pemasukan - pengeluaran

                    val valuesSaldo = ContentValues().apply {
                        put("saldo", runningSaldo)
                    }
                    db.update("wallets", valuesSaldo, "id=?", arrayOf(rowId.toString()))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return true
        }
        db.close()
        return false
    }

}