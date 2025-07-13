package com.techspec.agent.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MarkdownExporter {

    public static void exportToFile(String content, String outputPath) throws IOException {
        String markDown = extractMarkdown(content);
        Files.createDirectories(Path.of(outputPath).getParent());
        Files.writeString(Path.of(outputPath), content);
        System.out.println("✅ Markdown exported to: " + outputPath);
    }

    private String extractMarkdown(String responseBody) throws IOException {
   
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);
            if (node.has("output")) {
                return node.get("output").asText();
            } else if (node.has("text")) {
                return node.get("text").asText();
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

    
        return responseBody;
    }

    public static void exportAsHtml(String content, String htmlOutputPath) throws IOException {
        String markDown = extractMarkdown(content);
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        String styledHtml = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Master Technical Specification</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 2em;
                    line-height: 1.6;
                    background-color: #f8f9fa;
                    color: #333;
                }
                h1, h2, h3 {
                    color: #1a73e8;
                }
                pre, code {
                    background: #eee;
                    padding: 0.5em;
                    border-radius: 4px;
                    font-family: monospace;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 1.5em;
                }
                th, td {
                    border: 1px solid #ccc;
                    padding: 0.5em;
                    text-align: left;
                }
                th {
                    background-color: #f1f1f1;
                }
                a {
                    color: #1a73e8;
                }
            </style>
        </head>
        <body>
            %s
        </body>
        </html>
        """.formatted(html);


        Files.writeString(Path.of(htmlOutputPath), styledHtml);
        System.out.println("✅ HTML exported to: " + htmlOutputPath);
    }


    private static String stripMarkdown(String markdown) {
    return markdown
        .replaceAll("(?m)^#{1,6} ", "")
        .replaceAll("[*_]{1,2}([^*_]+)[*_]{1,2}", "$1")
        .replaceAll("!\\[[^]]*]\\([^)]*\\)", "")
        .replaceAll("\\[([^]]+)]\\([^)]*\\)", "$1")
        .replaceAll("`{1,3}([^`]*)`{1,3}", "$1")
        .replaceAll("\\r\\n|\\r|\\n", "\n")
        .replaceAll("\\\\n", "\n");
    }


   

    public static void exportAsDocx(String markdown, String docxOutputPath) throws IOException {
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(stripMarkdown(markdown)); 

            Files.createDirectories(Path.of(docxOutputPath).getParent());
            try (FileOutputStream out = new FileOutputStream(docxOutputPath)) {
                doc.write(out);
            }
            System.out.println("✅ Word DOCX exported to: " + docxOutputPath);
        }
    }



}
