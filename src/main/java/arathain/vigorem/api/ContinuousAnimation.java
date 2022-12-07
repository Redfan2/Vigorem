package arathain.vigorem.api;

import arathain.vigorem.anim.Animation;
import arathain.vigorem.anim.Keyframe;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map;

public abstract class ContinuousAnimation extends Animation {
	public final Map<String, List<Keyframe>> endKeyframes;
	private final int endLength;
	protected int stage;
	public ContinuousAnimation(int initLength, Map<String, List<Keyframe>> initKeyframes, int endLength, Map<String, List<Keyframe>> endKeyframes) {
		super(initLength, initKeyframes);
		this.endKeyframes = endKeyframes;
		this.endLength = endLength;
	}
	public boolean shouldRemove() {
		return frame >= endLength && stage == 2;
	}

	public void tick() {
		if(stage == 2 || stage == 0) {
			super.tick();
		}
		if(frame >= getLength()) {
			stage = 1;
		}
		if(stage == 1) {
			if(this.shouldEnd()) {
				stage = 2;
			}
			codeTick();
		}
	}

	@Override
	public Vec3f getRot(String query, float tickDelta) {
		if(stage == 1) {
			return this.getCodeRot(query, tickDelta);
		} else if(stage == 2) {
			if(!endKeyframes.containsKey(query)) {
				return Vec3f.ZERO;
			}
			Keyframe lastFrame = null;
			Keyframe nextFrame = null;
			boolean bl = false;
			for(Keyframe frame : endKeyframes.get(query)) {
				if(frame.frame == (this.frame + tickDelta)) {
					lastFrame = frame;
					nextFrame = frame;
					bl = true;
				}
				if(lastFrame == null && frame.frame < (this.frame + tickDelta)) {
					lastFrame = frame;
				} else {
					if(lastFrame != null && frame.frame > lastFrame.frame && frame.frame < (this.frame + tickDelta)) {
						lastFrame = frame;
					}
				}
				if(nextFrame == null && frame.frame > (this.frame + tickDelta)) {
					nextFrame = frame;
				} else {
					if(nextFrame != null && frame.frame < nextFrame.frame && frame.frame > (this.frame + tickDelta)) {
						nextFrame = frame;
					}
				}
			}
			assert lastFrame != null;
			if(nextFrame == null) {
				nextFrame = lastFrame;
			}
			return getRot(lastFrame, nextFrame, tickDelta, bl);
		}
		return super.getRot(query, tickDelta);
	}
	protected abstract Vec3f getCodeRot(String query, float tickDelta);

	@Override
	public Vec3f getPivot(String query, float tickDelta) {
		if(stage == 1) {
			return this.getCodePivot(query, tickDelta);
		} else if(stage == 2) {
			if(!endKeyframes.containsKey(query)) {
				return Vec3f.ZERO;
			}
			Keyframe lastFrame = null;
			Keyframe nextFrame = null;
			boolean bl = false;
			for(Keyframe frame : endKeyframes.get(query)) {
				if(frame.frame == (this.frame + tickDelta)) {
					lastFrame = frame;
					nextFrame = frame;
					bl = true;
				}
				if(lastFrame == null && frame.frame < (this.frame + tickDelta)) {
					lastFrame = frame;
				} else {
					if(lastFrame != null && frame.frame > lastFrame.frame && frame.frame < (this.frame + tickDelta)) {
						lastFrame = frame;
					}
				}
				if(nextFrame == null && frame.frame > (this.frame + tickDelta)) {
					nextFrame = frame;
				} else {
					if(nextFrame != null && frame.frame < nextFrame.frame && frame.frame > (this.frame + tickDelta)) {
						nextFrame = frame;
					}
				}
			}
			assert lastFrame != null;
			if(nextFrame == null) {
				nextFrame = lastFrame;
			}
			return getPivot(lastFrame, nextFrame, tickDelta, bl);
		}
		return super.getPivot(query, tickDelta);
	}

