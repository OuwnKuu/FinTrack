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
import com.example.fintrack.DatabaseHelper
import com.example.fintrack.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var dbHelper: DatabaseHelper

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

        val accounts = dbHelper.loadData()
        accounts.add(0, "-- Pilih Akun --")
        accounts.add(1, "Buat Akun Baru")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            accounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnAkun.adapter = adapter

        binding.spnAkun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (val selectedAccount = accounts[position]) {
                    "-- Pilih Akun --" -> {
                        Toast.makeText(requireContext(), "Silahkan pilih opsinya ya", Toast.LENGTH_SHORT).show()
                        binding.etVirtualAcc.visibility = View.INVISIBLE
                    }
                    "Buat Akun Baru" -> {
                        binding.etVirtualAcc.visibility = View.VISIBLE
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Akun: $selectedAccount", Toast.LENGTH_SHORT).show()
                        binding.etVirtualAcc.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Pilih akun terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}