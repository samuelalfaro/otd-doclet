#current="$(readlink -f $(dirname "$0"))"
#previous="$(pwd)"
#cd "$current"
cd "$(readlink -f $(dirname "$0"))"
javadoc -doclet org.sam.odf_doclet.ODFDoclet -docletpath odf-doclet.jar -projectRootpath /media/DATA/Samuel/Proyectos/odf-doclet/ -projectClasspath lib/batik-anim.jar:lib/batik-awt-util.jar:lib/batik-bridge.jar:lib/batik-codec.jar:lib/batik-css.jar:lib/batik-dom.jar:lib/batik-ext.jar:lib/batik-extension.jar:lib/batik-gui-util.jar:lib/batik-gvt.jar:lib/batik-parser.jar:lib/batik-script.jar:lib/batik-svg-dom.jar:lib/batik-svggen.jar:lib/batik-swing.jar:lib/batik-transcoder.jar:lib/batik-util.jar:lib/batik-xml.jar:lib/js.jar:lib/pdf-transcoder.jar:lib/xml-apis-ext.jar:lib/xml-apis.jar:lib/xalan-2.6.0.jar:lib/xerces_2_5_0.jar:lib/htmlcleaner-2.2.jar:bin -subpackages org src