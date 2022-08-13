package pl.glownia.maciej.unlockenglishword.ui.unlock

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pl.glownia.maciej.unlockenglishword.R
import pl.glownia.maciej.unlockenglishword.databinding.FragmentUnlockWordBinding
import pl.glownia.maciej.unlockenglishword.ui.UnlockWordViewModel


class UnlockWordFragment : Fragment() {

    // Property delegation
    private val viewModel: UnlockWordViewModel by viewModels()

    // Binding object instance with access to the views in the fragment_unlock_word.xml layout
    private lateinit var binding: FragmentUnlockWordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_unlock_word, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.unlockWordViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // TODO Setup a click listener for Tip and Definition buttons
        binding.btnCheck.setOnClickListener { onCheckWord() }
        binding.btnTip.setOnClickListener { onShowTip() }
        binding.btnDefinition.setOnClickListener { onShowDefinition() }
        binding.btnSkip.setOnClickListener { onSkipWord() }
    }

    private fun onShowTip() {
        viewModel.countTipClick()
        val tip = viewModel.tip.value
        binding.tvTip.text = tip
        binding.tvTip.visibility = View.VISIBLE
    }

    /**
     * Displays a definition after user clicks definition button
     */
    private fun onShowDefinition() {
        view?.rootView?.let { context?.let { it1 -> hideKeyboardFrom(it1, it) } }
        val definition = viewModel.definitionOfDrawnNumber
        binding.tvDefinition.text = definition.value
        binding.tvDefinition.visibility = View.VISIBLE
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Checks the user word and displays the next word to unlock.
     */
    private fun onCheckWord() {
        makeDefinitionInvisibleForUser()
        makeTipInvisibleForUser()
        val userWord = binding.etInputFieldForUser.text.toString()

        if (viewModel.isUserWordCorrect(userWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /**
     * Skips the current word to unlock if user wants to omit it.
     */
    private fun onSkipWord() {
        makeDefinitionInvisibleForUser()
        makeTipInvisibleForUser()
        if (viewModel.currentWordCount.value!! <= MAX_NUMBER_OF_WORDS) {
            viewModel.countSkippedWords()
        }
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            viewModel.clearClickTip()
        } else {
            showFinalDialog()
        }
    }

    /**
     * Sets and resets the text field error status.
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.tilInputFieldForUserLayout.isErrorEnabled = true
            binding.tilInputFieldForUserLayout.error = "Try again!"
        } else {
            binding.tilInputFieldForUserLayout.isErrorEnabled = false
            binding.etInputFieldForUser.text = null
        }
    }

    private fun showFinalDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Result!")
            .setMessage(
                "You accomplished all ${viewModel.listOfDisplayedWordsToUnlock.size} words!" +
                        "\n Correct: ${viewModel.wordsDoneCorrectly.value} " +
                        "\n Skipped: ${viewModel.wordsSkipped.value}"
            )
            .setCancelable(false)
            .setNegativeButton("Exit") { _, _ ->
                exitProgram()
            }
            .setPositiveButton("Start Again") { _, _ ->
                restartProgram()
            }
            .show()
    }

    /**
     * Re-initializes the data in the ViewModel and updates the views
     * with the new data to restarts the program.
     */
    private fun restartProgram() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /**
     * Exits the program.
     */
    private fun exitProgram() {
        activity?.finish()
    }

    private fun makeDefinitionInvisibleForUser() {
        binding.tvDefinition.visibility = View.INVISIBLE
    }

    private fun makeTipInvisibleForUser() {
        binding.tvTip.visibility = View.INVISIBLE
    }
}