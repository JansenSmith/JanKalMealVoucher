import eu.mihosoft.vrl.v3d.svg.*;

import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Extrude;
import eu.mihosoft.vrl.v3d.Polygon

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
// extrude just one layer to 10mm
// The string "1-holes" represents the layer name in Inkscape
def holeParts = s.extrudeLayerToCSG(10,"1-holes")
// seperate holes and outsides using layers to differentiate
// The string "2-outsides" represents the layer name in Inkscape
def outsideParts = s.extrudeLayerToCSG(10,"2-outsides")
					.difference(holeParts)
// layers can be extruded at different depths
// The string "3-boarder" represents the layer name in Inkscape					
def boarderParts = s.extrudeLayerToCSG(5,"3-boarder")

return [CSG.unionAll([boarderParts,outsideParts]),s.extrudeLayerToCSG(2,"4-star")]