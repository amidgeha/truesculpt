package truesculpt.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class PickHighlight
{
	private FloatBuffer mColorBuffer;
	private ShortBuffer mIndexBuffer;
	private FloatBuffer mVertexBuffer;

	private float mX = 0.0f;
	private float mY = 0.0f;
	private float mZ = 0.0f;

	public PickHighlight()
	{
		float small = 0.1f;
		float one = 1.0f;
		float vertices[] = { -small, -small, -small, small, -small, -small, small, small, -small, -small, small, -small, -small, -small, small, small, -small, small, small, small, small, -small, small, small, };
		float colors[] = { one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, };
		short indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

		// Buffers to be passed to gl*Pointer() functions
		// must be direct, i.e., they must be placed on the
		// native heap where the garbage collector cannot
		// move them.
		//
		// Buffers with multi-byte datatypes (e.g., short, int, float)
		// must have their byte order set to native order

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glPushMatrix();
		gl.glTranslatef(mX, mY, mZ);

		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);

		gl.glPopMatrix();

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}

	public void setPickHighlightPosition(float[] pt)
	{
		mX = pt[0];
		mY = pt[1];
		mZ = pt[2];
	}
}
