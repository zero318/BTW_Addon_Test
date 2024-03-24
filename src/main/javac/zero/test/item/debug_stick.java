package zero.test.item;

import net.minecraft.src.*;

import btw.entity.LightningBoltEntity;
import btw.entity.mob.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import btw.AddonHandler;

import java.util.List;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.INBTBaseMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;

import zero.test.mixin.ISpiderEntityAccessMixins;
import zero.test.mixin.ICowEntityAccessMixins;

#include "..\util.h"
#include "..\feature_flags.h"

#if ENABLE_METADATA_EXTENSION_COMPAT
#define DBG_GET_BLOCK_METADATA(world, x, y, z) ZeroCompatUtil.getBlockFullMetadata(world, x, y, z)
#else
#define DBG_GET_BLOCK_METADATA(world, x, y, z) world.getBlockMetadata(x, y, z)
#endif

// 
#define DEBUG_STICK_WAILA 0
#define DEBUG_STICK_WAILA_LOG 1

#define DEBUG_STICK_CYCLE_META 2

// Block::updateTick
#define DEBUG_STICK_UPDATE_TICK 3

// Block::randomUpdateTick
#define DEBUG_STICK_RANDOM_UPDATE 4

// Block::onNeighborBlockChange
#define DEBUG_STICK_BLOCK_UPDATE 5

#define DEBUG_STICK_ENTITY_STATE 6

#define DEBUG_STICK_META_0 7
#define DEBUG_STICK_META_1 8
#define DEBUG_STICK_META_2 9
#define DEBUG_STICK_META_3 10
#define DEBUG_STICK_META_4 11
#define DEBUG_STICK_META_5 12
#define DEBUG_STICK_META_6 13
#define DEBUG_STICK_META_7 14
#define DEBUG_STICK_META_8 15
#define DEBUG_STICK_META_9 16
#define DEBUG_STICK_META_10 17
#define DEBUG_STICK_META_11 18
#define DEBUG_STICK_META_12 19
#define DEBUG_STICK_META_13 20
#define DEBUG_STICK_META_14 21
#define DEBUG_STICK_META_15 22

#define DEBUG_STICK_COMPARATOR_UPDATE 23
#define DEBUG_STICK_LIGHT_UPDATE 24

// Block::onStruckByLightning
#define DEBUG_STICK_LIGHTNING_STRIKE 25

// Block::convertBlockFromMobSpawner
#define DEBUG_STICK_MOB_SPAWNER_CONVERT 26

// Block::attemptToAffectBlockWithSoul
#define DEBUG_STICK_SOUL_POSSESSION 27

// IBlockMixins::updateShape
#define DEBUG_STICK_SHAPE_UPDATE 28

// Block::onGrazed
#define DEBUG_STICK_GRAZE_BLOCK 29

#define DEBUG_STICK_PRINT_SKY_LIGHT 30
#define DEBUG_STICK_PRINT_BLOCK_LIGHT 31

#define DEBUG_STICK_LIGHT_0 32
#define DEBUG_STICK_LIGHT_1 33
#define DEBUG_STICK_LIGHT_2 34
#define DEBUG_STICK_LIGHT_3 35
#define DEBUG_STICK_LIGHT_4 36
#define DEBUG_STICK_LIGHT_5 37
#define DEBUG_STICK_LIGHT_6 38
#define DEBUG_STICK_LIGHT_7 39
#define DEBUG_STICK_LIGHT_8 40
#define DEBUG_STICK_LIGHT_9 41
#define DEBUG_STICK_LIGHT_10 42
#define DEBUG_STICK_LIGHT_11 43
#define DEBUG_STICK_LIGHT_12 44
#define DEBUG_STICK_LIGHT_13 45
#define DEBUG_STICK_LIGHT_14 46
#define DEBUG_STICK_LIGHT_15 47

#define DEBUG_STICK_TYPE_COUNT 48

