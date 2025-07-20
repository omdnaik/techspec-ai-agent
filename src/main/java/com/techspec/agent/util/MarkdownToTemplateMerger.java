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

    private static String convertMarkdownToHtml(String markdown) {
    MutableDataSet options = new MutableDataSet();
    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    Document document = parser.parse(markdown);
    String body = renderer.render(document);

    // Wrap body in full XHTML structure
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml">
          <head>
            <meta charset="UTF-8" />
            <title>Generated Doc</title>
          </head>
          <body>
            %s
          </body>
        </html>
        """.formatted(body.trim());
    }

    public static void mergeMarkdownSectionsIntoTemplate(Map<String, String> sectionMarkdownMap,
                                                     String templatePath,
                                                     String outputPath) throws Exception {

    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(templatePath));
    MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();
    XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);

    List<Object> allTextElements = mainPart.getJAXBNodesViaXPath("//w:t", true);

    for (Map.Entry<String, String> entry : sectionMarkdownMap.entrySet()) {
        String placeholder = "${" + entry.getKey() + "}";
        String html = MarkdownToStyledDocxExporter.convertMarkdownToHtml(entry.getValue());

        for (Object obj : allTextElements) {
            Text textElement = (Text) obj;
            if (textElement.getValue().equals(placeholder)) {
                // Replace placeholder text
                textElement.setValue(""); // Optional: clear placeholder before inserting

                // Get the parent run and paragraph
                R run = (R) textElement.getParent();
                P paragraph = (P) run.getParent();

                // Get the index in document content
                List<Object> content = mainPart.getContent();
                int index = content.indexOf(paragraph);

                if (index != -1) {
                    // Convert HTML
                    List<Object> imported = importer.convert(html, null);

                    // Replace the placeholder paragraph
                    content.remove(index);
                    content.addAll(index, imported);
                    break; // Move to next placeholder
                }
            }
        }
    }

    wordMLPackage.save(new File(outputPath));
    }
    public class HtmlUtils {

    /**
     * Escapes and formats plain SQL or code as HTML with <pre> and <br/> for line breaks.
     * Suitable for insertion into DOCX via XHTMLImporterImpl in Docx4j.
     *
     * @param code  The raw SQL or code block
     * @return      Formatted HTML string
     */
    public static String prepareHtmlCodeBlock(String code) {
        if (code == null) return "";

        // Escape HTML special characters
        String escaped = code
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        // Replace line breaks with <br/> for HTML rendering
        String htmlBody = escaped
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;") // Optional: Convert tabs
                .replace(" ", "&nbsp;")                    // Preserve spacing
                .replace("\n", "<br/>");

        // Wrap in <pre> with optional styling
        return "<pre style=\"font-family: Consolas, monospace; font-size: 10pt;\">" +
                htmlBody +
                "</pre>";
    }
    }
}
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkdownHtmlFormatter {

    /**
     * Applies inline styling only to code blocks in an HTML string converted from Markdown.
     * @param html the raw HTML converted from Markdown
     * @return styled HTML ready for Docx4j import
     */
    public static String formatHtmlFromMarkdown(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Elements codeBlocks = doc.select("pre > code");

        for (Element code : codeBlocks) {
            Element pre = code.parent();
            // Remove <code> and keep contents
            pre.html(code.html());

            // Apply inline styles
            pre.attr("style", "font-family: Consolas, monospace; font-size:10pt; white-space:pre-wrap;");
        }

        return doc.body().html();
    }
}
