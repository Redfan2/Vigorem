package arathain.vigorem.anim;

import arathain.vigorem.Vigorem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map;

public abstract class Animation {
	public final Map<String, List<Keyframe>> keyframes;
	private final int length;
	public int frame = 0;

	public Animation(int length, Map<String, List<Keyframe>> keyframes) {
		this.length = length;
		this.keyframes = keyframes;
	}

	public boolean shouldRemove() {
		return frame >= length;
	}
	public void serverTick(PlayerEntity player) {}
	public void clientTick(PlayerEntity player) {}

	public void tick() {
		this.frame++;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public boolean canInterrupt() {
		return false;
	}
	public boolean canCancel() {
		return false;
	}
	public Identifier getId() {
		return Vigorem.id("balls");
	}

	public float getMovementMultiplier() {
		return 1;
	}
	public boolean isBlockingMovement() {
		return false;
	}

	public void setModelAngles(PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerEntity player, float tickDelta) {
		for(String part : keyframes.keySet()) {
			Keyframe lastFrame = null;
			Keyframe nextFrame = null;
			boolean bl = false;
			for(Keyframe frame : keyframes.get(part)) {
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
	private void setPartAngles(ModelPart part, Keyframe prev, Keyframe next, float tickDelta, boolean same) {
		if(same) {
			part.setAngles(prev.rotation.getX(),prev.rotation.getY(), prev.rotation.getZ());
			part.setPivot(part.pivotX + prev.translation.getX(), part.pivotY + prev.translation.getY(), part.pivotZ + prev.translation.getZ());
			part.scaleX = 1 + prev.scale.getX();
			part.scaleY = 1 + prev.scale.getY();
			part.scaleZ = 1 + prev.scale.getZ();
			((OffsetModelPart)(Object)part).setOffset(prev.offset.getX(), prev.offset.getY(), prev.offset.getZ());
		} else {
			float percentage = (this.frame + tickDelta - prev.frame) / ((float) next.frame - prev.frame);
			part.setAngles(MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.rotation.getX(), next.rotation.getX()), MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.rotation.getY(), next.rotation.getY()), MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.rotation.getZ(), next.rotation.getZ()));
			part.setPivot(part.pivotX + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.translation.getX(), next.translation.getX()), part.pivotY + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.translation.getY(), next.translation.getY()), part.pivotZ + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.translation.getZ(), next.translation.getZ()));
			part.scaleX = 1 + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.scale.getX(), next.scale.getX());
			part.scaleY = 1 + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.scale.getY(), next.scale.getY());
			part.scaleZ = 1 + MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.scale.getZ(), next.scale.getZ());
			((OffsetModelPart)(Object)part).setOffset(MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.offset.getX(), next.offset.getX()), MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.offset.getY(), next.offset.getY()), MathHelper.lerp(prev.easing.ease(percentage, 0, 1, 1), prev.offset.getZ(), next.offset.getZ()));
		}
	}

}
