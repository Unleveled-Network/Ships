/*******************************************************************************
 * Copyright (c) 2014 jeff.
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
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cuchaz.ships.ShipClipboard;
import cuchaz.ships.ShipWorld;
import cuchaz.ships.Ships;
import cuchaz.ships.items.ItemProjector;
import cuchaz.ships.persistence.PersistenceException;

public class PacketPlaceProjector extends Packet<PacketPlaceProjector> {
	
	private String m_encodedBlocks;
	private int m_x;
	private int m_y;
	private int m_z;
	
	public PacketPlaceProjector() {
		// for registration
	}
	
	public PacketPlaceProjector(String encodedBlocks, int x, int y, int z) {
		m_encodedBlocks = encodedBlocks;
		m_x = x;
		m_y = y;
		m_z = z;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, m_encodedBlocks);
		buf.writeInt(m_x);
		buf.writeInt(m_y);
		buf.writeInt(m_z);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		m_encodedBlocks = ByteBufUtils.readUTF8String(buf);
		m_x = buf.readInt();
		m_y = buf.readInt();
		m_z = buf.readInt();
	}
	
	// boilerplate code is annoying...
	@Override
	public IMessageHandler<PacketPlaceProjector,IMessage> getServerHandler() {
		return new IMessageHandler<PacketPlaceProjector,IMessage>() {
			
			@Override
			public IMessage onMessage(PacketPlaceProjector message, MessageContext ctx) {
				return message.onReceivedServer(ctx.getServerHandler());
			}
		};
	}
	
	private IMessage onReceivedServer(NetHandlerPlayServer netServer) {
		if (m_encodedBlocks == null) {
			return null;
		}
		
		// place the projector
		try {
			ShipWorld shipWorld = ShipClipboard.createShipWorld(netServer.playerEntity.worldObj, m_encodedBlocks);
			ItemProjector.placeProjector(netServer.playerEntity.worldObj, m_x, m_y, m_z, shipWorld);
			
			// if the player is holding a projector and we're not in creative mode, use it
			if (!netServer.playerEntity.capabilities.isCreativeMode) {
				ItemStack heldItem = netServer.playerEntity.getHeldItem();
				if (heldItem.getItem() == Ships.m_itemProjector) {
					heldItem.stackSize--;
					if (heldItem.stackSize <= 0) {
						netServer.playerEntity.inventory.mainInventory[netServer.playerEntity.inventory.currentItem] = null;
						MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(netServer.playerEntity, heldItem));
					}
				}
			}
		} catch (PersistenceException ex) {
			Ships.logger.error(ex, "Unable to place ship projector!");
		}
		
		return null;
	}
}
