package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.databinding.FragmentMainBinding
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.utils.Constants.TAG
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.utils.NetworkUtils
import com.example.notesapp.utils.TokenManager
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    private val noteViewModel by viewModels<NoteViewModel>()

    private lateinit var adapter: NoteAdapter

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        //pass function as parameter
        adapter = NoteAdapter(::onNoteClick, ::shareNote)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.getNotes()
        binding.notesRV.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.notesRV.adapter = adapter

        if (networkUtils.isInternetAvailable(requireContext())) {
            binding.addNote.isVisible = true
            binding.addNote.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
            }
        } else {
            binding.addNote.isVisible = false
        }
        binding.SignOut.setOnClickListener {
            tokenManager.clearCredentials()
            Toast.makeText(requireContext(), "You're successfully signed out!!", Toast.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()
            requireActivity().finish()
        }

        bindObserver()
    }

    private fun bindObserver() {
        noteViewModel.noteLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    adapter.submitList(it.data)
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

    }

    private fun onNoteClick(noteResponse: NoteResponse) {
        //pass note response that is note with title and description in noteFragment from mainFragment
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        if (networkUtils.isInternetAvailable(requireContext())) {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment, bundle)
        }
    }

    private fun shareNote(noteResponse: NoteResponse) {
        Log.d(TAG, "btn clicked")
        Log.d(TAG, noteResponse.title)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,  "Note Title is ${noteResponse.title} and Description is ${noteResponse.discription}")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}