package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notesapp.databinding.FragmentNoteBinding
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private var note: NoteResponse? = null

    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        bindHandlers()
        bindObserver()
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            note?.let{
                noteViewModel.deleteNotes(it._id)
            }
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val description = binding.txtDesc.text.toString()
            val noteRequest = NoteRequest(title, description)
            if(note == null){
                noteViewModel.createNotes(noteRequest)
            } else{
                noteViewModel.updateNotes(note!!._id, noteRequest)
            }
        }
    }

    private fun bindObserver() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun setInitialData() {
        val jsonNote = arguments?.getString("note")
        if(jsonNote != null){
            //update note
            note = Gson().fromJson(jsonNote, NoteResponse::class.java)
            note?.let{
                binding.txtTitle.setText(it.title)
                binding.txtDesc.setText(it.discription)
            }
        }else{
            //add note
            binding.addEditText.text = "Add Note"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}