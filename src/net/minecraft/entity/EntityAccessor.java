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
package net.minecraft.entity;

public class EntityAccessor
{
	// access protected methods of classes by package-injection
	public static void updateFallState( Entity entity, double dy, boolean isOnGround )
	{
		entity.updateFallState( dy, isOnGround );
	}
}
