package pl.glownia.maciej.unlockenglishword.ui.unlock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding = FragmentUnlockWordBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: UnlockWordFragment created/re-created.")
        Log.d(
            TAG,
            "Word: ${viewModel.currentWordToUnlock} " +
                    "WordsDoneCorrectly: ${viewModel.wordsDoneCorrectly} "
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO Setup a click listener for Tip and Definition buttons
        binding.btnCheck.setOnClickListener { onCheckWord() }
        binding.btnTip.setOnClickListener { }
        binding.btnDefinition.setOnClickListener { }
        binding.btnSkip.setOnClickListener { onSkipWord() }

        // Set UI
        updateNextWordOnScreen()
        binding.tvWordsDoneCorrectly.text = getString(R.string.words_done_correctly, 0)
        binding.tvWordsSkipped.text = getString(R.string.words_skipped, 0)
    }

    /**
     * Displays the next word to unlock on the screen.
     */
    private fun updateNextWordOnScreen() {
        binding.tvWordToUnlock.text = viewModel.currentWordToUnlock
    }

    /**
     * Checks the user word and displays the next word to unlock.
     */
    private fun onCheckWord() {
        val userWord = binding.etInputFieldForUser.text.toString()

        if (viewModel.isUserWordCorrect(userWord)) {
            setErrorTextField(false)
            updateNextWordOnScreen()
            if (viewModel.nextWord()) {
                updateNextWordOnScreen()
            } else {
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
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            updateNextWordOnScreen()
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
            .setTitle("Incredible!")
            .setMessage("You accomplished all ${viewModel.listOfDisplayedWordsToUnlock.size} words!")
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
        updateNextWordOnScreen()
    }

    /**
     * Exits the program.
     */
    private fun exitProgram() {
        activity?.finish()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: UnlockWordFragment destroyed.")
    }

    companion object {
        const val TAG = "UnlockWordFragment"
    }
}