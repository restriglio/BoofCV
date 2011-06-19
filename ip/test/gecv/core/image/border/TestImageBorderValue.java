/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.core.image.border;

import gecv.core.image.SingleBandImage;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageUInt8;

/**
 * @author Peter Abeles
 */
public class TestImageBorderValue extends GenericImageBorderTests {

	float value = 43;

	@Override
	public ImageBorder_I wrap(ImageUInt8 image) {
		return ImageBorderValue.wrap(image,(int)value);
	}

	@Override
	public ImageBorder_F32 wrap(ImageFloat32 image) {
		return ImageBorderValue.wrap(image,value);
	}

	@Override
	public Number get(SingleBandImage img, int x, int y) {
		if( img.getImage().isInBounds(x,y))
			return img.get(x,y);
		return value;
	}

	@Override
	public void checkBorderSet(int x, int y, Number val,
							SingleBandImage border, SingleBandImage orig) {
		// the original image should not be modified
	}
}
