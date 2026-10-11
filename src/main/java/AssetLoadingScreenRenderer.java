import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import java.awt.Container;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.LoadingStage;
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ae")
public final class AssetLoadingScreenRenderer implements Runnable {

	@OriginalMember(owner = "client!ae", name = "l", descriptor = "Z")
	private boolean fullRepaint;

	@OriginalMember(owner = "client!ae", name = "n", descriptor = "Z")
	private volatile boolean stopped;

	@OriginalMember(owner = "client!ae", name = "r", descriptor = "J")
	private long lastComponentUpdate;

	@OriginalMember(owner = "client!ae", name = "s", descriptor = "I")
	private int loadingPercentage;

	@OriginalMember(owner = "client!ae", name = "t", descriptor = "Lclient!jj;")
	private LoadingStage loadingStage;

	@OriginalMember(owner = "client!ae", name = "u", descriptor = "J")
	private long loadingStageChangedTime;

	@OriginalMember(owner = "client!ae", name = "x", descriptor = "I")
	private int cycles;

	@OriginalMember(owner = "client!ae", name = "y", descriptor = "Ljava/lang/String;")
	private String loadingText;

	@OriginalMember(owner = "client!ae", name = "k", descriptor = "Lclient!dca;")
	private LoginScreenComponent frontComponent = new AwtLoadingScreen();

