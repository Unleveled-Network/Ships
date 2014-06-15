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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cuchaz.modsShared.blocks.BlockSide;
import cuchaz.ships.Ships;

public class ItemBerth extends ItemBed
{
	public ItemBerth( int itemId )
	{
		super( itemId );
		
		setMaxStackSize( 1 );
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IconRegister iconRegister )
	{
		itemIcon = iconRegister.registerIcon( "ships:berth" );
	}
	
	@Override
	public boolean onItemUse( ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ )
	{
		// ignore on clients
		if( world.isRemote )
		{
			return true;
		}
		
		// ignore unless placed on top of a block
		if( side != BlockSide.Top.getId() )
		{
			return false;
		}
		
		// decide where to place the two berth blocks
		BlockSide placementSide = BlockSide.getByYaw( player.rotationYaw ).getOppositeSide();
		int meta = placementSide.getXZOffset();
		int dx = BlockBed.footBlockToHeadBlockMap[meta][0];
		int dz = BlockBed.footBlockToHeadBlockMap[meta][1];
		
		if( isValidForBerthBlock( world, x, y + 1, z, player, side, itemStack )
			&& isValidForBerthBlock( world, x + dx, y + 1, z + dz, player, side, itemStack ) )
		{
			// set the two berth blocks
			world.setBlock( x, y + 1, z, Ships.m_blockBerth.blockID, meta, 3 );
			world.setBlock( x + dx, y + 1, z + dz, Ships.m_blockBerth.blockID, meta + 8, 3 );
			
			// use the item
			itemStack.stackSize--;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean isValidForBerthBlock( World world, int x, int y, int z, EntityPlayer player, int side, ItemStack itemStack )
	{
		return player.canPlayerEdit( x, y, z, side, itemStack )
			&& world.isAirBlock( x, y, z )
			&& world.doesBlockHaveSolidTopSurface( x, y - 1, z );
	}
}
