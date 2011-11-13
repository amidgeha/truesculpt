package truesculpt.tools.sculpting;

import truesculpt.actions.SculptAction;
import truesculpt.main.Managers;
import truesculpt.main.R;
import truesculpt.mesh.RenderFaceGroup;
import truesculpt.mesh.Vertex;
import truesculpt.tools.base.SculptingTool;
import truesculpt.utils.MatrixUtils;

public class FlattenTool extends SculptingTool
{
	private float mfInitDist = -1;
	private boolean mbOrigSet = false;
	private final float[] temp = new float[3];

	public FlattenTool(Managers managers)
	{
		super(managers);
	}

	@Override
	public void Start(float xScreen, float yScreen)
	{
		super.Start(xScreen, yScreen);

		mbOrigSet = false;
	}

	@Override
	protected void Work()
	{
		if (!mbOrigSet)// only after first start
		{
			mfInitDist = MatrixUtils.magnitude(mOrigVertex.Coord);
			mbOrigSet = true;
		}

		for (Vertex vertex : mVerticesRes)
		{
			MatrixUtils.copy(vertex.Coord, VNormal);
			MatrixUtils.normalize(VNormal);
			MatrixUtils.copy(VNormal, VOffset);

			// MatrixUtils.scalarMultiply(VOffset, mfInitDist);
			// MatrixUtils.minus(VOffset, vertex.Coord, VOffset);

			MatrixUtils.minus(vertex.mLastIntersectPt, vertex.Coord, temp);
			float newOffsetFactor = MatrixUtils.dot(temp, VOffset);
			MatrixUtils.scalarMultiply(VOffset, newOffsetFactor);

			MatrixUtils.plus(VOffset, vertex.Coord, VOffset);
			((SculptAction) mAction).AddNewVertexValue(VOffset, vertex);

			// preview
			MatrixUtils.scalarMultiply(VNormal, 1 - vertex.mLastTempSqDistance / mSquareMaxDistance);
			for (RenderFaceGroup renderGroup : mMesh.mRenderGroupList)
			{
				renderGroup.UpdateVertexValue(vertex.Index, VOffset, VNormal);
			}
		}
	}

	@Override
	public String GetName()
	{
		return "Flatten";
	}

	@Override
	public int GetIcon()
	{
		return R.drawable.flatten;
	}

}
