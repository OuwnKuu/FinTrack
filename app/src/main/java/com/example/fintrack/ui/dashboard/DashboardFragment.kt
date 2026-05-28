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
                    }
                    "Catat" -> {
                        binding.btnCatat.visibility = View.VISIBLE
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnHapus.visibility = View.INVISIBLE
                        binding.etId.visibility = View.INVISIBLE
                    }
                    "Edit" -> {
                        binding.btnCatat.visibility = View.INVISIBLE
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.btnHapus.visibility = View.INVISIBLE
                        binding.etId.visibility = View.VISIBLE
                    }
                    "Hapus" -> {
                        binding.btnCatat.visibility = View.INVISIBLE
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnHapus.visibility = View.VISIBLE
                        binding.etId.visibility = View.VISIBLE
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
                    }
                    "Pemasukan" -> {
                        binding.etPemasukan.visibility = View.VISIBLE
                        binding.etPengeluaran.visibility = View.INVISIBLE
                    }
                    "Pengeluaran" -> {
                        binding.etPemasukan.visibility = View.INVISIBLE
                        binding.etPengeluaran.visibility = View.VISIBLE
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}