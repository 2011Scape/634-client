import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import awt.Container
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.concurrent.Volatile
import kotlin.jvm.Synchronized
import lang.Runnable
import withLock
import awt.getInsets

@OriginalClass("client!ae")
class AssetLoadingScreenRenderer : Runnable {

	@OriginalMember(owner = "client!ae", name = "l", descriptor = "Z")
	private var fullRepaint: Boolean = false

	@OriginalMember(owner = "client!ae", name = "n", descriptor = "Z")
	@Volatile
	private var stopped: Boolean = false

	@OriginalMember(owner = "client!ae", name = "r", descriptor = "J")
	private var lastComponentUpdate: Long = 0L

	@OriginalMember(owner = "client!ae", name = "s", descriptor = "I")
	private var loadingPercentage: Int = 0

	@OriginalMember(owner = "client!ae", name = "t", descriptor = "Lclient!jj;")
	private var loadingStage: LoadingStage? = null

	@OriginalMember(owner = "client!ae", name = "u", descriptor = "J")
	private var loadingStageChangedTime: Long = 0L

	@OriginalMember(owner = "client!ae", name = "x", descriptor = "I")
	private var cycles: Int = 0

	@OriginalMember(owner = "client!ae", name = "y", descriptor = "Ljava/lang/String;")
	private var loadingText: String? = null

	@OriginalMember(owner = "client!ae", name = "k", descriptor = "Lclient!dca;")
	private var frontComponent: LoginScreenComponent? = AwtLoadingScreen()

