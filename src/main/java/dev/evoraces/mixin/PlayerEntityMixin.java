package dev.evoraces.mixin;

import dev.evoraces.player.PlayerDataHolder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerDataHolder {

    // --- DataTracker key ---
    @Unique
    private static final TrackedData<String> EVORACES_RACE =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    // --- Campo auxiliar para carregamento do NBT (antes do DataTracker estar pronto) ---
    @Unique
    private String evoraces$pendingRace = null;

    // --- Última raça conhecida para detectar mudanças ---
    @Unique
    private String evoraces$lastKnownRace = null;

    // --- Dimensões (anão, fada, gnomo) ---
    @Unique private static final EntityDimensions DWARF_NORMAL = EntityDimensions.fixed(0.5f, 1.1f);
    @Unique private static final EntityDimensions DWARF_CROUCHING = EntityDimensions.changing(0.5f, 0.9f);
    @Unique private static final EntityDimensions FAIRY_NORMAL = EntityDimensions.fixed(0.33f, 0.99f);
    @Unique private static final EntityDimensions FAIRY_CROUCHING = EntityDimensions.changing(0.33f, 0.82f);
    @Unique private static final EntityDimensions GNOME_NORMAL = EntityDimensions.fixed(0.33f, 0.99f);
    @Unique private static final EntityDimensions GNOME_CROUCHING = EntityDimensions.changing(0.33f, 0.82f);

    @Unique
    private PlayerEntity evoraces$self() {
        return (PlayerEntity)(Object)this;
    }

    // --- Inicialização do DataTracker (chamado uma vez, na criação da entidade) ---
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void evoraces$initDataTracker(CallbackInfo ci) {
        // Começa com raça nula (string vazia representa "sem raça")
        evoraces$self().getDataTracker().startTracking(EVORACES_RACE, "default");
    }

    // --- Implementação da interface PlayerDataHolder ---
    @Override
    public String evoraces$getRaceId() {
        String race = evoraces$self().getDataTracker().get(EVORACES_RACE);
        return race.isEmpty() ? null : race;
    }

    @Override
    public void evoraces$setRaceId(String raceId) {
        String newRace = (raceId == null || raceId.isEmpty()) ? "" : raceId;
        evoraces$self().getDataTracker().set(EVORACES_RACE, newRace);
        // Recalcula dimensões se o jogador já estiver no mundo
        PlayerEntity self = evoraces$self();
        if (self.getWorld() != null && !self.getWorld().isClient) {
            self.calculateDimensions();
        }
    }

    // --- Dimensões (getDimensions) ---
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void evoraces$getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        String race = evoraces$getRaceId();
        if (race == null) return; // sem raça → padrão

        switch (race) {
            case "dwarf":
                cir.setReturnValue(pose == EntityPose.CROUCHING ? DWARF_CROUCHING : DWARF_NORMAL);
                break;
            case "fairy":
                cir.setReturnValue(pose == EntityPose.CROUCHING ? FAIRY_CROUCHING : FAIRY_NORMAL);
                break;
            case "gnome":
                cir.setReturnValue(pose == EntityPose.CROUCHING ? GNOME_CROUCHING : GNOME_NORMAL);
                break;
            default:
                return;
        }
        cir.cancel();
    }

    // --- Altura dos olhos (getActiveEyeHeight) ---
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void evoraces$getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        String race = evoraces$getRaceId();
        if (race == null) return;

        float eyeHeight = evoraces$calculateEyeLevel(pose, dimensions);
        cir.setReturnValue(eyeHeight);
        cir.cancel();
    }

    // --- Persistência: escrever no NBT ---
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        String race = evoraces$getRaceId();
        if (race != null) {
            nbt.putString("evoraces_race_id", race);
        }
    }

    // --- Persistência: ler do NBT (sem acessar DataTracker diretamente) ---
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void evoraces$readNbtHead(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("evoraces_race_id")) {
            evoraces$pendingRace = nbt.getString("evoraces_race_id");
        } else {
            evoraces$pendingRace = null;
        }
    }

    // --- Aplicar a raça lida assim que o DataTracker estiver pronto (primeiro tick) ---
    @Inject(method = "tick", at = @At("HEAD"))
    private void evoraces$tick(CallbackInfo ci) {
        PlayerEntity self = evoraces$self();

        // 1. Se há uma raça pendente do NBT, aplica agora (DataTracker já disponível)
        if (evoraces$pendingRace != null) {
            String race = evoraces$pendingRace.isEmpty() ? null : evoraces$pendingRace;
            evoraces$setRaceId(race);
            evoraces$pendingRace = null;
        }

        // 2. Sincroniza step height e recalcula dimensões se a raça mudou
        String currentRace = evoraces$getRaceId();


        // 3. Efeitos físicos por raça
        if (currentRace != null) {
            // Anão na água
            if ("dwarf".equals(currentRace) && self.isTouchingWater()) {
                self.setVelocity(self.getVelocity().multiply(0.6, 0.8, 0.6));
                self.addVelocity(0, -0.01, 0);
            }
            // Fada: queda lenta
            if ("fairy".equals(currentRace) && !self.isOnGround() && !self.isSubmergedInWater()) {
                if (self.getVelocity().y < 0 && !self.isSneaking()) {
                    self.setVelocity(self.getVelocity().multiply(1.0, 0.6, 1.0));
                }
            }
        }
    }

    // --- Cálculo auxiliar da altura dos olhos ---
    @Unique
    private float evoraces$calculateEyeLevel(EntityPose pose, EntityDimensions dimensions) {
        float height = dimensions.height;
        float offset = (pose == EntityPose.CROUCHING) ? height * 0.15f : height * 0.1f;
        return height - offset;
    }
}