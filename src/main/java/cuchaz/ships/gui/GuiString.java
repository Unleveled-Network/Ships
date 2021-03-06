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

import cpw.mods.fml.common.registry.LanguageRegistry;

public enum GuiString
{
	Yes( "Yes" ),
	No( "No" ),
	ShipConstruction( "Ship Construction" ),
	ShipDashboard( "Ship Dashboard" ),
	ShipNumBlocks( "Blocks" ),
	ShipTooLarge( "Ship is too large!" ),
	ShipLaunch( "Launch Ship" ),
	ShipUnlaunch( "Dock Ship" ),
	ShipInOrAboveWater( "Ship in or above water" ),
	ShipHasAirAbove( "Ship has air above" ),
	ShipFoundWaterHeight( "Found water surface" ),
	ShipWillItFloat( "Will it float" ),
	ShipAlignedToDirection( "Aligned to launch direction" ),
	ShipAwayFromBlocks( "Away from solid blocks" ),
	ShipPropulsion( "Propulsion" ),
	NoShipBlock( "Couldn't find a ship nearby!" ),
	InvalidShip( "Ship is invalid!" ),
	ClipboardUsage( "Use the clipboard on a ship block to copy a ship. Use the clipboard on water to paste a ship."  ),
	NoShipWasFoundHere( "No ship was found here!" ),
	CopiedShip( "Copied ship to clipboard." ),
	ErrorCheckLogForDetails( "An Error has occured! Check the Minecraft log for details." ),
	NoShipOnClipboard( "No ship was found on the clipboard!" ),
	NoRoomToPasteShip( "There was no room here to paste the ship. Need room for a %d x %d x %d box." ),
	PastedShip( "Pasted ship to the world." ),
	OnlyCreative( "This only works in creative mode." ),
	NoPropulsion( "No propulsion methods found!" ),
	FoundPropulsion( "Found %s" ),
	ShipUnlaunchOverride( "Override docking check" ),
	ShipUnlaunchOverrideWarning( "Warning: You may not be able to launch your ship after docking here." ),
	ListOfSupporters( "Cuchaz Interactive: Supporters" ),
	NotASupporter( "Become a supporter of Cuchaz Interactive to use this feature. http://www.patreon.com/cuchaz" ),
	EraserUsage( "Use the eraser on a ship block to erase a ship."  ),
	ShipPlaque( "Name your ship" ),
	Done( "Done" ),
	ShipIsUnsinkable( "Ship is un-sinkable!" ),
	ShipFloatsCloseToSinkLine( "Ship is close to sinking!" ),
	Sink( "Sink" ),
	Slept( "Naps are nap-a-licious!" ),
	BerthNotFound( "Berth was not found!" ),
	TryOnStillWater( "Try using this on top of still water" );
	
	private String m_unlocalizedText;
	
	private GuiString( String unlocalizedText )
	{
		m_unlocalizedText = unlocalizedText;
	}
	
	public String getKey( )
	{
		return "cuchaz.ships." + name();
	}
	
	public String getUnlocalizedText( )
	{
		return m_unlocalizedText;
	}
	
	public String getLocalizedText( )
	{
		String text = LanguageRegistry.instance().getStringLocalization( getKey() );
		if( text == null || text.length() <= 0 )
		{
			// no translation available? Just return the unlocalized text.
			text = getUnlocalizedText();
		}
		return text;
	}
}
