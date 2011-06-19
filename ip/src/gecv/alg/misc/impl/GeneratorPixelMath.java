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

package gecv.alg.misc.impl;

import gecv.misc.CodeGeneratorUtil;
import gecv.misc.TypeImage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * Generates functions inside of {@link gecv.alg.misc.ImageTestingOps}.
 *
 * @author Peter Abeles
 */
public class GeneratorPixelMath {

	String className = "PixelMath";

	PrintStream out;

	private TypeImage input;

	public GeneratorPixelMath() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
	}

	public void generate() {
		printPreamble();
		printAllSigned();
		printAll();
		out.println("}");
	}

	private void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.misc;\n" +
				"\n" +
				"import gecv.struct.image.*;\n" +
				"\n" +
				"import gecv.alg.InputSanityCheck;\n" +
				"import java.util.Random;\n" +
				"\n" +
				"\n" +
				"/**\n" +
				" * Standard mathematical operations performed on a per-pixel basis or computed across the whole image.\n" +
				" *\n" +
				" * DO NOT MODIFY: Generated by {@link gecv.alg.misc.impl.GeneratorPixelMath}.\n"+
				" *\n"+
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" {\n\n");
	}

	public void printAll() {
		TypeImage types[] = TypeImage.getSpecificTypes();

		for( TypeImage t : types ) {
			input = t;
			printMaxAbs();
			printDivide();
			printMult();
			printPlus();
			printBoundImage();
		}
	}

	public void printAllSigned() {
		TypeImage types[] = TypeImage.getSigned();

		for( TypeImage t : types ) {
			input = t;
			printAbs();
		}
	}

	public void printAbs()
	{
		out.print("\t/**\n" +
				"\t * Sets each pixel in the output image to be the absolute value of the input image.\n" +
				"\t * Both the input and output image can be the same instance.\n" +
				"\t * \n" +
				"\t * @param input The input image. Not modified.\n" +
				"\t * @param output Where the absolute value image is written to. Modified.\n" +
				"\t */\n" +
				"\tpublic static void abs( "+ input.getImageName()+" input , "+ input.getImageName()+" output ) {\n" +
				"\n" +
				"\t\tInputSanityCheck.checkSameShape(input,output);\n" +
				"\t\t\n" +
				"\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\tint indexSrc = input.startIndex + y* input.stride;\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride;\n" +
				"\t\t\tint end = indexSrc + input.width;\n" +
				"\n" +
				"\t\t\tfor( ; indexSrc < end; indexSrc++ , indexDst++) {\n" +
				"\t\t\t\toutput.data[indexDst] = "+input.getTypeCastFromSum()+"Math.abs(input.data[indexSrc]);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printMaxAbs() {
		out.print("\t/**\n" +
				"\t * Returns the absolute value of the element with the largest absolute value.\n" +
				"\t * \n" +
				"\t * @param input Input image. Not modified.\n" +
				"\t * @return Largest pixel absolute value.\n" +
				"\t */\n" +
				"\tpublic static "+input.getSumType()+" maxAbs( "+input.getImageName()+" input ) {\n" +
				"\n" +
				"\t\t"+input.getSumType()+" max = 0;\n" +
				"\n" +
				"\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\tint index = input.startIndex + y*input.stride;\n" +
				"\t\t\tint end = index + input.width;\n" +
				"\n" +
				"\t\t\tfor( ; index < end; index++ ) {\n");
		if( input.isSigned() )
			out.print("\t\t\t\t"+input.getSumType()+" v = Math.abs(input.data[index]);\n");
		else
			out.print("\t\t\t\t"+input.getSumType()+" v = input.data[index] "+input.getBitWise()+";\n");
		out.print("\t\t\t\tif( v > max )\n" +
				"\t\t\t\t\tmax = v;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\treturn max;\n" +
				"\t}\n\n");
	}

	public void printDivide() {
		out.print("\t/**\n" +
				"\t * Divides each element by the denominator. Both input and output images can\n" +
				"\t * be the same.\n" +
				"\t *\n" +
				"\t * @param input The input image. Not modified.\n" +
				"\t * @param output The output image. Modified.\n" +
				"\t * @param denominator What each element is divided by.\n" +
				"\t */\n" +
				"\tpublic static void divide( "+input.getImageName()+" input , "+input.getImageName()+" output, "+input.getSumType()+" denominator ) {\n" +
				"\n" +
				"\t\tInputSanityCheck.checkSameShape(input,output);\n" +
				"\n" +
				"\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\tint indexSrc = input.startIndex + y* input.stride;\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride;\n" +
				"\t\t\tint end = indexSrc + input.width;\n" +
				"\n" +
				"\t\t\tfor( ; indexSrc < end; indexSrc++, indexDst++ ) {\n");
		if( input.isInteger() ) {
			String typeCast = input.getTypeCastFromSum();
			if( input.isSigned() )
				out.print("\t\t\t\toutput.data[indexDst] = "+typeCast+"((input.data[indexSrc] "+input.getBitWise()+")/ denominator);\n");
			else
				out.print("\t\t\t\toutput.data[indexDst] = "+typeCast+"(input.data[indexSrc] / denominator);\n");
		} else {
			out.print("\t\t\t\toutput.data[indexDst] = input.data[indexSrc] / denominator;\n");
		}
		out.print("\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printMult() {
		out.print("\t/**\n" +
				"\t * Multiplied each element by the scale factor. Both input and output images can\n" +
				"\t * be the same.\n" +
				"\t *\n" +
				"\t * @param input The input image. Not modified.\n" +
				"\t * @param output The output image. Modified.\n" +
				"\t * @param scale What each element is divided by.\n" +
				"\t */\n" +
				"\tpublic static void multiply( "+input.getImageName()+" input , "+input.getImageName()+" output, "+input.getSumType()+" scale ) {\n" +
				"\n" +
				"\t\tInputSanityCheck.checkSameShape(input,output);\n" +
				"\n" +
				"\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\tint indexSrc = input.startIndex + y* input.stride;\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride;\n" +
				"\t\t\tint end = indexSrc + input.width;\n" +
				"\n" +
				"\t\t\tfor( ; indexSrc < end; indexSrc++, indexDst++ ) {\n");
		if( input.isInteger() ) {
			if( input.isSigned() )
				out.print("\t\t\t\tint val = input.data[indexSrc] * scale;\n");
			else
				out.print("\t\t\t\tint val = (input.data[indexSrc] "+input.getBitWise()+")* scale;\n");
			if( input.getPrimativeType() != int.class) {
				out.print("\t\t\t\tif( val < "+input.getMin()+" ) val = "+input.getMin()+";\n" +
						"\t\t\t\telse if( val > "+input.getMax()+" ) val = "+input.getMax()+";\n");
			}
			out.print("\t\t\t\toutput.data[indexDst] = "+input.getTypeCastFromSum()+"val;\n");
		} else {
			out.print("\t\t\t\toutput.data[indexDst] = input.data[indexSrc] * scale;\n");
		}
		out.print("\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printPlus() {
		out.print("\t/**\n" +
				"\t * Each element has the specified number added to it. Both input and output images can\n" +
				"\t * be the same.\n" +
				"\t *\n" +
				"\t * @param input The input image. Not modified.\n" +
				"\t * @param output The output image. Modified.\n" +
				"\t * @param value What is added to each element.\n" +
				"\t */\n" +
				"\tpublic static void plus( "+input.getImageName()+" input , "+input.getImageName()+" output, "+input.getSumType()+" value ) {\n" +
				"\n" +
				"\t\tInputSanityCheck.checkSameShape(input,output);\n" +
				"\n" +
				"\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\tint indexSrc = input.startIndex + y* input.stride;\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride;\n" +
				"\t\t\tint end = indexSrc + input.width;\n" +
				"\n" +
				"\t\t\tfor( ; indexSrc < end; indexSrc++, indexDst++ ) {\n");
		if( input.isInteger() ) {
			if( input.isSigned() )
				out.print("\t\t\t\tint val = input.data[indexSrc] + value;\n");
			else
				out.print("\t\t\t\tint val = (input.data[indexSrc] "+input.getBitWise()+") + value;\n");
			if( input.getPrimativeType() != int.class) {
				out.print("\t\t\t\tif( val < "+input.getMin()+" ) val = "+input.getMin()+";\n" +
						"\t\t\t\telse if( val > "+input.getMax()+" ) val = "+input.getMax()+";\n");
			}
			out.print("\t\t\t\toutput.data[indexDst] = "+input.getTypeCastFromSum()+"val;\n");
		} else {
			out.print("\t\t\t\toutput.data[indexDst] = input.data[indexSrc] + value;\n");
		}
		out.print("\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printBoundImage() {

		String sumType = input.getSumType();

		out.print("\t/**\n" +
				"\t * Bounds image pixels to be between these two values\n" +
				"\t * \n" +
				"\t * @param img Image\n" +
				"\t * @param min minimum value.\n" +
				"\t * @param max maximum value.\n" +
				"\t */\n" +
				"\tpublic static void boundImage( "+input.getImageName()+" img , "+sumType+" min , "+sumType+" max ) {\n" +
				"\t\tfinal int h = img.getHeight();\n" +
				"\t\tfinal int w = img.getWidth();\n" +
				"\n" +
				"\t\t"+sumType+" range = max-min;\n" +
				"\n" +
				"\t\t"+input.getDataType()+"[] data = img.data;\n" +
				"\n" +
				"\t\tfor (int y = 0; y < h; y++) {\n" +
				"\t\t\tint index = img.getStartIndex() + y * img.getStride();\n" +
				"\t\t\tfor (int x = 0; x < w; x++,index++) {\n" +
				"\t\t\t\t"+sumType+" value = data[index];\n" +
				"\t\t\t\tif( value < min )\n" +
				"\t\t\t\t\tdata[index] = "+input.getTypeCastFromSum()+"min;\n" +
				"\t\t\t\telse if( value > max )\n" +
				"\t\t\t\t\tdata[index] = "+input.getTypeCastFromSum()+"max;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GeneratorPixelMath gen = new GeneratorPixelMath();
		gen.generate();
	}
}