	protected abstract Vec3f getCodePivot(String query, float tickDelta);

	@Override
	public Vec3f getOffset(String query, float tickDelta) {
		if(stage == 1) {
			return this.getCodeOffset(query, tickDelta);
		} else if(stage == 2) {
			if(!endKeyframes.containsKey(query)) {
				return Vec3f.ZERO;
			}
			Keyframe lastFrame = null;
			Keyframe nextFrame = null;
			boolean bl = false;
			for(Keyframe frame : endKeyframes.get(query)) {
				if(frame.frame == (this.frame + tickDelta)) {
					lastFrame = frame;
					nextFrame = frame;
					bl = true;
				}
				if(lastFrame == null && frame.frame < (this.frame + tickDelta)) {
					lastFrame = frame;
				} else {
					if(lastFrame != null && frame.frame > lastFrame.frame && frame.frame < (this.frame + tickDelta)) {
						lastFrame = frame;
					}
				}
				if(nextFrame == null && frame.frame > (this.frame + tickDelta)) {
					nextFrame = frame;
				} else {
					if(nextFrame != null && frame.frame < nextFrame.frame && frame.frame > (this.frame + tickDelta)) {
						nextFrame = frame;
					}
				}
			}
			assert lastFrame != null;
			if(nextFrame == null) {
				nextFrame = lastFrame;
			}
			return getOffset(lastFrame, nextFrame, tickDelta, bl);
		}
		return super.getOffset(query, tickDelta);
	}
	protected abstract Vec3f getCodeOffset(String query, float tickDelta);

	@Override
	public void setModelAngles(PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerEntity player, float tickDelta) {
		switch(stage) {
			case 0 -> super.setModelAngles(model, player, tickDelta);
			case 1 -> this.setCodeModelAngles(model, player, tickDelta);
			default -> {
				for(String part : endKeyframes.keySet()) {
					Keyframe lastFrame = null;
					Keyframe nextFrame = null;
					boolean bl = false;
					for(Keyframe frame : endKeyframes.get(part)) {
						if(frame.frame == (this.frame + tickDelta)) {
							lastFrame = frame;
							nextFrame = frame;
							bl = true;
						}
						if(lastFrame == null && frame.frame < (this.frame + tickDelta)) {
							lastFrame = frame;
						} else {
							if(lastFrame != null && frame.frame > lastFrame.frame && frame.frame < (this.frame + tickDelta)) {
								lastFrame = frame;
							}
						}
						if(nextFrame == null && frame.frame > (this.frame + tickDelta)) {
							nextFrame = frame;
						} else {
							if(nextFrame != null && frame.frame < nextFrame.frame && frame.frame > (this.frame + tickDelta)) {
								nextFrame = frame;
							}
						}
					}
					assert lastFrame != null;
					if(nextFrame == null) {
						nextFrame = lastFrame;
					}
					switch(part) {
						case "head" -> {
							setPartAngles(model.head, lastFrame, nextFrame, tickDelta, bl);
						}
						case "body" -> {
							setPartAngles(model.body, lastFrame, nextFrame, tickDelta, bl);
						}
						case "right_arm" -> {
							setPartAngles(model.rightArm, lastFrame, nextFrame, tickDelta, bl);
						}
						case "left_arm" -> {
							setPartAngles(model.leftArm, lastFrame, nextFrame, tickDelta, bl);
						}
						case "left_leg" -> {
							setPartAngles(model.leftLeg, lastFrame, nextFrame, tickDelta, bl);
						}
						case "right_leg" -> {
							setPartAngles(model.rightLeg, lastFrame, nextFrame, tickDelta, bl);
						}
						default -> {}
					}
				}
			}
		}
	}
	protected abstract void setCodeModelAngles(PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerEntity player, float tickDelta);

	protected abstract void codeTick();
	public abstract boolean shouldEnd();
}
