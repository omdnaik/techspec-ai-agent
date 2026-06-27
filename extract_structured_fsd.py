import sys
import os
import json
from pydantic import BaseModel, Field
from llama_index.core import SimpleDirectoryReader
from llama_index.core.program import LLMTextCompletionProgram
from llama_index.llms.openai import OpenAI # Or whichever LLM you are using locally

# 1. Define the exact structure of your FSD template
class FSDDataExtract(BaseModel):
    core_business_domains: list[str] = Field(
        description="The main business entities discussed (e.g., ShoppingCart, TaxRate, UserProfile)."
    )
    database_tables_mentioned: list[str] = Field(
        description="Any specific database tables or columns mentioned in the Data Dictionary section."
    )
    business_rules: list[str] = Field(
        description="The bullet points from the 'Business Logic' or 'Validation Rules' section."
    )
    api_endpoints: list[str] = Field(
        description="Any URIs or REST endpoints mentioned in the API section."
    )

def parse_fsd_template(file_path: str):
    if not os.path.exists(file_path):
        print(json.dumps({"error": f"File {file_path} not found."}))
        return

    # 2. Read the document
    documents = SimpleDirectoryReader(input_files=[file_path]).load_data()
    full_text = "\n".join([doc.text for doc in documents])

    # 3. Use LlamaIndex to extract the templated data into our Pydantic model
    llm = OpenAI(model="gpt-4o-mini") # Configure to your local/preferred model
    program = LLMTextCompletionProgram.from_defaults(
        output_cls=FSDDataExtract,
        llm=llm,
        prompt_template_str=(
            "You are an expert technical analyst. Extract the relevant information "
            "from the following Functional Specification Document into the requested JSON format.\n"
            "Document Text:\n{text}"
        ),
    )

    try:
        # 4. Execute the extraction
        structured_output = program(text=full_text)
        # Print the clean JSON string to stdout for the OpenCode agent
        print(structured_output.model_dump_json(indent=2))
    except Exception as e:
        print(json.dumps({"error": str(e)}), file=sys.stderr)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        parse_fsd_template(sys.argv[1])
    else:
        print(json.dumps({"error": "Usage: python extract_structured_fsd.py <file_path>"}), file=sys.stderr)
