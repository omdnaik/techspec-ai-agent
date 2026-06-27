import sys
import os
import tree_sitter_java as tsjava
from tree_sitter import Language, Parser

def get_java_method_source(file_path: str, method_name: str):
    if not os.path.exists(file_path):
        print(f"Error: File {file_path} not found.", file=sys.stderr)
        return

    # Initialize the parser
    JAVA_LANGUAGE = Language(tsjava.language())
    parser = Parser()
    parser.language = JAVA_LANGUAGE

    with open(file_path, "rb") as f:
        file_bytes = f.read()

    tree = parser.parse(file_bytes)
    root_node = tree.root_node

    def find_method(node, target_name):
        if node.type == 'method_declaration':
            name_node = node.child_by_field_name('name')
            if name_node and file_bytes[name_node.start_byte:name_node.end_byte].decode('utf8') == target_name:
                return node
        
        for child in node.children:
            result = find_method(child, target_name)
            if result:
                return result
        return None

    target_node = find_method(root_node, method_name)

    if not target_node:
        print(f"Error: Method '{method_name}' not found in AST of {file_path}.", file=sys.stderr)
        return

    method_source = file_bytes[target_node.start_byte:target_node.end_byte].decode('utf8')
    
    # The God-Method Fail-Safe
    line_count = len(method_source.splitlines())
    if line_count > 150:
        signature = method_source.split('{')[0]
        print(
            f"WARNING: Method '{method_name}' is too large ({line_count} lines). \n"
            f"Signature: {signature} {{ \n"
            f"  // [METHOD BODY REDACTED DUE TO SIZE. REFACTOR REQUIRED.] \n"
            f"}}"
        )
        return

    # Print directly to stdout for OpenCode to capture
    print(method_source)

if __name__ == "__main__":
    if len(sys.argv) > 2:
        get_java_method_source(sys.argv[1], sys.argv[2])
    else:
        print("Usage: python ast_extractor.py <file_path> <method_name>", file=sys.stderr)