#define USE_FAKE_BLOCK_STATE_WAILA 1

public class DebugStick
#if ENABLE_DEBUG_STICKS
extends Item
#endif
{
#if ENABLE_DEBUG_STICKS

    // These entities don't need to actually do anything
    // except have a call to getDisruptsEarthOnGraze work
    static PigEntity fake_pig = new PigEntity(null);
    static SheepEntity fake_sheep = new SheepEntity(null);
    
    // Why did lightning need to have that call to fling entities?
    static public class FakeLightningBolt extends LightningBoltEntity {
        
        public FakeLightningBolt(World world, double x, double z) {
            // Put the coords down in the void to prevent spawning fire
            super(world, x, -5.0D, z);
        }
        
        private void onStrikeBlock(World world, int x, int y, int z) {
        }
    }
        
    static FakeLightningBolt fake_lightning_bolt = null;
    
    static LightningBoltEntity getFakeBolt(Entity target) {
        if (fake_lightning_bolt != null) {
            fake_lightning_bolt.posX = target.posX;
            fake_lightning_bolt.posZ = target.posZ;
            return fake_lightning_bolt;
        }
        return fake_lightning_bolt = new FakeLightningBolt(target.worldObj, target.posX, target.posZ);
    }

    public DebugStick(int itemId) {
        super(itemId);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabAllSearch);
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float clickX, float clickY, float clickZ) {
        if (player.canPlayerEdit(x, y, z, facing, stack)) {
            int blockId;
            Block block = Block.blocksList[blockId = world.getBlockId(x, y, z)];
            if (!BLOCK_IS_AIR(block)) {
                boolean isSprinting = player.isUsingSpecialKey();
                boolean isSneaking = player.isSneaking();
                boolean waila_log = false;
                int playerX = MathHelper.floor_double(player.posX);
                int playerY = MathHelper.floor_double(player.posY);
                int playerZ = MathHelper.floor_double(player.posZ);
                int meta;
                switch (meta = stack.getItemDamage()) {
                    case DEBUG_STICK_LIGHT_0: case DEBUG_STICK_LIGHT_1: case DEBUG_STICK_LIGHT_2: case DEBUG_STICK_LIGHT_3:
                    case DEBUG_STICK_LIGHT_4: case DEBUG_STICK_LIGHT_5: case DEBUG_STICK_LIGHT_6: case DEBUG_STICK_LIGHT_7:
                    case DEBUG_STICK_LIGHT_8: case DEBUG_STICK_LIGHT_9: case DEBUG_STICK_LIGHT_10: case DEBUG_STICK_LIGHT_11:
                    case DEBUG_STICK_LIGHT_12: case DEBUG_STICK_LIGHT_13: case DEBUG_STICK_LIGHT_14: case DEBUG_STICK_LIGHT_15:
#if ENABLE_LIGHT_STICKS
                        if (isSneaking) {
                            x = playerX;
                            y = playerY;
                            z = playerZ;
                        }
                        ((IWorldMixins)world).setLightOverride(x, y, z, meta - DEBUG_STICK_LIGHT_0);
                        break;
#endif
                    // Sticks that don't apply
                    default:
                        return false;
                    case DEBUG_STICK_CYCLE_META:
                        meta = world.getBlockMetadata(x, y, z) + DEBUG_STICK_META_0 + (isSneaking ? -1 : 1) & 0xF;
                    case DEBUG_STICK_META_0: case DEBUG_STICK_META_1: case DEBUG_STICK_META_2: case DEBUG_STICK_META_3:
                    case DEBUG_STICK_META_4: case DEBUG_STICK_META_5: case DEBUG_STICK_META_6: case DEBUG_STICK_META_7:
                    case DEBUG_STICK_META_8: case DEBUG_STICK_META_9: case DEBUG_STICK_META_10: case DEBUG_STICK_META_11:
                    case DEBUG_STICK_META_12: case DEBUG_STICK_META_13: case DEBUG_STICK_META_14: case DEBUG_STICK_META_15:
                        world.setBlockMetadataWithNotify(
                            x, y, z, meta - DEBUG_STICK_META_0,
                            isSprinting
                                ? UPDATE_NEIGHBORS | UPDATE_CLIENTS
                                : UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE
                        );
                        break;
                    case DEBUG_STICK_UPDATE_TICK:
                        if (!world.isRemote) {
                            block.updateTick(world, x, y, z, world.rand);
                        }
                        break;
                    case DEBUG_STICK_RANDOM_UPDATE:
                        if (!world.isRemote) {
                            block.randomUpdateTick(world, x, y, z, world.rand);
                        }
                        break;
                    case DEBUG_STICK_LIGHTNING_STRIKE:
                        if (!world.isRemote) {
                            block.onStruckByLightning(world, x, y, z);
                        }
                        break;
                    case DEBUG_STICK_MOB_SPAWNER_CONVERT:
                        if (!world.isRemote) {
                            block.convertBlockFromMobSpawner(world, x, y, z);
                        }
                        break;
                    case DEBUG_STICK_SOUL_POSSESSION:
                        if (!world.isRemote) {
                            block.attemptToAffectBlockWithSoul(world, x, y, z);
                        }
                        break;
#if ENABLE_DIRECTIONAL_UPDATES
                    case DEBUG_STICK_SHAPE_UPDATE:
                        if (!world.isRemote) {
                            ((IWorldMixins)world).updateFromNeighborShapes(x, y, z, blockId, world.getBlockMetadata(x, y, z));
                        }
                        break;
#endif
                    case DEBUG_STICK_BLOCK_UPDATE:
                        if (!world.isRemote) {
                            world.notifyBlockOfNeighborChange(x, y, z, blockId);
                            if (isSprinting) {
                                world.notifyBlocksOfNeighborChange(x, y, z, blockId);
                            }
                        }
                        break;
                    case DEBUG_STICK_COMPARATOR_UPDATE:
                        if (!world.isRemote) {
                            world.func_96440_m(x, y, z, blockId);
                        }
                        break;
                    case DEBUG_STICK_LIGHT_UPDATE:
                        world.updateAllLightTypes(x, y, z);
                        break;
                    case DEBUG_STICK_GRAZE_BLOCK:
                        if (!world.isRemote) {
                            block.onGrazed(
                                world, x, y, z,
                                isSprinting ? fake_pig : fake_sheep
                            );
                        }
                        break;
                    case DEBUG_STICK_PRINT_SKY_LIGHT: {
                        if (isSneaking) {
                            x = playerX;
                            y = playerY;
                            z = playerZ;
                        }
                        String text = "" + world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
                        if (isSprinting) {
                            AddonHandler.logMessage((world.isRemote ? "C: " : "S: ") + text);
                        }
                        if (!world.isRemote) {
                            player.sendChatToPlayer(text);
                        }
                        break;
                    }
                    case DEBUG_STICK_PRINT_BLOCK_LIGHT: {
                        if (isSneaking) {
                            x = playerX;
                            y = playerY;
                            z = playerZ;
                        }
                        String text = "" + world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
                        if (isSprinting) {
                            AddonHandler.logMessage((world.isRemote ? "C: " : "S: ") + text);
                        }
                        if (!world.isRemote) {
                            player.sendChatToPlayer(text);
                        }
                        break;
                    }
                    case DEBUG_STICK_WAILA_LOG:
                        waila_log = true;
                    case DEBUG_STICK_WAILA:
                        if (!world.isRemote) {
                            TileEntity tileEntity = null;
                            boolean printTile = isSprinting && (tileEntity = world.getBlockTileEntity(x, y, z)) != null;
                            if (isSneaking && !printTile) {
                                x = playerX;
                                y = playerY;
                                z = playerZ;
#if USE_FAKE_BLOCK_STATE_WAILA
                                block = Block.blocksList[blockId = world.getBlockId(x, y, z)];
#else
                                blockId = world.getBlockId(x, y, z);
#endif
                            }
                            StringBuilder str = new StringBuilder()
                                .append("\247e") // yellow text
                                .append("Block: ")
                                .append(blockId)
#if USE_FAKE_BLOCK_STATE_WAILA
                                .append('(')
                                .append(block != null ? block.getUnlocalizedName() : "tile.air")
                                .append(")[meta=")
                                .append(DBG_GET_BLOCK_METADATA(world, x, y, z))
                                .append(']');
#else
                                .append(':')
                                .append(DBG_GET_BLOCK_METADATA(world, x, y, z));
#endif
                            
                            if (printTile) {
                                NBTTagCompound tileEntityData = new NBTTagCompound();
                                tileEntity.writeToNBT(tileEntityData);
                                ((INBTBaseMixins)tileEntityData).toSNBT(str);
                            }
                            String text = str.toString();
                            if (waila_log) {
                                AddonHandler.logMessage(text);
                            }
                            player.sendChatToPlayer(text);
                        }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityLiving entity) {
        //boolean waila_log = false;
        switch (stack.getItemDamage()) {
            case DEBUG_STICK_LIGHTNING_STRIKE:
                entity.onStruckByLightning(getFakeBolt(entity));
                return true;
                // Doesn't look like the chat can be sent from here?
            /*
            case DEBUG_STICK_WAILA_LOG:
                waila_log = true;
            case DEBUG_STICK_WAILA: {
                StringBuilder str = new StringBuilder()
                    .append("\247e") // yellow text;
                    .append("Entity: ");
                NBTTagCompound entityData = new NBTTagCompound();
                entity.writeToNBT(entityData);
                ((INBTBaseMixins)tileEntityData).toSNBT(str);
                
                String text = str.toString();
                if (waila_log) {
                    AddonHandler.logMessage(text);
                }
                player.sendChatToPlayer(text);
                return true;
            }
            */
            case DEBUG_STICK_ENTITY_STATE:
                if (!entity.worldObj.isRemote) {
                    if (entity instanceof CowEntity) {
                        ((ICowEntityAccessMixins)entity).callSetGotMilk(
                            BOOL_INVERT(((CowEntity)entity).gotMilk())
                        );
                        return true;
                    }
                    if (entity instanceof ChickenEntity) {
                        ((ChickenEntity)entity).dropItem(Item.egg.itemID, 1);
                        return true;
                    }
                    if (entity instanceof SheepEntity) {
                        ((SheepEntity)entity).setSheared(
                            BOOL_INVERT(((SheepEntity)entity).getSheared())
                        );
                        return true;
                    }
                    if (entity instanceof WolfEntity) {
                        ((WolfEntity)entity).attemptToShit();
                        return true;
                    }
                    if (entity instanceof SpiderEntity) {
                        ((ISpiderEntityAccessMixins)entity).setTimeToNextWeb(
                            ((SpiderEntity)entity).hasWeb()
                                ? 24000
                                : 0
                        );
                        return true;
                    }
                }
                return false;
            case DEBUG_STICK_SOUL_POSSESSION:
                if (
                    entity instanceof EntityCreature &&
                    ((EntityCreature)entity).getCanCreatureTypeBePossessed()
                ) {
                    ((EntityCreature)entity).onFullPossession();
                    return true;
                }
        }
        return false;
    }
    
    @Override
    public boolean isMultiUsePerClick() {
        return false;
    }
    
    @Override
    public void getSubItems(int itemId, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < DEBUG_STICK_TYPE_COUNT; ++i) {
            list.add(new ItemStack(itemId, 1, i));
        }
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.debug_stick" + stack.getItemDamage();
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.itemIcon = Item.stick.itemIcon;
    }
#endif
}