/*******************************************************************************
 * Copyright (c) 2013 jeff.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     jeff - initial API and implementation
 ******************************************************************************/
package cuchaz.ships;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.world.World;
import cuchaz.modsShared.blocks.BlockUtils;
import cuchaz.modsShared.blocks.BlockUtils.UpdateRules;
import cuchaz.modsShared.blocks.Coords;

public class BlockStorage
{
	public int id;
	public int meta;
	
	public BlockStorage( )
	{
		id = 0;
		meta = 0;
	}
	
	public void writeToStream( DataOutputStream out ) throws IOException
	{
		out.writeInt( id );
		out.writeInt( meta );
	}
	
	public void readFromStream( DataInputStream in ) throws IOException
	{
		id = in.readInt();
		meta = in.readInt();
	}
	
	public void readFromWorld( World world, Coords coords )
	{
		id = world.getBlockId( coords.x, coords.y, coords.z );
		meta = world.getBlockMetadata( coords.x, coords.y, coords.z );
	}
	
	public void writeToWorld( World world, Coords coords )
	{
		BlockUtils.changeBlockWithoutNotifyingIt( world, coords.x, coords.y, coords.z, id, meta, UpdateRules.UpdateClients );
	}
}
