public class JavaDocExtractor {

    public static void extractDocs(Path srcDir, Path outputFile) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        Files.walk(srcDir)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(file -> {
                try {
                    CompilationUnit cu = JavaParser.parse(file);
                    Optional<ClassOrInterfaceDeclaration> classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class);
                    if (classDecl.isPresent()) {
                        Map<String, Object> classData = new HashMap<>();
                        classData.put("class", classDecl.get().getNameAsString());

                        classData.put("classJavadoc", classDecl.get()
                            .getJavadoc()
                            .map(Javadoc::toText)
                            .orElse(""));

                        List<Map<String, String>> methods = classDecl.get()
                            .getMethods().stream()
                            .map(m -> Map.of(
                                "name", m.getNameAsString(),
                                "javadoc", m.getJavadoc().map(Javadoc::toText).orElse("")))
                            .collect(Collectors.toList());

                        classData.put("methods", methods);
                        results.add(classData);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse: " + file + " -> " + e.getMessage());
                }
            });

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(outputFile.toFile(), results);
        System.out.println("✅ JavaDocs extracted to: " + outputFile);
    }
}
