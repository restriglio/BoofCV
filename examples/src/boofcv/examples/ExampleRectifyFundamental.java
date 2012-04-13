/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.examples;

import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.geo.AssociatedPair;
import boofcv.alg.geo.RectifyImageOps;
import boofcv.alg.geo.rectify.RectifyFundamental;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.image.ShowImages;
import boofcv.gui.stereo.RectifiedPairPanel;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;
import org.ejml.data.DenseMatrix64F;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class ExampleRectifyFundamental {

	private ImageFloat32 left;
	private ImageFloat32 right;

	private DenseMatrix64F F;
	private List<AssociatedPair> matches;


	public static void rectify( DenseMatrix64F F , List<AssociatedPair> inliers ,
								BufferedImage origLeft , BufferedImage origRight ) {
		// distorted images
		MultiSpectral<ImageFloat32> distLeft = ConvertBufferedImage.convertFromMulti(origLeft, null, ImageFloat32.class);
		MultiSpectral<ImageFloat32> distRight = ConvertBufferedImage.convertFromMulti(origRight, null, ImageFloat32.class);

		// storage for rectified images
		MultiSpectral<ImageFloat32> rectLeft = new MultiSpectral<ImageFloat32>(ImageFloat32.class,
				distLeft.getWidth(),distLeft.getHeight(),distLeft.getNumBands());
		MultiSpectral<ImageFloat32> rectRight = new MultiSpectral<ImageFloat32>(ImageFloat32.class,
				distRight.getWidth(),distRight.getHeight(),distRight.getNumBands());

		// Compute rectification
		RectifyFundamental rectifyAlg = RectifyImageOps.createFundamental();

		rectifyAlg.process(F,inliers,origLeft.getWidth(),origLeft.getHeight());

		// rectification matrix for each image
		DenseMatrix64F rect1 = rectifyAlg.getRect1();
		DenseMatrix64F rect2 = rectifyAlg.getRect2();

		// Adjust the rectification to make the view area more useful
		RectifyImageOps.fullViewLeft(origLeft.getWidth(),origLeft.getHeight(), true , rect1, rect2 );
//		RectifyImageOps.allInsideLeft(param.left, leftHanded, rect1, rect2, rectK);

		rect1.print();
		rect2.print();

		// undistorted and rectify images
		ImageDistort<ImageFloat32> imageDistortLeft =
				RectifyImageOps.rectifyImage(origLeft.getHeight(),true, rect1,ImageFloat32.class);
		ImageDistort<ImageFloat32> imageDistortRight =
				RectifyImageOps.rectifyImage(origLeft.getHeight(),true, rect2,ImageFloat32.class);

		DistortImageOps.distortMS(distLeft, rectLeft, imageDistortLeft);
		DistortImageOps.distortMS(distRight, rectRight, imageDistortRight);

		// convert for output
		BufferedImage outLeft = ConvertBufferedImage.convertTo(rectLeft,null);
		BufferedImage outRight = ConvertBufferedImage.convertTo(rectRight, null);

		// show results and draw a horizontal line where the user clicks to see rectification easier
		ShowImages.showWindow(new RectifiedPairPanel(true, origLeft, origRight), "Original");
		ShowImages.showWindow(new RectifiedPairPanel(true,outLeft,outRight),"Rectified");


	}

	public static void main( String args[] ) {

		String dir = "../data/evaluation/structure/";

		BufferedImage imageA = UtilImageIO.loadImage(dir + "cyto02.jpg");
		BufferedImage imageB = UtilImageIO.loadImage(dir + "cyto05.jpg");

		List<AssociatedPair> matches = ExampleFundamentalMatrix.computeMatches(imageA,imageB);

		for( AssociatedPair p : matches ) {
			p.currLoc.y = imageA.getHeight() - p.currLoc.y-1;
			p.keyLoc.y = imageA.getHeight() - p.keyLoc.y-1;
		}

		List<AssociatedPair> inliers = new ArrayList<AssociatedPair>();
		DenseMatrix64F F = ExampleFundamentalMatrix.robustFundamental(matches,inliers);

		for( AssociatedPair p : inliers ) {
			p.currLoc.y = imageA.getHeight() - p.currLoc.y-1;
			p.keyLoc.y = imageA.getHeight() - p.keyLoc.y-1;
		}

		rectify(F,inliers,imageA,imageB);
	}

}