package com.techspec.agent.export;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MarkdownToStyledDocxExporter {

    /**
     * Convert a Markdown file to a styled Word document using a pre-defined template.
     *
     * @param markdownPath Path to the markdown input file
     * @param templatePath Path to the .docx Word template
     * @param outputDocxPath Final output path for the styled Word doc
     */
    public static void exportWithTemplate(String markdownPath, String templatePath, String outputDocxPath) {
        try {
            // Read Markdown content
            String markdown = Files.readString(Paths.get(markdownPath));
            String html = convertMarkdownToHtml(markdown);

            // Load the .docx template with styles
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(templatePath));

            // Import HTML (converted from Markdown) using template styles
            XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(importer.convert(html, null));

            // Save final .docx
            wordMLPackage.save(new File(outputDocxPath));

            System.out.println("✅ Exported " + outputDocxPath + " using template: " + templatePath);
        } catch (IOException | Docx4JException e) {
            System.err.println("❌ Failed to export Word doc: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String convertMarkdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Document document = parser.parse(markdown);
        return renderer.render(document);
    }
}
