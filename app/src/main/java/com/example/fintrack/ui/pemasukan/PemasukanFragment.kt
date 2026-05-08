package com.example.fintrack.ui.pemasukan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fintrack.databinding.FragmentPemasukanBinding

class PemasukanFragment : Fragment() {

    private var _binding: FragmentPemasukanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pemasukanViewModel =
            ViewModelProvider(this)[PemasukanViewModel::class.java]

        _binding = FragmentPemasukanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPemasukan
        pemasukanViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}