	@OriginalMember(owner = "client!ae", name = "w", descriptor = "Lclient!dca;")
	private var backComponent: LoginScreenComponent? = null

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(B)I")
	fun getLoadingStageCompletion(): Int {
		if (this.loadingStage == null) {
			return 0
		}
		@Pc(19) val step: Int = this.loadingStage!!.getStep()
		if (this.loadingStage!!.hasProgressTracking && this.loadingStage!!.secondaryCompletionDisplay > this.loadingPercentage) {
			return this.loadingPercentage + 1
		} else if (step >= 0 && LoadingStage.LOADING_STAGES!!.size - 1 > step) {
			return if (this.loadingPercentage == this.loadingStage!!.primaryCompletionDisplay) this.loadingStage!!.secondaryCompletionDisplay else this.loadingStage!!.primaryCompletionDisplay
		} else {
			return 100
		}
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(Lclient!jj;Ljava/lang/String;IJZ)V")
	@Synchronized
	fun setLoadingState(@OriginalArg(0) loadState: LoadingStage?, @OriginalArg(1) loadingText: String?, @OriginalArg(2) loadingPercentage: Int, @OriginalArg(3) loadingStageChangedTime: Long) {
		this.loadingText = loadingText
		this.loadingPercentage = loadingPercentage
		this.loadingStageChangedTime = loadingStageChangedTime
		this.loadingStage = loadState
	}

	@OriginalMember(owner = "client!ae", name = "run", descriptor = "()V")
	override fun run() {
		while (!this.stopped) {
			@Pc(10) val cycleStart: Long = Utils.currentTimeMillis()
			// Kotlin: `continue` out of the synchronized block is carried by this flag.
			var skip: Boolean = false
			withLock(this) {
				try {
					this.cycles++
					if (this.frontComponent is AwtLoadingScreen) {
						this.frontComponent!!.render(this.fullRepaint)
					} else {
						@Pc(35) val currentTime: Long = Utils.currentTimeMillis()
						if (ClientState.activeToolkit == null || this.backComponent == null || this.backComponent!!.getFadeDuration() == 0 || this.lastComponentUpdate < currentTime - this.backComponent!!.getFadeDuration().toLong()) {
							if (this.backComponent != null) {
								this.fullRepaint = true
								this.backComponent!!.cleanup()
								this.backComponent = null
							}
							if (this.fullRepaint) {
								GameShell.clearCanvasBackground()
								if (ClientState.activeToolkit != null) {
									ClientState.activeToolkit!!.GA(0)
								}
							}
							this.frontComponent!!.render(this.fullRepaint || ClientState.activeToolkit != null && ClientState.activeToolkit!!.supportsFullscreen())
						} else {
							@Pc(71) val dstAlpha: Int = ((currentTime - this.lastComponentUpdate) * 255L / this.backComponent!!.getFadeDuration().toLong()).toInt()
							@Pc(76) val srcAlpha: Int = 255 - dstAlpha
							@Pc(82) val srcColor: Int = (srcAlpha shl 24) or 0xFFFFFF
							@Pc(88) val dstColor: Int = (dstAlpha shl 24) or 0xFFFFFF
							GameShell.clearCanvasBackground()
							ClientState.activeToolkit!!.GA(0)
							@Pc(99) val sprite: NativeSprite? = ClientState.activeToolkit!!.generateEmptySprite(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight, true)
							ClientState.activeToolkit!!.renderToSprite(sprite)
							this.backComponent!!.render(true)
							ClientState.activeToolkit!!.restoreSurface()
							sprite!!.render(0, 0, 0, srcColor, 1)
							ClientState.activeToolkit!!.renderToSprite(sprite)
							ClientState.activeToolkit!!.GA(0)
							this.frontComponent!!.render(true)
							ClientState.activeToolkit!!.restoreSurface()
							sprite.render(0, 0, 0, dstColor, 1)
						}
						try {
							if (ClientState.activeToolkit != null && this.frontComponent !is AwtLoadingScreen) {
								ClientState.activeToolkit!!.renderFrame()
							}
						} catch (@Pc(189) ex: FlipException) {
							ExplicitRuntimeException.reportError(ex.message + " (Recovered) " + ActiveClient.activeClient!!.getDebugString(), ex)
							ToolkitSwitch.loadAssets(true, 0)
						}
					}
					@Pc(214) val container: Container?
					if (GameShell.engineFrame != null) {
						container = GameShell.engineFrame
					} else if (GameShell.applet == null) {
						container = GameShell.anApplet_Sub1_1
					} else {
						container = GameShell.applet
					}
					container!!.getSize()
					container.getSize()
					if (GameShell.engineFrame === container) {
						GameShell.engineFrame!!.getInsets()
					}
					this.fullRepaint = false
					if (ClientState.activeToolkit != null && this.frontComponent !is AwtLoadingScreen && this.loadingStage!!.getStep() < LoadingStage.aClass167_21.getStep()) {
						GameCanvasLayout.adjustActiveWindow()
					}
				} catch (@Pc(257) ex2: Exception) {
					skip = true
				}
			}
			if (skip) {
				continue
			}
			@Pc(269) val cycleEnd: Long = Utils.currentTimeMillis()
			@Pc(278) val sleepDuration: Int = (cycleStart + 20L - cycleEnd).toInt()
			if (sleepDuration > 0) {
				TimeUtils.sleep(sleepDuration.toLong())
			}
		}
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(I)Ljava/lang/String;")
	fun getLoadingText(): String? {
		return this.loadingText
	}

	@OriginalMember(owner = "client!ae", name = "c", descriptor = "(B)V")
	fun stop() {
		this.stopped = true
	}

	@OriginalMember(owner = "client!ae", name = "d", descriptor = "(B)I")
	fun getLoadingPercentage(): Int {
		return this.loadingPercentage
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(ILclient!dca;)V")
	@Synchronized
	fun setFrontComponent(@OriginalArg(1) loadingScreen: LoginScreenComponent?) {
		this.backComponent = this.frontComponent
		this.frontComponent = loadingScreen
		this.lastComponentUpdate = Utils.currentTimeMillis()
	}

	@OriginalMember(owner = "client!ae", name = "b", descriptor = "(I)Z")
	@Synchronized
	fun isFrontComponentFinished(): Boolean {
		return this.frontComponent!!.finished(this.lastComponentUpdate)
	}

	@OriginalMember(owner = "client!ae", name = "c", descriptor = "(I)J")
	fun getLoadingStageChangedTime(): Long {
		return this.loadingStageChangedTime
	}

	@OriginalMember(owner = "client!ae", name = "d", descriptor = "(I)Lclient!jj;")
	fun getLoadingStage(): LoadingStage? {
		return this.loadingStage
	}

	@OriginalMember(owner = "client!ae", name = "e", descriptor = "(B)I")
	fun getCycles(): Int {
		return this.cycles
	}

	@OriginalMember(owner = "client!ae", name = "e", descriptor = "(I)V")
	@Synchronized
	fun requestFullRepaint() {
		this.fullRepaint = true
	}
}
