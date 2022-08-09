package pl.glownia.maciej.unlockenglishword.ui.unlock

import android.util.Log
import androidx.lifecycle.ViewModel
import pl.glownia.maciej.unlockenglishword.ui.unlock.UnlockWordFragment.Companion.TAG

class UnlockWordViewModel : ViewModel() {

    private lateinit var _currentWordToUnlock: String
    val currentWordToUnlock: String
        get() = _currentWordToUnlock

    // This is needed to keep words to unlock which have been already displayed to user
    private var _listOfDisplayedWordsToUnlock: MutableList<String> = mutableListOf()
    val listOfDisplayedWordsToUnlock: List<String>
        get() = _listOfDisplayedWordsToUnlock
    private lateinit var currentWord: String

    init {
        Log.d("UnlockWordFragment", "UnlockWordViewModel created.")
        getNextWord()
    }

    private fun getRandomNumber(): Int {
        return (0 until allWordsAndItsDefinitionList.size).random()
    }

    private fun getNextWord() {
//        currentWord = mapOfWords.entries.elementAt(random.nextInt(mapOfWords.size)).toString()
        val entries = allWordsAndItsDefinitionList.entries.elementAt(getRandomNumber())
        currentWord = entries.key
        val tempWordKeyNeededInCaseOfFindItsValueToDisplayDefinition = currentWord
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // Continue the loop until the word to unlock is not the same as the original one
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        // Check if a word has been used already
        if (_listOfDisplayedWordsToUnlock.contains(currentWord)) {
            getNextWord()
        } else {
            _currentWordToUnlock = String(tempWord)
            _listOfDisplayedWordsToUnlock.add(currentWord)
        }
    }

    /**
     * Gets next random word to display to user and return true if it has been displayed
     */
    fun nextWord(): Boolean {
        return if (_listOfDisplayedWordsToUnlock.size != allWordsAndItsDefinitionList.size) {
            getNextWord()
            true
        } else
            false
    }

    /**
     * Validate the user's word.
     */
    fun isUserWordCorrect(userWord: String): Boolean {
        if (userWord.equals(currentWord, true)) {
            return true
        }
        return false
    }

    /**
     * Re-initializes unlock word data to restart the program.
     */
    fun reinitializeData() {
        _listOfDisplayedWordsToUnlock.clear()
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared(): UnlockViewModel destroyed.")
    }
}