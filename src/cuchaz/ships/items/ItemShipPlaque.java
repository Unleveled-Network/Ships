/*******************************************************************************
 * Copyright (c) 2014 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jeff Martin - initial API and implementation
 ******************************************************************************/
package cuchaz.ships.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cuchaz.modsShared.Environment;
import cuchaz.modsShared.blocks.BlockSide;
import cuchaz.ships.EntityShipPlaque;
import cuchaz.ships.EntitySupporterPlaque;
import cuchaz.ships.Supporters;
import cuchaz.ships.gui.GuiString;

public class ItemShipPlaque extends ItemHangingEntity {
	
	private static final int MinRank = 4;
	
	public ItemShipPlaque() {
		super(EntitySupporterPlaque.class);
		
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabDecorations);
		setUnlocalizedName("cuchaz.ships.shipPlaque");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("ships:shipPlaque");
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int sideId, float xHit, float yHit, float zHit) {
		// is the player a supporter?
		if (!canUse(player)) {
			if (Environment.isClient()) {
				player.addChatMessage(new ChatComponentTranslation(GuiString.NotASupporter.getLocalizedText()));
			}
			return false;
		}
		int supporterId = Supporters.getId(player.getCommandSenderName());
		
		// was the plaque placed on the top or bottom of a block?
		BlockSide side = BlockSide.getById(sideId);
		if (side == BlockSide.Bottom || side == BlockSide.Top) {
			return false;
		}
		
		// create the entity
		EntityHanging entity = new EntityShipPlaque(world, supporterId, x, y, z, Direction.facingToDirection[sideId]);
		if (entity.onValidSurface()) {
			if (!world.isRemote) {
				world.spawnEntityInWorld(entity);
			}
			
			// use the item
			--itemStack.stackSize;
			
			return true;
		}
		
		return false;
	}
	
	public static boolean canUse(EntityPlayer player) {
		int supporterId = Supporters.getId(player.getCommandSenderName());
		if (supporterId != Supporters.InvalidSupporterId) {
			// does the player meet the min rank?
			if (Supporters.getRank(supporterId) >= MinRank) {
				return true;
			}
		}
		return false;
	}
}
