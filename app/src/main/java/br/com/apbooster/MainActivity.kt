package br.com.apbooster

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var ramValue: TextView
    private lateinit var storageValue: TextView
    private lateinit var batteryValue: TextView
    private lateinit var tempValue: TextView
    private val bg = Color.rgb(12, 17, 29)
    private val panel = Color.rgb(24, 32, 49)
    private val accent = Color.rgb(76, 137, 255)
    private val muted = Color.rgb(164, 177, 199)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
        refreshStats()
    }

    private fun buildScreen() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 34, 22, 28)
        }
        scroll.addView(page)

        page.addView(label("AP BOOSTER", 27f, Color.WHITE, true))
        page.addView(label("PAINEL DE DESEMPENHO • GALAXY", 11f, muted, true).apply {
            setPadding(0, 4, 0, 22)
        })

        val hero = card()
        hero.addView(label("MODO FREE FIRE", 17f, Color.WHITE, true))
        hero.addView(label("Prepare o celular antes de entrar no jogo.", 13f, muted, false))
        hero.addView(actionButton("ABRIR FREE FIRE", accent) { openFreeFire() })
        page.addView(hero)

        page.addView(label("MONITORAMENTO", 15f, Color.WHITE, true).apply {
            setPadding(2, 24, 0, 10)
        })
        val stats = card()
        ramValue = statRow(stats, "RAM disponível")
        storageValue = statRow(stats, "Armazenamento livre")
        batteryValue = statRow(stats, "Bateria")
        tempValue = statRow(stats, "Temperatura da bateria")
        stats.addView(actionButton("ATUALIZAR DADOS", Color.rgb(48, 64, 88)) { refreshStats() })
        page.addView(stats)

        page.addView(label("ATALHOS ÚTEIS", 15f, Color.WHITE, true).apply {
            setPadding(2, 24, 0, 10)
        })
        val shortcuts = card()
        shortcuts.addView(actionButton("Bateria e uso em segundo plano") {
            openSettings(Settings.ACTION_BATTERY_SAVER_SETTINGS, Settings.ACTION_SETTINGS)
        })
        shortcuts.addView(actionButton("Armazenamento do aparelho") {
            openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS, Settings.ACTION_SETTINGS)
        })
        shortcuts.addView(actionButton("Informações dos apps") {
            openSettings(Settings.ACTION_APPLICATION_SETTINGS, Settings.ACTION_SETTINGS)
        })
        shortcuts.addView(actionButton("Não perturbe") {
            openSettings(Settings.ACTION_SOUND_SETTINGS, Settings.ACTION_SETTINGS)
        })
        page.addView(shortcuts)

        page.addView(label(
            "O AP Booster não encerra processos à força nem promete aumentar FPS. " +
            "O Android controla a RAM automaticamente. Use os atalhos para revisar apps, " +
            "armazenamento e bateria com segurança.",
            12f, muted, false
        ).apply { setPadding(4, 18, 4, 0) })

        setContentView(scroll)
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(16, 16, 16, 16)
        setBackgroundColor(panel)
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
        }

    private fun statRow(parent: LinearLayout, title: String): TextView {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 8, 0, 8)
        }
        row.addView(label(title, 13f, muted, false), LinearLayout.LayoutParams(0, -2, 1f))
        val value = label("--", 14f, Color.WHITE, true)
        row.addView(value)
        parent.addView(row)
        return value
    }

    private fun actionButton(text: String, color: Int = accent, action: () -> Unit): Button =
        Button(this).apply {
            this.text = text
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(color)
            setOnClickListener { action() }
            val p = LinearLayout.LayoutParams(-1, -2)
            p.topMargin = 10
            layoutParams = p
        }

    private fun refreshStats() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mem = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mem)
        ramValue.text = String.format(Locale.getDefault(), "%.1f GB", mem.availMem / 1073741824.0)

        val stat = StatFs(Environment.getDataDirectory().path)
        val free = stat.availableBytes / 1073741824.0
        val total = stat.totalBytes / 1073741824.0
        storageValue.text = String.format(Locale.getDefault(), "%.1f / %.1f GB", free, total)

        val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (battery != null) {
            val level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
            batteryValue.text = "${(level * 100) / scale}%"
            val tenths = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            tempValue.text = if (tenths >= 0) String.format(Locale.getDefault(), "%.1f °C", tenths / 10.0) else "Indisponível"
        } else {
            batteryValue.text = "Indisponível"
            tempValue.text = "Indisponível"
        }
    }

    private fun openFreeFire() {
        val packages = listOf("com.dts.freefiremax", "com.dts.freefireth")
        for (pkg in packages) {
            val launch = packageManager.getLaunchIntentForPackage(pkg)
            if (launch != null) {
                startActivity(launch)
                return
            }
        }
        openSettings(Settings.ACTION_APPLICATION_SETTINGS, Settings.ACTION_SETTINGS)
    }

    private fun openSettings(primary: String, fallback: String) {
        try {
            startActivity(Intent(primary))
        } catch (_: Exception) {
            startActivity(Intent(fallback))
        }
    }
}
