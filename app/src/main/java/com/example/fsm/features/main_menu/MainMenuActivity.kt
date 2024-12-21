package com.example.fsm.features.main_menu


import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fsm.core.extentions.ContextManager
import org.koin.android.ext.android.inject
import android.content.pm.ActivityInfo
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Build
import android.os.Debug
import android.os.StatFs
import com.example.fsm.core.extentions.getScreenDpiAndResolution
import com.example.fsm.core.utils.log
import com.example.fsm.databinding.ActivityMainMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile


class MainMenuActivity: AppCompatActivity(), MainMenuContract.View{

    private lateinit var binding: ActivityMainMenuBinding
    private val presenter: MainMenuContract.Presenter by inject()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getScreenDpiAndResolution(this)


        this.setUpUi()
        this.start()
        presenter.attachView(this)
    }


    private fun setUpUi(){
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.navigationBarColor = Color.parseColor(ContextManager.getColorHex(1))
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            statusBarColor = Color.parseColor(ContextManager.getColorHex(0))
        }
    }


    private fun start(){
        log("Memory Info: ${this.getMemoryInfo(this)}")
        log("CPU Usage: ${this.getCpuUsage()}")
        log("CPU Frequency Usage: ${this.getCpuFrequencyUsage()}")
        log("GPU Usage: ${this.getGpuUsage()}")
        log("Disk Usage: ${this.getDiskIOStats()}")
        log("Network Usage: D: ${this.monitorNetworkUsage().first} | U: ${this.monitorNetworkUsage().second}")
        log("Device Temperature: ${this.getBatteryTemperature(this)}")
    }




    fun getMemoryInfo(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Informações gerais de memória
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            memoryInfo.totalMem // Total de memória RAM (em bytes)
        } else {
            -1 // Não disponível em versões mais antigas
        }

        val availableMem = memoryInfo.availMem // Memória disponível (em bytes)
        val isLowMemory = memoryInfo.lowMemory // Se o dispositivo está com pouca memória

        // Memória usada pela aplicação atual
        val runtime = Runtime.getRuntime()
        val usedMemByApp = runtime.totalMemory() - runtime.freeMemory()

        return """
        Total RAM: ${totalMem / (1024 * 1024)} MB
        RAM Disponível: ${availableMem / (1024 * 1024)} MB
        Memória Usada pela Aplicação: ${usedMemByApp / (1024 * 1024)} MB
        Dispositivo com Baixa Memória: $isLowMemory
    """.trimIndent()
    }


    fun getCpuUsage(): String {
        try {
            // Ler o arquivo /proc/stat para obter as estatísticas de CPU
            val reader = RandomAccessFile("/proc/stat", "r")
            val load = reader.readLine() // Lê a primeira linha, que contém as estatísticas gerais da CPU
            val tokens = load.split(" ").filter { it.isNotEmpty() } // Divide por espaços em branco e remove vazios

            // Os valores relevantes estão a partir do índice 1
            val userTime = tokens[1].toLong()
            val niceTime = tokens[2].toLong()
            val systemTime = tokens[3].toLong()
            val idleTime = tokens[4].toLong()

            // Total de tempo ativo e ocioso
            val totalTime = userTime + niceTime + systemTime + idleTime
            val activeTime = totalTime - idleTime

            // Calcular porcentagem de uso
            val cpuUsage = (activeTime.toDouble() / totalTime.toDouble()) * 100

            reader.close()

            return "Uso da CPU: %.2f%%".format(cpuUsage)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Erro ao obter uso da CPU: ${e.message}"
        }
    }


    fun getCpuFrequencyUsage(): String {
        try {
            val currentFreq = File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq").readText().trim().toLong()
            val maxFreq = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq").readText().trim().toLong()

            val usage = (currentFreq.toDouble() / maxFreq) * 100
            return "Frequência Atual da CPU: ${currentFreq / 1000} MHz\nUso Aproximado da CPU: %.2f%%".format(usage)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Erro ao obter frequência da CPU: ${e.message}"
        }
    }


    fun getGpuUsage(): String {
        try {
            // Caminhos comuns para informações da GPU
            val gpuLoadPath = "/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage" // Para GPUs Adreno
            val gpuFreqPath = "/sys/class/kgsl/kgsl-3d0/gpuclk"             // Frequência atual
            val maxGpuFreqPath = "/sys/class/kgsl/kgsl-3d0/max_gpuclk"      // Frequência máxima

            // Leitura da carga de GPU
            val gpuLoad = File(gpuLoadPath).takeIf { it.exists() }?.readText()?.trim()?.toInt() ?: -1
            val gpuFreq = File(gpuFreqPath).takeIf { it.exists() }?.readText()?.trim()?.toLong() ?: -1
            val maxGpuFreq = File(maxGpuFreqPath).takeIf { it.exists() }?.readText()?.trim()?.toLong() ?: -1

            // Verificação se conseguimos acessar os dados
            if (gpuLoad == -1 && gpuFreq.toInt() == -1 && maxGpuFreq.toInt() == -1) {
                return "- Informações de GPU não disponíveis neste dispositivo."
            }

            // Construção da resposta
            val gpuUsageMessage = if (gpuLoad != -1) "Uso da GPU: $gpuLoad%" else "Carga da GPU não disponível."
            val gpuFreqMessage = if (gpuFreq.toInt() != -1 && maxGpuFreq.toInt() != -1) {
                val usageApprox = (gpuFreq.toDouble() / maxGpuFreq) * 100
                "- Frequência Atual da GPU: ${gpuFreq / 1_000_000} MHz\nUso Aproximado: %.2f%%".format(usageApprox)
            } else {
                "- Frequência da GPU não disponível."
            }

            return "$gpuUsageMessage\n$gpuFreqMessage"

        } catch (e: Exception) {
            e.printStackTrace()
            return """
                - Erro ao obter uso da GPU: ${e.message}
                Informações detalhadas de uso da GPU não estão disponíveis neste dispositivo.
                Isso ocorre devido a restrições de segurança do sistema Android.
                Para monitorar o uso da GPU, use ferramentas como o Android Studio Profiler
                ou um aplicativo de monitoramento especializado.
            """.trimIndent()
        }
    }



    fun getDiskIOStats(): String {
        try {
            val diskStatsPath = "/proc/diskstats"

            // Ler o conteúdo do arquivo
            val lines = File(diskStatsPath).readLines()
            val stats = lines.find { it.contains("mmcblk0") } // Identificar o disco principal (ajuste conforme necessário)

            if (stats != null) {
                val tokens = stats.split("\\s+".toRegex())
                val readsCompleted = tokens[3].toLong()
                val readsMerged = tokens[4].toLong()
                val sectorsRead = tokens[5].toLong()
                val readTime = tokens[6].toLong()
                val writesCompleted = tokens[7].toLong()
                val writesMerged = tokens[8].toLong()
                val sectorsWritten = tokens[9].toLong()
                val writeTime = tokens[10].toLong()

                return """
                Estatísticas de I/O do Disco:
                Leituras Concluídas: $readsCompleted
                Leituras Mescladas: $readsMerged
                Setores Lidos: $sectorsRead
                Tempo de Leitura: $readTime ms
                Escritas Concluídas: $writesCompleted
                Escritas Mescladas: $writesMerged
                Setores Escritos: $sectorsWritten
                Tempo de Escrita: $writeTime ms
            """.trimIndent()
            } else {
                return "Informações do disco principal não encontradas em $diskStatsPath"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Erro ao obter estatísticas de I/O do disco: ${e.message}"
        }
    }


    fun monitorNetworkUsage(intervalMs: Long = 1000): Pair<Double, Double> {
        try {
            var previousRxBytes = TrafficStats.getTotalRxBytes()
            var previousTxBytes = TrafficStats.getTotalTxBytes()

            if (previousRxBytes == TrafficStats.UNSUPPORTED.toLong() || previousTxBytes == TrafficStats.UNSUPPORTED.toLong()) {
                return Pair(-1.0, -1.0) // Indica que a funcionalidade não é suportada
            }

            // Atraso simples
            Thread.sleep(intervalMs)

            val currentRxBytes = TrafficStats.getTotalRxBytes()
            val currentTxBytes = TrafficStats.getTotalTxBytes()

            val downloadSpeed = (currentRxBytes - previousRxBytes) * 8.0 / 1000.0 / (intervalMs / 1000.0) // Kbps
            val uploadSpeed = (currentTxBytes - previousTxBytes) * 8.0 / 1000.0 / (intervalMs / 1000.0) // Kbps

            return Pair(downloadSpeed, uploadSpeed)

        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(-1.0, -1.0) // Indica erro
        }
    }



    fun getBatteryTemperature(context: Context): Float {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1

        return if (temperature != -1) {
            temperature / 10.0f // Temperatura em graus Celsius
        } else {
            -1f // Indica erro ao obter a temperatura
        }
    }






    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
