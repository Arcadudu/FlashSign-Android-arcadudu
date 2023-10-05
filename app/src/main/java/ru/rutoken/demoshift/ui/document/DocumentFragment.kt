/*
 * Copyright (c) 2020, Aktiv-Soft JSC. See https://download.rutoken.ru/License_Agreement.pdf.
 * All Rights Reserved.
 */

package ru.rutoken.demoshift.ui.document

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.rutoken.demoshift.R
import ru.rutoken.demoshift.databinding.FragmentDocumentBinding
import ru.rutoken.demoshift.ui.document.DocumentFragmentDirections.toSignFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.DIALOG_RESULT_KEY
import ru.rutoken.demoshift.ui.pin.PinDialogFragment.Companion.PIN_KEY
import timber.log.Timber

class DocumentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDocumentBinding.inflate(inflater)
        val args: DocumentFragmentArgs by navArgs()

        val viewModel = getViewModel<DocumentViewModel>()

        val getContentLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
            uri?.let { viewModel.documentUri.value = it }
        }

        // select button
        binding.selectButton.setOnClickListener {
            getContentLauncher.launch("*/*")
        }

        viewModel.documentUri.observe(viewLifecycleOwner) { uri ->

            val documentType = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(requireContext().contentResolver.getType(uri))

            val fileIsPDF = documentType == "pdf"
            val fileIsImage = documentType == "png" || documentType == "jpg"


            with(binding) {
                // pdf viewer
                documentPdfView.apply {
                    isVisible = fileIsPDF
                    if (fileIsPDF)
                        fromUri(uri).scrollHandle(DefaultScrollHandle(requireContext())).load()
                }
                // image viewer
                imageDocumentViewer.apply {
                    isVisible = fileIsImage
                    if (fileIsImage)
                        Glide.with(requireContext()).load(uri).into(this)
                }

                // sign button
                signButton.apply {
                    isVisible = true
                    setOnClickListener {
                        PinDialogFragment().show(childFragmentManager, null)
                    }
                }
                // disclaimer
                unsupportedFileSelected.apply {
                    isVisible = !fileIsPDF && !fileIsImage
                    text = getString(R.string.can_not_display_file)
                }

            }

        }

        childFragmentManager.setFragmentResultListener(DIALOG_RESULT_KEY, this) { _, bundle ->
            findNavController().navigate(
                toSignFragment(
                    checkNotNull(bundle.getString(PIN_KEY)),
                    args.userId,
                    viewModel.documentUri.value!!
                )
            )
        }

        return binding.root
    }
}