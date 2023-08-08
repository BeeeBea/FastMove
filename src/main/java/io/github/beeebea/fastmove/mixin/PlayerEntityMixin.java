package io.github.beeebea.fastmove.mixin;

import io.github.beeebea.fastmove.FastMove;
import io.github.beeebea.fastmove.IFastPlayer;
import io.github.beeebea.fastmove.MoveState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IFastPlayer  {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract boolean isMainPlayer();
    @Shadow protected abstract void updatePose();
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private PlayerAbilities abilities;
    @Shadow protected HungerManager hungerManager;
    @Unique private MoveState moveState = MoveState.NONE;
    @Unique MoveState lastMoveState = MoveState.NONE;
    @Unique private Vec3d bonusVelocity = Vec3d.ZERO;
    @Unique private int rollTickCounter = 0;
    @Unique private boolean jumpInput = false;
    @Unique  private boolean lastSneakingState = false;
    @Unique private boolean lastJumpInput = false;
    @Unique private boolean lastSprintingState = false;
    @Unique private Vec3d lastWallDir = Vec3d.ZERO;
    @Unique private boolean isWallLeft = false;

    @Override
    public MoveState fastmove_getMoveState() {
        return moveState;
    }

    @Override
    public void fastmove_setMoveState(MoveState moveState) {
        this.moveState = moveState;
    }

    @Override
    public void fastmove_setJumpInput(boolean input) {
        if(input == jumpInput) return;
        lastJumpInput = jumpInput;
        jumpInput = input;
    }

    @Unique
    private void updateCurrentMoveState(){
        if(lastMoveState != moveState){
            lastMoveState = moveState;
            if(moveState == MoveState.ROLLING){
                rollTickCounter = 0;
            }
            if(this.isMainPlayer()){
                FastMove.moveStateUpdater.setMoveState((PlayerEntity) (Object) this, moveState);
            }
            FastMove.moveStateUpdater.setAnimationState((PlayerEntity) (Object) this, moveState);
            updatePose();
            calculateDimensions();
        }
    }

    @Unique
    private static Vec3d fastmove_movementInputToVelocity(Vec3d movementInput, double speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        } else {
            Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
            double f = MathHelper.sin(yaw * 0.017453292F);
            double g = MathHelper.cos(yaw * 0.017453292F);
            return new Vec3d(vec3d.x * g - vec3d.z * f, vec3d.y, vec3d.z * g + vec3d.x * f);
        }
    }

    @Unique
    private static Vec3d fastmove_velocityToMovementInput(Vec3d velocity, float yaw){
        double d = velocity.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        float f = MathHelper.sin(yaw * 0.017453292F);
        float g = MathHelper.cos(yaw * 0.017453292F);
        Vec3d unrotatedVec = new Vec3d(
                velocity.x * g + velocity.z * f,
                velocity.y,
                -velocity.x * f + velocity.z * g
        );
        return (unrotatedVec.lengthSquared() > 1.0 ? unrotatedVec.normalize() : unrotatedVec);
    }

    @Unique
    private boolean getWallDirection(){
        var flat = getVelocity().multiply(1,0,1);
        if(flat.lengthSquared() < 0.01) return false;
        flat = flat.normalize();
        var world = getWorld();
        var left = flat.rotateY(-90).multiply(0.5,0,0.5);
        var right = flat.rotateY(90).multiply(0.5,0,0.5);
        var lowerLeftHit = world.raycast(new RaycastContext(getPos().add(0,0.2,0), getPos().add(left), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if(lowerLeftHit.getType() == HitResult.Type.BLOCK){
            var upperLeftHit = world.raycast(new RaycastContext(getPos().add(0,1.5,0), getPos().add(left), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            if(upperLeftHit.getType() == HitResult.Type.BLOCK)
            {
                lastWallDir = getBlockPos().toCenterPos().subtract(lowerLeftHit.getBlockPos().toCenterPos());
                isWallLeft = true;
                return true;
            }
        }
        var lowerRightHit = world.raycast(new RaycastContext(getPos().add(0,0.2,0), getPos().add(right), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if(lowerRightHit.getType() == HitResult.Type.BLOCK){
            var upperRightHit = world.raycast(new RaycastContext(getPos().add(0,1.5,0), getPos().add(right), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            if(upperRightHit.getType() == HitResult.Type.BLOCK)
            {
                lastWallDir = getBlockPos().toCenterPos().subtract(lowerRightHit.getBlockPos().toCenterPos());
                isWallLeft = false;
                return true;
            }
        }
        lastWallDir = Vec3d.ZERO;
        return false;
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void fastmove_getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        var moveState = fastmove_getMoveState();
        if( moveState != null && moveState != MoveState.NONE) cir.setReturnValue(moveState.dimensions);
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    public void fastmove_getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        var moveState = fastmove_getMoveState();
        if( moveState != null && moveState != MoveState.NONE) cir.setReturnValue(moveState.dimensions.height * 0.85f);
    }

    @Inject(method = "tick" , at = @At("HEAD"))
    private void fastmove_tick(CallbackInfo info) {
        if(this.isMainPlayer()) {
            if (abilities.flying) {
                moveState = MoveState.NONE;
                return;
            }
            var bonusDecay = 0.9;
            if (moveState == MoveState.ROLLING) {
                rollTickCounter++;
                if (rollTickCounter >= 10) {
                    rollTickCounter= 0;
                    moveState = lastSneakingState ? MoveState.PRONE : MoveState.NONE;
                }
                setPose(EntityPose.SWIMMING);
                bonusDecay = 0.98;
            }
            if(moveState == MoveState.SLIDING) {
                if(!lastSneakingState){
                    moveState = MoveState.NONE;
                }
            }
            if(moveState == MoveState.PRONE) {
                setPose(EntityPose.SWIMMING);
            }
            var vel = getVelocity();
            var hasWall = getWallDirection();
            if(!isOnGround() && jumpInput && !lastJumpInput && hasWall && vel.y <= 0) {
                moveState = MoveState.WALLRUNNING_LEFT;
            }
            if(moveState == MoveState.WALLRUNNING_LEFT || moveState == MoveState.WALLRUNNING_RIGHT) {
                if(!hasWall || isOnGround()) {
                    moveState = MoveState.NONE;
                } else {
                    setSprinting(true);
                    var flatVel = vel.multiply(1,0,1);
                    var wallVel = isWallLeft ? flatVel.normalize().rotateY(90) : flatVel.normalize().rotateY(-90);
                    moveState = !isWallLeft ? MoveState.WALLRUNNING_LEFT : MoveState.WALLRUNNING_RIGHT;
                    if(fastmove_velocityToMovementInput(flatVel, getYaw()).dotProduct(lastWallDir) < 0) {
                        addVelocity(wallVel.multiply(-0.1,0,-0.1));
                    }
                    addVelocity(new Vec3d(0,-vel.y,0));
                    bonusVelocity = Vec3d.ZERO;
                    if(!jumpInput){
                        addVelocity(wallVel.multiply(0.3,0,0.3).add(new Vec3d(0,0.4,0)));
                        moveState = MoveState.NONE;
                    }
                }
            }
            addVelocity(bonusVelocity);
            bonusVelocity = bonusVelocity.multiply(bonusDecay,0,bonusDecay);
        }
        updateCurrentMoveState();
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void fastmove_travel(Vec3d movementInput, CallbackInfo info) {
        if (!isMainPlayer()) return;
        if (abilities.flying) return;
        if(isSneaking()){
            if(!lastSneakingState) {
                if (!isOnGround()) {
                    hungerManager.addExhaustion(50);
                    moveState = MoveState.ROLLING;
                    bonusVelocity = fastmove_movementInputToVelocity(new Vec3d(0, 0, 1), 0.1f, getYaw());
                } else if (isSprinting()) {
                    moveState = MoveState.SLIDING;
                    bonusVelocity = fastmove_movementInputToVelocity(new Vec3d(0,0,1), 0.2f, getYaw());
                }
                setSprinting(true);
            }
        }else{
            if(moveState == MoveState.PRONE) {
                moveState = MoveState.NONE;
            }
        }
        lastSneakingState = isSneaking();
        lastSprintingState = isSprinting();
    }

    @Inject(method = "adjustMovementForSneaking", at = @At("HEAD"), cancellable = true)
    private void fastmove_adjustMovementForSneaking(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        if (this.isMainPlayer()) {
            if (moveState == MoveState.ROLLING || moveState == MoveState.SLIDING) {
                cir.setReturnValue(movement);
            }
        }
    }

    @Inject(method="jump", at=@At("HEAD"))
    private void fastmove_jump(CallbackInfo info){
        if(this.isMainPlayer()){
            setSprinting(lastSprintingState);
            if(moveState == MoveState.SLIDING || moveState == MoveState.PRONE){
                moveState = MoveState.NONE;
            }
            if(moveState == MoveState.ROLLING){
                moveState = MoveState.PRONE;
            }
        }
    }

    @Inject(method= "damage", at=@At("HEAD"), cancellable = true)
    private void fastmove_damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if(source.getType().deathMessageType() == DeathMessageType.FALL_VARIANTS &&  moveState == MoveState.ROLLING){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
