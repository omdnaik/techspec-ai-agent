import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.wml.Text;
import org.docx4j.wml.R;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkdownToTemplateMerger {

    public static void mergeMarkdownSectionsIntoTemplate(Map<String, String> sectionMarkdownMap,
                                                          String templatePath,
                                                          String outputPath) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(templatePath));
        MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();
        XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);

        for (Map.Entry<String, String> entry : sectionMarkdownMap.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String html = MarkdownToStyledDocxExporter.convertMarkdownToHtml(entry.getValue());

            // Find all Text elements in the document
            List<Object> texts = mainPart.getJAXBNodesViaXPath("//w:t", true);
            for (Object obj : texts) {
                Text textElement = (Text) obj;
                if (textElement.getValue().equals(placeholder)) {
                    // Get the parent <w:r> node (Run)
                    R run = (R) textElement.getParent();

                    // Replace this run with imported HTML content
                    int index = mainPart.getContent().indexOf(run.getParent());
                    if (index != -1) {
                        List<Object> htmlObjects = importer.convert(html, null);
                        mainPart.getContent().remove(run.getParent());
                        mainPart.getContent().addAll(index, htmlObjects);
                        break;
                    }
                }
            }
        }

        wordMLPackage.save(new File(outputPath));
        System.out.println("✅ Merged markdown into Word template → " + outputPath);
    }
}