	@OriginalMember(owner = "client!ae", name = "w", descriptor = "Lclient!dca;")
	private LoginScreenComponent backComponent = null;

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(B)I")
	public int getLoadingStageCompletion() {
		if (this.loadingStage == null) {
			return 0;
		}
		@Pc(19) int step = this.loadingStage.getStep();
		if (this.loadingStage.hasProgressTracking && this.loadingStage.secondaryCompletionDisplay > this.loadingPercentage) {
			return this.loadingPercentage + 1;
		} else if (step >= 0 && LoadingStage.LOADING_STAGES.length - 1 > step) {
			return this.loadingPercentage == this.loadingStage.primaryCompletionDisplay ? this.loadingStage.secondaryCompletionDisplay : this.loadingStage.primaryCompletionDisplay;
		} else {
			return 100;
		}
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(Lclient!jj;Ljava/lang/String;IJZ)V")
	public synchronized void setLoadingState(@OriginalArg(0) LoadingStage loadState, @OriginalArg(1) String loadingText, @OriginalArg(2) int loadingPercentage, @OriginalArg(3) long loadingStageChangedTime) {
		this.loadingText = loadingText;
		this.loadingPercentage = loadingPercentage;
		this.loadingStageChangedTime = loadingStageChangedTime;
		this.loadingStage = loadState;
	}

	@OriginalMember(owner = "client!ae", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (!this.stopped) {
			@Pc(10) long cycleStart = Utils.currentTimeMillis();
			synchronized (this) {
				try {
					this.cycles++;
					if (this.frontComponent instanceof AwtLoadingScreen) {
						this.frontComponent.render(this.fullRepaint);
					} else {
						@Pc(35) long currentTime = Utils.currentTimeMillis();
						if (ClientState.activeToolkit == null || this.backComponent == null || this.backComponent.getFadeDuration() == 0 || this.lastComponentUpdate < currentTime - (long) this.backComponent.getFadeDuration()) {
							if (this.backComponent != null) {
								this.fullRepaint = true;
								this.backComponent.cleanup();
								this.backComponent = null;
							}
							if (this.fullRepaint) {
								GameShell.clearCanvasBackground();
								if (ClientState.activeToolkit != null) {
									ClientState.activeToolkit.GA(0);
								}
							}
							this.frontComponent.render(this.fullRepaint || ClientState.activeToolkit != null && ClientState.activeToolkit.supportsFullscreen());
						} else {
							@Pc(71) int dstAlpha = (int) ((currentTime - this.lastComponentUpdate) * 255L / (long) this.backComponent.getFadeDuration());
							@Pc(76) int srcAlpha = 255 - dstAlpha;
							@Pc(82) int srcColor = srcAlpha << 24 | 0xFFFFFF;
							@Pc(88) int dstColor = dstAlpha << 24 | 0xFFFFFF;
							GameShell.clearCanvasBackground();
							ClientState.activeToolkit.GA(0);
							@Pc(99) NativeSprite sprite = ClientState.activeToolkit.generateEmptySprite(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight, true);
							ClientState.activeToolkit.renderToSprite(sprite);
							this.backComponent.render(true);
							ClientState.activeToolkit.restoreSurface();
							sprite.render(0, 0, 0, srcColor, 1);
							ClientState.activeToolkit.renderToSprite(sprite);
							ClientState.activeToolkit.GA(0);
							this.frontComponent.render(true);
							ClientState.activeToolkit.restoreSurface();
							sprite.render(0, 0, 0, dstColor, 1);
						}
						try {
							if (ClientState.activeToolkit != null && !(this.frontComponent instanceof AwtLoadingScreen)) {
								ClientState.activeToolkit.renderFrame();
							}
						} catch (@Pc(189) FlipException ex) {
							ExplicitRuntimeException.reportError(ex.getMessage() + " (Recovered) " + ActiveClient.activeClient.getDebugString(), ex);
							ToolkitSwitch.loadAssets(true, 0);
						}
					}
					@Pc(214) Container container;
					if (GameShell.engineFrame != null) {
						container = GameShell.engineFrame;
					} else if (GameShell.applet == null) {
						container = GameShell.anApplet_Sub1_1;
					} else {
						container = GameShell.applet;
					}
					container.getSize();
					container.getSize();
					if (GameShell.engineFrame == container) {
						GameShell.engineFrame.getInsets();
					}
					this.fullRepaint = false;
					if (ClientState.activeToolkit != null && !(this.frontComponent instanceof AwtLoadingScreen) && this.loadingStage.getStep() < LoadingStage.aClass167_21.getStep()) {
						GameCanvasLayout.adjustActiveWindow();
					}
				} catch (@Pc(257) Exception ex2) {
					continue;
				}
			}
			@Pc(269) long cycleEnd = Utils.currentTimeMillis();
			@Pc(278) int sleepDuration = (int) (cycleStart + 20L - cycleEnd);
			if (sleepDuration > 0) {
				TimeUtils.sleep((long) sleepDuration);
			}
		}
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(I)Ljava/lang/String;")
	public String getLoadingText() {
		return this.loadingText;
	}

	@OriginalMember(owner = "client!ae", name = "c", descriptor = "(B)V")
	public void stop() {
		this.stopped = true;
	}

	@OriginalMember(owner = "client!ae", name = "d", descriptor = "(B)I")
	public int getLoadingPercentage() {
		return this.loadingPercentage;
	}

	@OriginalMember(owner = "client!ae", name = "a", descriptor = "(ILclient!dca;)V")
	public synchronized void setFrontComponent(@OriginalArg(1) LoginScreenComponent loadingScreen) {
		this.backComponent = this.frontComponent;
		this.frontComponent = loadingScreen;
		this.lastComponentUpdate = Utils.currentTimeMillis();
	}

	@OriginalMember(owner = "client!ae", name = "b", descriptor = "(I)Z")
	public synchronized boolean isFrontComponentFinished() {
		return this.frontComponent.finished(this.lastComponentUpdate);
	}

	@OriginalMember(owner = "client!ae", name = "c", descriptor = "(I)J")
	public long getLoadingStageChangedTime() {
		return this.loadingStageChangedTime;
	}

	@OriginalMember(owner = "client!ae", name = "d", descriptor = "(I)Lclient!jj;")
	public LoadingStage getLoadingStage() {
		return this.loadingStage;
	}

	@OriginalMember(owner = "client!ae", name = "e", descriptor = "(B)I")
	public int getCycles() {
		return this.cycles;
	}

	@OriginalMember(owner = "client!ae", name = "e", descriptor = "(I)V")
	public synchronized void requestFullRepaint() {
		this.fullRepaint = true;
	}
}
