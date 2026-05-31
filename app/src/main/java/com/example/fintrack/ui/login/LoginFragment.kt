package com.example.fintrack.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fintrack.DatabaseHelper
import com.example.fintrack.MainActivity
import com.example.fintrack.R
import com.example.fintrack.databinding.FragmentLoginBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var accounts: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val loginViewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dbHelper = DatabaseHelper(requireContext())
        /*
        val textView: TextView = binding.textLogin
        loginViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

         */

        accounts = dbHelper.loadDaftarAkun()
        accounts.add(0, "-- Pilih Akun --")
        accounts.add(1, "Buat Akun Baru")
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            accounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnAkun.adapter = adapter

        binding.spnAkun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (accounts[position]) {
                    "-- Pilih Akun --" -> {
                        binding.etVirtualAcc.visibility = View.INVISIBLE
                        binding.etPassword.visibility = View.INVISIBLE
                        binding.btnMasuk.visibility = View.INVISIBLE
                        binding.btnDaftar.visibility = View.INVISIBLE
                        binding.tvSyaratDaftar.visibility = View.INVISIBLE
                        binding.etVirtualAcc.text.clear()
                        binding.etPassword.text.clear()
                    }
                    "Buat Akun Baru" -> {
                        binding.etVirtualAcc.visibility = View.VISIBLE
                        binding.etPassword.visibility = View.VISIBLE
                        binding.btnMasuk.visibility = View.INVISIBLE
                        binding.btnDaftar.visibility = View.VISIBLE
                        binding.tvSyaratDaftar.visibility = View.VISIBLE
                        binding.etPassword.text.clear()
                    }
                    else -> {
                        binding.etVirtualAcc.visibility = View.INVISIBLE
                        binding.etPassword.visibility = View.VISIBLE
                        binding.btnMasuk.visibility = View.VISIBLE
                        binding.btnDaftar.visibility = View.INVISIBLE
                        binding.tvSyaratDaftar.visibility = View.INVISIBLE
                        binding.etVirtualAcc.text.clear()
                        binding.etPassword.text.clear()
                    }
                }
                binding.btnMasuk.setOnClickListener {
                    val akunTerpilih = binding.spnAkun.selectedItem.toString()
                    val inputPasswd = binding.etPassword.text.toString()
                    val passwdAsli = dbHelper.getPasswordByAkun(akunTerpilih)
                    if (inputPasswd == passwdAsli) {
                        Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                        val accountId = dbHelper.getAccountIdByUsername(akunTerpilih)
                        (activity as MainActivity).isUserLoggedIn = true
                        (activity as MainActivity).currentAccountId = accountId
                        findNavController().navigate(R.id.navigation_dashboard)
                        binding.etVirtualAcc.text.clear()
                        binding.etPassword.text.clear()
                    } else {
                        Toast.makeText(requireContext(), "Password salah/tidak dimasukkan!", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.btnDaftar.setOnClickListener {
                    val namaAkun = binding.etVirtualAcc.text.toString().trim()
                    val passwdAkun = binding.etPassword.text.toString().trim()
                    val tanggalSekarang = SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault()).format(
                        Date()
                    )
                    if (!dbHelper.cekDuplikasiAkun(namaAkun) && namaAkun.isNotBlank() && passwdAkun.length < 3) {
                        dbHelper.buatAkun(namaAkun, passwdAkun, tanggalSekarang)
                        refreshSpinner()
                        Toast.makeText(requireContext(), "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Akun sudah ada/tidak memenuhi syarat", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Pilih akun terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
            fun refreshSpinner() {
                val dataTerbaru = dbHelper.loadDaftarAkun()
                accounts.clear()
                accounts.add(0, "-- Pilih Akun --")
                accounts.add(1, "Buat Akun Baru")
                accounts.addAll(dataTerbaru)
                adapter.notifyDataSetChanged()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}