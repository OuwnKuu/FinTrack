package com.example.fintrack.ui.dashboard

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
import com.example.fintrack.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var dbHelper: DatabaseHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dbHelper = DatabaseHelper(requireContext())
        /*
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

         */

        val opsiData = listOf(
            "-- Pilih opsi data --",
            "Catat",
            "Edit",
            "Hapus"
        )

        val opsiInput = listOf(
            "-- Pilih Opsi Input Data --",
            "Pemasukan",
            "Pengeluaran"
        )

        val adapterData = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            opsiData
        )

        val adapterInput = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            opsiInput
        )

        adapterInput.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnOpsiInput.adapter = adapterInput

        adapterData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnOpsiData.adapter = adapterData

        binding.spnOpsiData.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (opsiData[position]) {
                    "-- Pilih opsi data --" -> {
                        binding.btnCatat.visibility = View.INVISIBLE
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnHapus.visibility = View.INVISIBLE
                        binding.etId.visibility = View.INVISIBLE
                        binding.etId.text.clear()
                    }
                    "Catat" -> {
                        binding.btnCatat.visibility = View.VISIBLE
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnHapus.visibility = View.INVISIBLE
                        binding.etId.visibility = View.INVISIBLE
                        binding.etId.text.clear()
                    }
                    "Edit" -> {
                        binding.btnCatat.visibility = View.INVISIBLE
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.btnHapus.visibility = View.INVISIBLE
                        binding.etId.visibility = View.VISIBLE
                        binding.etId.text.clear()
                    }
                    "Hapus" -> {
                        binding.btnCatat.visibility = View.INVISIBLE
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnHapus.visibility = View.VISIBLE
                        binding.etId.visibility = View.VISIBLE
                        binding.etId.text.clear()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.spnOpsiInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (opsiInput[position]) {
                    "-- Pilih Opsi Input Data --" -> {
                        binding.etPemasukan.visibility = View.INVISIBLE
                        binding.etPengeluaran.visibility = View.INVISIBLE
                        binding.etPemasukan.text.clear()
                        binding.etPengeluaran.text.clear()
                    }
                    "Pemasukan" -> {
                        binding.etPemasukan.visibility = View.VISIBLE
                        binding.etPengeluaran.visibility = View.INVISIBLE
                        binding.etPengeluaran.text.clear()
                    }
                    "Pengeluaran" -> {
                        binding.etPemasukan.visibility = View.INVISIBLE
                        binding.etPengeluaran.visibility = View.VISIBLE
                        binding.etPemasukan.text.clear()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.btnLogOut.setOnClickListener {
            (activity as MainActivity).isUserLoggedIn = false
            findNavController().navigate(R.id.navigation_login)
            Toast.makeText(requireContext(), "Berhasil Log Out", Toast.LENGTH_SHORT).show()
        }

        binding.btnCatat.setOnClickListener {
            val currentAccountId = (activity as MainActivity).currentAccountId
            val formatTanggal = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val tanggalSekarang = formatTanggal.format(Date())
            var nominalPemasukan = 0
            var nominalPengeluaran = 0
            val jenisTransaksi = binding.spnOpsiInput.selectedItem.toString()

            if (jenisTransaksi.equals("Pemasukan", ignoreCase = true)) {
                val input = binding.etPemasukan.text.toString()
                nominalPemasukan = if (input.isNotEmpty()) input.toInt() else 0
            } else {
                val input = binding.etPengeluaran.text.toString()
                nominalPengeluaran = if (input.isNotEmpty()) input.toInt() else 0
            }

            if (nominalPemasukan == 0 && nominalPengeluaran == 0) {
                Toast.makeText(requireContext(), "Masukkan nominal terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val saldoTerakhir = dbHelper.getLatestSaldo(currentAccountId)
            val saldoTerbaru = saldoTerakhir + nominalPemasukan - nominalPengeluaran
            val sukses = dbHelper.catatKeuangan(currentAccountId, tanggalSekarang, nominalPemasukan, nominalPengeluaran, saldoTerbaru)

            if (sukses) {
                binding.etPemasukan.text.clear()
                binding.etPengeluaran.text.clear()
                refreshListKeuangan(currentAccountId)
            } else {
                Toast.makeText(requireContext(), "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun refreshListKeuangan(accountId: Int) {
        val dataTerbaru = dbHelper.loadDataKeuangan(accountId)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataTerbaru)
        binding.lvKeuangan.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        val accountIdAktif = (activity as MainActivity).currentAccountId
        val dataKeuanganAccount = dbHelper.loadDataKeuangan(accountIdAktif)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataKeuanganAccount)
        binding.lvKeuangan.adapter = adapter
    }
}