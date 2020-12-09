package emil.babazade.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private const val TAG = "GameViewModel"

class GameViewModel : ViewModel() {
    //    Countdown time
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    companion object {
        //        Time when the game is over
        private const val DONE = 0L

        //        Countdown time interval
        private const val ONE_SECOND = 1000L

        //        Total time for the game
        private const val COUNTDOWN_TIME = 60000L
    }

    private val timer: CountDownTimer

    init {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
                onGameFinish()
            }
        }

        timer.start()
    }

//    The String version of the current time
    val currentTimeString = Transformations.map(currentTime) { time -> DateUtils.formatElapsedTime(time) }

    private val _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() : LiveData<String> = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i(TAG, "GameViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
//        cancel the timer to avoid memory links
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (wordList.isEmpty())
            resetList()
        else //Select and remove a word from the list
            _word.value = wordList.removeAt(0)
    }

    fun onSkip() {
        _score.value = _score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = _score.value?.plus(1)
        nextWord()
    }

    fun onGameFinish() {
        _eventGameFinished.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinished.value = false
    }
}