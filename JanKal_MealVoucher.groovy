import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.svg.*
import eu.mihosoft.vrl.v3d.CSG
import javafx.scene.text.Font;

File f = ScriptingEngine
	.fileFromGit(
		"https://github.com/JansenSmith/JanKalMealVoucher.git",//git repo URL
		"main",//branch
		"JanKal_MealVoucher.svg"// File from within the Git repo
	)
println "Extruding SVG "+f.getAbsolutePath()
SVGLoad s = new SVGLoad(f.toURI())
println "Layers= "+s.getLayers()
// A map of layers to polygons
HashMap<String,List<Polygon>> polygonsByLayer = s.toPolygons()
// extrude all layers to a map to 10mm thick
HashMap<String,ArrayList<CSG>> csgByLayers = s.extrudeLayers(10)
// The string represents the layer name in Inkscape
def border = s.extrudeLayerToCSG(7,"Border")
CSG voucher = border
def ticket = s.extrudeLayerToCSG(10,"Ticket")
voucher = voucher.union(ticket)
def lines = s.extrudeLayerToCSG(3,"Lines")
lines = lines.movez(7)
voucher = voucher.difference(lines)
def relief = s.extrudeLayerToCSG(3,"Relief")
relief = relief.movez(7)//.hull(relief.toolOffset(2))
voucher = voucher.difference(relief)
def innards = s.extrudeLayerToCSG(3,"Innards")
innards = innards.movez(7)
voucher = voucher.union(innards)

Font ribbonFont = new Font("Arial",  5)
//Font ribbonFont = new Font("Constantia",  5)
Font playfulFont = new Font("Arial",  7)
//Font playfulFont = new Font("MV Boli",  30)

CSG ribbonText = CSG.unionAll(TextExtrude.text((double)3.0,"ONE MEAL",ribbonFont))
	.rotx(180).toZMin().centerx().centery()
	.movex(relief.getCenterX())
	.movey(relief.getCenterY()-3.5)
	.movez(7)
voucher = voucher.union(ribbonText)

CSG enjoyText = CSG.unionAll(TextExtrude.text((double)3.0,"ENJOY A MEAL",playfulFont))
	.rotx(180).toZMin().centerx().toYMax()
	.movex(relief.getCenterX())
	.movey(lines.getMaxY())
	.movez(7)
println(enjoyText.getCenterX())
voucher = voucher.difference(enjoyText)

return voucher.setColor(javafx.scene.paint.Color.MAGENTA)
//return ribbonText