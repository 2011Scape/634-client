/*
 * Not part of the original client: 634HD (toolkit 6). The debug panel (client.debug builds only):
 * a window beside the game, opened and closed with F10 or ::hdpanel (HdSettings.panel), with
 *
 * - Stats: FPS, CPU and GPU frame time (a timer query around the 3D view), draw calls and triangles
 *   by kind, point lights and shadow casters, the last region's build time and worst frame;
 * - Features: every HdSettings switch;
 * - Overlays: normals, texture ids, light counts, shadow map, chunk bounds, occlusion, shadows;
 * - Environment and Lights: the environment the player is in and the light nearest the player,
 *   edited live, with Save writing environments.json / lights.json (which hot reload then reads);
 * - Inspect: what is under the mouse - the texture (with the texture overlay), the mini menu's
 *   targets (object or NPC, their ids and tile) - for writing overrides;
 * - Content: where the data files come from, and their warnings.
 *
 * The game thread fills [Data] once a frame (HdToolkit.debugPanelTick); the panel only reads it and
 * changes settings or the content's definitions, never GL state.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.graphics.hd.content.HdContent
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.WindowConstants

class HdDebugPanel(private val content: HdContent) {

    companion object {
        val OVERLAYS = arrayOf("none", "normals", "texture ids", "light count", "shadow map", "chunk bounds", "occlusion only", "shadows only")

        /** Whether this is a debug build (client.debug), the only kind the panel opens in. */
        val enabled: Boolean = System.getProperty("client.debug") != null
    }

    /** What the game thread measured this frame. */
    class Data {
        @Volatile var fps: Int = 0
        @Volatile var frameMs: Float = 0f
        @Volatile var gpuMs: Float = 0f
        @Volatile var drawCalls: Int = 0
        @Volatile var triangles: Int = 0
        @Volatile var modelDraws: Int = 0
        @Volatile var groundDraws: Int = 0
        @Volatile var otherDraws: Int = 0
        @Volatile var lights: Int = 0
        @Volatile var casters: Int = 0
        @Volatile var regionBuildMs: Int = 0
        @Volatile var regionHitchMs: Int = 0
        @Volatile var chunkRebuilds: Int = 0
        @Volatile var mapsMb: Long = 0
        @Volatile var environment: HdContent.EnvironmentDef? = null
        @Volatile var nearestLight: HdContent.LightDef? = null
        @Volatile var playerTile: String = ""
        @Volatile var hoveredTexture: String = ""
        @Volatile var menuTargets: String = ""
        @Volatile var mouseX: Int = -1
        @Volatile var mouseY: Int = -1
    }

    val data: Data = Data()

    /** The overlay chosen (HdFrameUniforms.setDebug); client.test.hdOverlay=<n> starts with one. */
    @Volatile
    var overlay: Int = System.getProperty("client.test.hdOverlay")?.toIntOrNull() ?: 0

    private var frame: JFrame? = null
    private val labels = HashMap<String, JLabel>()
    private val featureBoxes = ArrayList<Pair<String, javax.swing.JComponent>>()
    private var shownEnvironment: HdContent.EnvironmentDef? = null
    private var shownLight: HdContent.LightDef? = null
    private val envFields = LinkedHashMap<String, JTextField>()
    private val lightFields = LinkedHashMap<String, JTextField>()
    private val envTitle = JLabel()
    private val lightTitle = JLabel()
    private val status = JLabel(" ")
    private val warnings = JTextArea(8, 40)
    private val inspect = JTextArea(10, 40)

    fun isOpen(): Boolean = frame?.isVisible == true

    /** Closes and frees the window: the toolkit it shows is stopping. */
    fun dispose() {
        SwingUtilities.invokeLater {
            frame?.dispose()
            frame = null
        }
    }

    fun setOpen(open: Boolean) {
        SwingUtilities.invokeLater {
            if (open) {
                if (frame == null) build()
                frame!!.isVisible = true
            } else {
                frame?.isVisible = false
            }
        }
    }

    private fun build() {
        val window = JFrame("634HD debug")
        window.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        window.addWindowListener(object : java.awt.event.WindowAdapter() {
            override fun windowClosing(e: java.awt.event.WindowEvent?) {
                HdSettings.panel = false
            }
        })
        val tabs = JTabbedPane()
        tabs.addTab("Stats", stats())
        tabs.addTab("Features", features())
        tabs.addTab("Environment", editor(envFields, envTitle, arrayOf("sky top", "sky horizon", "fog colour", "fog start", "fog end", "height fog density", "height fog top", "lut", "water tint", "sun colour", "sun intensity"), { applyEnvironment() }, {
            applyEnvironment()
            status.text = content.saveEnvironments()
        }))
        tabs.addTab("Lights", editor(lightFields, lightTitle, arrayOf("colour", "radius", "intensity", "offset x", "offset height", "offset z", "flicker"), { applyLight() }, {
            applyLight()
            status.text = content.saveLights()
        }))
        tabs.addTab("Inspect", inspectTab())
        tabs.addTab("Content", contentTab())
        window.contentPane.add(tabs, BorderLayout.CENTER)
        window.contentPane.add(status, BorderLayout.SOUTH)
        window.minimumSize = Dimension(460, 560)
        window.pack()
        window.setLocation(40, 40)
        frame = window
        Timer(250) { refresh() }.start()
        // client.test.hdPanelShot=<file.png>: the panel's tabs painted into an image after a few seconds (testing)
        val shot: String? = System.getProperty("client.test.hdPanelShot")
        if (shot != null) {
            val timer = Timer(6000) {
                val pane = window.contentPane
                val image = java.awt.image.BufferedImage(pane.width * tabs.tabCount, pane.height, java.awt.image.BufferedImage.TYPE_INT_RGB)
                val g = image.createGraphics()
                for (i in 0 until tabs.tabCount) {
                    tabs.selectedIndex = i
                    pane.validate()
                    val tile = g.create(pane.width * i, 0, pane.width, pane.height)
                    pane.paint(tile)
                    tile.dispose()
                }
                g.dispose()
                tabs.selectedIndex = 0
                javax.imageio.ImageIO.write(image, "png", java.io.File(shot))
                println("634HD: debug panel painted to " + shot)
            }
            timer.isRepeats = false
            timer.start()
        }
    }

    private fun row(panel: JPanel, y: Int, name: String, component: javax.swing.JComponent) {
        val c = GridBagConstraints()
        c.gridx = 0
        c.gridy = y
        c.anchor = GridBagConstraints.WEST
        c.insets = Insets(2, 6, 2, 6)
        panel.add(JLabel(name), c)
        c.gridx = 1
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        panel.add(component, c)
    }

    private fun stats(): JPanel {
        val panel = JPanel(GridBagLayout())
        val names = arrayOf("FPS", "Frame (CPU)", "3D view (GPU)", "Draw calls", "  models", "  ground", "  2D and other", "Triangles", "Point lights", "Shadow casters", "Region build", "Region worst frame", "Chunk rebuilds", "Material maps", "Environment", "Player tile")
        for ((i, name) in names.withIndex()) {
            val label = JLabel("-")
            label.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            labels[name] = label
            row(panel, i, name, label)
        }
        return panel
    }

    private fun features(): JPanel {
        val panel = JPanel(GridBagLayout())
        var y = 0
        val choices = mapOf(
            "tonemap" to arrayOf("off", "aces", "agx"), "anisotropy" to arrayOf("0", "2", "4", "8", "16"),
            "shadows" to arrayOf("off", "1024", "2048", "4096"), "water" to arrayOf("0", "1"), "interfaceScale" to arrayOf("off", "2", "3", "4"),
        )
        val switches = arrayOf("tonemap", "dither", "anisotropy", "mipmaps", "fog", "fade", "ambientOcclusion", "pixelLighting", "shadows", "lights", "terrainBlend", "water", "foliage", "sky", "softParticles", "fxaa", "bloom", "colourGrading", "materials", "particles", "smoothInterface")
        for (name in switches) {
            val options = choices[name]
            if (options != null) {
                val box = JComboBox(options)
                box.addActionListener { report(HdSettings.set(name, box.selectedItem as String)) }
                featureBoxes.add(name to box)
                row(panel, y++, name, box)
            } else {
                val box = JCheckBox()
                box.addActionListener { report(HdSettings.set(name, if (box.isSelected) "on" else "off")) }
                featureBoxes.add(name to box)
                row(panel, y++, name, box)
            }
        }
        val overlays = JComboBox(OVERLAYS)
        overlays.selectedIndex = overlay
        overlays.addActionListener { overlay = overlays.selectedIndex }
        row(panel, y++, "Overlay", overlays)
        val all = JPanel(GridLayout(1, 2))
        val on = JButton("All on")
        on.addActionListener { report(HdSettings.set("all", "on")); syncFeatures() }
        val off = JButton("All off")
        off.addActionListener { report(HdSettings.set("all", "off")); syncFeatures() }
        all.add(on)
        all.add(off)
        row(panel, y, "", all)
        syncFeatures()
        return panel
    }

    private fun report(error: String?) {
        status.text = error ?: "634HD: " + HdSettings.describe().joinToString("; ").take(160)
    }

    /** The feature controls set from HdSettings (after All on/off, or a change from ::hd). */
    private fun syncFeatures() {
        for ((name, component) in featureBoxes) {
            val value: String = currentValue(name)
            if (component is JCheckBox) {
                component.isSelected = value == "on"
            } else if (component is JComboBox<*>) {
                for (i in 0 until component.itemCount) {
                    if (component.getItemAt(i) == value) {
                        if (component.selectedIndex != i) component.selectedIndex = i
                    }
                }
            }
        }
    }

    private fun currentValue(name: String): String = when (name) {
        "tonemap" -> arrayOf("off", "aces", "agx")[HdSettings.tonemap]
        "anisotropy" -> HdSettings.anisotropy.toString()
        "shadows" -> if (HdSettings.shadows == 0) "off" else HdSettings.shadows.toString()
        "water" -> HdSettings.water.toString()
        "interfaceScale" -> if (HdSettings.interfaceScale == 0) "off" else HdSettings.interfaceScale.toString()
        "dither" -> onOff(HdSettings.dither)
        "mipmaps" -> onOff(HdSettings.mipmaps)
        "fog" -> onOff(HdSettings.fog)
        "fade" -> onOff(HdSettings.fade)
        "ambientOcclusion" -> onOff(HdSettings.ambientOcclusion)
        "pixelLighting" -> onOff(HdSettings.pixelLighting)
        "lights" -> onOff(HdSettings.lights)
        "terrainBlend" -> onOff(HdSettings.terrainBlend)
        "foliage" -> onOff(HdSettings.foliage)
        "sky" -> onOff(HdSettings.sky)
        "softParticles" -> onOff(HdSettings.softParticles)
        "fxaa" -> onOff(HdSettings.fxaa)
        "bloom" -> onOff(HdSettings.bloom)
        "colourGrading" -> onOff(HdSettings.colourGrading)
        "materials" -> onOff(HdSettings.materials)
        "particles" -> onOff(HdSettings.particles)
        "smoothInterface" -> onOff(HdSettings.smoothInterface)
        else -> ""
    }

    private fun onOff(b: Boolean): String = if (b) "on" else "off"

    private fun editor(fields: LinkedHashMap<String, JTextField>, title: JLabel, names: Array<String>, apply: () -> Unit, save: () -> Unit): JPanel {
        val outer = JPanel(BorderLayout())
        title.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        outer.add(title, BorderLayout.NORTH)
        val panel = JPanel(GridBagLayout())
        for ((i, name) in names.withIndex()) {
            val field = JTextField(14)
            field.addActionListener { apply() }
            fields[name] = field
            row(panel, i, name, field)
        }
        outer.add(panel, BorderLayout.CENTER)
        val buttons = JPanel(GridLayout(1, 2))
        val applyButton = JButton("Apply")
        applyButton.addActionListener { apply() }
        val saveButton = JButton("Save to file")
        saveButton.addActionListener { save() }
        buttons.add(applyButton)
        buttons.add(saveButton)
        outer.add(buttons, BorderLayout.SOUTH)
        return outer
    }

    private fun inspectTab(): JPanel {
        val panel = JPanel(BorderLayout())
        val help = JLabel("<html>Choose the <b>texture ids</b> overlay to read the texture under the mouse.<br>Objects and NPCs come from the mini menu at the mouse.</html>")
        help.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        panel.add(help, BorderLayout.NORTH)
        inspect.isEditable = false
        inspect.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        panel.add(JScrollPane(inspect), BorderLayout.CENTER)
        return panel
    }

    private fun contentTab(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val source = JLabel("Data: " + (content.folder?.toString() ?: "the jar (no hot reload: run from the repository with client.debug)"))
        source.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        panel.add(source)
        val reload = JButton("Reload now")
        reload.addActionListener { content.loadAll(); status.text = "reloaded" }
        panel.add(reload)
        warnings.isEditable = false
        warnings.foreground = Color(160, 40, 0)
        panel.add(JScrollPane(warnings))
        return panel
    }

    private fun hex(c: FloatArray?): String = if (c == null) "" else String.format("#%02x%02x%02x", (c[0] * 255 + 0.5f).toInt(), (c[1] * 255 + 0.5f).toInt(), (c[2] * 255 + 0.5f).toInt())

    private fun colour(text: String): FloatArray? = if (Regex("^#[0-9a-fA-F]{6}$").matches(text.trim())) HdContent.colour(text.trim()) else null

    private fun number(text: String): Float = text.trim().toFloatOrNull() ?: Float.NaN

    private fun applyEnvironment() {
        val e = shownEnvironment ?: return
        val f = envFields
        e.skyTop = colour(f["sky top"]!!.text)
        e.skyHorizon = colour(f["sky horizon"]!!.text)
        e.fogColour = colour(f["fog colour"]!!.text)
        e.fogStart = number(f["fog start"]!!.text)
        e.fogEnd = number(f["fog end"]!!.text)
        e.heightFogDensity = number(f["height fog density"]!!.text).let { if (it.isNaN()) 0f else it }
        e.heightFogTop = number(f["height fog top"]!!.text).let { if (it.isNaN()) 0f else it }
        e.lut = f["lut"]!!.text.trim().ifEmpty { null }
        e.waterTint = colour(f["water tint"]!!.text)
        e.sunColour = colour(f["sun colour"]!!.text)
        e.sunIntensity = number(f["sun intensity"]!!.text).let { if (it.isNaN()) 1f else it }
        status.text = "applied to " + e.name
    }

    private fun applyLight() {
        val l = shownLight ?: return
        val f = lightFields
        colour(f["colour"]!!.text)?.let { l.red = it[0]; l.green = it[1]; l.blue = it[2] }
        number(f["radius"]!!.text).let { if (!it.isNaN()) l.radius = it }
        number(f["intensity"]!!.text).let { if (!it.isNaN()) l.intensity = it }
        number(f["offset x"]!!.text).let { if (!it.isNaN()) l.offsetX = it }
        number(f["offset height"]!!.text).let { if (!it.isNaN()) l.offsetY = it }
        number(f["offset z"]!!.text).let { if (!it.isNaN()) l.offsetZ = it }
        l.flicker = when (f["flicker"]!!.text.trim()) { "flicker" -> 1; "pulse" -> 2; else -> 0 }
        status.text = "applied to " + l.name
    }

    private fun fmt(v: Float): String = if (v.isNaN()) "" else if (v == Math.round(v).toFloat()) Math.round(v).toString() else String.format(java.util.Locale.ROOT, "%.2f", v)

    private fun refresh() {
        val d = data
        labels["FPS"]!!.text = d.fps.toString()
        labels["Frame (CPU)"]!!.text = String.format(java.util.Locale.ROOT, "%.2f ms", d.frameMs)
        labels["3D view (GPU)"]!!.text = String.format(java.util.Locale.ROOT, "%.2f ms", d.gpuMs)
        labels["Draw calls"]!!.text = d.drawCalls.toString()
        labels["  models"]!!.text = d.modelDraws.toString()
        labels["  ground"]!!.text = d.groundDraws.toString()
        labels["  2D and other"]!!.text = d.otherDraws.toString()
        labels["Triangles"]!!.text = d.triangles.toString()
        labels["Point lights"]!!.text = d.lights.toString()
        labels["Shadow casters"]!!.text = d.casters.toString()
        labels["Region build"]!!.text = d.regionBuildMs.toString() + " ms"
        labels["Region worst frame"]!!.text = d.regionHitchMs.toString() + " ms"
        labels["Chunk rebuilds"]!!.text = d.chunkRebuilds.toString()
        labels["Material maps"]!!.text = d.mapsMb.toString() + " MB"
        labels["Environment"]!!.text = d.environment?.name ?: "(none)"
        labels["Player tile"]!!.text = d.playerTile
        val e = d.environment
        if (e !== shownEnvironment) {
            shownEnvironment = e
            envTitle.text = if (e == null) "No environment here" else "Environment: " + e.name + " (environments.json line " + e.line + ")"
            val f = envFields
            f["sky top"]!!.text = hex(e?.skyTop)
            f["sky horizon"]!!.text = hex(e?.skyHorizon)
            f["fog colour"]!!.text = hex(e?.fogColour)
            f["fog start"]!!.text = fmt(e?.fogStart ?: Float.NaN)
            f["fog end"]!!.text = fmt(e?.fogEnd ?: Float.NaN)
            f["height fog density"]!!.text = fmt(e?.heightFogDensity ?: Float.NaN)
            f["height fog top"]!!.text = fmt(e?.heightFogTop ?: Float.NaN)
            f["lut"]!!.text = e?.lut ?: ""
            f["water tint"]!!.text = hex(e?.waterTint)
            f["sun colour"]!!.text = hex(e?.sunColour)
            f["sun intensity"]!!.text = fmt(e?.sunIntensity ?: Float.NaN)
        }
        val l = d.nearestLight
        if (l !== shownLight) {
            shownLight = l
            lightTitle.text = if (l == null) "No light near the player (turn lights on)" else "Nearest light: " + l.name
            val f = lightFields
            f["colour"]!!.text = if (l == null) "" else hex(floatArrayOf(l.red, l.green, l.blue))
            f["radius"]!!.text = fmt(l?.radius ?: Float.NaN)
            f["intensity"]!!.text = fmt(l?.intensity ?: Float.NaN)
            f["offset x"]!!.text = fmt(l?.offsetX ?: Float.NaN)
            f["offset height"]!!.text = fmt(l?.offsetY ?: Float.NaN)
            f["offset z"]!!.text = fmt(l?.offsetZ ?: Float.NaN)
            f["flicker"]!!.text = if (l == null) "" else arrayOf("none", "flicker", "pulse")[l.flicker]
        }
        inspect.text = "Mouse: " + d.mouseX + "," + d.mouseY + "\n" + d.hoveredTexture + "\n\n" + d.menuTargets
        val text = if (content.warnings.isEmpty()) "No warnings." else content.warnings.joinToString("\n")
        if (warnings.text != text) warnings.text = text
        if (HdSettings.version != syncedVersion) {
            syncedVersion = HdSettings.version
            syncFeatures()
        }
    }

    private var syncedVersion: Int = -1
}
