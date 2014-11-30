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
package cuchaz.ships.gui;

import static cuchaz.ships.gui.GuiSettings.LeftMargin;
import static cuchaz.ships.gui.GuiSettings.TopMargin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import cpw.mods.fml.common.network.PacketDispatcher;
import cuchaz.modsShared.blocks.BlockArray;
import cuchaz.modsShared.blocks.BlockSide;
import cuchaz.ships.ShipLauncher;
import cuchaz.ships.ShipLauncher.LaunchFlag;
import cuchaz.ships.packets.PacketLaunchShip;
import cuchaz.ships.render.RenderShip2D;

public class GuiShipLaunch extends GuiShip
{
	private ShipLauncher m_shipLauncher;
	private GuiButton m_buttonLaunchShip;
	private BlockSide m_shipSide;
	private GuiButton m_buttonRotateLeft;
	private GuiButton m_buttonRotateRight;
	
	public GuiShipLaunch( Container container, ShipLauncher shipLauncher )
	{
		super( container );
		
		m_shipLauncher = shipLauncher;
		
		m_buttonLaunchShip = null;
		m_shipSide = m_shipLauncher.getShipSide();
		m_buttonRotateLeft = null;
		m_buttonRotateRight = null;
	}
	
	@Override
	@SuppressWarnings( "unchecked" )
	public void initGui( )
	{
		super.initGui();
		
		// add the launch button
		m_buttonLaunchShip = new GuiButton( 
			0,
			guiLeft + LeftMargin,
			guiTop + ySize - TopMargin - 20,
			80,
			20,
			GuiString.ShipLaunch.getLocalizedText()
		);
		m_buttonLaunchShip.enabled = m_shipLauncher.isLaunchable();
		buttonList.add( m_buttonLaunchShip );
		
		// add the rotate buttons
		m_buttonRotateLeft = new GuiButton( 
			1,
			guiLeft + xSize - LeftMargin - 20 - 20 - 10,
			guiTop + ySize - TopMargin - 20,
			20,
			20,
			"<"
		);
		buttonList.add( m_buttonRotateLeft );
		m_buttonRotateRight = new GuiButton( 
			2,
			guiLeft + xSize - LeftMargin - 20,
			guiTop + ySize - TopMargin - 20,
			20,
			20,
			">"
		);
		buttonList.add( m_buttonRotateRight );
	}
	
	@Override
	protected void actionPerformed( GuiButton button )
	{
		if( button.id == m_buttonLaunchShip.id )
		{
			// tell the server to spawn a ship
			PacketLaunchShip packet = new PacketLaunchShip( m_shipLauncher.getShipBlock() );
			PacketDispatcher.sendPacketToServer( packet.getCustomPacket() );
			close();
		}
		else if( button.id == m_buttonRotateLeft.id )
		{
			m_shipSide = m_shipSide.rotateXZCcw( 1 );
		}
		else if( button.id == m_buttonRotateRight.id )
		{
			m_shipSide = m_shipSide.rotateXZCw( 1 );
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
	{
		drawHeaderText( GuiString.ShipConstruction.getLocalizedText(), 0 );
		
		String valueText;
		
		// right number of blocks
		if( m_shipLauncher.getLaunchFlag( LaunchFlag.RightNumberOfBlocks ) )
		{
			valueText = String.format( "%d / %d",
				m_shipLauncher.getNumBlocks(),
				m_shipLauncher.getShipType().getMaxNumBlocks()
			);
		}
		else
		{
			valueText = String.format( "%d%s / %d",
				m_shipLauncher.getNumBlocksChecked(),
				( m_shipLauncher.getNumBlocksChecked() == m_shipLauncher.getNumBlocksToCheck() ? "+" : "" ),
				m_shipLauncher.getShipType().getMaxNumBlocks()
			);
		}
		drawYesNoText(
			GuiString.ShipNumBlocks.getLocalizedText(),
			valueText,
			m_shipLauncher.getLaunchFlag( LaunchFlag.RightNumberOfBlocks ),
			1
		);
		
		// draw the launch flags
		drawYesNoText(
			GuiString.ShipWillItFloat.getLocalizedText(),
			m_shipLauncher.getLaunchFlag( LaunchFlag.WillItFloat ),
			2
		);
		
		// show any sink messages if needed
		if( m_shipLauncher.getSinkWaterHeight() == null )
		{
			// ship is unsinkable
			drawText( GuiString.ShipIsUnsinkable.getLocalizedText(), 3 );
		}
		else if( m_shipLauncher.getEquilibriumWaterHeight() != null && m_shipLauncher.getSinkWaterHeight() - m_shipLauncher.getEquilibriumWaterHeight() < 0.5 )
		{
			// ship floats close to the sink line
			drawText( GuiString.ShipFloatsCloseToSinkLine.getLocalizedText(), 3, NoColor );
		}
		
		// draw the ship and show the water height
		if( m_shipSide != null )
		{
			BlockArray envelope = m_shipLauncher.getShipEnvelope( m_shipSide );
			int x = LeftMargin;
			int y = getLineY( 3 );
			int width = xSize - LeftMargin*2;
			int height = 96;
			RenderShip2D.drawWater(
				envelope,
				m_shipLauncher.getEquilibriumWaterHeight(),
				m_shipLauncher.getSinkWaterHeight(),
				x, y, zLevel, width, height
			);
			RenderShip2D.drawShip(
				envelope,
				m_shipSide,
				m_shipLauncher.getShipWorld(),
				x, y, zLevel, width, height
			);
		}
	}
}
