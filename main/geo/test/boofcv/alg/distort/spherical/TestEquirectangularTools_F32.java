/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.distort.spherical;

import georegression.misc.GrlConstants;
import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Vector3D_F32;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestEquirectangularTools_F32 {

	Random rand = new Random(234);
	int width = 300;
	int height = 250;

	/**
	 * Test converting back and forth between equirectangular coordinates and lat-lon using different
	 * centers
	 */
	@Test
	public void equiToLonlat_reverse() {

		EquirectangularTools_F32 tools = new EquirectangularTools_F32();
		tools.configure(width,height);

		testCoordinate(tools, width/2, height/2);
		testCoordinate(tools, 0, height/2);
		testCoordinate(tools, width-1, height/2);
		testCoordinate(tools, width/2, 0);
		testCoordinate(tools, width/2, height-1);

	}

	private void testCoordinate(EquirectangularTools_F32 tools, float x , float y) {
		Point2D_F32 ll = new Point2D_F32();
		Point2D_F32 r = new Point2D_F32();

		tools.equiToLonlat(x,y,ll);
		tools.lonlatToEqui(ll.x,ll.y,r);

		assertEquals(x,r.x, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(y,r.y, GrlConstants.FLOAT_TEST_TOL);
	}

	/**
	 * Test one very simple case with a known answer
	 */
	@Test
	public void equiToNorm() {
		EquirectangularTools_F32 tools = new EquirectangularTools_F32();

		tools.configure(300,250);

		Vector3D_F32 found = new Vector3D_F32();
		tools.equiToNorm(300.0f/2.0f, 250.0f/2.0f, found);

		assertEquals(1.0f,found.x, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(0.0f,found.y, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(0.0f,found.z, GrlConstants.FLOAT_TEST_TOL);
	}

	@Test
	public void equiToNorm_reverse() {

		EquirectangularTools_F32 tools = new EquirectangularTools_F32();
		tools.configure(width,height);

		equiToNorm_reverse(tools, width/2, height/2);
		equiToNorm_reverse(tools, 0, height/2);
		equiToNorm_reverse(tools, width-1, height/2);
		equiToNorm_reverse(tools, width/2, 0);
		equiToNorm_reverse(tools, width/2, height-1);

		for (int i = 0; i < 100; i++) {
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);

			equiToNorm_reverse(tools,x,y);
		}

	}

	private void equiToNorm_reverse(EquirectangularTools_F32 tools, float x , float y) {
		Vector3D_F32 n = new Vector3D_F32();
		Point2D_F32 r = new Point2D_F32();

		tools.equiToNorm(x,y,n);
		tools.normToEqui(n.x,n.y,n.z,r);

		assertEquals(x,r.x, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(y,r.y, GrlConstants.FLOAT_TEST_TOL);
	}
}