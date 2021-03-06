package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dataclasses.Player
import com.example.myapplication.gaming.CardMissionConsequence
import com.example.myapplication.gaming.CardTimedTask
import com.example.myapplication.gaming.GeneratorMissionOrConsequence
import com.example.myapplication.gaming.GeneratorTimedTask
import com.example.myapplication.localappdatabase.CardTimedTaskRepository
import com.example.myapplication.localappdatabase.LocalAppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class SharedViewModel(application: Application) : AndroidViewModel(application), ISharedViewModel {

    private val context = getApplication<Application>().applicationContext

    override var listOfPlayers = mutableListOf<Player>()
    override var lopSortedByPoints = mutableListOf<Player>()
    override var listOfRandomTimedTaskTurns =
        mutableListOf<Int>()
    override var listOfMissionOrConsequenceTurns =
        mutableListOf<Pair<Int, CardMissionConsequence>>()
    private val evenTurns: Int get() = pCount.plus(1)
    override val amRounds: Int get() = amountOfRounds.value!!
    override val pCount: Int get() = playerCount.value!!
    override val totalTurnsPlus: Int get() = amRounds.times(pCount).plus(amRounds)


    override var currentFragmentPos = MutableLiveData<Int>().apply {
        value = 0
    }

    override var playerCount = MutableLiveData<Int>().apply {
        value = 0
    }

    override var amountOfRounds = MutableLiveData<Int>().apply {
        value = 4
    }

    override fun updateFragmentPos() {
        currentFragmentPos.increaseBy(1)
    }

    override fun setPlayerCount(i: Int) = viewModelScope.launch {
        playerCount.putValue(i)
    }

    override fun addPlayerToList(player: Player) = viewModelScope.launch(Dispatchers.IO) {
        listOfPlayers.add(player)
    }

    override fun listSumPairSort() = viewModelScope.launch(Dispatchers.IO) {
        lopSortedByPoints = listOfPlayers
            .sortedByDescending { player -> player.sumPointsFromListCards() }
            .toMutableList()
    }


    override fun createListOfMissionConsequence() = viewModelScope.launch {

        val list: MutableList<Pair<Int, CardMissionConsequence>> = mutableListOf()
        for (i in ((1..totalTurnsPlus).filter { (it % evenTurns) != 0 })) {
            list.add(Pair(i, GeneratorMissionOrConsequence(context).generateNewCard()))
        }
        listOfMissionOrConsequenceTurns.addAll(list)
    }

    override fun getListCard(currentTurn: Int): CardMissionConsequence {
        //Log.d("!", "getListCard() $currentTurn")
        return listOfMissionOrConsequenceTurns.find { it.first == currentTurn }!!.second
    }

    override fun createRandomTaskList() = viewModelScope.launch {

        var ran: Int
        val random = Random
        val listTasksTurns: MutableList<Int> = mutableListOf()
        val listOfExcludeValues: MutableList<Int> = mutableListOf()

        listOfExcludeValues.addExcludeEvenTurns(totalTurnsPlus, evenTurns)

        do {
            ran = random.nextInt(evenTurns, totalTurnsPlus)
            val ranExcludeInterval = 1.createTaskExcludeTurns(ran)

            if (!listOfExcludeValues.contains(ran)) {
                listTasksTurns.add(ran)
                listOfExcludeValues.addAll(ranExcludeInterval)
            }

        } while (listTasksTurns.count() != pCount)

        listOfRandomTimedTaskTurns.addAll(listTasksTurns)
    }

    private val db = LocalAppDataBase.getDatabase(context)
    private val timedTaskDao = db.timedTaskDao()
    private val repository: CardTimedTaskRepository get() = CardTimedTaskRepository(timedTaskDao)

    fun addTasksToDatabase() = GlobalScope.launch {

        val generatorTimedTask = GeneratorTimedTask(context)

        db.clearAllTables()

        for (i in 1..generatorTimedTask.listOfListTasks.count()
            .times(generatorTimedTask.arrayOfColors.count())) {
            CardTimedTask(
                uid = null,
                task = generatorTimedTask.randomListTask(),
                consequence = generatorTimedTask.randomListCon(),
                15L)

                .apply {
                    cardTimeTaskInsert(this)
                }

            // repository.insertEntity(ctt)
        }

    }

    private fun cardTimeTaskInsert(cardTimedTask: CardTimedTask) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEntity(cardTimedTask)
        }

    fun cardTimeTaskDelete(cardTimedTask: CardTimedTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteEntity(cardTimedTask)
    }

    private fun Int.createTaskExcludeTurns(excludeRef: Int): MutableList<Int> {
        return (excludeRef.minus(this)..excludeRef.plus(this)).toMutableList()
    }

    private fun MutableList<Int>.addExcludeEvenTurns(totalTurns: Int, evenTurns: Int) {
        (1..totalTurns).filter { (it % evenTurns) == 0 }.run { addAll(this) }
    }

}

private fun MutableLiveData<Int>.increaseBy(i: Int) {
    this.postValue(this.value?.plus(i))
}
private fun MutableLiveData<Int>.putValue(i: Int) {
    this.value = i
}



