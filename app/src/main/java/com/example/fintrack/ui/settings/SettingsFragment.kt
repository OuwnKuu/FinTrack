package com.example.fintrack.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fintrack.DatabaseHelper
import com.example.fintrack.MainActivity
import com.example.fintrack.R
import com.example.fintrack.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var dbHelper: DatabaseHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

         */
        /*
        val currentAccountId = (activity as MainActivity).currentAccountId.toString()
        binding.tvAccountId.text = (currentAccountId)

         */

        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                binding.cbGantiNamaAkun.id -> if (isChecked) {
                    binding.etGantiNamaAkun.visibility = View.VISIBLE
                    binding.btnGanti.visibility = View.VISIBLE
                    binding.etGantiNamaAkun.text.clear()
                } else {
                    binding.etGantiNamaAkun.visibility = View.INVISIBLE
                }
                binding.cbGantiPasswd.id -> if (isChecked) {
                    binding.etGantiPasswd.visibility = View.VISIBLE
                    binding.btnGanti.visibility = View.VISIBLE
                    binding.etGantiPasswd.text.clear()
                } else {
                    binding.etGantiPasswd.visibility = View.INVISIBLE
                }
                binding.cbGantiNamaAkun.id, binding.cbGantiPasswd.id -> if (isChecked) {
                    binding.btnGanti.visibility = View.INVISIBLE
                    binding.etGantiNamaAkun.text.clear()
                    binding.etGantiPasswd.text.clear()
                }
            }
        }

        binding.cbGantiNamaAkun.setOnCheckedChangeListener(listener)
        binding.cbGantiPasswd.setOnCheckedChangeListener(listener)

        binding.btnLogOut.setOnClickListener {
            (activity as MainActivity).isUserLoggedIn = false
            (activity as MainActivity).currentAccountId = -1
            findNavController().navigate(R.id.navigation_login)
            Toast.makeText(requireContext(), "Berhasil Log Out", Toast.LENGTH_SHORT).show()
        }

        binding.btnHapusAkun.setOnClickListener {
            val id = (activity as MainActivity).currentAccountId
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Hapus Akun")
            builder.setMessage("Apakah anda yakin? Ini akan menghapus SEMUA data yang ada di akun ini")

            builder.setPositiveButton("Hapus sekarang") { _, _ ->
                dbHelper.hapusAkun(id)
                (activity as MainActivity).isUserLoggedIn = false
                (activity as MainActivity).currentAccountId = -1
                Toast.makeText(requireContext(), "Akun berhasil terhapus", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.navigation_login)
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        val accountIdAktif = (activity as MainActivity).currentAccountId
        val dataAccount = dbHelper.loadDataAkun(accountIdAktif)

        if (dataAccount != null) {
            val id = dataAccount.getInt("id")
            val namaAkun = dataAccount.getString("namaAkun") ?: ""
            val password = dataAccount.getString("password") ?: ""
            val tanggalPembuatan = dataAccount.getString("tanggalPembuatan")

            val maskPassword = "*".repeat(password.length)

            val listInfoAkun = arrayListOf<String>()
            listInfoAkun.add("ID Akun: $id")
            listInfoAkun.add("Nama Akun: $namaAkun")
            listInfoAkun.add("Password Akun: $maskPassword")
            listInfoAkun.add("Terdaftar: $tanggalPembuatan")

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listInfoAkun
            )

            binding.lvInformasiUmum.adapter = adapter
        }
    }
}