package uniba.jp.coroutinestest01

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import timber.log.Timber


class MainActivityViewModel : ViewModel(), LifecycleObserver {

    private val viewModelJob = Job()

    // GlobalScope.launchは呼び出したときからアプリが終了するまで保持される(生存期間が長すぎる)ため使わない方が良い？
    // https://qiita.com/superman9387/items/2891f1ef0299e4a30eee

    private val scopeIo = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val scopeMain = CoroutineScope(Dispatchers.Main + viewModelJob)

    val text1 = MutableLiveData<String>()
    val text2 = MutableLiveData<String>()
    val text3 = MutableLiveData<String>()
    val text4 = MutableLiveData<String>()
    val text5 = MutableLiveData<String>()

    val text6 = MutableLiveData<Int>().apply { postValue(0) }

    private fun heavyTask() {
        Thread.sleep(1000)
    }

    fun onClick1() {
        Timber.d("onClick START")
        // IOスレッドで実行
        scopeIo.launch {

            text1.postValue("LAUNCH")

            heavyTask()
            text2.postValue("LAUNCH")

            heavyTask()
            text3.postValue("LAUNCH")

            heavyTask()
            text4.postValue("LAUNCH")

            heavyTask()
            text5.postValue("LAUNCH")

        }
        Timber.d("onClick END")
    }

    fun onClick2() {
        // Main(UI)スレッドで実行
        scopeMain.launch {

            text1.postValue("LAUNCH")

            heavyTask()
            text2.postValue("LAUNCH")

            heavyTask()
            text3.postValue("LAUNCH")

            heavyTask()
            text4.postValue("LAUNCH")

            heavyTask()
            text5.postValue("LAUNCH")

        }
    }

    fun onClick3() {
        text1.postValue("ABORT")
        text2.postValue("ABORT")
        text3.postValue("ABORT")
        text4.postValue("ABORT")
        text5.postValue("ABORT")
    }

    fun onClick4() {
        text6.postValue(text6.value?.plus(1))
    }

    private fun task1(): Int {
        Timber.d("task 1 START")
        Thread.sleep(2000)
        Timber.d("task 1 END")
        return 2
    }

    private fun task2(): Int {
        Timber.d("task 2 START")
        Thread.sleep(3000)
        Timber.d("task 2 END")
        return 10
    }

    fun onClick5() {
        Timber.d("onClick START")

        CoroutineScope(Dispatchers.Main).launch {
            Timber.d("Co-routine START")

            // task1()とtask2()の並列処理開始
            val a = async(Dispatchers.IO) { task1() }
            val b = async(Dispatchers.IO) { task2() }

            // task1()とtask2()の両方が終わるまで待機
            Timber.d("Result: %d", a.await() * b.await())
            Timber.d("Co-routine END")
        }

        Timber.d("onClick END")
    }

    fun onClick6() {
//        val a = CoroutineScope(Dispatchers.IO).async { task1() }
//        val b = CoroutineScope(Dispatchers.IO).async { task2() }
//
//        Timber.d("Result: %d", a.await() * b.await())
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
        viewModelJob.cancel()
    }
}