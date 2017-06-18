package collision;

import java.util.ArrayList;

import entities.Entity;

public class BroadPhase
{
	public ArrayList<Integer> checkBroadPhaseCollision(Entity entity, ArrayList<Entity> entities)
	{
		ArrayList<Integer> collidingEntities= new ArrayList<Integer>();
		for(int i=0; i<entities.size(); i++)
		{
			if(entities.get(i)!=entity)
			{
				if(colliding(entity.getAabb(),entities.get(i).getAabb()))
				{
					collidingEntities.add(i);
				}
			}
		}
		return collidingEntities;
	}
	public boolean colliding(AABB aabb1, AABB aabb2)
	{
		
		boolean Xcol=(((aabb1.getXmin()>=aabb2.getXmin())&&(aabb1.getXmin()<=aabb2.getXmax()))||((aabb1.getXmax()>=aabb2.getXmin())&&(aabb1.getXmax()<=aabb2.getXmax())));
		boolean Ycol=(((aabb1.getYmin()>=aabb2.getYmin())&&(aabb1.getYmin()<=aabb2.getYmax()))||((aabb1.getYmax()>=aabb2.getYmin())&&(aabb1.getYmax()<=aabb2.getYmax())));
		boolean Zcol=(((aabb1.getZmin()>=aabb2.getZmin())&&(aabb1.getZmin()<=aabb2.getZmax()))||((aabb1.getZmax()>=aabb2.getZmin())&&(aabb1.getZmax()<=aabb2.getZmax())));
		return Xcol&&Ycol&&Zcol;
	}
}
