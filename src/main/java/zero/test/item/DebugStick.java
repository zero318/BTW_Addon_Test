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
import zero.test.ZeroMetaUtil;
import zero.test.mixin.ISpiderEntityAccessMixins;
import zero.test.mixin.ICowEntityAccessMixins;
// Block piston reactions
// 
// Block::updateTick
// Block::randomUpdateTick
// Block::onNeighborBlockChange
// Block::onStruckByLightning
// Block::convertBlockFromMobSpawner
// Block::attemptToAffectBlockWithSoul
// IBlockMixins::updateShape
// Block::onGrazed
public class DebugStick
extends Item
{
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
            if (!((block)==null)) {
                boolean isSprinting = player.isUsingSpecialKey();
                boolean isSneaking = player.isSneaking();
                boolean waila_log = false;
                int playerX = MathHelper.floor_double(player.posX);
                int playerY = MathHelper.floor_double(player.posY);
                int playerZ = MathHelper.floor_double(player.posZ);
                int meta;
                switch (meta = stack.getItemDamage()) {
                    case 32: case 33: case 34: case 35:
                    case 36: case 37: case 38: case 39:
                    case 40: case 41: case 42: case 43:
                    case 44: case 45: case 46: case 47:
                        if (isSneaking) {
                            x = playerX;
                            y = playerY;
                            z = playerZ;
                        }
                        ((IWorldMixins)world).setLightOverride(x, y, z, meta - 32);
                        break;
                    // Sticks that don't apply
                    default:
                        return false;
                    case 2:
                        meta = world.getBlockMetadata(x, y, z) + 7 + (isSneaking ? -1 : 1) & 0xF;
                    case 7: case 8: case 9: case 10:
                    case 11: case 12: case 13: case 14:
                    case 15: case 16: case 17: case 18:
                    case 19: case 20: case 21: case 22:
                        world.setBlockMetadataWithNotify(
                            x, y, z, meta - 7,
                            isSprinting
                                ? 0x01 | 0x02
                                : 0x02 | 0x10
                        );
                        break;
                    case 3:
                        if (!world.isRemote) {
                            block.updateTick(world, x, y, z, world.rand);
                        }
                        break;
                    case 4:
                        if (!world.isRemote) {
                            block.randomUpdateTick(world, x, y, z, world.rand);
                        }
                        break;
                    case 25:
                        if (!world.isRemote) {
                            block.onStruckByLightning(world, x, y, z);
                        }
                        break;
                    case 26:
                        if (!world.isRemote) {
                            block.convertBlockFromMobSpawner(world, x, y, z);
                        }
                        break;
                    case 27:
                        if (!world.isRemote) {
                            block.attemptToAffectBlockWithSoul(world, x, y, z);
                        }
                        break;
                    case 28:
                        if (!world.isRemote) {
                            ((IWorldMixins)world).updateFromNeighborShapes(x, y, z, blockId, world.getBlockMetadata(x, y, z));
                        }
                        break;
                    case 5:
                        if (!world.isRemote) {
                            world.notifyBlockOfNeighborChange(x, y, z, blockId);
                            if (isSprinting) {
                                world.notifyBlocksOfNeighborChange(x, y, z, blockId);
                            }
                        }
                        break;
                    case 23:
                        if (!world.isRemote) {
                            world.func_96440_m(x, y, z, blockId);
                        }
                        break;
                    case 24:
                        world.updateAllLightTypes(x, y, z);
                        break;
                    case 29:
                        if (!world.isRemote) {
                            block.onGrazed(
                                world, x, y, z,
                                isSprinting ? fake_pig : fake_sheep
                            );
                        }
                        break;
                    case 30: {
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
                    case 31: {
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
                    case 1:
                        waila_log = true;
                    case 0:
                        if (!world.isRemote) {
                            TileEntity tileEntity = null;
                            boolean printTile = isSprinting && (tileEntity = world.getBlockTileEntity(x, y, z)) != null;
                            if (isSneaking && !printTile) {
                                x = playerX;
                                y = playerY;
                                z = playerZ;
                                block = Block.blocksList[blockId = world.getBlockId(x, y, z)];
                            }
                            StringBuilder str = new StringBuilder()
                                .append("\247e") // yellow text
                                .append("Block: ")
                                .append(blockId)
                                .append('(')
                                .append(block != null ? block.getUnlocalizedName() : "tile.air")
                                .append(")[meta=")
                                .append(ZeroMetaUtil.getBlockFullMetadata(world, x, y, z))
                                .append(']');
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
            case 25:
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
            case 6:
                if (!entity.worldObj.isRemote) {
                    if (entity instanceof CowEntity) {
                        ((ICowEntityAccessMixins)entity).callSetGotMilk(
                            ((((CowEntity)entity).gotMilk())^true)
                        );
                        return true;
                    }
                    if (entity instanceof ChickenEntity) {
                        ((ChickenEntity)entity).dropItem(Item.egg.itemID, 1);
                        return true;
                    }
                    if (entity instanceof SheepEntity) {
                        ((SheepEntity)entity).setSheared(
                            ((((SheepEntity)entity).getSheared())^true)
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
            case 27:
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
        for (int i = 0; i < 48; ++i) {
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
}
