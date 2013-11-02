package cuchaz.ships.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class EntityMoveAdapter extends ObfuscationAwareAdapter
{
	private final String EntityClassName;
	
	private String m_className;
	
	public EntityMoveAdapter( int api, ClassVisitor cv, boolean isObfuscatedEnvironment )
	{
		super( api, cv, isObfuscatedEnvironment );
		
		m_className = null;
		
		// cache the runtime class names
		EntityClassName = getRuntimeClassName( "net/minecraft/entity/Entity" );
	}
	
	@Override
	public void visit( int version, int access, String name, String signature, String superName, String[] interfaces )
	{
		super.visit( version, access, name, signature, superName, interfaces );
		
		m_className = name;
	}
	
	@Override
	public MethodVisitor visitMethod( int access, final String methodName, String methodDesc, String signature, String[] exceptions )
	{
		return new MethodVisitor( api, cv.visitMethod( access, methodName, methodDesc, signature, exceptions ) )
		{
			@Override
			public void visitMethodInsn( int opcode, String calledOwner, String calledName, String calledDesc )
			{
				// should we transform this method call?
				if( opcode == Opcodes.INVOKEVIRTUAL
					&& calledDesc.equals( "(DDD)V" )
					&& calledName.equals( getRuntimeMethodName( m_className, "moveEntity", "func_70091_d" ) )
					&& InheritanceUtils.extendsClass( calledOwner, EntityClassName ) )
				{
					mv.visitMethodInsn( Opcodes.INVOKESTATIC, ShipIntermediary.Path, "onEntityMove", String.format( "(L%s;DDD)V", EntityClassName ) );
				}
				else
				{
					super.visitMethodInsn( opcode, calledOwner, calledName, calledDesc );
				}
			}
		};
	}
}
