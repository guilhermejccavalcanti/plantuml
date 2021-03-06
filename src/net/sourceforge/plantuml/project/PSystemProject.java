/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.eps.EpsStrategy;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.project.graphic.GanttDiagramUnused;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.eps.UGraphicEps;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public class PSystemProject extends AbstractPSystem {

	private final Project project = new Project();
	private final Color background = Color.WHITE;
	private final ColorMapper colorMapper = new ColorMapperIdentity();

	public int getNbImages() {
		return 1;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Project)");
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormatOption, long seed)
			throws IOException {
		final GanttDiagramUnused diagram = new GanttDiagramUnused(project);
		final FileFormat fileFormat = fileFormatOption.getFileFormat();
		if (fileFormat == FileFormat.PNG) {
			final BufferedImage im = createImage(diagram);
			PngIO.write(im, os, fileFormatOption.isWithMetadata() ? getMetadata() : null, 96);
		} else if (fileFormat == FileFormat.SVG) {
			final UGraphicSvg svg = new UGraphicSvg(true, new Dimension2DDouble(0, 0), colorMapper,
					StringUtils.getAsHtml(background), false, 1.0, fileFormatOption.getSvgLinkTarget(),
					fileFormatOption.getHoverColor(), seed());
			diagram.draw(svg, 0, 0);
			svg.createXml(os, fileFormatOption.isWithMetadata() ? getMetadata() : null);
		} else if (fileFormat == FileFormat.EPS) {
			final UGraphicEps eps = new UGraphicEps(colorMapper, EpsStrategy.getDefault2());
			diagram.draw(eps, 0, 0);
			os.write(eps.getEPSCode().getBytes());
		} else if (fileFormat == FileFormat.EPS_TEXT) {
			final UGraphicEps eps = new UGraphicEps(colorMapper, EpsStrategy.WITH_MACRO_AND_TEXT);
			diagram.draw(eps, 0, 0);
			os.write(eps.getEPSCode().getBytes());
		} else {
			throw new UnsupportedOperationException();
		}
		return ImageDataSimple.ok();
	}

	private BufferedImage createImage(GanttDiagramUnused diagram) {
		EmptyImageBuilder builder = new EmptyImageBuilder(10, 10, background);
		Graphics2D g2d = builder.getGraphics2D();
		UGraphicG2d ug = new UGraphicG2d(colorMapper, g2d, 1.0);

		final double height = diagram.getHeight(ug.getStringBounder());
		final double width = diagram.getWidth(ug.getStringBounder());

		g2d.dispose();

		builder = new EmptyImageBuilder(width, height, background);
		final BufferedImage im = builder.getBufferedImage();
		g2d = builder.getGraphics2D();

		ug = new UGraphicG2d(colorMapper, g2d, 1.0);
		ug.setBufferedImage(im);
		diagram.draw(ug, 0, 0);
		g2d.dispose();
		return im;
	}

	public final Project getProject() {
		return project;
	}

}
