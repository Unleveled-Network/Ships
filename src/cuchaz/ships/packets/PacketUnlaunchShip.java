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
package cuchaz.ships.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cuchaz.ships.EntityShip;
import cuchaz.ships.ShipLocator;
import cuchaz.ships.ShipUnlauncher;

public class PacketUnlaunchShip extends Packet<PacketUnlaunchShip>
{
	private int m_entityId;
	
	public PacketUnlaunchShip( )
	{
		// for registration
	}
	
	public PacketUnlaunchShip( int entityId )
	{
		m_entityId = entityId;
	}
	
	@Override
	public void toBytes( ByteBuf buf )
	{
		buf.writeInt( m_entityId );
	}
	
	@Override
	public void fromBytes( ByteBuf buf )
	{
		m_entityId = buf.readInt();
	}
	
	// boilerplate code is annoying...
	@Override
	public IMessageHandler<PacketUnlaunchShip,IMessage> getServerHandler( )
	{
		return new IMessageHandler<PacketUnlaunchShip,IMessage>( )
		{
			@Override
			public IMessage onMessage( PacketUnlaunchShip message, MessageContext ctx )
			{
				return message.onReceivedServer( ctx.getServerHandler() );
			}
		};
	}
	
	private IMessage onReceivedServer( NetHandlerPlayServer netServer )
	{
		// get the ship
		EntityShip ship = ShipLocator.getShip( netServer.playerEntity.worldObj, m_entityId );
		if( ship == null )
		{
			return null;
		}
		
		// unlaunch the ship
		ShipUnlauncher unlauncher = new ShipUnlauncher( ship );
		if( unlauncher.isUnlaunchable( true ) )
		{
			unlauncher.unlaunch();
		}
		
		return null;
	}
}
