/*******************************************************************************
 * Copyright (c) 2013 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jeff Martin - initial API and implementation
 ******************************************************************************/
package cuchaz.ships;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import cuchaz.modsShared.BlockSide;
import cuchaz.modsShared.CircleRange;
import cuchaz.modsShared.Util;

public class ShipUnlauncher
{
	public static enum UnlaunchFlag
	{
		AlignedToDirection
		{
			@Override
			public boolean computeValue( ShipUnlauncher unlauncher )
			{
				// the yaw has to be within 10 degrees of zero
				return Math.abs( MathHelper.wrapAngleTo180_float( unlauncher.m_ship.rotationYaw ) ) < 10.0;
			}
		},
		TouchingOnlySeparatorBlocks
		{
			@Override
			public boolean computeValue( ShipUnlauncher unlauncher )
			{
				// if any placed ship block has a neighbor that's not a ship block and not a separator block, the flag fails
				
				// put all the placed blocks into a data structure that has fast lookup
				TreeSet<ChunkCoordinates> placedBlocks = new TreeSet<ChunkCoordinates>();
				placedBlocks.addAll( unlauncher.m_correspondence.values() );
				
				// check for the neighbors of each ship block
				ChunkCoordinates neighborCoords = new ChunkCoordinates( 0, 0, 0 );
				for( ChunkCoordinates coords : placedBlocks )
				{
					// for each neighbor
					for( BlockSide side : BlockSide.values() )
					{
						neighborCoords.posX = coords.posX + side.getDx();
						neighborCoords.posY = coords.posY + side.getDy();
						neighborCoords.posZ = coords.posZ + side.getDz();
						
						// skip ship blocks
						if( placedBlocks.contains( neighborCoords ) )
						{
							continue;
						}
						
						if( !MaterialProperties.isSeparatorBlock( getBlock( unlauncher.m_ship.worldObj, neighborCoords ) ) )
						{
							return false;
						}
					}
				}
				
				return true;
			}
		};
		
		public abstract boolean computeValue( ShipUnlauncher unlauncher );

		protected Block getBlock( IBlockAccess world, ChunkCoordinates coords )
		{
			return Block.blocksList[world.getBlockId( coords.posX, coords.posY, coords.posZ )];
		}
	}
	
	private EntityShip m_ship;
	private List<Boolean> m_unlaunchFlags;
	private TreeMap<ChunkCoordinates,ChunkCoordinates> m_correspondence;
	private int m_waterSurfaceLevelBlocks;
	
	public ShipUnlauncher( EntityShip ship )
	{
		m_ship = ship;
		
		// compute the block correspondence
		computeCorrespondence();
		
		// compute the unlaunch flags
		m_unlaunchFlags = new ArrayList<Boolean>();
		for( UnlaunchFlag flag : UnlaunchFlag.values() )
		{
			m_unlaunchFlags.add( flag.computeValue( this ) );
		}
	}
	
	private void computeCorrespondence( )
	{
		// get the ship block position
		Vec3 p = Vec3.createVectorHelper( 0, 0, 0 );
		m_ship.blocksToShip( p );
		m_ship.shipToWorld( p );
		ChunkCoordinates shipBlock = new ChunkCoordinates(
			MathHelper.floor_double( p.xCoord + 0.5 ),
			MathHelper.ceiling_double_int( p.yCoord ),
			MathHelper.floor_double( p.zCoord + 0.5 )
		);
		
		// determine the water surface level
		m_waterSurfaceLevelBlocks = m_ship.getWaterHeight() - shipBlock.posY - 1;
		
		// get the set of coords we care about
		TreeSet<ChunkCoordinates> allCoords = new TreeSet<ChunkCoordinates>();
		allCoords.addAll( m_ship.getBlocks().coords() );
		allCoords.addAll( m_ship.getBlocks().getGeometry().getTrappedAir( m_waterSurfaceLevelBlocks ) );
		
		// compute the snap rotation
		double yaw = CircleRange.mapZeroToTwoPi( Math.toRadians( m_ship.rotationYaw ) );
		int rotation = Util.realModulus( (int)( yaw/Math.PI*2 + 0.5 ), 4 );
		int cos = new int[] { 1, 0, -1, 0 }[rotation];
		int sin = new int[] { 0, 1, 0, -1 }[rotation];
		
		// compute the actual correspondence
		m_correspondence = new TreeMap<ChunkCoordinates,ChunkCoordinates>();
		for( ChunkCoordinates coords : allCoords )
		{
			// rotate the coords
			int x = coords.posX*cos + coords.posZ*sin;
			int z = -coords.posX*sin + coords.posZ*cos;
			ChunkCoordinates worldCoords = new ChunkCoordinates( x, coords.posY, z );
			
			// translate to the world
			worldCoords.posX += shipBlock.posX;
			worldCoords.posY += shipBlock.posY;
			worldCoords.posZ += shipBlock.posZ;
			
			m_correspondence.put( coords, worldCoords );
		}
	}
	
	public boolean getUnlaunchFlag( UnlaunchFlag flag )
	{
		return m_unlaunchFlags.get( flag.ordinal() );
	}
	
	public boolean isUnlaunchable( )
	{
		boolean isValid = true;
		for( UnlaunchFlag flag : UnlaunchFlag.values() )
		{
			isValid = isValid && getUnlaunchFlag( flag );
		}
		return isValid;
	}
	
	public void unlaunch( )
	{
		List<Entity> riders = m_ship.getCollider().getRiders();
		
		// remove the ship entity
		m_ship.setDead();
		
		// restore all the blocks
		m_ship.getBlocks().restoreToWorld( m_ship.worldObj, m_correspondence, m_waterSurfaceLevelBlocks );
		
		// compute the unlaunch delta
		Vec3 sourceShipBlock = Vec3.createVectorHelper( 0, 0, 0 );
		m_ship.blocksToShip( sourceShipBlock );
		m_ship.shipToWorld( sourceShipBlock );
		ChunkCoordinates targetShipBlock = m_correspondence.get( new ChunkCoordinates( 0, 0, 0 ) );
		double dx = targetShipBlock.posX - sourceShipBlock.xCoord;
		double dy = targetShipBlock.posY - sourceShipBlock.yCoord;
		double dz = targetShipBlock.posZ - sourceShipBlock.zCoord;
		
		// move all riders
		for( Entity entity : riders )
		{
			entity.setPosition( entity.posX + dx, entity.posY + dy, entity.posZ + dz );
		}
	}
	
	public void snapToNearestDirection( )
	{
		// NOTE: math on circles is hard. Use the CircleRange class
		double yaw = CircleRange.mapZeroToTwoPi( Math.toRadians( m_ship.rotationYaw ) );
		double snappedYaw = (int)( yaw/Math.PI*2 + 0.5 )*Math.PI/2.0;
		m_ship.setPositionAndRotation(
			m_ship.posX, m_ship.posY, m_ship.posZ,
			(float)snappedYaw,
			m_ship.rotationPitch
		);
	}
}
