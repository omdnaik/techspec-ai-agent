import sys
import os

try:
    import pdfplumber
    import docx
except ImportError:
    print("Error: Missing dependencies. Run: pip install pdfplumber python-docx", file=sys.stderr)
    sys.exit(1)

def extract_text(file_path):
    """Extracts raw text from PDF or DOCX functional specifications."""
    if not os.path.exists(file_path):
        print(f"Error: File {file_path} not found.", file=sys.stderr)
        return

    ext = file_path.lower().split('.')[-1]
    extracted_text = ""

    try:
        if ext == 'pdf':
            with pdfplumber.open(file_path) as pdf:
                for page in pdf.pages:
                    # Extract text and append, handling None returns
                    page_text = page.extract_text()
                    if page_text:
                        extracted_text += page_text + "\n"
        elif ext in ['doc', 'docx']:
            doc = docx.Document(file_path)
            extracted_text = "\n".join([para.text for para in doc.paragraphs])
        else:
            print(f"Error: Unsupported file format '.{ext}'", file=sys.stderr)
            return

        # Print to standard output so OpenCode can read it
        print(extracted_text)

    except Exception as e:
        print(f"Extraction failed: {str(e)}", file=sys.stderr)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        target_file = sys.argv[1]
        extract_text(target_file)
    else:
        print("Usage: python extract_text.py <path_to_FSD_file>", file=sys.stderr)
