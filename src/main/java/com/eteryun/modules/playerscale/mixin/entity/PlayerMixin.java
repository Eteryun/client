package com.eteryun.modules.playerscale.mixin.entity;

import com.eteryun.modules.playerscale.extension.IPlayer;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IPlayer {
    @Shadow
    @Final
    private static Map<Pose, EntityDimensions> POSES;

    private Map<Pose, EntityDimensions> MODIFIED_POSES = POSES;

    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getScaleRender() {
        return this.entityData.get(DATA_SCALE);
    }

    @Override
    public void setScaleRender(float scale) {
        this.entityData.set(DATA_SCALE, scale);
    }

    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    private void defineSynchedData(CallbackInfo ci){
        entityData.define(DATA_SCALE, 1F);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci){
        if (nbt.contains("eteryun")) {
            CompoundTag data = nbt.getCompound("eteryun");
            setScaleRender(data.getFloat("scale"));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci){
        if (!nbt.contains("eteryun")) {
            nbt.put("eteryun", new CompoundTag());
        }

        CompoundTag data = nbt.getCompound("eteryun");
        data.putFloat("scale", getScaleRender());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey)
    {
        super.onSyncedDataUpdated(pKey);
        if (DATA_SCALE.equals(pKey)) {
            updateScale();
        }
    }

    private void updateScale(){
        ImmutableMap.Builder<Pose, EntityDimensions> newPosesBuilder = ImmutableMap.<Pose, EntityDimensions>builder();
        for (Pose pose : POSES.keySet()) {
            newPosesBuilder.put(pose, POSES.get(pose).scale(getScaleRender()));
        }
        MODIFIED_POSES = newPosesBuilder.build();
        this.refreshDimensions();
    }

    /**
     * @author Eteryun
     * @reason unknown
     */
    @Overwrite
    public float getStandingEyeHeight(Pose pPose, EntityDimensions pSize)
    {
        switch (pPose)
        {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;

            case CROUCHING:
                return 1.27F * getScaleRender();

            default:
                return 1.62F * getScaleRender();
        }
    }

    /**
     * @author Eteryun
     * @reason unknown
     */
    @Overwrite
    public EntityDimensions getDimensions(Pose pPose)
    {
        return MODIFIED_POSES.getOrDefault(pPose, Player.STANDING_DIMENSIONS);
    }
}
