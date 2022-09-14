package snapps.relics.items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;


public class SuperHeavyBow extends BowItem{
    public SuperHeavyBow(Item.Settings settings){super(settings);}

    float maxSeconds = 1.0f;

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            boolean hasInfiniteArrows = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = playerEntity.getArrowType(stack);
            if (!itemStack.isEmpty() || hasInfiniteArrows) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }

                int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
                float pullProgress = coolerGetPullProgress(useTime);
                if (!((double)pullProgress < 0.1)) {
                    boolean hasAvailableArrow = hasInfiniteArrows && itemStack.isOf(Items.ARROW);
                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);
                        persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, pullProgress * 3.0F, 1.0F);
                        if (pullProgress == maxSeconds) {
                            persistentProjectileEntity.setCritical(true);
                        }

                        int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (powerLevel > 0) {
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double)powerLevel * 0.5 + 0.5);
                        }

                        int KBLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                        if (KBLevel > 2) {
                            persistentProjectileEntity.setPunch(KBLevel);
                        }

                        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                            persistentProjectileEntity.setOnFireFor(200);
                        }

                        stack.damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(playerEntity.getActiveHand());
                        });
                        if (hasAvailableArrow || playerEntity.getAbilities().creativeMode && (itemStack.isOf(Items.SPECTRAL_ARROW) || itemStack.isOf(Items.TIPPED_ARROW))) {
                            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(persistentProjectileEntity);
                    }

                    world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);
                    if (!hasAvailableArrow && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    public float coolerGetPullProgress(int useTicks) {
        float sec = (float)useTicks / 20.0F;
        float pullBackSpeed = 0.25f;
        float progress = ((sec * sec + sec * 2.0F) / 3.0F) * pullBackSpeed;


        if (progress > maxSeconds) {
            progress = maxSeconds;
        }
        return progress;
    }
}