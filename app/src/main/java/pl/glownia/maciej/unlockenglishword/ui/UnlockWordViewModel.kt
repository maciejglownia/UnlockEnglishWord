package pl.glownia.maciej.unlockenglishword.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.glownia.maciej.unlockenglishword.ui.unlock.MAX_NUMBER_OF_WORDS
import pl.glownia.maciej.unlockenglishword.ui.unlock.allWordsAndItsDefinitionList

class UnlockWordViewModel : ViewModel() {

    private var _wordsSkipped = MutableLiveData(0)
    val wordsSkipped: LiveData<Int>
        get() = _wordsSkipped

    private var _wordsDoneCorrectly = MutableLiveData(0)
    val wordsDoneCorrectly: LiveData<Int>
        get() = _wordsDoneCorrectly

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentWordToUnlock = MutableLiveData<String>()
    val currentWordToUnlock: LiveData<String>
        get() = _currentWordToUnlock

    // This is needed to keep words to unlock which have been already displayed to user
    private var _listOfDisplayedWordsToUnlock: MutableList<String> = mutableListOf()
    val listOfDisplayedWordsToUnlock: List<String>
        get() = _listOfDisplayedWordsToUnlock
    private lateinit var currentWord: String

    private var _definitionOfDrawnNumber = MutableLiveData<String>()
    val definitionOfDrawnNumber: LiveData<String>
        get() = _definitionOfDrawnNumber

    init {
        getNextWord()
    }

    /**
     * Gets random word from map. Shuffle it.
     * Also check protects from shuffle to same state.
     * Assigns currentWord to currentWordToUnlock, counts words already displayed and adds
     * displayed word to the list, to protect from display it again.
     */
    private fun getNextWord() {
        currentWord = allWordsAndItsDefinitionList.keys.random()
        _definitionOfDrawnNumber.value = allWordsAndItsDefinitionList[currentWord].toString()
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
            _currentWordToUnlock.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            _listOfDisplayedWordsToUnlock.add(currentWord)
        }
    }

    /**
     * Gets next random word to display to user and return true if it has been displayed
     */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NUMBER_OF_WORDS) {
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
            countWordsDoneCorrectly()
            return true
        }
        return false
    }

    /**
     * Re-initializes unlock word data to restart the program.
     */
    fun reinitializeData() {
        _wordsDoneCorrectly.value = 0
        _currentWordCount.value = 0
        _wordsSkipped.value = 0
        _listOfDisplayedWordsToUnlock.clear()
        getNextWord()
    }

    /**
     * Counts wordse when user decides to skip it.
     */
    fun countSkippedWords() {
        _wordsSkipped.value = (_wordsSkipped.value)?.inc()
    }

    /**
     * Counts words when user guess it.
     */
    private fun countWordsDoneCorrectly() {
        _wordsDoneCorrectly.value = (_wordsDoneCorrectly.value)?.inc()
    }
}